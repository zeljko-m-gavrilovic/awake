package rs.bignumbers.relations.model;

import rs.bignumbers.annotations.Entity;
import rs.bignumbers.annotations.FetchType;
import rs.bignumbers.annotations.Property;
import rs.bignumbers.annotations.RelationshipForeignKey;

@Entity
public class Window {
	@Property
	private Long id;
	
	@Property
	private String size;
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

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
}
