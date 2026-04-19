package G16.menik._0.controllers;

import G16.menik._0.services.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final UserService userService;

    public HomeController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    public String landing(Model model) {
        model.addAttribute("title", "Welcome");
        model.addAttribute("pageTitle", "Welcome");
        model.addAttribute("pageSubtitle", "Sign up or log in to access the home page.");
        return "landing";
    }

    @GetMapping("/home")
    public String home(Model model) {
        if (!userService.isAuthenticated()) {
            return "redirect:/login";
        }
        model.addAttribute("title", "Home");
        model.addAttribute("pageTitle", "Home");
        model.addAttribute("pageSubtitle", "Welcome back, " + userService.getLoggedInUser().getFullName() + "!");
        model.addAttribute("username", userService.getLoggedInUser().getUsername());
        model.addAttribute("fullName", userService.getLoggedInUser().getFullName());
        return "home";
    }
}
