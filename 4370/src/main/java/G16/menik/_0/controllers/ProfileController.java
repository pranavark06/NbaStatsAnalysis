package G16.menik._0.controllers;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import G16.menik._0.services.UserService;

/**
 * Controller for handling user profile management.
 * Manages profile display, name updates, username changes, and password modifications.
 */
@Controller
public class ProfileController {

    private final UserService userService;

    /**
     * Constructs ProfileController with the specified UserService.
     *
     * @param userService the service for managing user profile updates
     */
    public ProfileController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Displays the user's profile page with current account information.
     * Shows success or error messages from previous update attempts.
     *
     * @param success optional success message parameter from completed updates
     * @param error optional error message parameter from failed updates
     * @param model the Spring model to pass data to the template
     * @return the view name "profile" for rendering the profile page
     */
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

    /**
     * Updates the user's full name.
     * Redirects to profile page with success or error message.
     *
     * @param fullName the new full name to update
     * @return a redirect to the profile page
     */
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

    /**
     * Updates the user's username.
     * Validates that the new username is not already taken.
     * Redirects to profile page with success or error message.
     *
     * @param username the new username to update
     * @return a redirect to the profile page
     */
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

    /**
     * Updates the user's password after verification of the current password.
     * Validates that new passwords match and meet minimum length requirements.
     * Redirects to profile page with success or error message.
     *
     * @param currentPassword the user's current password for verification
     * @param newPassword the new password to set
     * @param confirmPassword confirmation of the new password
     * @return a redirect to the profile page
     */
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

    /**
     * Deletes the currently authenticated user account.
     * Logs the user out after successful deletion.
     *
     * @return a redirect to landing page on success, or back to profile with error on failure
     */
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