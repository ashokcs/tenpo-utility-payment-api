package cl.tenpo.utility.payments.jpa.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@Entity
@Table(name = "categories")
@JsonPropertyOrder({
    "id",
    "name",
    "utilities"
})
public class Category
{
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	@JsonProperty("id")
	private Long id;
	private String name;
	private transient long utilities;

	public Long getId()
	{
		return id;
	}

	public void setId(final Long id)
	{
		this.id = id;
	}

	public String getName()
	{
		return name;
	}

	public void setName(final String name)
	{
		this.name = name;
	}

	public long getUtilities()
	{
		return utilities;
	}

	public Category setUtilities(final long utilities)
	{
		this.utilities = utilities;
		return this;
	}
}
