package cl.tenpo.utility.payments.object;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

public class FavoriteRequest
{
	@NotNull
	private Long utilityId;

	@NotBlank
	@Pattern(regexp = "^[A-Z0-9]{2,20}$")
	private String identifier;

	@NotBlank
	@Pattern(regexp = "^[a-zA-Z0-9\\s\\.\\,\\;\\-\\_\\:\\{\\}\\[\\]\\º\\ª\\!\\¡\\?\\¿\\=\\)\\(\\&\\%\\$\\#\\@]{2,300}$")
	private String description;

	public Long getUtilityId()
	{
		return utilityId;
	}

	public String getIdentifier()
	{
		return identifier;
	}

	public String getDescription()
	{
		return description;
	}
}
