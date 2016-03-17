package rs.bignumbers;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import rs.bignumbers.relationship.RelationshipJUnitTestSuite;
import rs.bignumbers.transaction.TransactionJUnitTestSuite;
@RunWith(Suite.class)
@Suite.SuiteClasses({
	RelationshipJUnitTestSuite.class,
	TransactionJUnitTestSuite.class
})
public class AllJUnitTestSuite {   
}
