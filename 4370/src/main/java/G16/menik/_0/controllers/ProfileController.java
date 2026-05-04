package G16.menik._0.controllers;

import G16.menik._0.services.UserService;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ProfileController {

    private final UserService userService;

    public ProfileController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    public String showProfile(@RequestParam(value = "success", required = false) String success,
                              @RequestParam(value = "error", required = false) String error,
                              Model model) {
        model.addAttribute("title", "My Profile");
        model.addAttribute("pageTitle", "My Profile");
        model.addAttribute("pageSubtitle", "Update your account details or delete your account.");
        model.addAttribute("username", userService.getLoggedInUser().getUsername());
        model.addAttribute("fullName", userService.getLoggedInUser().getFullName());
        if (success != null) model.addAttribute("success", success);
        if (error != null) model.addAttribute("error", error);
        return "profile";
    }

    @PostMapping("/profile/update-name")
    public String updateName(@RequestParam("fullName") String fullName) {
        try {
            userService.updateFullName(userService.getLoggedInUser().getId(), fullName.trim());
            String msg = URLEncoder.encode("Name updated successfully.", StandardCharsets.UTF_8);
            return "redirect:/profile?success=" + msg;
        } catch (Exception e) {
            String msg = URLEncoder.encode("Update failed: " + e.getMessage(), StandardCharsets.UTF_8);
            return "redirect:/profile?error=" + msg;
        }
    }

    @PostMapping("/profile/update-username")
    public String updateUsername(@RequestParam("username") String username) {
        try {
            userService.updateUsername(userService.getLoggedInUser().getId(), username.trim());
            String msg = URLEncoder.encode("Username updated successfully.", StandardCharsets.UTF_8);
            return "redirect:/profile?success=" + msg;
        } catch (Exception e) {
            String msg = URLEncoder.encode("Update failed: " + e.getMessage(), StandardCharsets.UTF_8);
            return "redirect:/profile?error=" + msg;
        }
    }

    @PostMapping("/profile/update-password")
    public String updatePassword(@RequestParam("currentPassword") String currentPassword,
                                 @RequestParam("newPassword") String newPassword,
                                 @RequestParam("confirmPassword") String confirmPassword) {
        if (!newPassword.equals(confirmPassword)) {
            String msg = URLEncoder.encode("New passwords do not match.", StandardCharsets.UTF_8);
            return "redirect:/profile?error=" + msg;
        }
        if (newPassword.length() < 6) {
            String msg = URLEncoder.encode("New password must be at least 6 characters.", StandardCharsets.UTF_8);
            return "redirect:/profile?error=" + msg;
        }
        try {
            userService.updatePassword(userService.getLoggedInUser().getId(), currentPassword, newPassword);
            String msg = URLEncoder.encode("Password updated successfully.", StandardCharsets.UTF_8);
            return "redirect:/profile?success=" + msg;
        } catch (Exception e) {
            String msg = URLEncoder.encode("Update failed: " + e.getMessage(), StandardCharsets.UTF_8);
            return "redirect:/profile?error=" + msg;
        }
    }

    @PostMapping("/profile/delete")
    public String deleteAccount() {
        try {
            userService.deleteUser(userService.getLoggedInUser().getId());
            userService.unAuthenticate();
            return "redirect:/";
        } catch (Exception e) {
            String msg = URLEncoder.encode("Delete failed. Please try again.", StandardCharsets.UTF_8);
            return "redirect:/profile?error=" + msg;
        }
    }
}