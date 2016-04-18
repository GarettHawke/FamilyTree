package cz.muni.fi.pv168.familytree;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.apache.derby.jdbc.EmbeddedDataSource;

public class Main {

    public static DataSource createMemoryDatabase() throws SQLException, IOException {
        EmbeddedDataSource ds = new EmbeddedDataSource();
        ds.setDatabaseName("memory:familyTreeDB");
        ds.setCreateDatabase("create");
        
        String path = "E:\\Programming\\FamilyTree\\FamilyTree\\";
        String createTablePeople = String.join("", Files.readAllLines(Paths.get(path, "SQL-createTablePeople.sql")));
        String insertPeople = String.join("", Files.readAllLines(Paths.get(path, "SQL-insertPeople.sql")));
        String createTableMarriages = String.join("", Files.readAllLines(Paths.get(path, "SQL-createTableMarriages.sql")));
        String insertMarriages = String.join("", Files.readAllLines(Paths.get(path, "SQL-insertMarriages.sql")));
        String createTableRelations = String.join("", Files.readAllLines(Paths.get(path, "SQL-createTableRelations.sql")));
        String insertRelations = String.join("", Files.readAllLines(Paths.get(path, "SQL-insertRelations.sql")));
        
        try (Connection connection = ds.getConnection()) {
            connection.prepareStatement(createTablePeople).executeUpdate();
            connection.prepareStatement(insertPeople).executeUpdate();
            connection.prepareStatement(createTableMarriages).executeUpdate();
            connection.prepareStatement(insertMarriages).executeUpdate();
            connection.prepareStatement(createTableRelations).executeUpdate();
            connection.prepareStatement(insertRelations).executeUpdate();
        }
        
        return ds;
    }

    public static void main(String[] args) throws SQLException, IOException {

        DataSource dataSource = createMemoryDatabase();
        PeopleManager peopleManager = new PeopleManagerImpl(dataSource);
        MarriageCatalog marriageCatalog = new MarriageCatalogImpl(dataSource);
        marriageCatalog.setPeopleManager(peopleManager);
        RelationCatalog relationCatalog = new RelationCatalogImpl(dataSource);
        relationCatalog.setPeopleManager(peopleManager);

        List<Person> allPeople = peopleManager.findAllPeople();
        System.out.println("All people = " + allPeople);
        List<Marriage> allMarriages = marriageCatalog.findAllMarriages();
        System.out.println("All marriages = " + allMarriages);
        Map<Person, List<Person>> relations = relationCatalog.findAllRelation();
        System.out.println("All relations = " + relations);
        
        Person p = peopleManager.findPersonById(3L);
        peopleManager.deletePerson(p);
        
        allPeople.clear();
        allPeople = peopleManager.findAllPeople();
        System.out.println("All people = " + allPeople);
        allMarriages.clear();
        allMarriages = marriageCatalog.findAllMarriages();
        System.out.println("All marriages = " + allMarriages);
        relations.clear();
        relations = relationCatalog.findAllRelation();
        System.out.println("All relations = " + relations);
    }
}