package G16.menik._0.components;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import G16.menik._0.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Interceptor component that enforces authentication for protected routes.
 * Redirects unauthenticated users to the login page before allowing access
 * to protected endpoints.
 */
@Component
public class AuthInterceptor implements HandlerInterceptor {

    private final UserService userService;

    /**
     * Constructs AuthInterceptor with the provided UserService.
     *
     * @param userService the service used to check authentication status
     */
    @Autowired
    public AuthInterceptor(UserService userService) {
        this.userService = userService;
    }

    /**
     * Checks if the user is authenticated before processing the request.
     * Redirects to login page if user is not authenticated.
     *
     * @param request the HTTP request
     * @param response the HTTP response
     * @param handler the handler object
     * @return {@code true} if request should continue, {@code false} if intercepted
     * @throws Exception if an error occurs during request handling
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!userService.isAuthenticated()) {
            response.sendRedirect("/login");
            return false;
        }
        return true;
    }
}
