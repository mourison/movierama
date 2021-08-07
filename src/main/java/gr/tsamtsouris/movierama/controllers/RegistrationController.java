package gr.tsamtsouris.movierama.controllers;

import gr.tsamtsouris.movierama.dtos.UserDto;
import gr.tsamtsouris.movierama.entities.User;
import gr.tsamtsouris.movierama.services.SecurityService;
import gr.tsamtsouris.movierama.services.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.util.Objects;

import static gr.tsamtsouris.movierama.constants.PageConstants.*;

@Controller
@RequestMapping("/registration")
public class RegistrationController {

    private final UserService userService;
    private final SecurityService securityService;

    public RegistrationController(UserService userService, SecurityService securityService) {
        this.userService = userService;
        this.securityService = securityService;
    }

    @GetMapping
    public String register(Model model) {
        model.addAttribute(USER_ATTRIBUTE, new User());
        return "/registration";
    }

    @PostMapping
    public String submit(@Valid UserDto user, BindingResult bindingResult, Model model) {
        UserDto registeredUser = userService.findByEmail(user.getEmail());
        if (Objects.nonNull(registeredUser)) {
            bindingResult.rejectValue(EMAIL_ATTRIBUTE, "error.user", "There is already a user registered with provided email");
            model.addAttribute(USER_ATTRIBUTE, user);
        }
        if (!bindingResult.hasErrors()) {
            UserDto savedUser = userService.save(user);
            model.addAttribute(SUCCESS_MESSAGE_ATTRIBUTE, "User has been registered successfully");
            model.addAttribute(USER_ATTRIBUTE, savedUser);
            securityService.login(user.getEmail(), user.getPassword());
            return "redirect:home";
        }
        return "/registration";

    }

}
