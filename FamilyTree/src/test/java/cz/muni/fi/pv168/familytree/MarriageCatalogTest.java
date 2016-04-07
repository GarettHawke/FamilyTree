package cz.muni.fi.pv168.familytree;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import org.apache.derby.jdbc.EmbeddedDataSource;
import static org.hamcrest.CoreMatchers.*;
import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * 
 */
public class MarriageCatalogTest {
    
    private enum Source {from, to, spouse1, spouse2}
    private Marriage m;
    private Person sp1, sp2;
    private final LocalDate date = LocalDate.now();
    private MarriageCatalog catalog;
    private PeopleManager manager;
    private DataSource ds;
    private long id;
    
    private final String createTableMarriages;
    private final String createTablePeople;
    
    public MarriageCatalogTest() throws IOException {
        createTableMarriages = String.join("", Files.readAllLines(Paths.get("SQL-createTableMarriages.sql")));
        createTablePeople = String.join("", Files.readAllLines(Paths.get("SQL-createTablePeople.sql")));
    }
    
    @Before
    public void setUp() throws SQLException {
        ds = prepareDataSource();
        try (Connection connection = ds.getConnection()) {
            connection.prepareStatement(createTablePeople).executeUpdate();
            connection.prepareStatement(createTableMarriages).executeUpdate();
        }
        catalog = new MarriageCatalogImpl(ds);
        manager = new PeopleManagerImpl(ds);
        catalog.setPeopleManager(manager);
        sp1 = new Person("Jhon Doe", GenderType.MAN, "Hospital", date.minusYears(50), "The same hospital", date);
        sp2 = new Person("Jane Doe", GenderType.WOMAN, "Different hospital", date.minusYears(50));
        manager.createPerson(sp1);
        manager.createPerson(sp2);
        m = new Marriage(sp1, sp2, date.minusYears(20));
    }
    
    private static DataSource prepareDataSource() throws SQLException {
        EmbeddedDataSource ds = new EmbeddedDataSource();
        ds.setDatabaseName("memory:MrrgCtlg-test");
        ds.setCreateDatabase("create");
        return ds;
    }
    
    @After
    public void tearDown() throws SQLException {
        try (Connection connection = ds.getConnection()) {
            connection.prepareStatement("DROP TABLE MARRIAGES").executeUpdate();
            connection.prepareStatement("DROP TABLE PEOPLE").executeUpdate();
        }
    }
    
    @Test
    public void update() {
        m = new Marriage(sp1, sp2, date.minusYears(20), date);
        Marriage m2 = new Marriage(sp2, sp1, date.minusYears(10), date);
        
        m.setId(1L);
        try{
            catalog.createMarriage(m);
            fail("Should reject given id");
        } catch (IllegalArgumentException e) {
            m.setId(null);
        }
        
        catalog.createMarriage(m);
        catalog.createMarriage(m2);
        id = m.getId();
        
        
        assertThat("Id of saved Marriage == null", id, is(not(equalTo(null))));
        assertThat("Retrieved Marraige != saved Marriage", catalog.findMarriageById(id), is(equalTo(m)));
        
        m.setFrom(date.minusYears(30));
        update(Source.from);
        
        m.setTo(date.minusYears(10));
        update(Source.to);
        
        Person sp3 = new Person("s3", GenderType.MAN, "s3Birth", date.minusYears(45));
        manager.createPerson(sp3);
        
        m.setSpouse1(sp3);
        update(Source.spouse1);
        
        m.setSpouse2(sp3);
        update(Source.spouse2);
        
        assertThat("m2 was changed while changing m", catalog.findMarriageById(m2.getId()), is(equalTo(m2)));
        
        try {
            m2.setSpouse1(null);
            catalog.updateMarriage(m2);
            fail("Should reject marriage with null Person");
        } catch (IllegalArgumentException e) {
            m2.setSpouse1(new Person("Jhon Doe", GenderType.MAN, "Hospital", date.minusYears(50), "The same hospital", date));
        }
        
        try {
            m2.setSpouse1(new Person());
            catalog.updateMarriage(m2);
            fail("Should reject marriage with illegal Person");
        } catch (IllegalArgumentException e) {
            m2.setSpouse1(new Person("Jhon Doe", GenderType.MAN, "Hospital", date.minusYears(50), "The same hospital", date));
        }
        
        try {
            m2.setSpouse1(new Person("Carl", GenderType.MAN, "carlBirth", date, "CarlDeath", date.minusDays(1)));
            catalog.updateMarriage(m2);
            fail("Should reject marriage with illegal Person");
        } catch (IllegalArgumentException e) {
            m2.setSpouse1(new Person("Jhon Doe", GenderType.MAN, "Hospital", date.minusYears(50), "The same hospital", date));
        }
    }
    
    private void update(Source source) {
        Marriage temp = catalog.findMarriageById(id);
        catalog.updateMarriage(m);
        m = catalog.findMarriageById(id);
        
        if (source == Source.from) {
            assertThat("From was not changed when changing from", m.getFrom(), is(not(equalTo(date.minusYears(20)))));
        } else {
            assertThat("From was changed when changing " + source, m.getFrom(), is(equalTo(date.minusYears(20))));
        }
        
        if (source == Source.to) {
            assertThat("To was not changed when changing To", m.getTo(), is(not(equalTo(date))));
        } else {
            assertThat("To was changed when changing " + source, m.getTo(), is(equalTo(date)));
        }
        
        if (source == Source.spouse1) {
            assertThat("Spouse1 was not changed when changing spouse1", m.getSpouse1(), is(not(equalTo(sp1))));
        } else {
            assertThat("Spouse1 was changed when changing " + source, m.getSpouse1(), is(equalTo(sp1)));
        }
        
        if (source == Source.spouse2) {
            assertThat("Spouse2 was not changed when changing spouse2", m.getSpouse2(), is(not(equalTo(sp2))));
        } else {
            assertThat("Spouse2 was changed when changing " + source, m.getSpouse2(), is(equalTo(sp2)));
        }
        m = temp;
        catalog.updateMarriage(temp);
    }
    
    @Test
    public void equals() {
        catalog.createMarriage(m);
        id = m.getId();
        
        assertThat("Id of retrieved marriage and is different than the id it was retrieved by", 
                catalog.findMarriageById(id).getId(), is(equalTo(id)));
    }
    
    @Test
    public void findAll() {
        Marriage m2 = new Marriage(sp2, sp1, date.minusYears(10), date);
        List<Marriage> list = new ArrayList<>();
        list.add(m);
        list.add(m2);
        
        catalog.createMarriage(m);
        catalog.createMarriage(m2);
        
        List<Marriage> otherList = catalog.findAllMarriages();
        
        assertThat("Retrieved list has differents contents than expected",
                list.containsAll(otherList), is(equalTo(otherList.containsAll(list))));
    }
    
    @Test
    public void findCurrent() {
        //System.out.println("here: v");
        catalog.createMarriage(m);
        Marriage m2 = new Marriage(sp1, sp2, date.minusYears(30), date.minusYears(25));
        catalog.createMarriage(m2);
        Marriage findCurrentMarriage = catalog.findCurrentMarriage(sp1);
        assertThat("The current marriage of a person was not returned", findCurrentMarriage, is(equalTo(m)));
    }
    
    @Test
    public void findMarriagesOfPerson() {
        catalog.createMarriage(m);
        Marriage m2 = new Marriage(sp1, sp2, date.minusYears(30), date.minusYears(25));
        catalog.createMarriage(m2);
        List<Marriage> otherList = new ArrayList<>();
        otherList.add(m);
        otherList.add(m2);
        List<Marriage> findAllMarriages = catalog.findAllMarriages();
        assertThat("The list of all marraiges was not returned", 
                findAllMarriages.containsAll(otherList), is(equalTo(otherList.containsAll(findAllMarriages))));
    }
    
    @Test
    public void constructor() {
        getters("; Using Construstor()");
        try {
            m = new Marriage(sp1, sp1, date.minusYears(20));
            catalog.createMarriage(m);
            fail("Spouse 1 and 2 can be set to same Person using constructor");
        } catch (IllegalArgumentException e) {
            //ok
        }
        
        try {
            m = new Marriage(sp1, sp2, date, date.minusYears(20));
            catalog.createMarriage(m);
            fail("Constructing Marriage with \"from\" after \"to\" didn't throw any exception");
        } catch (IllegalArgumentException e) {
            //ok
        }
        
        try {
            m = new Marriage(new Person(), sp2, date, date.minusYears(20));
            catalog.createMarriage(m);
            fail("Constructing Marriage with empty spouse1 didn't throw any exception");
        } catch (IllegalArgumentException e) {
            //ok
        }
        
        try {
            m = new Marriage(sp1, new Person(), date, date.minusYears(20));
            catalog.createMarriage(m);
            fail("Constructing Marriage with empty spouse2 didn't throw any exception");
        } catch (IllegalArgumentException e) {
            //ok
        }
    }
    
    @Test
    public void setters() {
        m = new Marriage();
        m.setSpouse1(sp1);
        m.setSpouse2(sp2);
        m.setFrom(date.minusYears(20));
        getters("; Using setters()");
        
        try {
            m.setSpouse1(sp2);
            catalog.createMarriage(m);
            fail("Spouse1 can be set to spouse2");
        } catch (IllegalArgumentException e) {
            //ok
        }
        
        try {
            m.setSpouse2(sp1);
            catalog.createMarriage(m);
            fail("Spouse2 can be set to spouse1");
        } catch (IllegalArgumentException e) {
            //ok
        }
        
        try {
            m.setFrom(m.getTo().plusDays(1));
            catalog.createMarriage(m);
            fail("From can be set after to");
        } catch (IllegalArgumentException e) {
            //ok
        }
        
        try {
            m.setTo(m.getFrom().minusDays(1));
            catalog.createMarriage(m);
            fail("To can be set before from");
        } catch (IllegalArgumentException e) {
            //ok
        }
        
        m = new Marriage(sp1, sp2, date.minusYears(20));
        try {
            m.setSpouse1(null);
            catalog.createMarriage(m);
            fail("Should reject marriage with null Person");
        } catch (IllegalArgumentException e) {
            m.setSpouse1(new Person("Jhon Doe", GenderType.MAN, "Hospital", date.minusYears(50), "The same hospital", date));
        }
        
        try {
            m.setSpouse1(new Person());
            catalog.createMarriage(m);
            fail("Should reject marriage with illegal Person");
        } catch (IllegalArgumentException e) {
            m.setSpouse1(new Person("Jhon Doe", GenderType.MAN, "Hospital", date.minusYears(50), "The same hospital", date));
        }
        
        try {
            m.setSpouse1(new Person("Carl", GenderType.MAN, "carlBirth", date, "CarlDeath", date.minusDays(1)));
            catalog.createMarriage(m);
            fail("Should reject marriage with illegal Person");
        } catch (IllegalArgumentException e) {
            m.setSpouse1(new Person("Jhon Doe", GenderType.MAN, "Hospital", date.minusYears(50), "The same hospital", date));
        }
    }
    
    private void getters(String source) {
        catalog.createMarriage(m);
        assertThat("Marriage id == null" + source, m.getId(), is(not(equalTo(null))));
        assertThat("Marriage spouse1 == null" + source, m.getSpouse1(), is(not(equalTo(null))));
        assertThat("Marriage spouse2 == null" + source, m.getSpouse2(), is(not(equalTo(null))));
        assertThat("Marriage dateFrom == null" + source, m.getFrom(), is(not(equalTo(null))));
        assertThat("Marriage spouse1 == spouse2" + source, m.getSpouse1(), is(not(equalTo(m.getSpouse2()))));
        
        assertThat("Marriage spouse1 != sp1" + source, m.getSpouse1(), is(equalTo(sp1)));
        assertThat("Marriage spouse2 != sp2" + source, m.getSpouse2(), is(equalTo(sp2)));
        assertThat("Marriage from != date.minusYears(20)" + source, m.getFrom(), is(equalTo(date.minusYears(20))));
        
        if (source.equals("; Using Constructor()")) {
            m = new Marriage(sp1, sp2, date.minusYears(20), date);
        } else {
            m.setTo(date);
            m.setId(null);
        }
        
        catalog.createMarriage(m);
        assertThat("Marriage dateTo != date" + source, m.getTo(), is(equalTo(date)));
    }
    
    @Test
    public void deleteMarriage() {
        Person p1 = new Person("Thomas Lee", GenderType.MAN, "p1Place", date.minusYears(30));
        Person p2 = new Person("Jane Lee", GenderType.WOMAN, "p2Place", date.minusYears(30));
        manager.createPerson(p1);
        manager.createPerson(p2);
        
        Marriage m2 = new Marriage(p1, p2, date.minusYears(5));
        catalog.createMarriage(m);
        catalog.createMarriage(m2);
        
        assertNotNull(catalog.findMarriageById(m.getId()));
        assertNotNull(catalog.findMarriageById(m2.getId()));

        catalog.deleteMarriage(m2);

        assertNotNull(catalog.findMarriageById(m.getId()));
        assertNull(catalog.findMarriageById(m2.getId()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void deleteNullMarriage() {
        catalog.deleteMarriage(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void deleteMarriageWithNullId() {
        m.setId(null);
        catalog.deleteMarriage(m);
    }

    @Test(expected = EntityNotFoundException.class)
    public void deleteMarriageWithNonExistingId() {
        m.setId(1L);
        catalog.deleteMarriage(m);
    }
}
