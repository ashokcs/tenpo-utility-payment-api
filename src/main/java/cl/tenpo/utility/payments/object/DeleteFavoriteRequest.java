package cl.tenpo.utility.payments.object;

import com.sun.istack.NotNull;

public class DeleteFavoriteRequest
{
	@NotNull
	private Long[] favorites;

	public Long[] getFavorites() {
		return favorites;
	}

	public void setFavorites(final Long[] favorites) {
		this.favorites = favorites;
	}
}
