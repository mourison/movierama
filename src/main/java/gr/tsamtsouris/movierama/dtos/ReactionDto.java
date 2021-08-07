package gr.tsamtsouris.movierama.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReactionDto {

    private long reactionId;

    private Boolean isLike;

    private UserDto user;

    private MovieDto movie;

}
