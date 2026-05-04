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
 * Controller for handling user login authentication.
 * Manages login form display and authentication processing.
 */
@Controller
public class LoginController {

    private final UserService userService;

    /**
     * Constructs LoginController with the specified UserService.
     *
     * @param userService the service for handling user authentication
     */
    public LoginController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Displays the login form to unauthenticated users.
     * Clears any existing authentication and shows error messages if provided.
     *
     * @param error optional error message parameter from failed login attempts
     * @param model the Spring model to pass data to the template
     * @return the view name "login" for rendering the login page
     */
    @GetMapping("/login")
    public String showLoginForm(@RequestParam(value = "error", required = false) String error,
                                Model model) {
        userService.unAuthenticate();
        model.addAttribute("title", "Login");
        model.addAttribute("pageTitle", "Login");
        model.addAttribute("pageSubtitle", "Enter your username and password to continue.");
        model.addAttribute("username", "");
        if (error != null) {
            model.addAttribute("error", error);
        }
        return "login";
    }

    /**
     * Processes login form submission and authenticates the user.
     * Redirects to home page on successful authentication or back to login with error message on failure.
     *
     * @param username the user's login username
     * @param password the user's password
     * @return a redirect to home page on success or login page with error on failure
     */
    @PostMapping("/login")
    public String login(@RequestParam("username") String username,
                        @RequestParam("password") String password) {
        try {
            if (userService.authenticate(username.trim(), password)) {
                return "redirect:/home";
            }
        } catch (Exception ignored) {
            String message = URLEncoder.encode("Authentication failed. Please try again.",
                    StandardCharsets.UTF_8);
            return "redirect:/login?error=" + message;
        }

        String message = URLEncoder.encode("Invalid username or password. Please try again.",
                StandardCharsets.UTF_8);
        return "redirect:/login?error=" + message;
    }

    /**
     * Logs out the current user and clears the session.
     * Redirects to the landing page.
     *
     * @return a redirect to the landing page
     */
    @GetMapping("/logout")
    public String logout() {
        userService.unAuthenticate();
        return "redirect:/";
    }
}
