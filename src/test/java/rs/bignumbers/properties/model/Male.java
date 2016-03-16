package rs.bignumbers.properties.model;

import rs.bignumbers.annotations.Entity;
import rs.bignumbers.annotations.FetchType;
import rs.bignumbers.annotations.Relationship;

@Entity
public class Male extends Person {

	@Relationship(columnName = "female_id", responsible = true, fetch=FetchType.Eager)
	private Female female;

	public Female getFemale() {
		return female;
	}

	public void setFemale(Female female) {
		this.female = female;
	}
}
