package gr.tsamtsouris.movierama.services.impl;

import gr.tsamtsouris.movierama.dtos.UserDto;
import gr.tsamtsouris.movierama.entities.Role;
import gr.tsamtsouris.movierama.entities.User;
import gr.tsamtsouris.movierama.mappers.MapStructMapper;
import gr.tsamtsouris.movierama.repositories.UserRepository;
import gr.tsamtsouris.movierama.services.UserService;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService {

    private static final String ROLE_USER = "ROLE_USER";

    private final UserRepository userRepository;
    private final MapStructMapper mapper;

    public UserServiceImpl(UserRepository userRepository, MapStructMapper mapper) {
        this.userRepository = userRepository;
        this.mapper = mapper;
    }

    @Override
    public UserDto findByEmail(String email) {
        Optional<User> result = userRepository.findByEmail(email);
        return result.map(mapper::userToUserDto).orElse(null);
    }

    @Override
    public UserDto save(UserDto user) {
        User userToRegister = createActiveUser(user);
        User result = userRepository.save(userToRegister);
        return mapper.userToUserDto(result);
    }

    private User createActiveUser(UserDto user) {
        User userToRegister = mapper.userDtoToUser(user);
        userToRegister.setActive(true);
        Role role = new Role();
        role.setRole(ROLE_USER);
        userToRegister.setRoleEntities(Set.of(role));
        return userToRegister;
    }

    @Override
    public UserDto findById(Long userId) {
        Optional<User> result = userRepository.findById(userId);
        return result.map(mapper::userToUserDto).orElse(null);
    }
}
