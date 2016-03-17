package cz.muni.fi.pv168.familytree;

import java.util.List;

/**
 * 
 * 
 */
public interface MarriageCatalog {
    
    void createMarriage(Marriage marriage, Person spouse1, Person spouse2);
    
    void updateMarriage(Long id);
    
    void deleteMarriage(Long id);
    
    Marriage findMarriageById(Long id);
    
    Marriage findCurrentMarriage(Person p);
    
    List<Marriage> findMarriagesOfPerson(Person p);
    
    List<Marriage> findAllMarriages();
}
