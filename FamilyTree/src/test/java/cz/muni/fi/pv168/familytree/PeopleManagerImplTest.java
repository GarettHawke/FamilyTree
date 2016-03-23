package cz.muni.fi.pv168.familytree;

import java.sql.SQLException;
import java.time.LocalDate;
import javax.annotation.Resource;
import javax.sql.DataSource;
import static org.hamcrest.CoreMatchers.*;
import org.junit.*;
import static org.junit.Assert.*;
/**
 * 
 * 
 */ 

public class PeopleManagerImplTest {
    
    private enum Source {id, name, gender, placeOfBirth, dateOfBirth, placeOfDeath, dateOfDeath}
    private PeopleManager manager;
    private Person p0;
    private Long id;
    private final LocalDate date = LocalDate.now();
    @Resource(name = "jdbc:derby://localhost:1527/pv168")
    private DataSource ds;
    
    @Before
    public void setUp() throws SQLException {
        manager = new PeopleManagerImpl(ds);
        p0 = new Person("p0", GenderType.MAN, "p0Birth", date.minusYears(30), "p0Death", date);
    }
    
    @Test
    public void update() {
        Person p1 = new Person("p1", GenderType.MAN, "p1Birth", date.minusYears(30), "p1Death", date);
        
        p0.setId(1L);
        try{
            manager.createPerson(p0);
            fail("Should reject given id");
        } catch (IllegalArgumentException e) {
            p0.setId(null);
        }
        
        manager.createPerson(p0);
        manager.createPerson(p1);
        id = p0.getId();
        
        
        assertThat("Id of saved Person == null", id, is(not(equalTo(null))));
        assertThat("Retrieved Person != saved Person", manager.findPersonById(id), is(equalTo(p0)));
        
        p0.setName("Jhon Doe");
        update(Source.name);
        
        p0.setGender(GenderType.WOMAN);
        update(Source.gender);
        
        p0.setPlaceOfBirth("!p0Birth");
        update(Source.placeOfBirth);
        
        p0.setDateOfBirth(date);
        update(Source.dateOfBirth);
        
        p0.setPlaceOfDeath("!p0Death");
        update(Source.placeOfDeath);
        
        p0.setDateOfDeath(date.minusDays(1));
        update(Source.dateOfDeath);
        
        assertThat("p1 was changed while changing p0", manager.findPersonById(p1.getId()), is(equalTo(p1)));
    }
    
    private void update(Source source) {
        Person temp = manager.findPersonById(id);
        manager.updatePerson(p0);
        p0 = manager.findPersonById(id);
        
        if (source == Source.name) {
            assertThat("Name was not changed when changing name", p0.getName(), is(not(equalTo("p0"))));
        } else {
            assertThat("Name was changed when changing " + source, p0.getName(), is(equalTo("p0")));
        }
        
        if (source == Source.gender) {
            assertThat("gender was not changed when changing gender", p0.getGender(), is(not(equalTo(GenderType.MAN))));
        } else {
            assertThat("gender was changed when changing " + source, p0.getGender(), is(equalTo(GenderType.MAN)));
        }
        
        if (source == Source.placeOfBirth) {
            assertThat("placeOfBirth was not changed when changing placeOfBirth", p0.getPlaceOfBirth(), is(not(equalTo("p0Birth"))));
        } else {
            assertThat("placeOfBirth was changed when changing " + source, p0.getPlaceOfBirth(), is(equalTo("p0Birth")));
        }
        
        if (source == Source.dateOfBirth) {
            assertThat("dateOfBirth was not changed when changing dateOfBirth", p0.getDateOfBirth(), is(not(equalTo(date.minusYears(30)))));
        } else {
            assertThat("dateOfBirth was changed when changing " + source, p0.getDateOfBirth(), is(equalTo(date.minusYears(30))));
        }
        
        if (source == Source.placeOfDeath) {
            assertThat("placeOfDeath was not changed when changing placeOfDeath", p0.getPlaceOfDeath(), is(not(equalTo("p0Death"))));
        } else {
            assertThat("placeOfDeath was changed when changing " + source, p0.getPlaceOfDeath(), is(equalTo("p0Death")));
        }
        
        if (source == Source.dateOfDeath) {
            assertThat("dateOfDeath was not changed when changing dateOfDeath", p0.getDateOfDeath(), is(not(equalTo(date))));
        } else {
            assertThat("dateOfDeath was changed when changing " + source, p0.getDateOfDeath(), is(equalTo(date)));
        }
        p0 = temp;
        manager.updatePerson(temp);
    }
    
    @Test
    public void equals() {
        Person person = new Person("!p1", GenderType.MAN, "!p1Birth", date.minusYears(30), "!p1Death", date);
        manager.createPerson(person);
        id = person.getId();
        
        assertThat("Id of retrieved person and is different than the id it was retrieved by", 
                manager.findPersonById(id).getId(), is(equalTo(id)));
    }
    
    @Test
    public void constructor() {
        p0 = new Person("Jhon Doe", GenderType.MAN, "Hospital", date.minusDays(10));
        getters("; Using Construstor()");
        
        try {
            p0 = new Person("Jhon Doe", GenderType.MAN, "Hospital", date, "The same Hospital", date.minusDays(10));
            manager.createPerson(p0);
            fail("Constructing Person with dateOfBirth after dateOfDeath didn't throw any exception");
        } catch (IllegalArgumentException e) {
            //ok
        }
    }
    
    @Test
    public void setters() {
        p0 = new Person();
        p0.setName("Jhon Doe");
        p0.setGender(GenderType.MAN);
        p0.setPlaceOfBirth("Hospital");
        p0.setDateOfBirth(date.minusDays(10));
        getters("; Using setters()");
        
        try {
            p0.setDateOfBirth(p0.getDateOfDeath().plusDays(1));
            p0.setId(null);
            manager.createPerson(p0);
            fail("Setting dateOfBirth after dateOfDeath didn't throw any exception");
        } catch (IllegalArgumentException e) {
            p0.setDateOfBirth(date.minusDays(10));
        }
        
        try {
            p0.setDateOfDeath(p0.getDateOfBirth().minusDays(1));
            p0.setId(null);
            manager.createPerson(p0);
            fail("Setting dateOfDeath before dateOfBirth didn't throw any exception");
        } catch (IllegalArgumentException e) {
            //ok
        }
    }
    
    private void getters(String source) {
        manager.createPerson(p0);
        assertThat("Person id == null" + source, p0.getId(), is(not(equalTo(null))));
        assertThat("Person Name == null" + source, p0.getName(), is(not(equalTo(null))));
        assertThat("Person gender == null" + source, p0.getGender(), is(not(equalTo(null))));
        assertThat("Person birthPlace == null" + source, p0.getPlaceOfBirth(), is(not(equalTo(null))));
        assertThat("Person birthDay == null" + source, p0.getDateOfBirth(), is(not(equalTo(null))));
        
        assertThat("Person id != \"Jhon Doe\"" + source, p0.getName(), is(equalTo("Jhon Doe")));
        assertThat("Person gender != GenderType.MAN" + source, p0.getGender(), is(equalTo(GenderType.MAN)));
        assertThat("Person placeOfBirth != \"Hospital\"" + source, p0.getPlaceOfBirth(), is(equalTo("Hospital")));
        assertThat("Person birthDay != date.minusDays(10)" + source, p0.getDateOfBirth(), is(equalTo(date.minusDays(10))));
        assertThat("Person placeOfDeath != null" + source, p0.getPlaceOfDeath(), is(equalTo(null)));
        assertThat("Person dateOfDeath != null" + source, p0.getDateOfDeath(), is(equalTo(null)));
        
        if (source.equals("; Using Constructor()")) {
            p0 = new Person("Jhon Doe", GenderType.MAN, "Hospital", date.minusDays(10), "The same Hospital", date );
            manager.createPerson(p0);
        } else {
            p0.setPlaceOfDeath("The same Hospital");
            p0.setDateOfDeath(date);
            p0.setId(null);
            manager.createPerson(p0);
        }
        
        assertThat("Person placeOfDeath != \"The same Hospital\"" + source, p0.getPlaceOfDeath(), is(equalTo("The same Hospital")));
        assertThat("Person dateOfDeath != date" + source, p0.getDateOfDeath(), is(equalTo(date)));
        
    }
}
