
import java.io.IOException;
import java.util.*;

import Objetos.Conflicto;
import Objetos.GrupoArmado;
import com.mongodb.*;
import org.bson.Document;


import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.codehaus.jackson.map.ObjectMapper;

import static com.mongodb.client.model.Filters.eq;
import static java.util.Arrays.asList;


public class MongoDBJDBC {

   public static void main( String args[] ) {
	   /**Comandos para crear la base de datos desde MONGO
		* crear bbdd: use pacFinal
		* crear colecciones:
		*/


	   MongoClient mongoClient = null;

	   try {
		   mongoClient = new MongoClient(new ServerAddress("192.168.1.136"));
		   System.out.println("conexion");

	     // New way to get database
	     MongoDatabase db = mongoClient.getDatabase("terrorismo");

	     // New way to get collection
	     MongoCollection<Document> collectionConflicto = db.getCollection("conflicto");
		   MongoCollection <Document>  collectionGrupoArmado = db.getCollection("grupoArmado");
	     System.out.println("collectionConflicto: " + collectionConflicto);
	     /*for (Document doc: collectionConflicto.find()) {
	    	  System.out.println(doc.toJson());
	    	}*/
		 System.out.println("collectionGrupoArmado: " + collectionGrupoArmado);
		   /*for (Document doc: collectionGrupoArmado.find()) {
			   System.out.println(doc.toJson());
		   }*/

		   // Menú
		   Scanner teclat = new Scanner(System.in);

		   int opcio = 1;

		   while (opcio != 0){

			   System.out.println("\n");
			   System.out.println("MENU:");

			   System.out.println("--------------OPCIONES--------------");
			   System.out.println("1 - altaGrupoArmado");
			   System.out.println("2 - altaConflicto");
			   System.out.println("3 - queryConflictoHeridos");
			   System.out.println("4 - infoConflicto");


			   System.out.println("0 - Sortir ");

			   opcio = teclat.nextInt();

			   switch (opcio){

				   case 0:

					   break;

				   case 1:
					   // Crear jugador
					   altaGrupoArmado(collectionGrupoArmado);
					   break;

				   case 2:
					   // Aumentar caracteristicas jugador
					   altaConflicto(collectionConflicto, collectionGrupoArmado);
					   break;

				   case 3:
					   // Traspasar jugador
					   queryConflictoHeridos(collectionConflicto);
					   break;

				   case 4:
					   // Retirar jugador
					   infoConflicto(collectionConflicto, collectionGrupoArmado);
					   break;


			   }
		   }

	   } catch (Exception e) {
	     e.printStackTrace();
	   } /*finally{

	     mongoClient.close();

	   }*/


   }

	private static void infoConflicto(MongoCollection<Document> collectionConflicto, MongoCollection<Document> collectionGrupoArmado) {

	}

	private static void queryConflictoHeridos(MongoCollection<Document> collectionConflicto) {

	}

	private static void altaConflicto(MongoCollection<Document> collectionConflicto, MongoCollection<Document> collectionGrupoArmado) throws IOException {

		Scanner teclatNumeros = new Scanner(System.in);
		Scanner teclatStrings = new Scanner(System.in);
		ObjectMapper mapper = new ObjectMapper();

		//BasicDBObject conflictoDoc = new BasicDBObject();

		Document conflictoDoc = new Document();

		Conflicto conf = new Conflicto();
		System.out.println("Entra codigo");
		int id = teclatNumeros.nextInt();
		conf.setId(id);
		conflictoDoc.put("_id",id);

		System.out.println("Entra nombre");
		String nombre = teclatStrings.next();
		conf.setNombre(nombre);
		conflictoDoc.put("nombre",nombre);

		System.out.println("Entra zona");
		String zona = teclatStrings.next();
		conf.setZona(zona);
		conflictoDoc.put("zona",zona);

		System.out.println("Entra heridos");
		int heridos = teclatNumeros.nextInt();
		conf.setHeridos(heridos);
		conflictoDoc.put("heridos",heridos);



		//collectionConflicto.insertOne(conflictoDoc);


		System.out.println("¿desea añadir Grupo al conflicto? s/n");
		for (Document doc: collectionGrupoArmado.find()) {
			System.out.println(doc.toJson());
		}

		String respuesta = teclatStrings.next();
		List<Document> hijos = new ArrayList<>();
		if(!respuesta.equalsIgnoreCase("s")){
			collectionConflicto.insertOne(conflictoDoc);
		}
		while(respuesta.equalsIgnoreCase("s")) {

			conflictoDoc.put("grupoArmado", asList());

			System.out.println("introducir id del grupo");
			int idGrupo = teclatNumeros.nextInt();
			Document grupoExtraer = collectionGrupoArmado.find(eq("_id", idGrupo)).first();
			GrupoArmado gaI = mapper.readValue(grupoExtraer.toJson().replace("_id", "id"), GrupoArmado.class);
			Document documentGrupo = new Document();

			documentGrupo.put("_id", gaI.getId());
			documentGrupo.put("nombre", gaI.getNombre());
			documentGrupo.put("bajas", gaI.getBajas());


			hijos.add(documentGrupo);

			conf.getListaGruposArmados().add(gaI);
			System.out.println("ARRAY: "+conf.getListaGruposArmados().size());

			conflictoDoc.put("grupoArmado",hijos);

			System.out.println("JSON: "+conflictoDoc.toJson());
			//collectionConflicto.insertOne(conflictoDoc);


			System.out.println("¿desea añadir otro? s/n");
			String respuesta2 = teclatStrings.next();
			if(respuesta2.equalsIgnoreCase("s")){


				respuesta = respuesta2;
			}else{
				System.out.println("Grupo insertado en conflicto");
				collectionConflicto.insertOne(conflictoDoc);
				//collectionConflicto.updateOne(conflictoDoc,documentGrupo);
				//conflictoDoc.put("grupoArmado", conf.getListaGruposArmados());
				//collectionConflicto.insertOne(conflictoDoc);
				break;
			}
		}
		/*if(respuesta.equalsIgnoreCase("s")){
			//te muestra los grupos

				System.out.println("introducir id del grupo");
				int idGrupo = teclatNumeros.nextInt();
				Document grupoExtraer = collectionGrupoArmado.find(eq("_id", idGrupo)).first();
				GrupoArmado gaI = mapper.readValue(grupoExtraer.toJson().replace("_id", "id"), GrupoArmado.class);
				Document documentGrupo = new Document();

				documentGrupo.put("_id", gaI.getId());
				documentGrupo.put("nombre", gaI.getNombre());
				documentGrupo.put("bajas", gaI.getBajas());

				conflictoDoc.put("grupoArmado", documentGrupo);

				collectionConflicto.insertOne(conflictoDoc);

				System.out.println("Grupo insertado en conflicto");
			}*/


			///document.put("grupoArmado",);
			//collectionConflicto.insertOne(conflictoDoc);
	/*		Scanner fin = new Scanner(System.in);
			System.out.println("Quiele salí?");
			String sal = fin.next();

			if(sal.equalsIgnoreCase("s")){
				bololo = false;
			}
		}*/
		/////////////////////////////////////////////

	}

	private static void altaGrupoArmado(MongoCollection<Document> collectionGrupoArmado) {
		Scanner teclatNumeros = new Scanner(System.in);
		Scanner teclatStrings = new Scanner(System.in);

		Document document = new Document();



		GrupoArmado ga = new GrupoArmado();
		System.out.println("Entra codigo");
		int id = teclatNumeros.nextInt();
		ga.setId(id);
		document.put("_id",id);

		System.out.println("Entra nombre");
		String nombre = teclatStrings.next();
		ga.setNombre(nombre);
		document.put("nombre",nombre);

		System.out.println("Entra bajas");
		int bajas = teclatNumeros.nextInt();
		ga.setBajas(bajas);
		document.put("bajas",bajas);

		collectionGrupoArmado.insertOne(document);

		//teclatNumeros.close();
		//teclatStrings.close();
	}
}
