package gr.tsamtsouris.movierama.repositories;

import gr.tsamtsouris.movierama.entities.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MovieRepository extends JpaRepository<Movie, Long> {

    Movie findById(long id);

    Page<Movie> findAll(Pageable pageable);

    Page<Movie> findAllByUserId(long userId, Pageable pageable);

    @Query("SELECT m FROM Movie m LEFT JOIN Reaction r ON m.movieId = r.movie.movieId AND r.isLike = ?1 WHERE m.user.id = ?2 GROUP BY m.movieId ORDER BY COUNT(r.user.id) ASC")
    Page<Movie> findAllByUserOrderByReactions(boolean reaction, long userId, Pageable pageable);

    @Query("SELECT m FROM Movie m LEFT JOIN Reaction r ON m.movieId = r.movie.movieId AND r.isLike = ?1 WHERE m.user.id = ?2 GROUP BY m.movieId ORDER BY COUNT(r.user.id) DESC")
    Page<Movie> findAllByUserOrderByReactionsDesc(boolean reaction, long userId, Pageable pageable);

    @Query("SELECT m FROM Movie m LEFT JOIN Reaction r ON m.movieId = r.movie.movieId AND r.isLike = ?1 GROUP BY m.movieId ORDER BY COUNT(r.user.id) ASC")
    Page<Movie> findAllOrderByReaction(boolean reaction, Pageable pageable);

    @Query("SELECT m FROM Movie m LEFT JOIN Reaction r ON m.movieId = r.movie.movieId AND r.isLike = ?1 GROUP BY m.movieId ORDER BY COUNT(r.user.id) DESC")
    Page<Movie> findAllOrderByReactionsDesc(boolean reaction, Pageable pageable);

}
