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
public class SignupController {

    private final UserService userService;

    public SignupController(UserService userService) {
        this.userService = userService;
    }

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
