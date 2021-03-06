package cl.tenpo.utility.payments.jpa.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@Entity
@Table(name = "categories")
@JsonPropertyOrder({
    "id",
    "status",
    "name",
    "utilities"
})
public class Category
{
	public static final String ENABLED  = "ENABLED";
	public static final String DISABLED = "DISABLED";

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@JsonIgnore
	private String status;
	private String name;
	@Column(name = "order")
	private Integer order;
	private transient Long quantity;

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

	public String getStatus()
	{
		return status;
	}

	public void setStatus(final String status)
	{
		this.status = status;
	}

	public Long getQuantity()
	{
		return quantity;
	}

	public Category setQuantity(final Long quantity)
	{
		this.quantity = quantity;
		return this;
	}
}
