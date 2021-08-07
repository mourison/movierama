package gr.tsamtsouris.movierama.services;

import gr.tsamtsouris.movierama.dtos.MovieDto;
import org.springframework.data.domain.Page;

public interface MovieService {

    Page<MovieDto> findUserMoviesBasedOnCriteriaPaginated(long id, int pageNumber, int size, String sortBy, String direction);

    MovieDto storeMovie(MovieDto movie);

    MovieDto findMovieById(long movieId);

    Page<MovieDto> findMoviesBasedOnCriteriaPaginated(int pageNumber, int size, String sortBy, String direction);

}
