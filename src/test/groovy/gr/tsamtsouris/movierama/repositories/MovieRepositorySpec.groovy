package gr.tsamtsouris.movierama.repositories

import gr.tsamtsouris.movierama.entities.Movie
import gr.tsamtsouris.movierama.entities.Reaction
import gr.tsamtsouris.movierama.entities.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.test.annotation.DirtiesContext
import spock.lang.Specification

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class MovieRepositorySpec extends Specification {

    @Autowired
    private MovieRepository movieRepository

    @Autowired
    private UserRepository userRepository

    @Autowired
    private ReactionRepository reactionRepository

    def "find movie by movieId"() {
        given:
        def movie = new Movie('movieId': 1L, 'description': 'desc', 'title': 'title')
        movieRepository.save(movie)

        when: "find movie by id"
        def result = movieRepository.findById(1L)

        then: "saved and retrieved"
        result != null
    }

    def "find all movies with paging with one page as result"() {
        given:
        def movie1 = new Movie('movieId': 0L, 'description': 'desc0', 'title': 'title0')
        def movie2 = new Movie('movieId': 2L, 'description': 'desc1', 'title': 'title1')
        def movie3 = new Movie('movieId': 3L, 'description': 'desc2', 'title': 'title2')
        def movie4 = new Movie('movieId': 4L, 'description': 'desc3', 'title': 'title3')

        movieRepository.saveAll(Arrays.asList(movie1, movie2, movie3, movie4))
        def pageRequest = PageRequest.of(0, 5, Sort.by('createdAt').ascending())

        when: "find all moves"
        Page<Movie> result = movieRepository.findAll(pageRequest)

        then: "saved and retrieved entities"
        result.empty == false
        result.getTotalElements() == 4
        result.getNumberOfElements() == 4

    }

    def "find all movies with paging with more than one page as result"() {
        given:
        def movie1 = new Movie('movieId': 1L, 'description': 'desc0', 'title': 'title0')
        def movie2 = new Movie('movieId': 2L, 'description': 'desc1', 'title': 'title1')
        def movie3 = new Movie('movieId': 3L, 'description': 'desc2', 'title': 'title2')
        def movie4 = new Movie('movieId': 4L, 'description': 'desc3', 'title': 'title3')
        def movie5 = new Movie('movieId': 5L, 'description': 'desc3', 'title': 'title3')
        def movie6 = new Movie('movieId': 6L, 'description': 'desc3', 'title': 'title3')

        movieRepository.saveAll(Arrays.asList(movie1, movie2, movie3, movie4, movie5, movie6))
        def pageRequest = PageRequest.of(0, 5, Sort.by('createdAt').ascending())

        when: "find all moves"
        Page<Movie> result = movieRepository.findAll(pageRequest)

        then: "saved and retrieved entities"
        result.empty == false
        result.getTotalElements() == 6
        result.getTotalPages() == 2

    }

    def "find all movies of specific user with paging with more than one page as result"() {
        given:
        def user = new User('id': 1L, 'email': 'email', 'firstName': 'first', 'lastName': 'last')
        def movie1 = new Movie('movieId': 1L, 'description': 'desc0', 'title': 'title0', user: user)
        def movie2 = new Movie('movieId': 2L, 'description': 'desc1', 'title': 'title1', user: user)
        def movie3 = new Movie('movieId': 3L, 'description': 'desc2', 'title': 'title2', user: user)
        def movie4 = new Movie('movieId': 4L, 'description': 'desc3', 'title': 'title3')

        userRepository.save(user)
        movieRepository.saveAll(Arrays.asList(movie1, movie2, movie3, movie4))
        def pageRequest = PageRequest.of(0, 5, Sort.by('createdAt').ascending())

        when: "find movies of user"
        Page<Movie> result = movieRepository.findAllByUserId(1L, pageRequest)

        then: "saved and retrieved entities"
        result.empty == false
        result.getTotalElements() == 3
        result.getTotalPages() == 1
    }

    def "find all movies of specific user order by like reactions ascending"() {
        given:
        def user = new User('id': 1L, 'email': 'email', 'firstName': 'first', 'lastName': 'last')
        def like = new Reaction('reactionId': 1L, 'user': user, 'isLike': true)
        def like1 = new Reaction('reactionId': 2L, 'user': user, 'isLike': true)

        def hate = new Reaction('reactionId': 1L, 'user': user, 'isLike': false)
        def hate1 = new Reaction('reactionId': 2L, 'user': user, 'isLike': false)

        def movie1 = new Movie('movieId': 1L, 'description': 'desc0', 'title': 'title0', user: user, reactions: Arrays.asList(like, like1))
        def movie2 = new Movie('movieId': 2L, 'description': 'desc1', 'title': 'title1', user: user, reactions: Arrays.asList(like))
        def movie3 = new Movie('movieId': 3L, 'description': 'desc2', 'title': 'title2', user: user, reactions: Arrays.asList(hate))
        def movie4 = new Movie('movieId': 4L, 'description': 'desc3', 'title': 'title3', user: user, reactions: Arrays.asList(hate, hate1))

        userRepository.save(user)
        reactionRepository.saveAll(Arrays.asList(like, like1, hate, hate1))
        movieRepository.saveAll(Arrays.asList(movie1, movie2, movie3, movie4))
        def pageRequest = PageRequest.of(0, 5, Sort.by('createdAt').ascending())

        when: "find movies of user"
        Page<Movie> result = movieRepository.findAllByUserOrderByReactions(true, 1L, pageRequest)

        then: "saved and retrieved entities in correct order"
        result.empty == false
        result.getContent().get(0).title == 'title0'
        result.getContent().get(0).reactions.size() == 2
        result.getContent().get(1).title == 'title1'
        result.getContent().get(1).reactions.size() == 1
        result.getContent().get(2).title == 'title2'
        result.getContent().get(2).reactions.size() == 1
        result.getContent().get(3).title == 'title3'
        result.getContent().get(3).reactions.size() == 2
        result.getTotalElements() == 4
        result.getTotalPages() == 1
    }

    def "find all movies of specific user order by like reactions descending"() {
        given:
        def user = new User('id': 1L, 'email': 'email', 'firstName': 'first', 'lastName': 'last')
        def like = new Reaction('reactionId': 1L, 'user': user, 'isLike': true)
        def like1 = new Reaction('reactionId': 2L, 'user': user, 'isLike': true)

        def hate = new Reaction('reactionId': 1L, 'user': user, 'isLike': false)
        def hate1 = new Reaction('reactionId': 2L, 'user': user, 'isLike': false)

        def movie1 = new Movie('movieId': 1L, 'description': 'desc0', 'title': 'title0', user: user, reactions: Arrays.asList(like, like1))
        def movie2 = new Movie('movieId': 2L, 'description': 'desc1', 'title': 'title1', user: user, reactions: Arrays.asList(like))
        def movie3 = new Movie('movieId': 3L, 'description': 'desc2', 'title': 'title2', user: user, reactions: Arrays.asList(hate))
        def movie4 = new Movie('movieId': 4L, 'description': 'desc3', 'title': 'title3', user: user, reactions: Arrays.asList(hate, hate1))

        userRepository.save(user)
        reactionRepository.saveAll(Arrays.asList(like, like1, hate, hate1))
        movieRepository.saveAll(Arrays.asList(movie1, movie2, movie3, movie4))
        def pageRequest = PageRequest.of(0, 5, Sort.by('createdAt').ascending())

        when: "find movies of user"
        Page<Movie> result = movieRepository.findAllByUserOrderByReactionsDesc(true, 1L, pageRequest)

        then: "saved and retrieved entities in correct order"
        result.empty == false
        result.getContent().get(3).title == 'title3'
        result.getContent().get(3).reactions.size() == 2
        result.getContent().get(0).title == 'title0'
        result.getContent().get(0).reactions.size() == 2
        result.getTotalElements() == 4
        result.getTotalPages() == 1
    }

    def "find all movies order by like reactions descending"() {
        given:
        def user = new User('id': 1L, 'email': 'email', 'firstName': 'first', 'lastName': 'last')
        def like = new Reaction('reactionId': 1L, 'user': user, 'isLike': true)
        def like1 = new Reaction('reactionId': 2L, 'user': user, 'isLike': true)

        def hate = new Reaction('reactionId': 1L, 'user': user, 'isLike': false)
        def hate1 = new Reaction('reactionId': 2L, 'user': user, 'isLike': false)

        def movie1 = new Movie('movieId': 1L, 'description': 'desc0', 'title': 'title0', user: user, reactions: Arrays.asList(like, like1))
        def movie2 = new Movie('movieId': 2L, 'description': 'desc1', 'title': 'title1', user: user, reactions: Arrays.asList(like))
        def movie3 = new Movie('movieId': 3L, 'description': 'desc2', 'title': 'title2', user: user, reactions: Arrays.asList(hate))
        def movie4 = new Movie('movieId': 4L, 'description': 'desc3', 'title': 'title3', user: user, reactions: Arrays.asList(hate, hate1))

        userRepository.save(user)
        reactionRepository.saveAll(Arrays.asList(like, like1, hate, hate1))
        movieRepository.saveAll(Arrays.asList(movie1, movie2, movie3, movie4))
        def pageRequest = PageRequest.of(0, 5, Sort.by('createdAt').ascending())

        when: "find movies"
        Page<Movie> result = movieRepository.findAllOrderByReaction(true, pageRequest)

        then: "saved and retrieved entities in correct order"
        result.empty == false
        result.getContent().get(3).title == 'title3'
        result.getContent().get(3).reactions.size() == 2
        result.getContent().get(2).title == 'title2'
        result.getContent().get(2).reactions.size() == 1
        result.getContent().get(1).title == 'title1'
        result.getContent().get(1).reactions.size() == 1
        result.getContent().get(0).title == 'title0'
        result.getContent().get(0).reactions.size() == 2
        result.getTotalElements() == 4
        result.getTotalPages() == 1
    }

    def "find all order by like reactions descending"() {
        given:
        def user = new User('id': 1L, 'email': 'email', 'firstName': 'first', 'lastName': 'last')
        def like = new Reaction('reactionId': 1L, 'user': user, 'isLike': true)
        def like1 = new Reaction('reactionId': 2L, 'user': user, 'isLike': true)

        def hate = new Reaction('reactionId': 1L, 'user': user, 'isLike': false)
        def hate1 = new Reaction('reactionId': 2L, 'user': user, 'isLike': false)

        def movie1 = new Movie('movieId': 1L, 'description': 'desc0', 'title': 'title0', user: user, reactions: Arrays.asList(like, like1))
        def movie2 = new Movie('movieId': 2L, 'description': 'desc1', 'title': 'title1', user: user, reactions: Arrays.asList(like))
        def movie3 = new Movie('movieId': 3L, 'description': 'desc2', 'title': 'title2', user: user, reactions: Arrays.asList(hate))
        def movie4 = new Movie('movieId': 4L, 'description': 'desc3', 'title': 'title3', user: user, reactions: Arrays.asList(hate, hate1))

        userRepository.save(user)
        reactionRepository.saveAll(Arrays.asList(like, like1, hate, hate1))
        movieRepository.saveAll(Arrays.asList(movie1, movie2, movie3, movie4))
        def pageRequest = PageRequest.of(0, 5, Sort.by('createdAt').ascending())

        when: "find mivies"
        Page<Movie> result = movieRepository.findAllOrderByReactionsDesc(true, pageRequest)

        then: "saved and retrieved entities in correct order"
        result.empty == false
        result.getContent().get(3).title == 'title3'
        result.getContent().get(3).reactions.size() == 2
        result.getContent().get(0).title == 'title0'
        result.getContent().get(0).reactions.size() == 2
        result.getTotalElements() == 4
        result.getTotalPages() == 1
    }

}
