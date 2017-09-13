package in.ac.amu.zhcet.controller.login;


import in.ac.amu.zhcet.data.model.dto.PasswordReset;
import in.ac.amu.zhcet.service.token.PasswordResetService;
import in.ac.amu.zhcet.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@Controller
public class ResetPasswordController {

    private final PasswordResetService passwordResetService;

    public ResetPasswordController(PasswordResetService passwordResetService) {
        this.passwordResetService = passwordResetService;
    }

    @GetMapping("/login/reset_password")
    public String resetPassword(Model model, @RequestParam("id") String id, @RequestParam("token") String token){
        String result = passwordResetService.validate(id, token);
        if (result != null) {
            model.addAttribute("error", result);
            return "reset_password";
        }
        PasswordReset passwordReset = new PasswordReset();
        passwordReset.setId(id);
        passwordReset.setToken(token);
        model.addAttribute("password", passwordReset);
        return "reset_password";
    }

    @PostMapping("/login/reset_password")
    public String savePassword(@Valid PasswordReset passwordReset, RedirectAttributes redirectAttributes) {
        String redirectUrl = "redirect:/login/reset_password?id="+passwordReset.getId()+"&token="+passwordReset.getToken();

        String result = passwordResetService.validate(passwordReset.getId(), passwordReset.getToken());
        if (result != null) {
            redirectAttributes.addAttribute("error", result);
            return redirectUrl;
        }

        List<String> errors = Utils.validatePassword(passwordReset.getNewPassword(), passwordReset.getConfirmPassword());

        if (!errors.isEmpty()) {
            redirectAttributes.addFlashAttribute("pass_errors", errors);
            return redirectUrl;
        }

        passwordResetService.resetPassword(passwordReset.getNewPassword(), passwordReset.getToken());
        redirectAttributes.addFlashAttribute("reset_success", true);
        return "redirect:/login";
    }

}