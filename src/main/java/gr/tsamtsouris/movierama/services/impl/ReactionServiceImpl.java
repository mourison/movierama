package gr.tsamtsouris.movierama.services.impl;

import gr.tsamtsouris.movierama.dtos.MovieDto;
import gr.tsamtsouris.movierama.dtos.ReactionDto;
import gr.tsamtsouris.movierama.dtos.UserDto;
import gr.tsamtsouris.movierama.entities.Movie;
import gr.tsamtsouris.movierama.entities.Reaction;
import gr.tsamtsouris.movierama.entities.User;
import gr.tsamtsouris.movierama.enums.ReactionEnum;
import gr.tsamtsouris.movierama.mappers.MapStructMapper;
import gr.tsamtsouris.movierama.repositories.ReactionRepository;
import gr.tsamtsouris.movierama.services.ReactionService;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
public class ReactionServiceImpl implements ReactionService {

    private final ReactionRepository reactionRepository;
    private final MapStructMapper mapper;

    public ReactionServiceImpl(ReactionRepository reactionRepository, MapStructMapper mapper) {
        this.reactionRepository = reactionRepository;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public void react(String react, UserDto userDto, MovieDto movieDto) {
        ReactionDto reaction = createReaction(react, userDto, movieDto);
        Optional<Reaction> currentReaction = findExistingReactionOfUserForMovie(reaction);

        boolean shouldRemoveReaction = currentReaction.isPresent() && currentReaction.get().getIsLike().equals(reaction.getIsLike());

        if (shouldRemoveReaction) {
            reactionRepository.deleteByReactionId(currentReaction.get().getReactionId());
        } else {
            if (currentReaction.isPresent()) {
                reactionRepository.deleteByReactionId(currentReaction.get().getReactionId());
            }
            Reaction updatedReaction = mapper.reactionDtoToReaction(reaction);
            reactionRepository.save(updatedReaction);
        }
    }

    private Optional<Reaction> findExistingReactionOfUserForMovie(ReactionDto reaction) {
        User user = mapper.userDtoToUser(reaction.getUser());
        Movie movie = mapper.movieDtoToMovie(reaction.getMovie());
        Optional<Reaction> currentReaction = reactionRepository.findByUserAndMovie(user, movie);
        return currentReaction;
    }

    private ReactionDto createReaction(String react, UserDto userDto, MovieDto movieDto) {
        ReactionDto newReaction = new ReactionDto();
        newReaction.setMovie(movieDto);
        newReaction.setUser(userDto);
        newReaction.setIsLike(isLike(react));
        return newReaction;
    }

    private boolean isLike(String react) {
        if (ReactionEnum.LIKE.getValue().equals(react)) {
            return true;
        } else if (ReactionEnum.HATE.getValue().equals(react)) {
            return false;
        }
        return false;
    }

}
