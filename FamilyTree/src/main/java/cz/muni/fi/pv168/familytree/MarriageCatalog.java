package cz.muni.fi.pv168.familytree;

import java.util.List;

/**
 * 
 * 
 */
public interface MarriageCatalog {
    
    void createMarriage(Marriage marriage);
    
    void updateMarriage(Marriage marriage);
    
    void deleteMarriage(Marriage marriage);
    
    Marriage findMarriageById(Long id);
    
    Marriage findCurrentMarriage(Person p);
    
    List<Marriage> findMarriagesOfPerson(Person p);
    
    List<Marriage> findAllMarriages();
}
