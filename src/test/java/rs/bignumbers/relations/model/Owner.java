package rs.bignumbers.relations.model;

import java.util.ArrayList;
import java.util.List;

import rs.bignumbers.annotations.Entity;
import rs.bignumbers.annotations.FetchType;
import rs.bignumbers.annotations.Property;
import rs.bignumbers.annotations.RelationshipForeignTable;

@Entity
public class Owner {
	
	@Property
	private Long id;
	
	@Property
	private Double amount;
	/*
	 * bidirectional many-to-many relationship, I don't manage the foreign key
	 */
	@RelationshipForeignTable(fetch = FetchType.Lazy, tableName="house_owner", columnName = "owner_id", otherSideColumnName="house_id")
	List<House> houses;
	
	public Owner() {
		this.houses = new ArrayList<House>();
	}

	public List<House> getHouses() {
		return houses;
	}

	public void setHouses(List<House> houses) {
		this.houses = houses;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}
}
