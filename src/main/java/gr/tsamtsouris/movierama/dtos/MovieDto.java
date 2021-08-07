package gr.tsamtsouris.movierama.dtos;

import gr.tsamtsouris.movierama.entities.Reaction;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Setter
@Getter
public class MovieDto {

    private long movieId;

    private String title;

    private String description;

    private long duration;

    private UserDto user;

    private Set<Reaction> reactions;

    public long likes() {
        return Optional.of(reactions).map(rs -> rs.stream()
                .filter(Objects::nonNull)
                .filter(Reaction::getIsLike)
                .count()).orElse(0L);
    }

    public long hates() {
        return Optional.of(reactions).map(rs -> rs.stream()
                .filter(Objects::nonNull)
                .filter(r -> !r.getIsLike())
                .count()).orElse(0L);
    }

    public boolean isLikedByUser(String user) {
        return Optional.ofNullable(reactions)
                .map(rs -> rs.stream().anyMatch(like -> isTheUserOfReaction(user, like) && Objects.nonNull(like.getIsLike()) && !like.getIsLike()))
                .orElse(false);
    }

    public boolean isHatedByUser(String user) {
        return Optional.ofNullable(reactions)
                .map(rs -> rs.stream().anyMatch(like -> isTheUserOfReaction(user, like) && Objects.nonNull(like.getIsLike()) && like.getIsLike()))
                .orElse(false);
    }

    private boolean isTheUserOfReaction(String user, Reaction like) {
        return like.getUser().getEmail().equals(user);
    }

}
