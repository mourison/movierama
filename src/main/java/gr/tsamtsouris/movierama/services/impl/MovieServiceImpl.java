package gr.tsamtsouris.movierama.services.impl;

import gr.tsamtsouris.movierama.constants.PageConstants;
import gr.tsamtsouris.movierama.dtos.MovieDto;
import gr.tsamtsouris.movierama.entities.Movie;
import gr.tsamtsouris.movierama.mappers.MapStructMapper;
import gr.tsamtsouris.movierama.repositories.MovieRepository;
import gr.tsamtsouris.movierama.services.MovieService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static gr.tsamtsouris.movierama.constants.PageConstants.*;

@Service
public class MovieServiceImpl implements MovieService {

    private final MovieRepository movieRepository;
    private final MapStructMapper mapper;

    public MovieServiceImpl(MovieRepository movieRepository, MapStructMapper mapper) {
        this.movieRepository = movieRepository;
        this.mapper = mapper;
    }

    @Override
    public MovieDto findMovieById(long movieId) {
        Movie movie = movieRepository.findById(movieId);
        return mapper.movieToMovieDto(movie);
    }

    @Override
    public MovieDto storeMovie(MovieDto movieDto) {
        Movie movie = mapper.movieDtoToMovie(movieDto);
        return mapper.movieToMovieDto(movieRepository.save(movie));
    }

    @Override
    public Page<MovieDto> findUserMoviesBasedOnCriteriaPaginated(long id, int pageNumber, int size, String sortBy, String direction) {
        Page<Movie> movieEntities;
        switch (sortBy) {
            case SORT_LIKES:
                movieEntities = findUserMoviesBasedOnReactionAction(id, pageNumber, size, direction, Boolean.TRUE);
                break;
            case SORT_HATES:
                movieEntities = findUserMoviesBasedOnReactionAction(id, pageNumber, size, direction, Boolean.FALSE);
                break;
            default:
                movieEntities = findUserMoviesDefaultSearch(id, getDefaultPageRequest(pageNumber, size, sortBy, direction));
        }
        return Objects.nonNull(movieEntities) ? movieEntities.map(movie -> mapper.movieToMovieDto(movie)) : Page.empty();
    }

    @Override
    public Page<MovieDto> findMoviesBasedOnCriteriaPaginated(int pageNumber, int size, String sortBy, String direction) {
        Page<Movie> movieEntities;
        switch (sortBy) {
            case SORT_LIKES:
                movieEntities = findAllMoviesBasedOnReactionAction(pageNumber, size, direction, Boolean.TRUE);
                break;
            case SORT_HATES:
                movieEntities = findAllMoviesBasedOnReactionAction(pageNumber, size, direction, Boolean.FALSE);
                break;
            default:
                movieEntities = findMoviesDefaultSearch(getDefaultPageRequest(pageNumber, size, sortBy, direction));
        }
        return Objects.nonNull(movieEntities) ? movieEntities.map(movie -> mapper.movieToMovieDto(movie)) : Page.empty();

    }

    Page<Movie> findUserMoviesBasedOnReactionAction(long userId, int pageNumber, int size, String ordering, Boolean action) {
        PageRequest pageRequest = PageRequest.of(pageNumber, size);
        if (ASC.equals(ordering)) {
            return movieRepository.findAllByUserOrderByReactions(action, userId, pageRequest);
        }
        return movieRepository.findAllByUserOrderByReactionsDesc(action, userId, pageRequest);
    }

    Page<Movie> findAllMoviesBasedOnReactionAction(int pageNumber, int size, String ordering, Boolean action) {
        PageRequest pageRequest = PageRequest.of(pageNumber, size);
        if (ASC.equals(ordering)) {
            return movieRepository.findAllOrderByReaction(action, pageRequest);
        }
        return movieRepository.findAllOrderByReactionsDesc(action, pageRequest);
    }

    Page<Movie> findMoviesDefaultSearch(PageRequest pageRequest) {
        return movieRepository.findAll(pageRequest);
    }

    Page<Movie> findUserMoviesDefaultSearch(long userId, PageRequest pageRequest) {
        return movieRepository.findAllByUserId(userId, pageRequest);
    }

    private PageRequest getDefaultPageRequest(int pageNumber, int size, String sortBy, String direction) {
        Sort sort = PageConstants.ASC.equals(direction) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        return PageRequest.of(pageNumber, size, sort);
    }

}
