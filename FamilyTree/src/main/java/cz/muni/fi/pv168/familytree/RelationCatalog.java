package cz.muni.fi.pv168.familytree;

import java.util.List;

/**
 * 
 * 
 */
public interface RelationCatalog {
    
    List<Person> findParents(Person p);
    
    List<Person> findChildren(Person p);
    
    void makeRelation(Person parent, Person child);
    
    void deleteRelation(Person parent, Person child);
    
    public void setPeopleManager(PeopleManager manager);
}
