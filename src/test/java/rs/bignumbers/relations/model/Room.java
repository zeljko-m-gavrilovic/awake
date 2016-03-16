package rs.bignumbers.relations.model;

import rs.bignumbers.annotations.Entity;

@Entity
public class Room {
	private Long id;
	// has a foreign_key to house

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

}
