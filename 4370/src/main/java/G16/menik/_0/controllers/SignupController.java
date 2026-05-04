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
 * Controller for handling user registration and account creation.
 * Manages signup form display and new user account creation.
 */
@Controller
public class SignupController {

    private final UserService userService;

    /**
     * Constructs SignupController with the specified UserService.
     *
     * @param userService the service for managing user accounts
     */
    public SignupController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Displays the signup form for new user registration.
     * Shows error messages if provided from previous validation failures.
     *
     * @param error optional error message parameter from failed signup attempts
     * @param model the Spring model to pass data to the template
     * @return the view name "signup" for rendering the signup page
     */
    @GetMapping("/signup")
    public String showSignupForm(@RequestParam(value = "error", required = false) String error,
                                 Model model) {
        model.addAttribute("title", "Sign Up");
        model.addAttribute("pageTitle", "Sign Up");
        model.addAttribute("pageSubtitle", "Create a new account to access the home page.");
        model.addAttribute("fullName", "");
        model.addAttribute("username", "");
        if (error != null) {
            model.addAttribute("error", error);
        }
        return "signup";
    }

    /**
     * Processes signup form submission and creates a new user account.
     * Validates form input including password confirmation and minimum length requirements.
     * Redirects to login page on successful account creation or back to signup with error on failure.
     *
     * @param username the desired login username
     * @param password the desired account password
     * @param confirmPassword confirmation of the password
     * @param fullName the user's full name
     * @return a redirect to login page on success or signup page with error on failure
     */
    @PostMapping("/signup")
    public String registerUser(@RequestParam("username") String username,
                               @RequestParam("password") String password,
                               @RequestParam("confirmPassword") String confirmPassword,
                               @RequestParam("fullName") String fullName) {
        if (username == null || username.isBlank() ||
            password == null || password.isBlank() ||
            fullName == null || fullName.isBlank()) {
            String message = URLEncoder.encode("All fields are required.", StandardCharsets.UTF_8);
            return "redirect:/signup?error=" + message;
        }

        if (password.trim().length() < 3) {
            String message = URLEncoder.encode("Passwords should have at least 3 nonempty letters.",
                    StandardCharsets.UTF_8);
            return "redirect:/signup?error=" + message;
        }

        if (!password.equals(confirmPassword)) {
            String message = URLEncoder.encode("Passwords do not match.", StandardCharsets.UTF_8);
            return "redirect:/signup?error=" + message;
        }

        try {
            userService.createUser(username.trim(), password, fullName.trim());
            return "redirect:/login?registered=true";
        } catch (Exception e) {
            String message = URLEncoder.encode("Username already exists or an error occurred.",
                    StandardCharsets.UTF_8);
            return "redirect:/signup?error=" + message;
        }
    }
}
