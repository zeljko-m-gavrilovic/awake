package rs.bignumbers.relations.model;

import java.util.ArrayList;
import java.util.List;

import rs.bignumbers.annotations.Entity;
import rs.bignumbers.annotations.FetchType;
import rs.bignumbers.annotations.Property;
import rs.bignumbers.annotations.RelationshipForeignKey;
import rs.bignumbers.annotations.RelationshipForeignTable;

@Entity
public class House {
	@Property
	private Long id;

	@Property(columnName = "house_no")
	private String houseNo;
	/*
	 * bidirectional one-to-one relationship, I manage the foreign key
	 */
	@RelationshipForeignKey(fetch = FetchType.Lazy, columnName = "address_id", responsible = true)
	private Address address;

	/*
	 * unidirectional one-to-one relationship, I manage the foreign key
	 */
	@RelationshipForeignKey(fetch = FetchType.Lazy, columnName = "door_id", responsible = true)
	private Door door;

	/*
	 * unidirectional one-to-one relationship, I don't manage the foreign key
	 * Note: kind of read-only relationship, because there is no dominant/owner
	 * side
	 */
	@RelationshipForeignKey(fetch = FetchType.Lazy, columnName = "door_id")
	private Chimney chimney;

	/*
	 * bidirectional many-to-one/one-to-many relationship, I don't manage the
	 * foreign key
	 */
	@RelationshipForeignKey(fetch = FetchType.Lazy, otherSidePropertyName = "house", entityClazz = Window.class)
	private List<Window> windows;

	/*
	 * unidirectional many-to-one/one-to-many relationship, I don't manage the
	 * foreign key toNote: kind of read-only relationship, because there is no
	 * dominant/owner side
	 */
	@RelationshipForeignKey(fetch = FetchType.Lazy, columnName = "house_id")
	private List<Room> rooms;

	/*
	 * bidirectional many-to-many relationship, I manage the foreign key
	 */
	@RelationshipForeignTable(fetch = FetchType.Lazy, tableName = "house_owner", columnName = "house_id", otherSideColumnName = "owner_id", responsible = true, entityClazz = Owner.class)
	private List<Owner> owners;

	/*
	 * unidirectional many-to-many relationship, I manage the foreign key
	 */
	@RelationshipForeignTable(fetch = FetchType.Lazy, tableName = "house_entrance", columnName = "house_id", otherSideColumnName = "entrance_id", responsible = true, entityClazz = Entrance.class)
	private List<Entrance> entrances;

	/*
	 * unidirectional many-to-many relationship, I don't manage the foreign key
	 * Note: kind of read-only relationship, because there is no dominant/owner
	 * side
	 */
	@RelationshipForeignTable(fetch = FetchType.Lazy, tableName = "house_owner", columnName = "house_id", otherSideColumnName = "tenant_id")
	private List<Tenant> tenants;

	public House() {
		this.owners = new ArrayList<Owner>();
	}

	public String getHouseNo() {
		return houseNo;
	}

	public void setHouseNo(String houseNo) {
		this.houseNo = houseNo;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public Door getDoor() {
		return door;
	}

	public void setDoor(Door door) {
		this.door = door;
	}

	public Chimney getChimney() {
		return chimney;
	}

	public void setChimney(Chimney chimney) {
		this.chimney = chimney;
	}

	public List<Window> getWindows() {
		return windows;
	}

	public void setWindows(List<Window> windows) {
		this.windows = windows;
	}

	public List<Room> getRooms() {
		return rooms;
	}

	public void setRooms(List<Room> rooms) {
		this.rooms = rooms;
	}

	public List<Owner> getOwners() {
		return owners;
	}

	public void setOwners(List<Owner> owners) {
		this.owners = owners;
	}

	public List<Entrance> getEntrances() {
		return entrances;
	}

	public void setEntrances(List<Entrance> entrances) {
		this.entrances = entrances;
	}

	public List<Tenant> getTenants() {
		return tenants;
	}

	public void setTenants(List<Tenant> tenants) {
		this.tenants = tenants;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

}