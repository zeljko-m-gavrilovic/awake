package rs.bignumbers.model;

import rs.bignumbers.annotations.DbTable;
import rs.bignumbers.annotations.DbForeignKey;

@DbTable
public class Male extends Person {

	@DbForeignKey(name="female_id")
	private Female female;

	public Female getFemale() {
		return female;
	}

	public void setFemale(Female female) {
		this.female = female;
	}
}
