package rs.bignumbers.relations.model;

import rs.bignumbers.annotations.FetchType;
import rs.bignumbers.annotations.RelationshipForeignKey;

public class Address {
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

}
