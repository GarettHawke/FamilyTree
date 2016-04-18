package cz.muni.fi.pv168.familytree;

import java.util.List;
import java.util.Map;

/**
 * 
 * 
 */
public interface RelationCatalog {
    
    List<Person> findParents(Person p);
    
    List<Person> findChildren(Person p);
    
    Map<Person, List<Person>> findAllRelation();
    
    void makeRelation(Person parent, Person child);
    
    void deleteRelation(Person parent, Person child);
    
    public void setPeopleManager(PeopleManager manager);
}
