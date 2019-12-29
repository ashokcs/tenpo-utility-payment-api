package cl.tenpo.utility.payments.object;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class OptionsRequest
{
	@NotNull
	private Boolean suggest;
	@NotNull
	private Boolean remind;
	@NotNull
	@Min(0)
	@Max(30)
	private Integer remindFrequency;

	public Boolean getSuggest() {
		return suggest;
	}

	public void setSuggest(final Boolean suggest) {
		this.suggest = suggest;
	}

	public Boolean getRemind() {
		return remind;
	}

	public void setRemind(final Boolean remind) {
		this.remind = remind;
	}

	public Integer getRemindFrequency() {
		return remindFrequency;
	}

	public void setRemindFrequency(final Integer remindFrequency) {
		this.remindFrequency = remindFrequency;
	}
}