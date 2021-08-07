package gr.tsamtsouris.movierama.mappers;

import gr.tsamtsouris.movierama.dtos.MovieDto;
import gr.tsamtsouris.movierama.dtos.ReactionDto;
import gr.tsamtsouris.movierama.dtos.UserDto;
import gr.tsamtsouris.movierama.entities.Movie;
import gr.tsamtsouris.movierama.entities.Reaction;
import gr.tsamtsouris.movierama.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
  By setting the componentModel attribute to spring, the MapStruct processor will produce a singleton Spring Bean mapper injectable wherever you need.
 **/
@Mapper(componentModel = "spring")
public interface MapStructMapper {

    @Mapping(source = "createdAt", target = "duration", qualifiedByName = "days")
    MovieDto movieToMovieDto(Movie movie);

    Movie movieDtoToMovie(MovieDto movieDto);

    User userDtoToUser(UserDto userDto);

    UserDto userToUserDto(User user);

    Reaction reactionDtoToReaction(ReactionDto reactionDto);

    @Named("days")
    default long creationDateToDuration(Timestamp timestamp){
        LocalDateTime localDateTime = timestamp.toLocalDateTime();
        return ChronoUnit.DAYS.between(localDateTime, LocalDateTime.now());
    }

}
