package rs.bignumbers.relations.model;

import rs.bignumbers.annotations.Entity;

@Entity
public class Tenant {
	private Long id;
	// there is a link table joining house and tenance

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

}
