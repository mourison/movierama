package gr.tsamtsouris.movierama.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.util.Objects;

@Controller
public class LoginController {

    public static final String LOGIN_PATH = "/login";

    @GetMapping(LOGIN_PATH)
    public String login(Principal principal) {
        return Objects.nonNull(principal) ? "redirect:home" : "/login";
    }
}
