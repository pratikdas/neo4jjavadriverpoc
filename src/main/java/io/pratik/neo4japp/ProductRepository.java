/**
 * 
 */
package io.pratik.neo4japp;

import static org.neo4j.driver.Values.parameters;

import java.util.logging.Logger;

import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.Transaction;
import org.neo4j.driver.TransactionWork;

/**
 * 
 * @author pratikdas
 *
 */
public class ProductRepository implements AutoCloseable {
	private static final Logger logger = Logger.getLogger(ProductRepository.class.getName());

	private final Driver driver;

	public ProductRepository(final String uri) {
		// Instantiate the Neo4j driver with URI of database. 
		// We have disabled user authentication so so we don't 
		// need userID/Password here for authentication.
		driver = GraphDatabase.driver(uri);
	}

	public String fetchProductByBrand(final String brandName) {
		// Cypher query to find orderitem for a 
		// specific brand represented by the parameter $brand
		final String QUERY_IN_CYPHER = "MATCH (n:OrderItem) "+
		                        "WHERE n.brand = $brand " + 
				                "RETURN n.itemName";
		
		// Get the session handle inside a extended try block
		try (Session session = driver.session()) {
			// Start a read transaction
			String resultItem = session.readTransaction(new TransactionWork<String>() {
				@Override
				public String execute(Transaction tx) {
					
					// Run the query after passing the value of the brand parameter
					Result result = tx.run(QUERY_IN_CYPHER, parameters("brand", brandName));
					return result.single().get(0).asString();
				}
			});
			System.out.println(resultItem);
			return resultItem;
		}
	}
	
	@Override
	public void close() throws Exception {
		driver.close();

	}

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		try (ProductRepository productRepository = new ProductRepository("bolt://localhost:7687");){
			String productName = productRepository.fetchProductByBrand("samsung");
			logger.info("productName::"+productName);
		}

	}

}
