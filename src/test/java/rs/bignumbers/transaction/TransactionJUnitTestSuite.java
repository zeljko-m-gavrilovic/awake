package rs.bignumbers.transaction;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({ TestDbService.class, TestEntityInterceptor.class, TestMetadataExtractor.class, TestSqlUtil.class,
		TestTransaction.class, TestTransactionDetached.class })
public class TransactionJUnitTestSuite {

}
