package gr.tsamtsouris.movierama.dtos;

import gr.tsamtsouris.movierama.entities.Movie;
import gr.tsamtsouris.movierama.entities.Role;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Setter
@Getter
public class UserDto {

    private long id;

    private String firstName;

    private String lastName;

    private String email;

    private String password;

    private Set<Movie> movies;

    private Set<Role> roles;

}
