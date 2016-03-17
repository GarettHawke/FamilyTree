package cz.muni.fi.pv168.familytree;

import java.sql.SQLException;
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
    public void setUp() throws SQLException {
        manager = new PeopleManagerImpl();
        p0 = new Person("p0", GenderType.MAN, "p0Birth", date.minusYears(30), "p0Death", date);
    }
    
    @Test
    public void create() {
        Person p1 = new Person("p1", GenderType.MAN, "p1Birth", date.minusYears(30), "p1Death", date);
        manager.createPerson(p1);
        
        p0.setId(1L);
        try{
            create("Id");
            fail("Should reject given id");
        } catch (IllegalArgumentException e) {
            //ok
        }
        
        p0.setName("Jhon Doe");
        create("Name");
        
        assertThat("Id of saved Person == null", id, is(not(equalTo(null))));
        assertThat("Retrieved Person != saved Person", manager.findPersonById(id), is(equalTo(p0)));
        
        p0.setGender(GenderType.WOMAN);
        create("gender");
        
        p0.setPlaceOfBirth("!p0Birth");
        create("placeOfBirth");
        
        p0.setDateOfBirth(date);
        create("dateOfBirth");
        
        p0.setPlaceOfDeath("!p0Death");
        create("placeOfDeath");
        
        p0.setDateOfDeath(date.minusDays(1));
        create("dateOfDeath");
        
        assertThat("p1 was changed while changing p0", manager.findPersonById(p1.getId()), is(equalTo(p1)) );
    }
    
    private void create(String source) {
        manager.createPerson(p0);
        p0 = manager.findPersonById(id);
        id = p0.getId();
        
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
        p0 = new Person("p0", GenderType.MAN, "p0Birth", date.minusYears(30), "p0Death", date);
    }
    
    @Test
    public void equals() {
        Person person = new Person();
        manager.createPerson(person);
        id = person.getId();
        
        assertThat("Id of retrieved person and is different than the id it was retrieved by", 
                manager.findPersonById(id).getId(), is(equalTo(id)));
    }
}
