package rs.bignumbers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import rs.bignumbers.model.Person;
import rs.bignumbers.service.DbService;
import rs.bignumbers.service.PersonService;
import rs.bignumbers.util.EntityMetadata;
import rs.bignumbers.util.MetadataExtractor;
import rs.bignumbers.util.SqlUtil;

@SpringBootApplication
public class AwakeApplication implements CommandLineRunner {

	Logger logger = LoggerFactory.getLogger(AwakeApplication.class);

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private PersonService personService;

	@Autowired
	private DbService dbService;

	public static void main(String[] args) {
		SpringApplication.run(AwakeApplication.class, args);
	}

	public void run(String... args) {
		/*Person person = new Person();
		person.setFirstName("FName");
		person.setLastName("LName");
		person.setAge(20);
		person.setPlace("Place");

		if (personService.insertPerson(person) > 0) {
			logger.info("Person saved successfully");
		}

		
		 * for (Person p : personService.getAllPerson("")) {
		 * logger.info(p.toString()); }
		 
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		SqlUtil su = new SqlUtil();
		MetadataExtractor me = new MetadataExtractor();
		EntityMetadata em = me.extractMetadataForClass(Person.class);
		for (Person p : dbService.getAll(su.query(em, parameters.getValues().keySet()), parameters, Person.class)) {
			logger.info(p.toString());
		}*/
	}
}