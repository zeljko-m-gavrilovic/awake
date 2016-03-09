package rs.bignumbers.model;

import rs.bignumbers.annotations.DbTable;
import rs.bignumbers.annotations.OtherSide;

@DbTable
public class Female extends Person {
	
	@OtherSide
	private Male male;

	public Male getMale() {
		return male;
	}

	public void setMale(Male male) {
		this.male = male;
	}
}
