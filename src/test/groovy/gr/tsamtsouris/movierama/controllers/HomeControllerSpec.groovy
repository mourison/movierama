package gr.tsamtsouris.movierama.controllers


import gr.tsamtsouris.movierama.services.impl.MovieServiceImpl
import org.junit.runner.RunWith
import org.spockframework.spring.SpringBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.data.domain.Page
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Specification

import static org.hamcrest.Matchers.equalTo
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@RunWith(SpringRunner.class)
@WebMvcTest(HomeController.class)
@AutoConfigureTestDatabase
class HomeControllerSpec extends Specification {

    @SpringBean
    private MovieServiceImpl movieService = Mock()

    @Autowired
    private MockMvc mvc

    def "when root path is performed then the response has correct attributes and result"() {
        given:
        movieService.findMoviesBasedOnCriteriaPaginated(*_) >> Page.empty()
        expect: "expected result of controller on root path"
        mvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("moviesPage", equalTo(Page.empty())))
                .andExpect(view().name("/home"));
    }

    def "when home path is performed then the response has correct attributes and result"() {
        given:
        movieService.findMoviesBasedOnCriteriaPaginated(*_) >> Page.empty()
        expect:
        mvc.perform(get("/home"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("moviesPage", equalTo(Page.empty())))
                .andExpect(view().name("/home"))
    }
}
