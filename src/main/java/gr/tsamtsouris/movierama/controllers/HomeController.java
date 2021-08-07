package gr.tsamtsouris.movierama.controllers;

import gr.tsamtsouris.movierama.dtos.MovieDto;
import gr.tsamtsouris.movierama.services.MovieService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static gr.tsamtsouris.movierama.constants.PageConstants.*;

@Controller
public class HomeController {

    private static final String HOME_VIEW = "/home";

    private static final String ROOT_PATH = "/";
    private static final String HOME_PATH = "/home";

    private final MovieService movieService;

    public HomeController(MovieService movieService) {
        this.movieService = movieService;
    }

    @GetMapping(value = {ROOT_PATH, HOME_PATH})
    public String home(
            @RequestParam(defaultValue = DEFAULT_PAGE) int page,
            @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int size,
            @RequestParam(defaultValue = SORT_CREATED_DATE) String sortBy,
            @RequestParam(defaultValue = ASC) String direction,
            Model model) {

        Page<MovieDto> movies = movieService.findMoviesBasedOnCriteriaPaginated(page, size, sortBy, direction);

        model.addAttribute(MOVIES_PAGE_NAME, page);
        model.addAttribute(SORT_BY_ATTRIBUTE, sortBy);
        model.addAttribute(DIRECTION_ATTRIBUTE, direction);

        int totalPages = movies.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute(PAGE_NUMBERS_ATTRIBUTE, pageNumbers);
        }
        applyPagination(model, movies, sortBy, direction);

        return HOME_VIEW;
    }

    private void applyPagination(Model model, Page<?> page, String sortBy, String direction) {
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
