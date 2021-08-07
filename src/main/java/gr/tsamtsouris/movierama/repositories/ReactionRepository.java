package gr.tsamtsouris.movierama.repositories;

import gr.tsamtsouris.movierama.entities.Movie;
import gr.tsamtsouris.movierama.entities.Reaction;
import gr.tsamtsouris.movierama.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReactionRepository extends JpaRepository<Reaction,Long> {

    Optional<Reaction> findByUserAndMovie(User user, Movie movie);

    @Modifying
    @Query("DELETE FROM Reaction R where R.reactionId = ?1")
    void deleteByReactionId(Long reactionId);

}
