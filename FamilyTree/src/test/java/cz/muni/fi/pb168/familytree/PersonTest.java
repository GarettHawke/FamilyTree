package cz.muni.fi.pb168.familytree;

import java.time.LocalDate;
import static org.hamcrest.CoreMatchers.*;
import org.junit.*;
import static org.junit.Assert.*;
import org.junit.rules.ExpectedException;

/**
 *
 * 
 */
public class PersonTest {
    
    private Person p;
    private final LocalDate date = LocalDate.now();
    
    @Test
    public void constructor() {
        p = new Person("Jhon Doe", GenderType.MAN, "Hospital", date.minusDays(10));
        getters("; Using Construstor()");
    }
    
    @Test
    public void setters() {
        p = new Person();
        p.setName("Jhon Doe");
        p.setGender(GenderType.MAN);
        p.setPlaceOfBirth("Hospital");
        p.setDateOfBirth(date.minusDays(10));
        getters("; Using setters()");
        
        
        try {
            p.setDateOfBirth(p.getDateOfDeath().plusDays(1));
            fail("Setting dateOfBirth after dateOfDeath didn't throw any exception");
        } catch (IllegalArgumentException e) {
            //ok
        }
        
        try {
            p.setDateOfDeath(p.getDateOfBirth().minusDays(1));
            fail("Setting dateOfDeath before dateOfBirth didn't throw any exception");
        } catch (IllegalArgumentException e) {
            //ok
        }
    }
    
    private void getters(String source) {
        assertThat("Person id == null" + source, p.getId(), is(not(equalTo(null))));
        assertThat("Person Name == null" + source, p.getName(), is(not(equalTo(null))));
        assertThat("Person gender == null" + source, p.getGender(), is(not(equalTo(null))));
        assertThat("Person birthPlace == null" + source, p.getPlaceOfBirth(), is(not(equalTo(null))));
        assertThat("Person birthDay == null" + source, p.getDateOfBirth(), is(not(equalTo(null))));
        
        assertThat("Person id != \"Jhon Doe\"" + source, p.getId(), is(equalTo("Jhon Doe")));
        assertThat("Person gender != GenderType.MAN" + source, p.getGender(), is(equalTo(GenderType.MAN)));
        assertThat("Person placeOfBirth != \"Hospital\"" + source, p.getPlaceOfBirth(), is(equalTo("Hospital")));
        assertThat("Person birthDay != date.minusDays(10)" + source, p.getDateOfBirth(), is(equalTo(date.minusDays(10))));
        assertThat("Person placeOfDeath != null" + source, p.getPlaceOfDeath(), is(equalTo(null)));
        assertThat("Person dateOfDeath != null" + source, p.getDateOfDeath(), is(equalTo(null)));
        
        if (source.equals("; Using Constructor()")) {
            p = new Person("Jhon Doe", GenderType.MAN, "Hospital", date.minusDays(10), "The same Hospital", date );
        } else {
            p.setPlaceOfDeath("The same Hospital");
            p.setDateOfDeath(date);
        }
        
        assertThat("Person placeOfDeath != \"The same Hospital\"" + source, p.getPlaceOfDeath(), is(equalTo("The same Hospital")));
        assertThat("Person dateOfDeath != date" + source, p.getDateOfDeath(), is(equalTo(date)));
        
    }
    
}
