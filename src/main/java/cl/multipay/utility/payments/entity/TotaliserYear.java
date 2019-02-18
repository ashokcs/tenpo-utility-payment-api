package cl.multipay.utility.payments.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "totaliser_yearly")
public class TotaliserYear
{
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private Integer year;
	private Long quantity;
	private Long amount;

	public Long getId()
	{
		return id;
	}

	public void setId(final Long id)
	{
		this.id = id;
	}

	public Integer getYear()
	{
		return year;
	}

	public void setYear(final Integer year)
	{
		this.year = year;
	}

	public Long getQuantity()
	{
		return quantity;
	}

	public void setQuantity(final Long quantity)
	{
		this.quantity = quantity;
	}

	public Long getAmount()
	{
		return amount;
	}

	public void setAmount(final Long amount)
	{
		this.amount = amount;
	}
}
