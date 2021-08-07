package gr.tsamtsouris.movierama.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final String UNKNOWN_ERROR = "Unknown error";
    private static final String ERROR_MESSAGE = "errorMessage";
    private static final String EXCEPTION_DURING_EXECUTION_OF_SPRING_SECURITY_APPLICATION = "Exception during execution of SpringSecurity application";
    private static Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ModelAndView forbiddenException(final Throwable throwable) {
        logger.error(EXCEPTION_DURING_EXECUTION_OF_SPRING_SECURITY_APPLICATION, throwable);
        ModelAndView modelAndView = new ModelAndView("/403");
        String errorMessage = (throwable != null ? throwable.toString() : UNKNOWN_ERROR);
        modelAndView.addObject(ERROR_MESSAGE, errorMessage);
        return modelAndView;
    }

    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ModelAndView exception(final Throwable throwable) {
        logger.error(EXCEPTION_DURING_EXECUTION_OF_SPRING_SECURITY_APPLICATION, throwable);
        ModelAndView modelAndView = new ModelAndView("/error");
        String errorMessage = (throwable != null ? throwable.toString() : UNKNOWN_ERROR);
        modelAndView.addObject(ERROR_MESSAGE, errorMessage);
        return modelAndView;
    }

}
