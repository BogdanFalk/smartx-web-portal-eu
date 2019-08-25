package smartx.businesslogic.authentication;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


@Component
public class MyAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Override
    protected void handle(HttpServletRequest request,
                          HttpServletResponse response, Authentication authentication) throws IOException {
        String targetUrl = determineTargetUrl(authentication);
        System.out.println("handle: " + targetUrl);

        if (response.isCommitted()) {
        	System.out.println("Can't redirect");
            return;
        }

        redirectStrategy.sendRedirect(request, response, targetUrl);
    }

    protected String determineTargetUrl(Authentication authentication) {
    	System.out.println("determineTargetUrl: " + authentication.getName());
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        List<String> roles = new ArrayList<>();
        for (GrantedAuthority a : authorities) {
            System.out.println("Authority: " + a.getAuthority());
            roles.add(a.getAuthority());
        }

        if (isAdmin(roles)) {
            return "/admin";
        } else if (isUser(roles)) {
            return "/home";
        } else {
            return "/login?error";
        }
    }

    @Override
    public void setRedirectStrategy(RedirectStrategy redirectStrategy) {
        this.redirectStrategy = redirectStrategy;
    }

    @Override
    protected RedirectStrategy getRedirectStrategy() {
        return redirectStrategy;
    }

    private boolean isUser(List<String> roles) {
        return roles.contains("ROLE_USER");
    }

    private boolean isAdmin(List<String> roles) {
        return roles.contains("ROLE_ADMIN");
    }
}
