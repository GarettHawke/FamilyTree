package cz.muni.fi.pv168.familytree;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;


public class PeopleManagerImpl implements PeopleManager {
    
    private final DataSource dataSource;

    public PeopleManagerImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    
    public static void validate(Person p) throws IllegalArgumentException {
        if (p == null) {
            throw new IllegalArgumentException("Person was null");
        }
        if (p.getName() == null) {
            throw new IllegalArgumentException("Person name was null");
        }
        if (p.getName().length() == 0) {
            throw new IllegalArgumentException("Person name was empty");
        }
        if (p.getGender() == null) {
            throw new IllegalArgumentException("Person gender was null");
        }
        if (p.getPlaceOfBirth() == null) {
            throw new IllegalArgumentException("Person placeOfBirth was null");
        }
        if (p.getPlaceOfBirth().length() == 0) {
            throw new IllegalArgumentException("Person placeOfBirth was empty");
        }
        if (p.getDateOfBirth() == null) {
            throw new IllegalArgumentException("Person dateOfBirth was null");
        }
        if ((p.getDateOfDeath() != null) != (p.getPlaceOfDeath() != null)) {
            throw new IllegalArgumentException("Both death entries must be filled");
        }
        if (p.getPlaceOfDeath() != null && p.getPlaceOfDeath().length() == 0) {
            throw new IllegalArgumentException("Person placeOfDeath was empty");
        }
        if (p.getDateOfDeath() != null && p.getDateOfBirth().isAfter(p.getDateOfDeath())) {
            throw new IllegalArgumentException("Person was born after death");
        }
    }

    @Override
    public void createPerson(Person p) throws ServiceFailureException {
        validate(p);
        if (p.getId() != null) {
            throw new IllegalArgumentException("Person id is already set");
        }
        
        try (
            Connection connection = dataSource.getConnection();
            PreparedStatement st = connection.prepareStatement(
                    "INSERT INTO PEOPLE (name,gender,birthDate,birthPlace,"
                            + "deathDate,deathPlace) VALUES (?,?,?,?,?,?)",
                    Statement.RETURN_GENERATED_KEYS)) {
            st.setString(1, p.getName());
            st.setString(2, p.getGender().toString());
            st.setDate(3, java.sql.Date.valueOf(p.getDateOfBirth()));
            st.setString(4, p.getPlaceOfBirth());
            if(p.getDateOfDeath() != null)
                st.setDate(5, java.sql.Date.valueOf(p.getDateOfDeath()));
            else
                st.setDate(5, null);
            st.setString(6, p.getPlaceOfDeath());
            
            int addedRows = st.executeUpdate();
            if (addedRows != 1) {
                throw new ServiceFailureException("Internal Error: More rows ("
                        + addedRows + ") inserted when trying to insert person " + p);
            }
            
            ResultSet keyRS = st.getGeneratedKeys();
            p.setId(getKey(keyRS, p));
        
        } catch (SQLException ex) {
            throw new ServiceFailureException(
                    "Error when creating person " + p, ex);
        }
        
    }
    
    private Long getKey(ResultSet keyRS, Person p) throws ServiceFailureException, SQLException {
        if (keyRS.next()) {
            if (keyRS.getMetaData().getColumnCount() != 1) {
                throw new ServiceFailureException("Internal Error: Generated key"
                        + "retrieving failed when trying to insert person " + p
                        + " - wrong key fields count: " + keyRS.getMetaData().getColumnCount());
            }
            Long result = keyRS.getLong(1);
            if (keyRS.next()) {
                throw new ServiceFailureException("Internal Error: Generated key"
                        + "retrieving failed when trying to insert person " + p
                        + " - more keys found");
            }
            return result;
        } else {
            throw new ServiceFailureException("Internal Error: Generated key"
                    + "retrieving failed when trying to insert person " + p
                    + " - no key found");
        }
    }
    
    @Override
    public void updatePerson(Person p) throws ServiceFailureException {
        validate(p);
        if (p.getId() == null) {
            throw new IllegalArgumentException("Person id is null");
        }
        
        try (
            Connection connection = dataSource.getConnection();
            PreparedStatement st = connection.prepareStatement(
                "UPDATE PEOPLE SET name = ?, gender = ?, birthDate = ?, "
                        + "birthPlace = ?, deathDate = ?, deathPlace = ? WHERE id = ?")) {
            st.setString(1, p.getName());
            st.setString(2, p.getGender().toString());
            st.setDate(3, java.sql.Date.valueOf(p.getDateOfBirth()));
            st.setString(4, p.getPlaceOfBirth());
            if(p.getDateOfDeath() != null)
                st.setDate(5, java.sql.Date.valueOf(p.getDateOfDeath()));
            else
                st.setDate(5, null);
            st.setString(6, p.getPlaceOfDeath());
            st.setLong(7, p.getId());
            
            int count = st.executeUpdate();
            if (count == 0) {
                throw new EntityNotFoundException("Person " + p + " was not found in database!");
            } else if (count != 1) {
                throw new ServiceFailureException("Invalid updated rows count detected"
                        + "(one row should be updated): " + count);
            }
        } catch (SQLException ex) {
            throw new ServiceFailureException(
                    "Error when updating person " + p, ex);
        }
    }

    @Override
    public void deletePerson(Person p) throws ServiceFailureException {
        if (p == null) {
            throw new IllegalArgumentException("Person is null");
        }
        if (p.getId() == null) {
            throw new IllegalArgumentException("Person id is null");
        }
        
        try (
            Connection connection = dataSource.getConnection();
            PreparedStatement st = connection.prepareStatement(
                    "DELETE FROM PEOPLE WHERE id = ?")) {
            st.setLong(1, p.getId());
            
            int count = st.executeUpdate();
            if (count == 0) {
                throw new EntityNotFoundException("Person " + p + " was not found in database!");
            } else if (count != 1) {
                throw new ServiceFailureException("Invalid deleted rows count detected (one row should be deleted): " + count);
            }
        } catch (SQLException ex) {
            throw new ServiceFailureException(
                    "Error when deleting Person " + p, ex);
        }
        
    }

    @Override
    public Person findPersonById(Long id) throws ServiceFailureException {
        try (
            Connection connection = dataSource.getConnection();
            PreparedStatement st = connection.prepareStatement(
                    "SELECT id,name,gender,birthDate,birthPlace,"
                            + "deathDate,deathPlace FROM PEOPLE WHERE id = ?")) {
            st.setLong(1, id);
            
            ResultSet rs = st.executeQuery();
            
            if (rs.next()) {
                Person p = resultSetToPerson(rs);

                if (rs.next()) {
                    throw new ServiceFailureException(
                            "Internal error: More entities with the same id found "
                            + "(source id: " + id + ", found " + p + " and " + resultSetToPerson(rs));
                }
                return p;
            } else {
                return null;
            }
        } catch (SQLException ex) {
            throw new ServiceFailureException(
                    "Error when retrieving Person with id " + id, ex);
        }
    }
    
    public static Person resultSetToPerson(ResultSet rs) throws SQLException {
        Person ret = new Person(rs.getString("name"), GenderType.valueOf(rs.getString("gender")), 
                rs.getString("birthPlace"), rs.getDate("birthDate").toLocalDate(),
                rs.getString("deathPlace"), (rs.getDate("deathDate") != null) ? rs.getDate("deathDate").toLocalDate() : null);
        ret.setId(rs.getLong("id"));
        return ret;
    }

    @Override
    public List<Person> findAllPeople() {
        try (
            Connection connection = dataSource.getConnection();
            PreparedStatement st = connection.prepareStatement(
                    "SELECT id,name,gender,birthDate,birthPlace,"
                            + "deathDate,deathPlace FROM PEOPLE")) {

            ResultSet rs = st.executeQuery();

            List<Person> result = new ArrayList<>();
            while (rs.next()) {
                result.add(resultSetToPerson(rs));
            }
            return result;

        } catch (SQLException ex) {
            throw new ServiceFailureException(
                    "Error when retrieving all People", ex);
        }
    }
    
    @Override
    public void deleteAll() {
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement st = connection.prepareStatement(
                        "DELETE FROM PEOPLE")) {
            st.executeUpdate();
        } catch(SQLException ex) {
            throw new ServiceFailureException("Error when deleting all people");
        }
    }
}
