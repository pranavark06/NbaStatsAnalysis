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
public class LoginController {

    private final UserService userService;

    public LoginController(UserService userService) {
        this.userService = userService;
    }

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

    @GetMapping("/logout")
    public String logout() {
        userService.unAuthenticate();
        return "redirect:/";
    }
}
