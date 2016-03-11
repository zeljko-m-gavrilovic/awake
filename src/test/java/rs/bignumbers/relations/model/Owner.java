package rs.bignumbers.relations.model;

import java.util.List;

import rs.bignumbers.annotations.FetchType;
import rs.bignumbers.annotations.RelationshipForeignTable;

public class Owner {
	
	/*
	 * bidirectional many-to-many relationship, I don't manage the foreign key
	 */
	@RelationshipForeignTable(fetch = FetchType.Lazy, tableName="house_owner", columnName = "owner_id", otherSideColumnName="house_id")
	List<House> houses;

	public List<House> getHouses() {
		return houses;
	}

	public void setHouses(List<House> houses) {
		this.houses = houses;
	}
	
}
