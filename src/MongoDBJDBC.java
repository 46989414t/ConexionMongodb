
import java.io.IOException;
import java.util.*;

import Objetos.Conflicto;
import Objetos.GrupoArmado;
import com.mongodb.*;
import com.mongodb.client.FindIterable;
import org.bson.Document;


import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.codehaus.jackson.map.ObjectMapper;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.gt;
import static java.util.Arrays.asList;


public class MongoDBJDBC {

   public static void main( String args[] ) {
	   /**Comandos para crear la base de datos desde MONGO
		* crear bbdd: use pacFinal
		* crear colecciones:
		*/


	   MongoClient mongoClient = null;

	   try {
		   //ip de la máquina donde se encuentra la MV con MONGODB
		   mongoClient = new MongoClient(new ServerAddress("192.168.1.136"));
		   System.out.println("conexion");

		   // Nombre de la dbs, si no existe la crea
		   MongoDatabase db = mongoClient.getDatabase("terrorismo");

		   // Nombre de las colecciones, si no existe la crea
		   MongoCollection<Document> collectionConflicto = db.getCollection("conflicto");
		   MongoCollection <Document>  collectionGrupoArmado = db.getCollection("grupoArmado");

		   Scanner teclat = new Scanner(System.in);

		   int opcio = 1;

		   while (opcio != 0){

			   System.out.println("\n");
			   System.out.println("MENU:");

			   System.out.println("--------------OPCIONES--------------");
			   System.out.println("1 - Alta de Grupo Armado");
			   System.out.println("2 - Alta de Conflicto");
			   System.out.println("3 - Conflictos con más de 300 heridos");
			   System.out.println("4 - Información de un conflicto");


			   System.out.println("0 - Salir ");

			   opcio = teclat.nextInt();

			   switch (opcio){
				   case 0:
					   mongoClient.close();
					   break;

				   case 1:
					   // Crear GA
					   altaGrupoArmado(collectionGrupoArmado);
					   break;

				   case 2:
					   // crear conf
					   altaConflicto(collectionConflicto, collectionGrupoArmado);
					   break;

				   case 3:
					   // query Conf mas de 300 heridos
					   queryConflictoHeridos(collectionConflicto);
					   break;

				   case 4:
					   // queri info Conflicto
					   infoConflicto(collectionConflicto);
					   break;


			   }
		   }

	   } catch (Exception e) {
	     e.printStackTrace();
	   } /*finally{

	     mongoClient.close();

	   }*/


   }

	/**
	 * Según el nombre introducido monta la query y genera un Document, éste se mapea para crear un Conflicto(),
	 * muestra la información del conflicto.
	 * Al hacer el mapeo ya detecta los subgrupos y los añade al Arraylist de Conflicto() automaicamente.
	 * Muestra toda la info que tiene el Conflicto()
	 *
	 * @param collectionConflicto
	 * @throws IOException
     */
	private static void infoConflicto(MongoCollection<Document> collectionConflicto) {
		Scanner teclatStrings = new Scanner(System.in);
		//Solo para saber los datos que hay en la collection
		for (Document doc: collectionConflicto.find()) {
			System.out.println(doc.toJson());
		}
		//------------------------------------

		System.out.println("Introduce nombre del conflicto");
		String nombreConflicto = teclatStrings.nextLine();
		ObjectMapper mapper = new ObjectMapper();
		try {
			Document nombreConflictoExtraer = collectionConflicto.find(eq("nombre", nombreConflicto)).first();
			Conflicto confInfo = new Conflicto();
			confInfo = mapper.readValue(nombreConflictoExtraer.toJson()
					.replace("_id", "id").replace("grupoArmado", "listaGruposArmados"), Conflicto.class);
			System.out.println("ID: "+confInfo.getId());
			System.out.println("NOMBRE: "+confInfo.getNombre());
			System.out.println("ZONA: "+confInfo.getZona());
			System.out.println("HERIDOS: "+confInfo.getHeridos());
			System.out.println("GRUPOS");
			Iterator<GrupoArmado> nombreIterator = confInfo.getListaGruposArmados().iterator();
			while(nombreIterator.hasNext()){
				GrupoArmado elemento = nombreIterator.next();
				System.out.print(elemento.toString());
			}

		}catch (Exception e){
			System.out.println("No hay coincidencias");
		}
	}

	/**
	 * Filtra la consiulta por todos los conflictos con mas de 299 heridos,
	 * mapea la consulta y la convierte en objeto Conflicto(), muestra la información
	 * @param collectionConflicto
     */
	private static void queryConflictoHeridos(MongoCollection<Document> collectionConflicto) {

		FindIterable<Document> iterable = collectionConflicto.find(gt("heridos",299));
		iterable.forEach(new Block<Document>() {
			@Override
			public void apply(final Document document) {
				ObjectMapper mapper = new ObjectMapper();
				try {
					Conflicto confInfo = mapper.readValue(document.toJson()
                            .replace("_id", "id").replace("grupoArmado", "listaGruposArmados"), Conflicto.class);
					System.out.println(confInfo.toString());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Inserta un nuevo conflicto, a éste se le pueden añadir GrupoArmado()
	 * @param collectionConflicto
	 * @param collectionGrupoArmado
	 * @throws IOException
     */
	private static void altaConflicto(MongoCollection<Document> collectionConflicto,
									  MongoCollection<Document> collectionGrupoArmado) throws IOException {

		Scanner teclatNumeros = new Scanner(System.in);
		Scanner teclatStrings = new Scanner(System.in);

		Document conflictoDoc = new Document();
		try {
			Conflicto conf = new Conflicto();
			System.out.println("Entra codigo (numerico)");
			int id = teclatNumeros.nextInt();
			conf.setId(id);
			conflictoDoc.put("_id", id);

			System.out.println("Entra nombre");
			String nombre = teclatStrings.nextLine();
			conf.setNombre(nombre);
			conflictoDoc.put("nombre", nombre);

			System.out.println("Entra zona");
			String zona = teclatStrings.nextLine();
			conf.setZona(zona);
			conflictoDoc.put("zona", zona);

			System.out.println("Entra heridos (numerico)");
			int heridos = teclatNumeros.nextInt();
			conf.setHeridos(heridos);
			conflictoDoc.put("heridos", heridos);



			//Añadir Grupos----------------------------------
			System.out.println("¿desea añadir Grupo al conflicto? s/n");
			for (Document doc: collectionGrupoArmado.find()) {
				System.out.println(doc.toJson());
			}

			String respuesta = teclatStrings.next();
			List<Document> hijosDeConflicto = new ArrayList<>();
			List<Document> hijosDeGrupo = new ArrayList<>();
			if(!respuesta.equalsIgnoreCase("s")){
				try{
					collectionConflicto.insertOne(conflictoDoc);
					System.out.println("conflicto insertado");
				}catch (Exception e){
					System.out.println("El identificador del conflicto ta existe");
				}
			}
			while(respuesta.equalsIgnoreCase("s")) {
				ObjectMapper mapper = new ObjectMapper();
				System.out.println("introducir id del grupo");
				//id seleccionado
				try {
					//seleccionar id
					int idGrupo = teclatNumeros.nextInt();
					//a partir del id montar el Document con los dato del id
					Document grupoExtraer = collectionGrupoArmado.find(eq("_id", idGrupo)).first();
					//añadir el grupo Extaido al Conflicto
					conflictoDoc.put("grupoArmado", asList());
					hijosDeConflicto.add(grupoExtraer);
					conflictoDoc.put("grupoArmado", hijosDeConflicto);

					//añadir el id del conflicto al Grupo para demostrar la relacion N-M
					/*GrupoArmado grupoExtraido = mapper.readValue(grupoExtraer.toJson()
							.replace("_id", "id").replace("conflicto", "listaConflicto"), GrupoArmado.class);
					Document grupoAddList = new Document();
					grupoAddList.put("conflicto", asList());
					hijosDeGrupo.add(conflictoDoc);
					grupoAddList.put("conflicto",hijosDeGrupo);
					collectionGrupoArmado.updateOne(grupoExtraer,grupoAddList);*/

				}catch (Exception e){
					System.out.println("El identificador seleccionado no existe");
				}

				System.out.println("¿desea añadir otro? s/n");
				String respuesta2 = teclatStrings.next();
				if(respuesta2.equalsIgnoreCase("s")){
					respuesta = respuesta2;
				}else{
					System.out.println("Grupo insertado en conflicto");
					try{
						collectionConflicto.insertOne(conflictoDoc);
					}catch (Exception e){
						System.out.println("El identificador del conflicto ta existe");
					}
					break;
				}
			}

		}catch (Exception e){
			System.out.println("El dato introducido no es correcto");
		}
	}


	/**
	 * Inserta un nuevo GrupoArmado() a la collection collectionGrupoArmado
	 * @param collectionGrupoArmado
     */
	private static void altaGrupoArmado(MongoCollection<Document> collectionGrupoArmado) {
		Scanner teclatNumeros = new Scanner(System.in);
		Scanner teclatStrings = new Scanner(System.in);

		Document document = new Document();
		try {
			GrupoArmado ga = new GrupoArmado();
			System.out.println("Entra codigo (numerico)");
			int id = teclatNumeros.nextInt();
			ga.setId(id);
			document.put("_id", id);

			System.out.println("Entra nombre");
			String nombre = teclatStrings.nextLine();
			ga.setNombre(nombre);
			document.put("nombre", nombre);

			System.out.println("Entra bajas (numerico)");
			int bajas = teclatNumeros.nextInt();
			ga.setBajas(bajas);
			document.put("bajas", bajas);
		}catch (Exception e){
			System.out.println("El dato introducido no es correcto");
		}
		try{
			collectionGrupoArmado.insertOne(document);
		}catch (Exception e){
			System.out.println("el identificador ya existe");
		}
	}
}




//collectionConflicto.insertOne(conflictoDoc);



/*
			//el conflicto también se añade al grupo para demostrar la relacion N-M
			Document docConflictoAdd = new Document();
			documentGrupo.put("conflicto", asList());

			docConflictoAdd.put("_id", conf.getId());
			docConflictoAdd.put("nombre", conf.getNombre());
			docConflictoAdd.put("zona", conf.getZona());
			docConflictoAdd.put("heridos",conf.getHeridos());

			hijosDeGrupo.add(docConflictoAdd);

			documentGrupo.put("conflicto",docConflictoAdd);

			System.out.println("GRUPO FASE 1: "+ grupoExtraer);
			System.out.println("GRUPO FASE 2: "+ documentGrupo);

*/
