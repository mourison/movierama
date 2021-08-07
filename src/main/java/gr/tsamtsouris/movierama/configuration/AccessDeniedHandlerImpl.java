package gr.tsamtsouris.movierama.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

public class AccessDeniedHandlerImpl implements AccessDeniedHandler {

    protected static final String FORBIDDEN = "/403";
    private static Logger logger = LoggerFactory.getLogger(AccessDeniedHandlerImpl.class);

    @Override
    public void handle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AccessDeniedException e) throws IOException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!Objects.isNull(auth)) {
            logger.info(String.format("User '%s' attempted to access the protected URL: %s", auth.getName(), httpServletRequest.getRequestURI()));
        }
        httpServletResponse.sendRedirect(httpServletRequest.getContextPath() + FORBIDDEN);
    }
}
