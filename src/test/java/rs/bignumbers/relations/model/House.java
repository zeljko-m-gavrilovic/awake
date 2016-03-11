package rs.bignumbers.relations.model;

import java.util.List;

import rs.bignumbers.annotations.FetchType;
import rs.bignumbers.annotations.RelationshipForeignKey;
import rs.bignumbers.annotations.RelationshipForeignTable;

public class House {

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
	 * Note: kind of read-only relationship, because there is no dominant/owner side
	 */
	@RelationshipForeignKey(fetch = FetchType.Lazy, columnName = "door_id")
	private Chimney chimney;

	/*
	 * bidirectional many-to-one/one-to-many relationship, I don't manage the foreign key
	 */
	@RelationshipForeignKey(fetch = FetchType.Lazy, columnName = "house_id")
	private List<Window> window;
	
	/*
	 * unidirectional many-to-one/one-to-many relationship, I don't manage the foreign key
	 * Note: kind of read-only relationship, because there is no dominant/owner side
	 */
	@RelationshipForeignKey(fetch = FetchType.Lazy, columnName = "house_id")
	private List<Room> rooms;
	
	/*
	 * bidirectional many-to-many relationship, I manage the foreign key
	 */
	@RelationshipForeignTable(fetch = FetchType.Lazy, tableName="house_owner", myColumnName = "house_id", otherSideColumnName="owner_id", responsible=true)
	private List<Owner> owners;
	
	
	/*
	 *  unidirectional many-to-many relationship, I manage the foreign key
	 */
	@RelationshipForeignTable(fetch = FetchType.Lazy, tableName="house_owner", myColumnName = "house_id", otherSideColumnName="entrance_id", responsible=true)
	private List<Entrance> entrances;
	
	/*
	 *  unidirectional many-to-many relationship, I don't manage the foreign key
	 *  Note: kind of read-only relationship, because there is no dominant/owner side
	 */
	@RelationshipForeignTable(fetch = FetchType.Lazy, tableName="house_owner", myColumnName = "house_id", otherSideColumnName="tenant_id")
	private List<Tenant> tenants;
}