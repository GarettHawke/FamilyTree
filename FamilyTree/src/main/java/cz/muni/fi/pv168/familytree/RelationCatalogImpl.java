package cz.muni.fi.pv168.familytree;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;


public class RelationCatalogImpl implements RelationCatalog {
    
    private static final long ACCEPTED_AGE_FOR_PARENTS = 5;
    private final DataSource dataSource;
    private PeopleManager manager;
    
    public RelationCatalogImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    
    @Override
    public void setPeopleManager(PeopleManager manager) {
        this.manager = manager;
    }

    @Override
    public List<Person> findParents(Person p) {
        validate(p);
        
        try(
           Connection connection = dataSource.getConnection();
           PreparedStatement st = connection.prepareStatement(
                   "SELECT parent_id FROM RELATIONS WHERE child_id = ?")) {
            st.setLong(1, p.getId());
            
            ResultSet rs = st.executeQuery();
            List<Person> parents = new ArrayList<>();
            if (rs.next()) {
                parents.add(manager.findPersonById(rs.getLong("parent_id")));
                if (rs.next()) {
                    parents.add(manager.findPersonById(rs.getLong("parent_id")));
                    if(rs.next())
                        throw new ServiceFailureException(
                                "Internal error: More than 2 parents found for person " + p);
                }
            }
            return parents;
        } catch (SQLException ex) {
            throw new ServiceFailureException("Error when getting parents of " + p, ex);
        }
    }

    @Override
    public List<Person> findChildren(Person p) {
        validate(p);
        
        try(
            Connection connection = dataSource.getConnection();
            PreparedStatement st = connection.prepareStatement(
                    "SELECT child_id FROM RELATIONS WHERE parent_id = ?")) {
            st.setLong(1, p.getId());
            
            ResultSet rs = st.executeQuery();
            List<Person> children = new ArrayList<>();
            while(rs.next()) {
                children.add(manager.findPersonById(rs.getLong("child_id")));
            }
            return children;
        } catch (SQLException ex) {
            throw new ServiceFailureException("Error when getting children of " + p, ex);
        }
    }
    
    @Override
    public Map<Person, List<Person>> findAllRelation() {
        
        
        try (
            Connection connection = dataSource.getConnection();
            PreparedStatement st = connection.prepareStatement(
                    "SELECT parent_id, child_id FROM RELATIONS")) {

            ResultSet rs = st.executeQuery();

            HashMap<Person, List<Person>> relations = new HashMap<>();
            while (rs.next()) {
                Person parent = manager.findPersonById(rs.getLong("parent_id"));
                if(!relations.containsKey(parent)) {
                    relations.put(parent, new ArrayList<>());
                }
                relations.getOrDefault(parent, null).add(manager.findPersonById(rs.getLong("child_id")));
            }
            
            return relations;
        } catch (SQLException ex) {
            throw new ServiceFailureException(
                    "Error when retrieving all People", ex);
        }
    }

    @Override
    public void makeRelation(Person parent, Person child) {
        validate(parent);
        validate(child);
        validate(parent, child);
        
        try(
                Connection connection = dataSource.getConnection();
                PreparedStatement st = connection.prepareStatement(
                        "INSERT INTO RELATIONS (parent_id,child_id) VALUES (?,?)")) {
            st.setLong(1, parent.getId());
            st.setLong(2, child.getId());
            int rows = st.executeUpdate();
            if(rows != 1) {
                throw new ServiceFailureException("Internal Error: More rows ("
                        + rows + ") inserted when trying to insert relation " + parent + " and " + child);
            }
        } catch (SQLException ex) {
            throw new ServiceFailureException(
                    "Error when creating relation " + parent + " and " + child, ex);
        }
    }

    @Override
    public void deleteRelation(Person parent, Person child) {
        validate(parent);
        validate(child);
        
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement st = connection.prepareStatement(
                        "DELETE FROM RELATIONS WHERE parent_id = ? AND child_id = ?")) {
            st.setLong(1, parent.getId());
            st.setLong(2, child.getId());
            
            int count = st.executeUpdate();
            if (count == 0) {
                throw new EntityNotFoundException(
                        "Relation " + parent + " and " + child + " was not found in database!");
            } else if (count != 1) {
                throw new ServiceFailureException(
                        "Invalid deleted rows count detected (one row should be deleted): " + count);
            }
        } catch (SQLException ex) {
            throw new ServiceFailureException(
                    "Error when deleting relation " + parent + " and " + child, ex);
        }
    }

    private void validate(Person parent, Person child) {
        if(parent.equals(child)) {
            throw new IllegalArgumentException("parent and child are the same person");
        }
        if(parent.getDateOfBirth().isAfter(child.getDateOfBirth())) {
            throw new IllegalArgumentException("parent is younger than child");
        }
        if(parent.getDateOfBirth().isAfter(child.getDateOfBirth().minusYears(ACCEPTED_AGE_FOR_PARENTS))) {
            throw new IllegalArgumentException("parent is too young");
        }
        List<Person> parents = findParents(child);
        if(parents.size() == 2) {
            throw new IllegalArgumentException("child has already 2 parents");
        }
        if(parents.contains(parent)) {
            throw new IllegalArgumentException("relation already exists");
        }
    }
    
    private void validate(Person person) {
        PeopleManagerImpl.validate(person);
        if(person.getId() == null) {
            throw new IllegalArgumentException("person id is null");
        }
    }
}
