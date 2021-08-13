package gr.tsamtsouris.movierama.controllers

import gr.tsamtsouris.movierama.dtos.MovieDto
import gr.tsamtsouris.movierama.dtos.UserDto
import gr.tsamtsouris.movierama.entities.Reaction
import gr.tsamtsouris.movierama.services.MovieService
import gr.tsamtsouris.movierama.services.ReactionService
import gr.tsamtsouris.movierama.services.UserService
import org.junit.runner.RunWith
import org.spockframework.spring.SpringBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Specification

import static org.hamcrest.Matchers.equalTo
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@RunWith(SpringRunner.class)
@WebMvcTest(MovieController.class)
@AutoConfigureTestDatabase
class MovieControllerSpec extends Specification {

    @SpringBean
    private UserService userService = Mock()
    @SpringBean
    private MovieService movieService = Mock()
    @SpringBean
    private ReactionService reactionService = Mock()

    @Autowired
    private MockMvc mvc;

    def "when request at /movies path is performed then the response has correct status"() {
        given:
        UserDto user = new UserDto(id: 1L, firstName: 'george', lastName: 'tsamtsouris', email: 'email', password: 'password')
        userService.findByEmail('email') >> user

        expect:
        mvc.perform(post('/movies')).andExpect(status().isFound())
    }

    def "when request at /movies/movieId path is performed then the response has correct status and attribute"() {
        given:
        UserDto user = new UserDto(id: 1L, firstName: 'george', lastName: 'tsamtsouris', email: 'email', password: 'password')
        MovieDto movie = new MovieDto(movieId: 1L, title: 'title', description: 'desc', reactions: new HashSet<Reaction>(), duration: 1L, user: user)
        movieService.findMovieById(1L) >> movie

        mvc.perform(get("/movies/1"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("movie", equalTo(movie)));
    }

    def "when request at /movies/newMovie path is performed then the response has correct attributes and result2"() {
        expect:
        mvc.perform(get("/movies/newMovie"))
                .andExpect(status().isFound());
    }

    def "when request at /movies?userId=1 path is performed then the response has correct attributes and result3"() {
        given :
        UserDto user = new UserDto(id: 1L, firstName: 'firstname', lastName: 'lastname', email: 'email', password: 'password')
        MovieDto movie = new MovieDto(movieId: 1L, title: 'title', description: 'desc', reactions: new HashSet<Reaction>(), duration: 1L, user: user)

        Page<MovieDto> page = new PageImpl<>(Arrays.asList(movie))
        movieService.findUserMoviesBasedOnCriteriaPaginated(1L, 0, 5, 'createdAt', 'ASC') >> page
        userService.findById(1L) >> user

        expect:
        mvc.perform(get("/movies?userId=1")

        ).andExpect(status().isOk())
                .andExpect(model().attribute("user", equalTo(user)))
                .andExpect(model().attribute("moviesPage", equalTo(page)));
    }

}
