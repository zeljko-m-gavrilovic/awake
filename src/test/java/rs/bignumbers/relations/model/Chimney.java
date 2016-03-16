package rs.bignumbers.relations.model;

import rs.bignumbers.annotations.Entity;

@Entity
public class Chimney {

	private Long id;
	// has a fereign key column in db table pointing to the house

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
}
