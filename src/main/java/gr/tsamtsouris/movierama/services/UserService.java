package gr.tsamtsouris.movierama.services;

import gr.tsamtsouris.movierama.dtos.UserDto;

public interface UserService {

    UserDto findByEmail(String email);

    UserDto save(UserDto user);

    UserDto findById(Long userId);

}
