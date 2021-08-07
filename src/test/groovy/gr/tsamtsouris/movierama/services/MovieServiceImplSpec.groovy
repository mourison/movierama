package gr.tsamtsouris.movierama.services

import gr.tsamtsouris.movierama.dtos.MovieDto
import gr.tsamtsouris.movierama.entities.Movie
import gr.tsamtsouris.movierama.entities.Reaction
import gr.tsamtsouris.movierama.entities.User
import gr.tsamtsouris.movierama.mappers.MapStructMapperImpl
import gr.tsamtsouris.movierama.repositories.MovieRepository
import gr.tsamtsouris.movierama.services.impl.MovieServiceImpl
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl

import java.sql.Timestamp
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.stream.Collectors
import java.util.stream.Stream

class MovieServiceImplSpec extends spock.lang.Specification {

    def movieRepository
    def movieService

    def setup() {
        movieRepository = Stub(MovieRepository)
        def mapper = new MapStructMapperImpl();
        movieService = new MovieServiceImpl(movieRepository, mapper)
    }

    def "test findMovieById of MovieService"() {
        when:
        movieRepository.findById(1L) >> createMovie(1L, description, "Mon, 09 Aug 2021", title, reactions)
        def result = movieService.findMovieById(1L)

        then:
        result.description == expectedDescr
        result.title == expectedTitle
        result.hates() == hates
        result.likes() == likes
        result.isLikedByUser(result.user.email) == isLiked;
        result.isHatedByUser(result.user.email) == isHated;
        result.reactions.size() == reactionsSize

        where:
        title | description | reactions                                                             || expectedTitle | expectedDescr | hates | likes | isLiked | isHated | reactionsSize
        "t"   | "d"         | getMultipleReactions(Stream.of(getHate('email'), getLike('email1')))  || "t"           | "d"           | 1     | 1     | true    | false   | 2
        "t1"  | "d1"        | getMultipleReactions(Stream.of(getLike('email'), getHate('email1')))  || "t1"          | "d1"          | 1     | 1     | false   | true    | 2
        "t2"  | "d2"        | getMultipleReactions(Stream.of(getLike('email'), getLike('email1')))  || "t2"          | "d2"          | 0     | 2     | false   | true    | 2
        "t3"  | "d3"        | getMultipleReactions(Stream.of(getHate('email'), getHate('email1')))  || "t3"          | "d3"          | 2     | 0     | true    | false   | 2
        "t3"  | "d3"        | getMultipleReactions(Stream.of(getLike('email1'), getHate('email2'))) || "t3"          | "d3"          | 1     | 1     | false   | false   | 2

    }

    def "test storeMovie of MovieService"() {
        when:
        def movieDto = new MovieDto()

        and:
        movieRepository.save(*_) >> createMovie(1L, description, "Mon, 09 Aug 2021", title, reactions)
        def result = movieService.storeMovie(movieDto)

        then:
        result.description == expectedDescr
        result.title == expectedTitle
        result.hates() == hates
        result.likes() == likes
        result.isHatedByUser(result.user.email) == isHated;
        result.isLikedByUser(result.user.email) == isLiked;
        result.reactions.size() == reactionsSize

        where:
        title | description | reactions                                                            || expectedTitle | expectedDescr | hates | likes | isHated | isLiked | reactionsSize
        "t"   | "d"         | getMultipleReactions(Stream.of(getHate('email'), getHate('email1'))) || "t"           | "d"           | 2     | 0     | false   | true    | 2
        "t1"  | "d1"        | getMultipleReactions(Stream.of(getHate('email'), getHate('email1'))) || "t1"          | "d1"          | 2     | 0     | false   | true    | 2
        "t2"  | "d2"        | getMultipleReactions(Stream.of(getHate('email'), getHate('email1'))) || "t2"          | "d2"          | 2     | 0     | false   | true    | 2
        "t3"  | "d3"        | getMultipleReactions(Stream.of(getHate('email'), getHate('email1'))) || "t3"          | "d3"          | 2     | 0     | false   | true    | 2

    }

    def "test findUserMoviesBasedOnReactionAction of MovieService for searching sortedBy likes and ascending order"() {
        given:
        def movieRepository = Mock(MovieRepository)
        def mapper = new MapStructMapperImpl();
        def movieService = new MovieServiceImpl(movieRepository, mapper)

        when:
        movieService.findUserMoviesBasedOnCriteriaPaginated(1L, 0, 5, "likesCount", "ASC")

        then:
        1 * movieRepository.findAllByUserOrderByReactions(*_)
    }

    def "test findUserMoviesBasedOnReactionAction of MovieService for searching sortedBy likes and descending order"() {
        given:
        def movieRepository = Mock(MovieRepository)
        def mapper = new MapStructMapperImpl();
        def movieService = new MovieServiceImpl(movieRepository, mapper)

        when:
        movieRepository.findAllByUserOrderByReactions(*_) >> Page.empty()
        movieService.findUserMoviesBasedOnCriteriaPaginated(1L, 0, 5, "likesCount", "DESC")

        then:
        1 * movieRepository.findAllByUserOrderByReactionsDesc(*_)

    }

    def "test findUserMoviesBasedOnReactionAction of MovieService for searching sortedBy hates and ascending order"() {
        given:
        def movieRepository = Mock(MovieRepository)
        def mapper = new MapStructMapperImpl();
        def movieService = new MovieServiceImpl(movieRepository, mapper)

        when:
        movieService.findUserMoviesBasedOnCriteriaPaginated(1L, 0, 5, "hatesCount", "ASC")

        then:
        1 * movieRepository.findAllByUserOrderByReactions(*_)
    }

    def "test findUserMoviesBasedOnReactionAction of MovieService for searching sortedBy hates and descending order"() {
        given:
        def movieRepository = Mock(MovieRepository)
        def mapper = new MapStructMapperImpl();
        def movieService = new MovieServiceImpl(movieRepository, mapper)

        when:
        movieService.findUserMoviesBasedOnCriteriaPaginated(1L, 0, 5, "hatesCount", "DESC")

        then:
        1 * movieRepository.findAllByUserOrderByReactionsDesc(*_)
    }

    def "test findUserMoviesBasedOnReactionAction of MovieService for searching sortedBy creation time and descending order"() {
        given:
        def movieRepository = Mock(MovieRepository)
        def mapper = new MapStructMapperImpl();
        def movieService = new MovieServiceImpl(movieRepository, mapper)

        when:
        movieRepository.findAllByUserOrderByReactions(*_) >> Page.empty()
        movieService.findUserMoviesBasedOnCriteriaPaginated(1L, 0, 5, "createdAt", "DESC")

        then:
        1 * movieRepository.findAllByUserId(*_)
    }

    def "test findMoviesBasedOnCriteriaPaginated of MovieService for searching sortedBy like and ascending order"() {
        given:
        def movieRepository = Mock(MovieRepository)
        def mapper = new MapStructMapperImpl();
        def movieService = new MovieServiceImpl(movieRepository, mapper)
        when:
        movieService.findMoviesBasedOnCriteriaPaginated(0, 5, "likesCount", "ASC")

        then:
        1 * movieRepository.findAllOrderByReaction(*_)
    }

    def "test findMoviesBasedOnCriteriaPaginated of MovieService for searching sortedBy likes and descending order"() {
        given:
        def movieRepository = Mock(MovieRepository)
        def mapper = new MapStructMapperImpl();
        def movieService = new MovieServiceImpl(movieRepository, mapper)

        when:
        movieService.findMoviesBasedOnCriteriaPaginated(0, 5, "likesCount", "DESC")

        then:
        1 * movieRepository.findAllOrderByReactionsDesc(*_)
    }

    def "test findMoviesBasedOnCriteriaPaginated of MovieService for searching sortedBy hates and ascending order"() {
        given:
        def movieRepository = Mock(MovieRepository)
        def mapper = new MapStructMapperImpl();
        def movieService = new MovieServiceImpl(movieRepository, mapper)

        when:

        movieService.findMoviesBasedOnCriteriaPaginated(0, 5, "hatesCount", "ASC")

        then:
        1 * movieRepository.findAllOrderByReaction(*_)
    }

    def "test findMoviesBasedOnCriteriaPaginated of MovieService for searching sortedBy hates and descending order"() {
        given:
        def movieRepository = Mock(MovieRepository)
        def mapper = new MapStructMapperImpl();
        def movieService = new MovieServiceImpl(movieRepository, mapper)

        when:
        movieService.findMoviesBasedOnCriteriaPaginated(0, 5, "hatesCount", "DESC")

        then:
        1 * movieRepository.findAllOrderByReactionsDesc(*_)
    }

    def "test findMoviesBasedOnCriteriaPaginated of MovieService for searching sortedBy creation time and ascending order"() {
        given:
        def movieRepository = Mock(MovieRepository)
        def mapper = new MapStructMapperImpl();
        def movieService = new MovieServiceImpl(movieRepository, mapper)

        when:
        movieService.findMoviesBasedOnCriteriaPaginated(0, 5, "createdAt", "ASC")

        then:
        1 * movieRepository.findAll(*_)
    }

    private PageImpl<Movie> getPageableMovies(def list) {
        new PageImpl<Movie>(list)
    }

    private Movie createMovie(def id, def description, def dateInString, def title, def reactions) {
        new Movie(
                'movieId': id,
                'description': description,
                'createdAt': getTimestampFromDate(dateInString),
                'title': title,
                'user': new User('email': "email"),
                'reactions': reactions
        )
    }

    private Reaction getLike(def email) {
        new Reaction(
                'user': new User('email': email),
                'isLike': true
        )
    }

    private Reaction getHate(def email) {
        new Reaction(
                'user': new User('email': email),
                'isLike': false
        )
    }

    private List<Reaction> getMultipleReactions(Stream<Reaction> stream) {
        return stream
                .collect(Collectors.toList())
    }


    private Timestamp getTimestampFromDate(String dateInString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE, d MMM yyyy");
        LocalDate dateTime = LocalDate.parse(dateInString, formatter);
        Timestamp.valueOf(dateTime.atStartOfDay())
    }

}
