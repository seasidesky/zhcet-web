package in.ac.amu.zhcet.service.firebase.messaging;

import feign.Feign;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.okhttp.OkHttpClient;
import in.ac.amu.zhcet.data.model.notification.Notification;
import in.ac.amu.zhcet.data.model.notification.NotificationRecipient;
import in.ac.amu.zhcet.service.UserService;
import in.ac.amu.zhcet.service.firebase.FirebaseService;
import in.ac.amu.zhcet.service.firebase.messaging.data.SendRequest;
import in.ac.amu.zhcet.service.firebase.messaging.data.SendResponse;
import in.ac.amu.zhcet.service.firebase.messaging.data.request.DataBody;
import in.ac.amu.zhcet.service.firebase.messaging.data.request.Message;
import in.ac.amu.zhcet.service.firebase.messaging.data.request.NotificationBody;
import in.ac.amu.zhcet.service.markdown.MarkDownService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class FirebaseMessagingService {

    private static final String BASE_URL = "https://fcm.googleapis.com/v1/projects/%s/messages:send";
    private static final Map<String, Object> HEADER_MAP = new HashMap<>();

    private final FirebaseService firebaseService;
    private final MarkDownService markDownService;
    private final UserService userService;

    private MessagingClient messagingClient;

    @Autowired
    public FirebaseMessagingService(FirebaseService firebaseService, MarkDownService markDownService, UserService userService) {
        this.firebaseService = firebaseService;
        this.markDownService = markDownService;
        this.userService = userService;

        log.info("CONFIG (Firebase): Firebase Messaging Running : {}", firebaseService.canSendMessage());
    }

    /**
     * Attaches FCM registration received from front end to user for push notifications
     * @param userId User ID to attach token info to
     * @param token FCM Registration Token to be attached
     */
    @Async
    public void attachToken(String userId, String token) {
        if (userId == null || token == null)
            return;

        userService.findById(userId).ifPresent(user -> {
            user.getDetails().setFcmToken(token);
            log.info("Added FCM token {} to user : {}", token, user.getUserId());
            userService.save(user);
        });
    }

    /**
     * Sends a push notification to notification recipient user using FCM token
     * NotificationBody and DataBody is created for FCM HTTP V1 API using the Notification content passed
     * If FCM token of user is not found, sending is skipped
     * @param notificationRecipient Notification Recipient
     */
    @Async
    public void sendMessage(NotificationRecipient notificationRecipient) {
        if (!firebaseService.canSendMessage())
            return;

        try {
            String fcmToken = notificationRecipient.getRecipient().getDetails().getFcmToken();

            if (fcmToken == null)
                return;

            Notification notification = notificationRecipient.getNotification();

            NotificationBody notificationBody = NotificationBody.builder()
                    .title("New Notification")
                    .body(notification.getTitle())
                    .build();

            DataBody dataBody = DataBody.builder()
                    .title(markDownService.render(notification.getTitle()))
                    .message(markDownService.render(notification.getMessage()))
                    .sender(notification.getSender().getName())
                    .sentTime(notification.getSentTime().toString())
                    .build();

            HEADER_MAP.put("Authorization", "Bearer " + firebaseService.getAccessToken());
            log.info(HEADER_MAP.toString());
            SendResponse sendResponse = getMessagingClient().sendMessage(
                    SendRequest.builder().message(
                            Message.builder()
                                    .notification(notificationBody)
                                    .data(dataBody)
                                    .build())
                            .build(),
                    HEADER_MAP);

            log.info("Sent Broadcast : {}", sendResponse);
        } catch (IOException e) {
            log.error("Error sending notification", e);
        }
    }

    private MessagingClient getMessagingClient() {
        if (messagingClient == null) {
            messagingClient = Feign.builder()
                    .client(new OkHttpClient())
                    .decoder(new JacksonDecoder())
                    .encoder(new JacksonEncoder())
                    .target(MessagingClient.class, String.format(BASE_URL, firebaseService.getProjectId()));
        }

        return messagingClient;
    }

}
