package cl.tenpo.utility.payments.object;

import java.util.List;

import cl.tenpo.utility.payments.entity.Favorite;
import cl.tenpo.utility.payments.entity.Suggestion;
import cl.tenpo.utility.payments.entity.Welcome;

public class HomeResponse
{
	private List<Favorite> favorites;
	private List<Suggestion> suggestions;
	private Welcome welcome;

	public HomeResponse(final List<Favorite> favorites, final List<Suggestion> suggestions, final Welcome welcome) {
		this.favorites = favorites;
		this.suggestions = suggestions;
		this.welcome = welcome;
	}

	public List<Favorite> getFavorites() {
		return favorites;
	}

	public void setFavorites(final List<Favorite> favorites) {
		this.favorites = favorites;
	}

	public List<Suggestion> getSuggestions() {
		return suggestions;
	}

	public void setSuggestions(final List<Suggestion> suggestions) {
		this.suggestions = suggestions;
	}

	public Welcome getWelcome() {
		return welcome;
	}

	public void setWelcome(final Welcome welcome) {
		this.welcome = welcome;
	}
}
