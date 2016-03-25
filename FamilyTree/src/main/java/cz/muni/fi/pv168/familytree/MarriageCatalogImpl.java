package cz.muni.fi.pv168.familytree;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;
import javax.sql.DataSource;


public class MarriageCatalogImpl implements MarriageCatalog {
    
    private static final long ACCEPTED_AGE_FOR_MARRIAGE = 16;
    private final DataSource dataSource;
    
    public MarriageCatalogImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void createMarriage(Marriage marriage) {
        validate(marriage);
        if(marriage.getId() != null) {
            throw new IllegalArgumentException("Marriage id is already set.");
        }
        marriageSetTo(marriage);
        
        try(
                Connection connection = dataSource.getConnection();
                PreparedStatement st = connection.prepareStatement(
                    "INSERT INTO PEOPLE (from,to,spouse1_id,spouse2_id)"
                            + " VALUES (?,?,?,?)",
                    Statement.RETURN_GENERATED_KEYS)) {
            st.setDate(1, Date.valueOf(marriage.getFrom()));
            if(marriage.getTo() != null)
                st.setDate(2, Date.valueOf(marriage.getTo()));
            else
                st.setDate(2, null);
            st.setLong(3, marriage.getSpouse1().getId());
            st.setLong(4, marriage.getSpouse2().getId());
            
            int addedRows = st.executeUpdate();
            if (addedRows != 1) {
                throw new ServiceFailureException("Internal Error: More rows ("
                        + addedRows + ") inserted when trying to insert marriage " + marriage);
            }
            
            ResultSet keyRS = st.getGeneratedKeys();
            marriage.setId(getKey(keyRS, marriage));
        } catch (SQLException ex) {
            throw new ServiceFailureException(
                    "Error when creating marriage " + marriage, ex);
        }
    }

    @Override
    public void updateMarriage(Marriage marriage) {
        validate(marriage);
        if(marriage.getId() == null) {
            throw new IllegalArgumentException("Marriage id is null.");
        }
        marriageSetTo(marriage);
        try(
                Connection connection = dataSource.getConnection();
                PreparedStatement st = connection.prepareStatement(
                        "UPDATE MARRIAGES SET from = ?, to = ?, spouse1_id = ?, "
                        + "spouse2_id = ? WHERE marriage_id = ?")) {
            st.setDate(1, Date.valueOf(marriage.getFrom()));
            if(marriage.getTo() != null)
                st.setDate(2, Date.valueOf(marriage.getTo()));
            else
                st.setDate(2, null);
            st.setLong(3, marriage.getSpouse1().getId());
            st.setLong(4, marriage.getSpouse2().getId());
            st.setLong(5, marriage.getId());
            
            int count = st.executeUpdate();
            if (count == 0) {
                throw new EntityNotFoundException(
                        "Marriage " + marriage + " was not found in database!");
            } else if (count != 1) {
                throw new ServiceFailureException("Invalid updated rows count detected"
                        + "(one row should be updated): " + count);
            }
        } catch (SQLException ex) {
            throw new ServiceFailureException(
                    "Error when updating marriage " + marriage, ex);
        }
    }

    @Override
    public void deleteMarriage(Marriage marriage) {
        if(marriage == null) {
            throw new IllegalArgumentException("Marriage is null.");
        }
        if(marriage.getId() == null) {
            throw new IllegalArgumentException("Marriage id is null.");
        }
        
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement st = connection.prepareStatement(
                        "DELETE FROM MARRIAGES WHERE marriage_id = ?")) {
            st.setLong(1, marriage.getId());
            
            int count = st.executeUpdate();
            if (count == 0) {
                throw new EntityNotFoundException(
                        "Marriage " + marriage + " was not found in database!");
            } else if (count != 1) {
                throw new ServiceFailureException("Invalid deleted rows count detected"
                        +"(one row should be deleted): " + count);
            }
        } catch (SQLException ex) {
            throw new ServiceFailureException(
                    "Error when deleting marriage " + marriage, ex);
        }
    }
    
    @Override
    public Marriage findMarriageById(Long id) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Marriage findCurrentMarriage(Person p) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<Marriage> findMarriagesOfPerson(Person p) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<Marriage> findAllMarriages() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private void validate(Person person) {
        PeopleManagerImpl.validate(person);
        if(person.getId() == null) {
            throw new IllegalArgumentException("Person id is null.");
        }
    }

    private void validate(Marriage marriage) {
        validate(marriage.getSpouse1());
        validate(marriage.getSpouse2());
        if(marriage == null) {
            throw new IllegalArgumentException("Marriage is null.");
        }
        if(marriage.getFrom() == null) {
            throw new IllegalArgumentException("Marriage date_from is null.");
        }
        
        if(marriage.getSpouse1().equals(marriage.getSpouse2())) {
            throw new IllegalArgumentException("Both spouses are the same person.");
        }
        
        if(marriage.getSpouse1().getDateOfBirth().isAfter(marriage.getFrom().minusYears(ACCEPTED_AGE_FOR_MARRIAGE))) {
            throw new IllegalArgumentException("Spouse1 is too young to get married.");
        }
        if(marriage.getSpouse2().getDateOfBirth().isAfter(marriage.getFrom().minusYears(ACCEPTED_AGE_FOR_MARRIAGE))) {
            throw new IllegalArgumentException("Spouse2 is too young to get married.");
        }
        
        if(marriage.getSpouse1().getDateOfDeath() != null
                && marriage.getSpouse1().getDateOfDeath().isBefore(marriage.getFrom())) {
            throw new IllegalArgumentException("Spouse1 is dead.");
        }
        if(marriage.getSpouse2().getDateOfDeath() != null
                && marriage.getSpouse2().getDateOfDeath().isBefore(marriage.getFrom())) {
            throw new IllegalArgumentException("Spouse2 is dead.");
        }
    }

    private void marriageSetTo(Marriage marriage) {
        LocalDate to = marriage.getSpouse1().getDateOfDeath();
        if(marriage.getSpouse2().getDateOfDeath() != null) {
            if(to == null || to.isAfter(marriage.getSpouse2().getDateOfDeath())) {
                to = marriage.getSpouse2().getDateOfDeath();
            }
        }
        if(to != null &&
                ((marriage.getTo() == null)
                || (marriage.getTo() != null && marriage.getTo().isAfter(to)))) {
            marriage.setTo(to);
        }
    }

    private Long getKey(ResultSet keyRS, Marriage marriage) throws SQLException {
        if (keyRS.next()) {
            if (keyRS.getMetaData().getColumnCount() != 1) {
                throw new ServiceFailureException("Internal Error: Generated key "
                        + "retrieving failed when trying to insert marriage "
                        + marriage + " with " + marriage.getSpouse1() + " and " + marriage.getSpouse2()
                        + " - wrong key fields count: " + keyRS.getMetaData().getColumnCount());
            }
            Long result = keyRS.getLong(1);
            if (keyRS.next()) {
                throw new ServiceFailureException("Internal Error: Generated key "
                        + "retrieving failed when trying to insert marriage "
                        + marriage + " with " + marriage.getSpouse1() + " and " + marriage.getSpouse2()
                        + " - more keys found");
            }
            return result;
        } else {
            throw new ServiceFailureException("Internal Error: Generated key "
                    + "retrieving failed when trying to insert marriage "
                        + marriage + " with " + marriage.getSpouse1() + " and " + marriage.getSpouse2()
                    + " - no key found");
        }
    }
}
