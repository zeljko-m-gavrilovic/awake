package rs.bignumbers.properties.model;

import rs.bignumbers.annotations.DbColumn;
import rs.bignumbers.annotations.DbTable;

@DbTable
public class Man extends Person {

	String armyDuty;

	@DbColumn
	String girlName;

	public String getArmyDuty() {
		return armyDuty;
	}

	public void setArmyDuty(String armyDuty) {
		this.armyDuty = armyDuty;
	}

	public String getGirlName() {
		return girlName;
	}

	public void setGirlName(String girlName) {
		this.girlName = girlName;
	}
}
