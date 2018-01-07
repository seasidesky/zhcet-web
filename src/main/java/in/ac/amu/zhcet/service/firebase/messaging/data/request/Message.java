package in.ac.amu.zhcet.service.firebase.messaging.data.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Message {
    private String token;
    private String topic;
    private NotificationBody notification;
    private DataBody data;
}
