import com.arangodb.*;
import com.arangodb.entity.BaseDocument;
import com.arangodb.entity.CollectionEntity;
import com.arangodb.mapping.ArangoJack;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        Scanner scan = new Scanner(System.in);

        ArangoDB arangoDB = new ArangoDB.Builder().user("root").password("student")
                .serializer(new ArangoJack())
                .build();

    String dbname = "PrzychodniaDB";
    if(!arangoDB.db(dbname).exists()) {
        arangoDB.createDatabase(dbname);
        System.out.println("Baza danych \'" + dbname + "\' stworzona.");
    }

    String collectionName = "Przychodnia";
    if(!arangoDB.db(dbname).collection(collectionName).exists()) {
       arangoDB.db(dbname).createCollection(collectionName);
    }

        int menu;
        int menu_interrior = -1;
        String name;
        int number_of_patient;
        int number_usg;
        int number_respirators;

        while (true) {
            System.out.print("Główne Menu:\n");
            System.out.print("\n");
            System.out.print("1.Dodawanie do bazy danych.\n");
            System.out.print("2.Usuwanie całej bazy danych.\n");
            System.out.print("3.Usuwanie rekordu z bazy danych o podanej nazwie.\n");
            System.out.print("4.Wyświetlenie całej bazy danych.\n");
            System.out.print("5.Wyświetlenie rekordu z bazy danych o podanej nazwie.\n");
            System.out.print("6.Modyfikacja rekordu o podanej nazwie.\n");
            System.out.print("0.Wyjście z programu.\n");

            System.out.print("Wybierz funkcje. I kliknij klawisz ENTER: ");
            menu = scan.nextInt();

            if (menu == 1) {
                System.out.print("Menu dodawania do bazy danych:\n");

                System.out.print("Dodaj nazwe: \n");
                name = in.readLine();

                System.out.print("Dodaj liczbe pacjentów: \n");
                number_of_patient = scan.nextInt();

                System.out.print("Dodaj liczbe sprzętu usg: \n");
                number_usg = scan.nextInt();

                System.out.print("Dodaj liczbe respiratorów: \n");
                number_respirators = scan.nextInt();

                Add(arangoDB, dbname, collectionName, name, number_of_patient, number_usg, number_respirators);

                System.out.print("\n");

                if (menu_interrior == 0) {
                    System.out.print("\n");
                    System.out.print("\n");
                    menu_interrior = -1;
                    continue;
                }
            }

            if (menu == 2) {
                System.out.print("Usunięto wszystkie dane z bazy danych.\n");
                System.out.print("\n");
                DeleteAll(arangoDB, dbname, collectionName);
            }

            if (menu == 3) {
                System.out.print("Wyświetlanie wszystkich danych:\n");
                ShowAll(arangoDB, dbname);

                System.out.print("Podaj nazwe do usunięcia:\n");
                String nam = in.readLine();

                DeleteByName(arangoDB, dbname, collectionName, nam);

                System.out.print("Usunięto dane.\n");
                System.out.print("\n");
            }

            if (menu == 4) {
                System.out.print("\n");
                    System.out.print("Wyświetlanie wszystkich danych:\n");
                    ShowAll(arangoDB, dbname);
            }


            if (menu == 5) {
                System.out.print("\n");
                ShowAll(arangoDB, dbname);

                System.out.print("Podaj nazwe:\n");
                String nam = in.readLine();

                System.out.print("Wyświetlanie przychodni o podanej nazwie:\n");
                ShowByName(arangoDB, dbname, collectionName, nam);
                System.out.print("\n");
            }

            if (menu == 6) {

                System.out.print("\n");
                System.out.print("Cała baza danych:\n");
                ShowAll(arangoDB, dbname);

                System.out.print("Podaj nazwe do modyfikacji:\n");
                String namKey = in.readLine();

                System.out.print("Modyfikowany rekord:\n");
                ShowByName(arangoDB, dbname, collectionName, namKey);

                System.out.print("Podaj nową liczbe pacjentów: \n");
                number_of_patient = scan.nextInt();

                System.out.print("Podaj nową liczbe sprzętu USG: \n");
                number_usg = scan.nextInt();

                System.out.print("Podaj nową liczbe respiratorów: \n");
                number_respirators = scan.nextInt();

                UpdateDocument(arangoDB, dbname, collectionName, namKey, number_of_patient, number_usg, number_respirators);

                System.out.print("Rekord został zmodyfikowany: \n");
                ShowByName(arangoDB, dbname, collectionName, namKey);
            }

            if (menu == 0) {
                System.out.print("\n");
                System.out.print("Wyłączanie programu...\n");
                scan.close();
                arangoDB.shutdown();
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

    private static void Add(ArangoDB arangoDB, String dbName, String collectionName, String name, int number_of_patient, int number_of_USG, int number_of_respirators){
        BaseDocument myDocument = new BaseDocument();
        myDocument.setKey(name);
        myDocument.addAttribute("number_of_patient", number_of_patient);
        myDocument.addAttribute("number_of_USG", number_of_USG);
        myDocument.addAttribute("number_of_respirators", number_of_respirators);

        try {
            arangoDB.db(dbName).collection(collectionName).insertDocument(myDocument);
            System.out.println("Nowy rekord został stworzony.");
        } catch(ArangoDBException e) {
            System.err.println("Wystąpił błąd w trakcie tworzenia: " + e.getMessage());
        }
    }

    private static void ShowAll(ArangoDB arangoDB, String dbName) {
        try {
            String query = "FOR t IN Przychodnia RETURN t";
            ArangoCursor<BaseDocument> cursor = arangoDB.db(dbName).query(query, null, null, BaseDocument.class);

            cursor.forEachRemaining(aDocument -> {
                System.out.println("Nazwa przychodni: " + aDocument.getKey());
                System.out.println("Ilość pacjentów: " + aDocument.getAttribute("number_of_patient"));
                System.out.println("Ilość sprzętu USG: " + aDocument.getAttribute("number_of_USG"));
                System.out.println("Ilość respiratorów: " + aDocument.getAttribute("number_of_respirators"));
                System.out.println("\n");
            });
        } catch (ArangoDBException e) {
            System.err.println("Nie można załadować rekordów: " + e.getMessage());
        }
    }

    private static void ShowByName(ArangoDB arangoDB, String dbName, String collectionName, String name){
        try {
            BaseDocument myDocument = arangoDB.db(dbName).collection(collectionName).getDocument(name, BaseDocument.class);
            System.out.println("Nazwa przychodni: " + myDocument.getKey());
            System.out.println("Ilość pacjentów: " + myDocument.getAttribute("number_of_patient"));
            System.out.println("Ilość sprzętu USG: " + myDocument.getAttribute("number_of_USG"));
            System.out.println("Ilość respiratorów: " + myDocument.getAttribute("number_of_respirators"));
            System.out.println("\n");
        } catch(ArangoDBException e) {
            System.err.println("Nie można załadować rekordu: " + e.getMessage());
        }
    }
    private static void UpdateDocument(ArangoDB arangoDB, String dbName, String collectionName, String name, int number_of_patient, int number_of_USG, int number_of_respirators){
        BaseDocument myObject = new BaseDocument();
        myObject.addAttribute("number_of_patient", number_of_patient);
        myObject.addAttribute("number_of_USG", number_of_USG);
        myObject.addAttribute("number_of_respirators", number_of_respirators);
        try {
            arangoDB.db(dbName).collection(collectionName).updateDocument(name, myObject);
        } catch (ArangoDBException e) {
            System.err.println("Failed to update document. " + e.getMessage());
        }
    }
    private static void DeleteAll(ArangoDB arangoDB, String dbName, String collectionName){
        try {
            String query = "FOR t IN Przychodnia RETURN t";
            ArangoCursor<BaseDocument> cursor = arangoDB.db(dbName).query(query, null, null, BaseDocument.class);
            cursor.forEachRemaining(aDocument -> {
                arangoDB.db(dbName).collection(collectionName).deleteDocument(aDocument.getKey());
            });

        } catch (ArangoDBException e) {
            System.err.println("Nie można usunąć rekordów: " + e.getMessage());
        }
    }

    private static void DeleteByName(ArangoDB arangoDB, String dbName, String collectionName, String name){
        try {
            arangoDB.db(dbName).collection(collectionName).deleteDocument(name);
        } catch (ArangoDBException e) {
            System.err.println("Nie można usunąć rekordu: " + e.getMessage());
        }
    }
}
