package rs.bignumbers.transaction.model;

import rs.bignumbers.annotations.Entity;
import rs.bignumbers.annotations.Relationship;

@Entity
public class Female extends Person {
	
	@Relationship(otherSidePropertyName = "female")
	private Male male;

	public Male getMale() {
		return male;
	}

	public void setMale(Male male) {
		this.male = male;
	}
}
