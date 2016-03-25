package cz.muni.fi.pv168.familytree;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import javax.sql.DataSource;
import org.apache.derby.jdbc.EmbeddedDataSource;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import org.junit.After;
import static org.junit.Assert.*;

/**
 *
 * @author Peter
 */
public class RelationCatalogImplTest {
    
    private RelationCatalogImpl manager;
    private DataSource ds;
    
    private final Person person1;
    private final Person person2;
    private final Person person3;
    private final Person person4;
    private final Person person5;
    
    public RelationCatalogImplTest() {
        person1 = newPerson("Tomas Stein", GenderType.MAN, LocalDate.of(1973, 12, 1), "Bratislava", null, null);
        person2 = newPerson("Zuzana Steinova", GenderType.WOMAN, LocalDate.of(1995, 8, 9), "Brno", null, null);
        person3 = newPerson("Petra Steinova", GenderType.WOMAN, LocalDate.of(1976, 1, 31), "Trencin", null, null);
        person4 = newPerson("Amy Steinova", GenderType.WOMAN, LocalDate.of(1991, 5, 27), "Bratislava", null, null);
        person5 = newPerson("Gregor Stein", GenderType.MAN, LocalDate.of(1974, 10, 14), "Bratislava", null, null);
    }
    
    private static DataSource prepareDataSource() throws SQLException {
        EmbeddedDataSource ds = new EmbeddedDataSource();
        ds.setDatabaseName("memory:FamilyTreeMngr-test");
        ds.setCreateDatabase("create");
        return ds;
    }
    
    /*@BeforeClass
    public void setUpClass() throws SQLException {
        ds = prepareDataSource();
        try (Connection connection = ds.getConnection()) {
            connection.prepareStatement("CREATE TABLE PEOPLE (" +
                                        "ID INTEGER PRIMARY KEY GENERATED ALWAYS AS IDENTITY," +
                                        "NAME VARCHAR(50)," +
                                        "GENDER VARCHAR(10)," +
                                        "BIRTHDATE DATE," +
                                        "BIRTHPLACE VARCHAR(50)," +
                                        "DEATHDATE DATE," +
                                        "DEATHPLACE VARCHAR(50))").executeUpdate();
        }
        PeopleManagerImpl mngr = new PeopleManagerImpl(ds);
        mngr.createPerson(person1);
        mngr.createPerson(person2);
        mngr.createPerson(person3);
        mngr.createPerson(person4);
        mngr.createPerson(person5);
    }*/
    
    @Before
    public void setUp() throws SQLException {
        ds = prepareDataSource();
        try (Connection connection = ds.getConnection()) {
            connection.prepareStatement("CREATE TABLE PEOPLE (" +
                                        "ID INTEGER PRIMARY KEY GENERATED ALWAYS AS IDENTITY," +
                                        "NAME VARCHAR(50)," +
                                        "GENDER VARCHAR(10)," +
                                        "BIRTHDATE DATE," +
                                        "BIRTHPLACE VARCHAR(50)," +
                                        "DEATHDATE DATE," +
                                        "DEATHPLACE VARCHAR(50))").executeUpdate();
        }
        PeopleManagerImpl mngr = new PeopleManagerImpl(ds);
        mngr.createPerson(person1);
        mngr.createPerson(person2);
        mngr.createPerson(person3);
        mngr.createPerson(person4);
        mngr.createPerson(person5);
        
        try (Connection connection = ds.getConnection()) {
            connection.prepareStatement("CREATE TABLE RELATIONS (" +
                                        "PARENT_ID INTEGER NOT NULL," +
                                        "CHILD_ID INTEGER NOT NULL," +
                                        "CONSTRAINT REL_ID PRIMARY KEY (PARENT_ID, CHILD_ID)," +
                                        "CONSTRAINT PARENT_FK " +
                                        "FOREIGN KEY (PARENT_ID) " +
                                        "REFERENCES PEOPLE (ID)," +
                                        "CONSTRAINT CHILD_FK " +
                                        "FOREIGN KEY (CHILD_ID) " +
                                        "REFERENCES PEOPLE (ID))").executeUpdate();
        }
        manager = new RelationCatalogImpl(ds);
    }
    
    @After
    public void tearDown() throws SQLException {
        try (Connection connection = ds.getConnection()) {
            connection.prepareStatement("DROP TABLE RELATIONS").executeUpdate();
        }
        try (Connection connection = ds.getConnection()) {
            connection.prepareStatement("DROP TABLE PEOPLE").executeUpdate();
        }
    }
    
    /*@AfterClass
    public void tearDownClass() throws SQLException {
        try (Connection connection = ds.getConnection()) {
            connection.prepareStatement("DROP TABLE PEOPLE").executeUpdate();
        }
    }*/
    
    @Test
    public void makeRelation() {
        manager.makeRelation(person1, person2);
        
        assertThat("child not found between children", manager.findChildren(person1).contains(person2), is(true));
        assertThat("parent not found between parents", manager.findParents(person2).contains(person1), is(true));
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void makeRelationWithNullParent() throws Exception {
        manager.makeRelation(null, person2);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void makeRelationWithNullChild() throws Exception {
        manager.makeRelation(person1, null);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void makeRelationWithSamePerson() throws Exception {
        manager.makeRelation(person1, person1);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void makeRelationWrongOrder() throws Exception {
        manager.makeRelation(person2, person1);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void makeRelationYoungParent() throws Exception {
        manager.makeRelation(person4, person2);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void makeRelationOnlyOnce() throws Exception {
        manager.makeRelation(person1, person2);
        manager.makeRelation(person1, person2);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void makeRelationOnlyTwoParents() throws Exception {
        manager.makeRelation(person1, person2);
        manager.makeRelation(person3, person2);
        manager.makeRelation(person5, person2);
    }
    
    @Test
    public void deleteRelation() {
        manager.makeRelation(person1, person2);
        
        manager.deleteRelation(person1, person2);
        
        assertThat("child found between children", manager.findChildren(person1).contains(person2), is(false));
        assertThat("parent found between parents", manager.findParents(person2).contains(person1), is(false));
    }
    
    @Test(expected = EntityNotFoundException.class)
    public void deleteRelationNonexistingRelation() {
        manager.deleteRelation(person1, person2);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void deleteRelationWithNullParent() {
        manager.deleteRelation(null, person2);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void deleteRelationWithNullChild() {
        manager.deleteRelation(person1, null);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void findParentsWithNull() {
        manager.makeRelation(person1, person2);
        
        manager.findParents(null);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void findChildrenWithNull() {
        manager.makeRelation(person1, person2);
        
        manager.findChildren(null);
    }
    
    @Test
    public void findParents() {
        assertThat(manager.findParents(person2).isEmpty(), is(true));
        
        manager.makeRelation(person1, person2);
        manager.makeRelation(person3, person2);
        
        assertThat("saved and retrieved relations differ", manager.findParents(person2).size(), is(equalTo(2)));
        assertThat("saved and retrieved relations differ", manager.findParents(person2).contains(person1), is(true));
        assertThat("saved and retrieved relations differ", manager.findParents(person2).contains(person3), is(true));
    }
    
    @Test
    public void findChildren() {
        assertThat(manager.findChildren(person1).isEmpty(), is(true));
        
        manager.makeRelation(person1, person2);
        manager.makeRelation(person1, person4);
        
        assertThat("saved and retrieved relations differ", manager.findChildren(person1).size(), is(equalTo(2)));
        assertThat("saved and retrieved relations differ", manager.findChildren(person1).contains(person2), is(true));
        assertThat("saved and retrieved relations differ", manager.findChildren(person1).contains(person4), is(true));
    }
    
    @Test
    public void findParentsWithoutParents() {
        assertThat("person without parents has parents", manager.findParents(person1).isEmpty(), is(true));
    }
    
    @Test
    public void findChildrenWithoutChildren() {
        assertThat("person without children has children", manager.findChildren(person1).isEmpty(), is(true));
    }
    
    private static Person newPerson(String name, GenderType type, LocalDate dateOfBirth,
            String placeOfBirth, LocalDate dateOfDeath, String placeOfDeath) {
        Person p = new Person();
        p.setName(name);
        p.setGender(type);
        p.setDateOfBirth(dateOfBirth);
        p.setPlaceOfBirth(placeOfBirth);
        p.setDateOfDeath(dateOfDeath);
        p.setPlaceOfDeath(placeOfDeath);
        return p;
    }
}
