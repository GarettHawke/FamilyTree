/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.pb168.familytree;

import java.sql.SQLException;
import java.time.LocalDate;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 *
 * @author Peter
 */
public class RelationCatalogImplTest {
    
    private RelationCatalogImpl manager;
    
    private final Person person1;
    private final Person person2;
    private final Person person3;
    private final Person person4;
    private final Person person5;
    
    public RelationCatalogImplTest() {
        person1 = newPerson(1L, "Tomas Stein", GenderType.MAN, LocalDate.of(1973, 12, 1), "Bratislava", null, null);
        person2 = newPerson(2L, "Zuzana Steinova", GenderType.WOMAN, LocalDate.of(1995, 8, 9), "Brno", null, null);
        person3 = newPerson(3L, "Petra Steinova", GenderType.WOMAN, LocalDate.of(1976, 1, 31), "Trencin", null, null);
        person4 = newPerson(4L, "Amy Steinova", GenderType.WOMAN, LocalDate.of(1991, 5, 27), "Bratislava", null, null);
        person5 = newPerson(5L, "Gregor Stein", GenderType.MAN, LocalDate.of(1974, 10, 14), "Bratislava", null, null);
    }
    
    @Before
    public void setUp() throws SQLException {
        manager = new RelationCatalogImpl();
    }
    
    @After
    public void tearDown() {
    }

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
    
    @Test(expected = IllegalArgumentException.class)
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
    
    private static Person newPerson(Long id, String name, GenderType type, LocalDate dateOfBirth,
            String placeOfBirth, LocalDate dateOfDeath, String placeOfDeath) {
        Person p = new Person();
        p.setId(id);
        p.setName(name);
        p.setGender(type);
        p.setDateOfBirth(dateOfBirth);
        p.setPlaceOfBirth(placeOfBirth);
        p.setDateOfDeath(dateOfDeath);
        p.setPlaceOfDeath(placeOfDeath);
        return p;
    }
}
