package rs.bignumbers.properties.model;

import rs.bignumbers.annotations.Entity;
import rs.bignumbers.annotations.RelationshipForeignKey;

@Entity
public class Male extends Person {

	@RelationshipForeignKey(columnName = "female_id", responsible = true)
	private Female female;

	public Female getFemale() {
		return female;
	}

	public void setFemale(Female female) {
		this.female = female;
	}
}
