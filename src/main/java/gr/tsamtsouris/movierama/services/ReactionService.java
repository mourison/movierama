package gr.tsamtsouris.movierama.services;

import gr.tsamtsouris.movierama.dtos.MovieDto;
import gr.tsamtsouris.movierama.dtos.UserDto;

public interface ReactionService {

    void react(String react, UserDto user, MovieDto movie);

}
