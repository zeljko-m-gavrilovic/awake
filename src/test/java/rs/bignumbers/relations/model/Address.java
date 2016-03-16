package rs.bignumbers.relations.model;

import rs.bignumbers.annotations.Entity;
import rs.bignumbers.annotations.FetchType;
import rs.bignumbers.annotations.RelationshipForeignKey;

@Entity
public class Address {
	
	private Long id;
	/*
	 * bidirectional one-to-one relationship, I don't manage the foreign key
	 */
	@RelationshipForeignKey(fetch=FetchType.Lazy, columnName="address_id")
	private House house;

	public House getHouse() {
		return house;
	}

	public void setHouse(House house) {
		this.house = house;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
}