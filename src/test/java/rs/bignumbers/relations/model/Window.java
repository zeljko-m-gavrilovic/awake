package rs.bignumbers.relations.model;

import rs.bignumbers.annotations.FetchType;
import rs.bignumbers.annotations.RelationshipForeignKey;

public class Window {
	
	/*
	 * bidirectional many-to-one/one-to-many relationship, I manage the foreign key
	 */
	@RelationshipForeignKey(fetch=FetchType.Lazy, columnName="house_id", responsible=true)
	private House house;

	public House getHouse() {
		return house;
	}

	public void setHouse(House house) {
		this.house = house;
	}

}
