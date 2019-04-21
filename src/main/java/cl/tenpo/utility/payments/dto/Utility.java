package cl.tenpo.utility.payments.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
    "utility_code",
    "utility_name",
    "collector_id",
    "collector_name",
    "category_id",
    "category_name",
    "identifiers"
})
public class Utility
{
	@JsonProperty("utility_name")
	private String name;
	@JsonProperty("utility_code")
	private String code;
	private String collectorId;
	private String collectorName;
	private String categoryId;
	private String categoryName;
	private List<String> identifiers;

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(final String code) {
		this.code = code;
	}

	public String getCollectorId() {
		return collectorId;
	}

	public void setCollectorId(final String collectorId) {
		this.collectorId = collectorId;
	}

	public String getCollectorName() {
		return collectorName;
	}

	public void setCollectorName(final String collectorName) {
		this.collectorName = collectorName;
	}

	public String getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(final String categoryId) {
		this.categoryId = categoryId;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(final String categoryName) {
		this.categoryName = categoryName;
	}

	public List<String> getIdentifiers() {
		return identifiers;
	}

	public void setIdentifiers(final List<String> identifiers) {
		this.identifiers = identifiers;
	}
}