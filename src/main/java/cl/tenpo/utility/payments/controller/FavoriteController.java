package cl.tenpo.utility.payments.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import cl.tenpo.utility.payments.jpa.entity.Category;
import cl.tenpo.utility.payments.jpa.entity.Favorite;
import cl.tenpo.utility.payments.jpa.entity.Suggestion;
import cl.tenpo.utility.payments.jpa.entity.Utility;
import cl.tenpo.utility.payments.jpa.repository.FavoriteRepository;
import cl.tenpo.utility.payments.jpa.repository.SuggestionRepository;
import cl.tenpo.utility.payments.object.DeleteFavoriteRequest;
import cl.tenpo.utility.payments.object.FavoriteRequest;
import cl.tenpo.utility.payments.service.UtilityService;
import cl.tenpo.utility.payments.util.Http;

@RestController
public class FavoriteController
{
	private final FavoriteRepository favoriteRepository;
	private final SuggestionRepository suggestionRepository;
	private final UtilityService utilityService;

	public FavoriteController(
		final FavoriteRepository favoriteRepository,
		final SuggestionRepository suggestionRepository,
		final UtilityService utilityService
	){
		this.favoriteRepository = favoriteRepository;
		this.suggestionRepository = suggestionRepository;
		this.utilityService = utilityService;
	}

	@GetMapping("/v1/utility-payments/favorites")
	public List<Favorite> index(@RequestHeader("x-mine-user-id") final UUID user)
	{
		// get categories and generate map
		final Map<Long, Category> categories = utilityService
				.findAllCategories().stream()
				.collect(Collectors.toMap(c -> c.getId(), c -> c));

		// get favorites and add category
		return favoriteRepository.findFirst20ByUserOrderById(user).stream().map(f -> {
			f.getUtility().setCategory(categories.get(f.getUtility().getCategoryId()));
			return f;
		}).collect(Collectors.toList());
	}

	@PostMapping("/v1/utility-payments/favorites")
	public ResponseEntity<Favorite> create(
		@RequestHeader("x-mine-user-id") final UUID user,
		@RequestBody @Valid final FavoriteRequest request
	){
		// get utility
		final Utility utility = utilityService.findUtilityById(request.getUtilityId()).orElseThrow(Http::NotFound);

		// count user favorites
		if (favoriteRepository.countByUser(user) > 20) {
			throw Http.ConfictFavoritesLimitReached();
		}

		// get or create favorite
		final Optional<Favorite> fav = favoriteRepository.findByUserAndUtilityIdAndIdentifier(user, request.getUtilityId(), request.getIdentifier());
		if (fav.isPresent()) {
			return ResponseEntity.status(HttpStatus.OK).body(fav.get());
		} else {
			// save favorite
			final Favorite favorite = new Favorite();
			favorite.setUser(user);
			favorite.setUtility(utility);
			favorite.setIdentifier(request.getIdentifier());
			if (request.getName() != null) {
				final String name = request.getName().trim().replaceAll(" +", " ");
				if (name.matches("[A-Za-z0-9\\s????????????????????????????]{3,20}")) {
					favorite.setName(name);
				}
			}
			favoriteRepository.save(favorite);

			// remove suggestions
			final List<Suggestion> suggestions = suggestionRepository.findAllByUserAndUtilityIdAndIdentifier(user, request.getUtilityId(), request.getIdentifier());
			if (suggestions != null && !suggestions.isEmpty()) {
				suggestions.forEach(s -> {
					if (Suggestion.ENABLED.equals(s.getStatus())) {
						s.setStatus(Suggestion.DISABLED);
						suggestionRepository.save(s);
					}
				});
			}

			// return created
			return ResponseEntity.status(HttpStatus.CREATED).body(favorite);
		}
	}

	@DeleteMapping("/v1/utility-payments/favorites/{id}")
	public ResponseEntity<Favorite> delete(
		@RequestHeader("x-mine-user-id") final UUID user, @PathVariable("id") final Long id
	){
		final Optional<Favorite> fav = favoriteRepository.findByUserAndId(user, id);
		if (fav.isPresent()) {
			favoriteRepository.delete(fav.get());
			return ResponseEntity.ok().build();
		}
		return ResponseEntity.of(Optional.empty());
	}

	@PostMapping("/v1/utility-payments/favorites/delete")
	public ResponseEntity<Favorite> deleteMultiple(
		@RequestHeader("x-mine-user-id") final UUID user,
		@RequestBody final DeleteFavoriteRequest deleteFavoriteRequest
	){
		for (final Long favorite : deleteFavoriteRequest.getFavorites()) {
			final Optional<Favorite> fav = favoriteRepository.findByUserAndId(user, favorite);
			if (fav.isPresent()) {
				favoriteRepository.delete(fav.get());
			}
		}
		return ResponseEntity.ok().build();
	}
}
