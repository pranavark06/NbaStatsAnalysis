package G16.menik._0.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import G16.menik._0.services.UserService;

/**
 * Controller for handling home page navigation.
 * Manages landing page and authenticated home page display.
 */
@Controller
public class HomeController {

    private final UserService userService;

    /**
     * Constructs HomeController with the specified UserService.
     *
     * @param userService the service for checking user authentication status
     */
    public HomeController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Displays the landing page for unauthenticated users.
     *
     * @param model the Spring model to pass data to the template
     * @return the view name "landing" for rendering the landing page
     */
    @GetMapping("/")
    public String landing(Model model) {
        model.addAttribute("title", "Welcome");
        model.addAttribute("pageTitle", "Welcome");
        model.addAttribute("pageSubtitle", "Sign up or log in to access the home page.");
        return "landing";
    }

    /**
     * Displays the home page for authenticated users.
     * Redirects to login page if user is not authenticated.
     *
     * @param model the Spring model to pass data to the template
     * @return the view name "home" for rendering the home page, or redirect to login if not authenticated
     */
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
