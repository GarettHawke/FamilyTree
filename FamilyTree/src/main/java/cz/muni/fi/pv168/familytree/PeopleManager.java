package cz.muni.fi.pv168.familytree;

import java.util.List;

/**
 * 
 * 
 */
public interface PeopleManager {
    
    void createPerson(Person p);
    
    void updatePerson(Person p);
    
    void deletePerson(Person p);
    
    Person findPersonById(Long id);
    
    List<Person> findAllPeople();
    
    void deleteAll();
}
