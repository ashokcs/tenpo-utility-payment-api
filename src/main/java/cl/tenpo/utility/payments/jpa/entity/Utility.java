package cl.tenpo.utility.payments.jpa.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import cl.tenpo.utility.payments.util.Utils;

@Entity
@Table(name = "utilities")
@JsonPropertyOrder({
    "id",
    "name",
    "identifier",
    "category_id",
    "category_name"
})
public class Utility
{
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	@JsonProperty("id")
	private Long id;
	@JsonIgnore
	private String status;
	@JsonIgnore
	private String name;
	@JsonIgnore
	private String code;
	@JsonProperty("category_id")
	private String areaId;
	@JsonProperty("category_name")
	private String areaName;
	@JsonIgnore
	private String collectorId;
	@JsonIgnore
	private String collectorName;
	@JsonIgnore
	private String glossIds;
	@JsonProperty("identifier")
	private String glossNames;

	@JsonProperty("name")
	public String friendlyName()
	{
		if (name != null && !name.isEmpty()) {
			return name;
		}
		return Utils.utilityFriendlyName(code);
	}

	public Long getId() {
		return id;
	}

	public void setId(final Long id) {
		this.id = id;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(final String status) {
		this.status = status;
	}

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

	public String getAreaId() {
		return areaId;
	}

	public void setAreaId(final String areaId) {
		this.areaId = areaId;
	}

	public String getAreaName() {
		return areaName;
	}

	public void setAreaName(final String areaName) {
		this.areaName = areaName;
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

	public String getGlossIds() {
		return glossIds;
	}

	public void setGlossIds(final String glossIds) {
		this.glossIds = glossIds;
	}

	public String getGlossNames() {
		return glossNames;
	}

	public void setGlossNames(final String glossNames) {
		this.glossNames = glossNames;
	}
}
