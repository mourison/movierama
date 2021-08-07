package gr.tsamtsouris.movierama.controllers;

import gr.tsamtsouris.movierama.constants.PageConstants;
import gr.tsamtsouris.movierama.dtos.MovieDto;
import gr.tsamtsouris.movierama.dtos.UserDto;
import gr.tsamtsouris.movierama.services.MovieService;
import gr.tsamtsouris.movierama.services.ReactionService;
import gr.tsamtsouris.movierama.services.UserService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static gr.tsamtsouris.movierama.constants.PageConstants.*;

@Controller
@RequestMapping("/movies")
public class MovieController {

    private final UserService userService;
    private final MovieService movieService;
    private final ReactionService reactionService;

    public MovieController(UserService userService, MovieService movieService, ReactionService reactionService) {
        this.userService = userService;
        this.movieService = movieService;
        this.reactionService = reactionService;
    }

    @GetMapping("/newMovie")
    public String createMovie(Principal principal, Model model) {
        UserDto user = userService.findByEmail(principal.getName());
        if (Objects.nonNull(user)) {
            MovieDto movie = new MovieDto();
            movie.setUser(user);
            model.addAttribute(MOVIE_ATTRIBUTE, movie);
            return "/movieForm";
        }
        return "/error";
    }

    @PostMapping
    public String submitMovie(@Valid MovieDto movie, Principal principal, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "movieForm";
        }
        UserDto user = userService.findByEmail(principal.getName());
        if (Objects.nonNull(user)) {
            movie.setUser(user);
            movieService.storeMovie(movie);
            return "redirect:movies?userId=" + user.getId();
        }
        return "/error";
    }

    @GetMapping
    public String getMoviesOfUser(
            @RequestParam(name = "userId") Long userId,
            @RequestParam(defaultValue = PageConstants.DEFAULT_PAGE) int page,
            @RequestParam(defaultValue = PageConstants.DEFAULT_PAGE_SIZE) int pageSize,
            @RequestParam(defaultValue = PageConstants.SORT_CREATED_DATE) String sortBy,
            @RequestParam(defaultValue = PageConstants.ASC) String direction,
            Model model) {

        Page<MovieDto> movies = movieService.findUserMoviesBasedOnCriteriaPaginated(userId, page, pageSize, sortBy, direction);
        UserDto user = userService.findById(userId);
        if (Objects.nonNull(user)) {
            applyPagination(model, movies, sortBy, direction, user);
            return "/movies";
        }
        return "/error";
    }

    @GetMapping("/{movieId}")
    public String getMovie(@PathVariable Long movieId, Model model, Principal principal) {
        MovieDto movie = movieService.findMovieById(movieId);
        if (Objects.nonNull(movie)) {
            model.addAttribute(MOVIE_ATTRIBUTE, movie);
            UserDto user = movie.getUser();
            boolean isTheAuthenticatedUser = !Objects.isNull(user) && !Objects.isNull(principal) && principal.getName().equals(user.getEmail());
            if (isTheAuthenticatedUser) {
                model.addAttribute(EMAIL_ATTRIBUTE, principal.getName());
            }
            return "/movie";
        }
        return "/error";
    }

    @GetMapping("/{movieId}/react")
    public String reactMovieById(@PathVariable Long movieId, @RequestParam String react, Principal principal) {
        UserDto user = userService.findByEmail(principal.getName());
        if (Objects.nonNull(user)) {
            MovieDto movie = movieService.findMovieById(movieId);
            if (!Objects.isNull(movie)) {
                boolean isNotTheOwnerOfMovie = movie.getUser().getId() != user.getId();
                if (isNotTheOwnerOfMovie) {
                    reactionService.react(react, user, movie);
                    return "redirect:/movies/" + movieId;
                }
            }
        }
        return "/error";
    }

    private void applyPagination(Model model, Page<?> page, String sortBy, String direction, UserDto user) {
        model.addAttribute(USER_ATTRIBUTE, user);
        model.addAttribute(MOVIES_PAGE_NAME, page);
        model.addAttribute(SORT_BY_ATTRIBUTE, sortBy);
        model.addAttribute(DIRECTION_ATTRIBUTE, direction);

        int totalPages = page.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute(PAGE_NUMBERS_ATTRIBUTE, pageNumbers);
        }
    }

}
