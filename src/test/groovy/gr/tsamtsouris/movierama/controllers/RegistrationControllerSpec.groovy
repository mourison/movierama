package gr.tsamtsouris.movierama.controllers

import gr.tsamtsouris.movierama.entities.User
import gr.tsamtsouris.movierama.services.SecurityService
import gr.tsamtsouris.movierama.services.UserService
import gr.tsamtsouris.movierama.services.impl.UserServiceImpl
import org.junit.runner.RunWith
import org.spockframework.spring.SpringBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Specification

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@RunWith(SpringRunner.class)
@WebMvcTest(RegistrationController.class)
@AutoConfigureTestDatabase
class RegistrationControllerSpec extends Specification {

    @SpringBean
    private UserService userService = Mock(UserServiceImpl)
    @SpringBean
    private SecurityService securityService = Mock(SecurityService)

    @Autowired
    private MockMvc mvc;

    def "when get request on /registration path is performed then the response has correct attributes and result"() {
        expect: "expected result of controller on root path"
        mvc.perform(get("/registration"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("user"))
                .andExpect(view().name("/registration"))
    }

    def "when post request on /registration path is performed then the response has correct attributes and result"() {
        given:
        def submittedUser = new User(firstName: 'name', lastName: 'lastname', password: 'pass', email: 'email')
        expect: "expected result of controller on root path"
        mvc.perform(post("/registration")
                .param('firstName', submittedUser.firstName)
                .param('lastName', submittedUser.lastName)
                .param('password', submittedUser.password)
                .param('email"', submittedUser.email)
        ).andExpect(view().name('redirect:home'))
    }

}
