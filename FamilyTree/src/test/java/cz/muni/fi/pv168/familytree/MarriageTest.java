package cz.muni.fi.pv168.familytree;

import cz.muni.fi.pv168.familytree.Marriage;
import cz.muni.fi.pv168.familytree.GenderType;
import cz.muni.fi.pv168.familytree.Person;
import java.time.LocalDate;
import static org.hamcrest.CoreMatchers.*;
import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * 
 */
public class MarriageTest {
    
    private Marriage m;
    private Person spouse1, spouse2;
    private final LocalDate date = LocalDate.now();
    
    @Test
    public void getters() {
        spouse1 = new Person("Jhon Doe", GenderType.MAN, "Hospital", date.minusYears(50), "The same hospital", date);
        spouse2 = new Person("Jane Doe", GenderType.WOMAN, "Different hospital", date.minusYears(50));
        m = new Marriage(spouse1, spouse2, date.minusYears(20));
        getters("; Using Construstor()");
        try {
            m = new Marriage(spouse1, spouse1, date.minusYears(20));
            fail("Spouse 1 and 2 can be set to same Person using constructor");
        } catch (IllegalArgumentException e) {
            //ok
        }
    }
    
    @Test
    public void setters() {
        m = new Marriage();
        m.setSpouse1(spouse1);
        m.setSpouse2(spouse2);
        m.setFrom(date.minusYears(20));
        getters("; Using setters()");
        
        try {
            m.setSpouse1(spouse2);
            fail("Spouse1 can be set to spouse2");
        } catch (IllegalArgumentException e) {
            //ok
        }
        
        try {
            m.setSpouse2(spouse1);
            fail("Spouse2 can be set to spouse1");
        } catch (IllegalArgumentException e) {
            //ok
        }
    }
    
    private void getters(String source) {
        assertThat("Marriage id == null" + source, m.getId(), is(not(equalTo(null))));
        assertThat("Marriage spouse1 == null" + source, m.getSpouse1(), is(not(equalTo(null))));
        assertThat("Marriage spouse2 == null" + source, m.getSpouse2(), is(not(equalTo(null))));
        assertThat("Marriage dateFrom == null" + source, m.getFrom(), is(not(equalTo(null))));
        assertThat("Marriage spouse1 == spouse2" + source, m.getSpouse1(), is(not(equalTo(m.getSpouse2()))));
        
        assertThat("Marriage id != spouse1" + source, m.getSpouse1(), is(equalTo(spouse1)));
        assertThat("Marriage id != spouse2" + source, m.getSpouse2(), is(equalTo(spouse2)));
        assertThat("Marriage id != date.minusYears(20)" + source, m.getFrom(), is(equalTo(date.minusYears(20))));
        assertThat("Marriage dateTo != null" + source, m.getTo(), is(equalTo(null)));
        
        if (source.equals("; Using Constructor()")) {
            m = new Marriage(spouse1, spouse2, date.minusYears(20), date);
        } else {
            m.setTo(date);
        }
        
        assertThat("Marriage dateTo != date" + source, m.getTo(), is(equalTo(date)));
    }
    
}