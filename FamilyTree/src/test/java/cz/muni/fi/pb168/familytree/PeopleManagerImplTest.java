package cz.muni.fi.pb168.familytree;

import java.time.LocalDate;
import static org.hamcrest.CoreMatchers.*;
import org.junit.*;
import static org.junit.Assert.*;
/**
 * 
 * 
 */
public class PeopleManagerImplTest {
    
    private PeopleManager manager;
    private Person p0;
    private Long id;
    private final LocalDate date = LocalDate.now();
    
    @Before
    public void setUp() {
        manager = new PeopleManagerImpl();
        p0 = new Person("p0", GenderType.MAN, "p0Birth", date.minusYears(30), "p0Death", date);
    }
    
    @Test
    public void create() {
        manager.createPerson(p0);
        id = p0.getId();
        
        assertThat("Id of saved Person == null", id, is(not(equalTo(null))));
        assertThat("Retrieved Person != saved Person", manager.findPeronById(id), is(equalTo(p0)));
    }
    
    @Test
    public void update() {
        Person p1 = new Person("p1", GenderType.MAN, "p1Birth", date.minusYears(30), "p1Death", date);
        manager.createPerson(p1);
        
        p0.setName(null);
        update("Name to null");
        
        p0.setName("Jhon Doe");
        update("Name");
        
        p0.setGender(GenderType.WOMAN);
        update("gender");
        
        p0.setPlaceOfBirth("!p0Birth");
        update("placeOfBirth");
        
        p0.setDateOfBirth(date);
        update("dateOfBirth");
        
        p0.setPlaceOfDeath("!p0Death");
        update("placeOfDeath");
        
        p0.setDateOfDeath(date.minusDays(1));
        update("dateOfDeath");
    }
    
    private void update(String source) {
        p0 = new Person("p0", GenderType.MAN, "p0Birth", date.minusYears(30), "p0Death", date);
        manager.createPerson(p0);
        id = p0.getId();
        
        manager.updatePerson(p0);
        p0 = manager.findPeronById(id);
        if (source.equals("Name")) {
            assertThat("Name was not changed when changing Name", p0.getName(), is(not(equalTo("p0"))));
        } else {
            assertThat("Name was changed when changing " + source, p0.getName(), is(equalTo("p0")));
        }
        
        if (source.equals("gender")) {
            assertThat("gender was not changed when changing gender", p0.getGender(), is(not(equalTo(GenderType.MAN))));
        } else {
            assertThat("gender was changed when changing " + source, p0.getGender(), is(equalTo(GenderType.MAN)));
        }
        
        if (source.equals("placeOfBirth")) {
            assertThat("placeOfBirth was not changed when changing placeOfBirth", p0.getPlaceOfBirth(), is(not(equalTo("p0Birth"))));
        } else {
            assertThat("placeOfBirth was changed when changing " + source, p0.getPlaceOfBirth(), is(equalTo("p0Birth")));
        }
        
        if (source.equals("dateOfBirth")) {
            assertThat("dateOfBirth was not changed when changing dateOfBirth", p0.getDateOfBirth(), is(not(equalTo(date.minusYears(30)))));
        } else {
            assertThat("dateOfBirth was changed when changing " + source, p0.getDateOfBirth(), is(equalTo(date.minusYears(30))));
        }
        
        if (source.equals("placeOfDeath")) {
            assertThat("placeOfDeath was not changed when changing placeOfDeath", p0.getName(), is(not(equalTo("p0Death"))));
        } else {
            assertThat("placeOfDeath was changed when changing " + source, p0.getPlaceOfDeath(), is(equalTo("p0Death")));
        }
        
        if (source.equals("dateOfDeath")) {
            assertThat("dateOfDeath was not changed when changing dateOfDeath", p0.getDateOfDeath(), is(not(equalTo(date))));
        } else {
            assertThat("dateOfDeath was changed when changing " + source, p0.getDateOfDeath(), is(equalTo(date)));
        }
    }
    
    @Test
    public void equals() {
        Person person = new Person();
        manager.createPerson(person);
        id = person.getId();
        
        assertThat("Id of retrieved person and is different than the id it was retrieved by", 
                manager.findPeronById(id).getId(), is(equalTo(id)));
    }
}
