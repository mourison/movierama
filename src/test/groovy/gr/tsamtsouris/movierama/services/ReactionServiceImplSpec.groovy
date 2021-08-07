package gr.tsamtsouris.movierama.services

import gr.tsamtsouris.movierama.dtos.MovieDto
import gr.tsamtsouris.movierama.dtos.UserDto
import gr.tsamtsouris.movierama.entities.Reaction
import gr.tsamtsouris.movierama.mappers.MapStructMapperImpl
import gr.tsamtsouris.movierama.repositories.ReactionRepository
import gr.tsamtsouris.movierama.services.impl.ReactionServiceImpl
import spock.lang.Specification

class ReactionServiceImplSpec extends Specification {

    def reactionRepository
    def mapper
    def reactionService

    def setup() {
        reactionRepository = Mock(ReactionRepository)
        mapper = new MapStructMapperImpl();
        reactionService = new ReactionServiceImpl(reactionRepository, mapper)
    }

    def "test react of ReactionServiceImpl removing reaction if exists"() {
        def reaction = new Reaction('isLike': true)
        def currentReaction = Optional.of(reaction)

        when:
        reactionRepository.findByUserAndMovie(*_) >> currentReaction
        reactionService.react('like', new UserDto(), new MovieDto())

        then:
        1 * reactionRepository.deleteByReactionId(*_)
        0 * reactionRepository.save(*_)

    }

    def "test react of ReactionServiceImpl adding different action"() {
        def reaction = new Reaction('isLike': true)
        def currentReaction = Optional.of(reaction)

        when:
        reactionRepository.findByUserAndMovie(*_) >> currentReaction
        reactionService.react('hate', new UserDto(), new MovieDto())

        then:
        1 * reactionRepository.save(*_)
        1 * reactionRepository.deleteByReactionId(*_)

    }

    def "test react of ReactionServiceImpl adding new reaction"() {
        when:
        reactionRepository.findByUserAndMovie(*_) >> Optional.empty()
        reactionService.react('hate', new UserDto(), new MovieDto())

        then:
        1 * reactionRepository.save(*_)
        0 * reactionRepository.deleteByReactionId(*_)
    }
}
