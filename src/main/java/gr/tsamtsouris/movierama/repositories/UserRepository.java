package gr.tsamtsouris.movierama.repositories;

import gr.tsamtsouris.movierama.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByEmail(String email);
}
