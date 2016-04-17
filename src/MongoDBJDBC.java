
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
 
import org.bson.Document;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;



import com.mongodb.DBCursor;
import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.ServerAddress;

import java.util.Arrays;

public class MongoDBJDBC {

   public static void main( String args[] ) {
	
	   MongoClient mongoClient = null;
	     
	   try {
		//  MongoClientURI uri = new MongoClientURI("mongodb://alberto:verano731@192.168.1.37:27017");
		 //  mongoClient = new MongoClient (uri);
		   mongoClient = new MongoClient(new ServerAddress("192.168.1.136"));
		   System.out.println("conexion");

	     // New way to get database
	     MongoDatabase db = mongoClient.getDatabase("Prueba1");

	     // New way to get collection
	     MongoCollection<Document> collection = db.getCollection("Prueba1");
	//     System.out.println("collection: " + collection);
	     for (Document doc: collection.find()) {
	    	  System.out.println(doc.toJson());
	    	}
	    
	 
	   } catch (Exception e) {
	     e.printStackTrace();
	   } finally{
		   
	     mongoClient.close();
	     
	   }
   }
}