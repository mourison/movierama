package gr.tsamtsouris.movierama.controllers;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ErrorHandlingController implements ErrorController {

    private static final String ERROR_VIEW = "/error";

    private static final String FORBIDDEN_VIEW = "/403";

    @RequestMapping("/error")
    public ModelAndView error() {
        return new ModelAndView(ERROR_VIEW);
    }

    @GetMapping("/403")
    public ModelAndView error403() {
        return new ModelAndView(FORBIDDEN_VIEW);
    }

}
