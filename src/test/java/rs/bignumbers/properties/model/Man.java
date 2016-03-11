package rs.bignumbers.properties.model;

import rs.bignumbers.annotations.Property;
import rs.bignumbers.annotations.Entity;

@Entity
public class Man extends Person {

	String armyDuty;

	@Property
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
