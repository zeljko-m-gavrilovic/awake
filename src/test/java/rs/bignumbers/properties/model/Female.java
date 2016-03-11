package rs.bignumbers.properties.model;

import rs.bignumbers.annotations.Entity;
import rs.bignumbers.annotations.RelationshipForeignKey;

@Entity
public class Female extends Person {
	
	@RelationshipForeignKey(columnName = "female_id")
	private Male male;

	public Male getMale() {
		return male;
	}

	public void setMale(Male male) {
		this.male = male;
	}
}
