package in.ac.amu.zhcet.controller.faculty;

import in.ac.amu.zhcet.data.model.notification.Notification;
import in.ac.amu.zhcet.service.notification.NotificationManagementService;
import in.ac.amu.zhcet.utils.NotificationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.List;

@Controller
public class ManageNotificationsController {

    private final NotificationManagementService notificationManagementService;

    @Autowired
    public ManageNotificationsController(NotificationManagementService notificationManagementService) {
        this.notificationManagementService = notificationManagementService;
    }

    @GetMapping("/notification/manage")
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

        return "faculty/manage_notifications";
    }

    @GetMapping("/notification/edit/{notification}")
    public String editNotification(@RequestParam(required = false) Integer page, @PathVariable Notification notification, RedirectAttributes redirectAttributes) {
        if (notification == null)
            return "redirect:/notification/manage?page=" + NotificationUtils.normalizePage(page);

        notificationManagementService.deleteNotification(notification);
        redirectAttributes.addFlashAttribute("notification_success", "Notification Deleted");
        return "redirect:/notification/manage?page=" + page;
    }

    @PostMapping("/notification/edit/{notification}")
    public String saveEditNotification(@RequestParam(required = false) Integer page, @PathVariable Notification notification,
                                       @RequestParam @Valid Notification edited, BindingResult result,
                                       RedirectAttributes redirectAttributes)
    {
        if (notification == null)
            return "redirect:/notification/manage?page=" + NotificationUtils.normalizePage(page);

        notification.setTitle(edited.getTitle());
        notification.setMessage(edited.getMessage());
        notificationManagementService.saveNotification(notification);
        redirectAttributes.addFlashAttribute("notification_success", "Notification Edited");
        return "redirect:/notification/manage?page=" + page;
    }

    @GetMapping("/notification/delete/{notification}")
    public String deleteNotification(@RequestParam(required = false) Integer page, @PathVariable Notification notification, RedirectAttributes redirectAttributes) {
        if (notification == null)
            return "redirect:/notification/manage?page=" + NotificationUtils.normalizePage(page);

        notificationManagementService.deleteNotification(notification);
        redirectAttributes.addFlashAttribute("notification_success", "Notification Deleted");
        return "redirect:/notification/manage?page=" + page;
    }

}
