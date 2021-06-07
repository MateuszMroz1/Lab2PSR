import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

import static com.mongodb.client.model.Filters.*;

public class Main {
    public static void main(String[] args) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        Scanner scan = new Scanner(System.in);

        String user = "student01";
        String password = "student01";
        String host = "localhost";
        int port = 27017;
        String database = "przychodnia";

        String clientURI = "mongodb://" + user + ":" + password + "@" + host + ":" + port + "/" + database;
        MongoClientURI uri = new MongoClientURI(clientURI);

        MongoClient mongoClient = new MongoClient(uri);

        MongoDatabase db = mongoClient.getDatabase(database);

        db.getCollection("przychodniaDB").drop();

        MongoCollection<Document> collection = db.getCollection("przychodniaDB");

        int menu;
        int menu_interrior = -1;
        String name;
        int number_of_patient;
        int number_usg;
        int number_respirators;

        while(true) {
            System.out.print("Główne Menu:\n");
            System.out.print("\n");
            System.out.print("1.Dodawanie do bazy danych.\n");
            System.out.print("2.Usuwanie całej bazy danych.\n");
            System.out.print("3.Usuwanie rekordu z bazy danych o podanym id.\n");
            System.out.print("4.Usuwanie rekordu z bazy danych o podanej nazwie.\n");
            System.out.print("5.Wyświetlenie całej bazy danych.\n");
            System.out.print("6.Wyświetlenie rekordu z bazy danych o podanym id.\n");
            System.out.print("7.Wyświetlenie rekordu z bazy danych o podanej nazwie.\n");
            System.out.print("8.Wyświetlenie rekordu z bazy danych o podanej ilości pacjentów.\n");
            System.out.print("9.Modyfikacja rekordu o podanym id.\n");
            System.out.print("0.Wyjście z programu.\n");

            System.out.print("Wybierz funkcje. I kliknij klawisz ENTER: ");
            menu = scan.nextInt();

            if (menu == 1) {
                System.out.print("Menu dodawania do bazy danych:\n");
                System.out.print("Dodaj id: \n");
                int id = scan.nextInt();

                System.out.print("Dodaj nazwe: \n");
                name = in.readLine();

                System.out.print("Dodaj liczbe pacjentów: \n");
                number_of_patient = scan.nextInt();

                System.out.print("Dodaj liczbe sprzętu usg: \n");
                number_usg = scan.nextInt();

                System.out.print("Dodaj liczbe respiratorów: \n");
                number_respirators = scan.nextInt();

                Add(collection, id, name, number_of_patient, number_usg, number_respirators);
                System.out.print("\n");

                if (menu_interrior == 0) {
                    System.out.print("\n");
                    System.out.print("\n");
                    menu_interrior = -1;
                    continue;
                }
            }

            if (menu == 2) {

                DeleteAll(collection);
                System.out.print("Usunięto wszystkie dane z bazy danych.\n");
                System.out.print("\n");
            }

            if (menu == 3) {
                System.out.print("Podaj klucz do usunięcia:\n");
                Integer key = scan.nextInt();
                DeleteByID(collection, key);
                System.out.print("\n");
            }

            if (menu == 4) {
                System.out.print("Wyświetlanie wszystkich danych:\n");
                for (Document doc : collection.find())
                System.out.println(doc.toJson());

                System.out.print("\n");
                System.out.print("Podaj nazwe do usunięcia:\n");

                String nam = in.readLine();
                DeleteByName(collection, nam);

                System.out.print("Usunięto dane:\n");
                System.out.print("\n");
            }

            if (menu == 5) {
                System.out.print("\n");
                System.out.print("Wyświetlanie wszystkich danych:\n");
                for (Document doc : collection.find())
                    System.out.println(doc.toJson());
                System.out.print("\n");
            }

            if (menu == 6) {
                System.out.print("\n");
                System.out.print("Podaj id:\n");
                int key = scan.nextInt();
                System.out.print("Wyświetlanie danych o podanym id:\n");
                for (Document doc : collection.find(new Document("_id", key)))
                    System.out.println(doc.toJson());
                System.out.print("\n");
            }

            if (menu == 7) {
                System.out.print("\n");
                System.out.print("Podaj nazwe:\n");
                String nam = in.readLine();
                System.out.print("Wyświetlanie przychodni o podanej nazwie:\n");
                for (Document doc : collection.find(new Document("name", nam)))
                    System.out.println(doc.toJson());
                System.out.print("\n");
            }

            if (menu == 8) {
                System.out.print("\n");
                System.out.print("Podaj liczbe pacjentów:\n");
                number_of_patient = scan.nextInt();
                System.out.print("Wyświetlanie przychodni o podanej ilości pacjentów:\n");
                for (Document doc : collection.find(new Document("number_of_patient", number_of_patient)))
                    System.out.println(doc.toJson());
                System.out.print("\n");
            }

            if (menu == 9) {
                for (Document doc : collection.find())
                    System.out.println(doc.toJson());
                System.out.print("\n");

                System.out.print("Podaj klucz do modyfikacji:\n");
                Integer key = scan.nextInt();

                System.out.print("\n");
                System.out.print("Podaj nową nazwe: \n");
                String nam = in.readLine();

                System.out.print("Podaj nową liczbe pacjentów: \n");
                number_of_patient = scan.nextInt();

                System.out.print("Podaj nową liczbe sprzętu USG: \n");
                number_usg = scan.nextInt();

                System.out.print("Podaj nową liczbe respiratorów: \n");
                number_respirators = scan.nextInt();
                collection.updateOne(eq("_id", key), new Document("$set", new Document("name", nam).append("number_of_patient", number_of_patient).append("devices", new Document("USG", number_usg).append("Respirator", number_respirators))));
                System.out.print("\n");
                System.out.print("Zmieniony rekord:\n");
                for (Document doc : collection.find(new Document("_id", key)))
                    System.out.println(doc.toJson());
                System.out.print("\n");

            }

            if (menu == 0) {
                System.out.print("\n");
                System.out.print("Wyłączanie programu...\n");
                scan.close();
                mongoClient.close();
                System.exit(0);
            }


            if (menu < 0 || menu > 9) {
                System.out.print("\n");
                System.out.print("Nie ma takiej opcji!\n");
                System.out.print("\n");
                continue;
            }
        }
    }

    private static void Add(MongoCollection collection,int id, String name, int number_of_patient, int usg_number, int respirator){
        Document clinic = new Document("_id", id)
        .append("name", name)
        .append("number_of_patient", number_of_patient)
        .append("devices", new Document("USG", usg_number).append("Respirator", respirator));
        collection.insertOne(clinic);
    }

    private static void DeleteAll(MongoCollection collection){
        collection.deleteMany(gt("_id", 0));
    }

    public static  void DeleteByID(MongoCollection collection, int id){
        collection.deleteOne(eq("_id", id));
    }

    public static  void DeleteByName(MongoCollection collection, String name){
        collection.deleteOne(eq("name", name));
    }
}
