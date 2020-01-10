package microservices.io.moviecatalogservice.resources;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import microservices.io.moviecatalogservice.models.CatalogItem;
import microservices.io.moviecatalogservice.models.Movie;
import microservices.io.moviecatalogservice.models.UserRating;

@RestController
@RequestMapping("/catalog")
public class MovieCatalogResource {

	@Autowired
	private RestTemplate restTemplate;
	
//	@Autowired
//	private WebClient.Builder webClientBuilder;
	
	@RequestMapping("/{userId}")
	public List<CatalogItem> getCatalog(@PathVariable("userId") String userId){
		
		UserRating ratings = restTemplate.getForObject("http://ratings-data-service/ratingsdata/users/" + userId, UserRating.class);

		
		return ratings.getUserRating().stream().map(rating -> {
			//RestTemplateWay
			Movie movie = restTemplate.getForObject("http://movie-info-service/movies/" + rating.getMovieId(), Movie.class);
			
			/*
			 
			//WebClient Way

			Movie movie = webClientBuilder.build()
				.get()
				.uri("hhtp://localhost:8082/movies/" + rating.getMovieId())
				.retrieve() //fetch the object
				.bodyToMono(Movie.class) //convert rest body to class, Mono means a promise of an object.
				.block(); //synchronize

			*/
			
			return new CatalogItem(movie.getName(), "desc", rating.getRating());
		}).collect(Collectors.toList());
	}
}
