package rs.bignumbers.transaction.model;

import rs.bignumbers.annotations.Property;
import rs.bignumbers.annotations.Entity;

@Entity
public class Person {

	@Property
	private Long id;
	
	@Property(columnName="first_name")
	private String firstName;
	
	@Property(columnName="familly_name", ignore=true)
	private String lastName;
	
	@Property
	private Integer age;
	
	private String place;
	private Integer notProperty;

	public Person() {
	}
	
	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public String getPlace() {
		return place;
	}

	public void setPlace(String place) {
		this.place = place;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(this.getFirstName()).append(", ").append(this.getLastName()).append(", ").append(this.getPlace())
				.append(", ").append(this.getAge());

		return builder.toString();
	}
}