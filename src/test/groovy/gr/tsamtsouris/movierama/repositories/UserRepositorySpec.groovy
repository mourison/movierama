package gr.tsamtsouris.movierama.repositories

import gr.tsamtsouris.movierama.entities.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.annotation.DirtiesContext
import spock.lang.Specification

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class UserRepositorySpec extends Specification {

    @Autowired
    private UserRepository userRepository

    def "find user by email" () {
        given:
        def user = new User('id': 1L, 'email' : 'email', 'firstName' : 'first', 'lastName':'last')
        userRepository.save(user)

        when: "find user by email"
        def result = userRepository.findByEmail('email')

        then:"saved and retrieved entity must be equal"
        result.isPresent() == true
        result.get().email == user.email
        result.get().lastName == user.lastName
        result.get().firstName == user.firstName
    }

    def "find user by email with not existing user in db" () {
        given:
        def user1 = new User('id': 1L, 'email' : 'email1', 'firstName' : 'first', 'lastName':'last')
        def user2 = new User('id': 1L, 'email' : 'email2', 'firstName' : 'first', 'lastName':'last')
        def user3 = new User('id': 1L, 'email' : 'email3', 'firstName' : 'first', 'lastName':'last')
        def user4 = new User('id': 1L, 'email' : 'email4', 'firstName' : 'first', 'lastName':'last')

        userRepository.saveAll(Arrays.asList(user1,user2,user3,user4))

        when: "find user by email"
        def result = userRepository.findByEmail('email')

        then:"user does not exist"
        result.isPresent() == false
    }

}
