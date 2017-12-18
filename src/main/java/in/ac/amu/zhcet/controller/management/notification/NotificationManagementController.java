package in.ac.amu.zhcet.controller.management.notification;

import in.ac.amu.zhcet.data.model.notification.Notification;
import in.ac.amu.zhcet.service.notification.NotificationManagementService;
import in.ac.amu.zhcet.utils.NotificationUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Slf4j
@Controller
public class NotificationManagementController {

    private final NotificationManagementService notificationManagementService;

    @Autowired
    public NotificationManagementController(NotificationManagementService notificationManagementService) {
        this.notificationManagementService = notificationManagementService;
    }

    @GetMapping("/management/notifications")
    public String manageNotifications(@RequestParam(required = false) Integer page, Model model) {
        model.addAttribute("page_title", "Manage Notifications");
        model.addAttribute("page_subtitle", "Notification Manager");
        model.addAttribute("page_description", "Manage and monitor sent notifications");

        int currentPage = NotificationUtils.normalizePage(page);
        Page<Notification> notificationPage = notificationManagementService.getNotifications(currentPage);

        NotificationUtils.prepareNotifications(model, notificationPage, currentPage);
        List<Notification> notifications = notificationPage.getContent();
        notificationManagementService.setInformation(notifications);
        model.addAttribute("notifications", notifications);

        return "management/manage_notifications";
    }

    @GetMapping("/management/notifications/{notification}/report")
    public String notificationReport(@RequestParam(required = false) Integer page, @PathVariable Notification notification, Model model) {
        String templateUrl = "management/notification_report";
        if (notification == null)
            return templateUrl;

        model.addAttribute("page_title", "Notification Report");
        model.addAttribute("page_subtitle", "Notification Manager");
        model.addAttribute("page_description", "View notification receipt");

        notificationManagementService.setSeenCount(notification);
        model.addAttribute("notification", notification);

        return templateUrl;
    }

    @GetMapping("/management/notifications/{notification}/delete")
    public String deleteNotification(@RequestParam(required = false) Integer page, @PathVariable Notification notification, RedirectAttributes redirectAttributes) {
        int currentPage = NotificationUtils.normalizePage(page);
        String redirectUrl = "redirect:/management/notifications?page=" + currentPage;

        if (notification == null)
            return redirectUrl;

        notificationManagementService.deleteNotification(notification);
        redirectAttributes.addFlashAttribute("notification_success", "Notification Deleted");
        return redirectUrl;
    }

}