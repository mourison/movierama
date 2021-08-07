package gr.tsamtsouris.movierama.repositories

import gr.tsamtsouris.movierama.entities.Movie
import gr.tsamtsouris.movierama.entities.Reaction
import gr.tsamtsouris.movierama.entities.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.annotation.DirtiesContext
import spock.lang.Specification

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ReactionRepositorySpec extends Specification {

    @Autowired
    private ReactionRepository reactionRepository

    def "find reaction by user and movie reference" () {
        given:
        def user = new User('id': 1L, 'email' : 'email', 'firstName' : 'first', 'lastName':'last')
        def movie = new Movie('movieId': 1L, 'description' : 'desc', 'title' : 'title')
        def reaction = new Reaction('reactionId': 1L ,'user' : user, 'movie' : movie ,'isLike' : true)
        reactionRepository.save(reaction)

        when: "find reaction by user and movie reference"
        def result = reactionRepository.findByUserAndMovie(user, movie)

        then:"saved and retrieved entity must be equal"
        result.isPresent() == true
        result.get().user.email == user.email
        result.get().user.firstName == user.firstName
        result.get().user.lastName == user.lastName
        result.get().movie.title == movie.title
        result.get().movie.description == movie.description
        result.get().getIsLike() == true
    }

    def "does not find reaction by user and movie reference" () {
        given:
        def user = new User('id': 1L, 'email' : 'email', 'firstName' : 'first', 'lastName':'last')
        def movie = new Movie('movieId': 2L, 'description' : 'desc', 'title' : 'title')
        def reaction = new Reaction('reactionId': 2L ,'user' : user, 'movie' : movie ,'isLike' : true)
        reactionRepository.save(reaction)

        when: "find reaction by user and movie reference"
        def result = reactionRepository.findByUserAndMovie(user, movie)

        then:"should return empty result"
        result.isPresent() == false
    }

    def "delete reaction of specific user if exists" () {
        given:
        def reaction = new Reaction('reactionId': 4L)
        reactionRepository.save(reaction)

        when: "delete reaction by reactionId"
        reactionRepository.deleteByReactionId(reaction.reactionId)

        and: "try to search reaction with reactionId"
        def result = reactionRepository.findById(reaction.reactionId)

        then:"should retrieve empty result"
        result.isPresent() == false
    }

}
