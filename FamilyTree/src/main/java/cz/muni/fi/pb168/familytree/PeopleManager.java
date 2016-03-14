package cz.muni.fi.pb168.familytree;

import java.util.List;

/**
 * 
 * 
 */
public interface PeopleManager {
    
    void createPerson(Person p);
    
    void updatePerson(Person p);
    
    void deletePerson(Person p);
    
    Person findPeronById(Long id);
    
    List<Person> findAllPeople();
}
