package cz.muni.fi.pb168.familytree;

import java.time.LocalDate;
import java.util.Objects;

/**
 * 
 * 
 */
public class Person {
    
    private Long id;
    private String name, placeOfBirth, placeOfDeath;
    private GenderType gender;
    private LocalDate dateOfBirth, dateOfDeath;

    public Person() {
    }
    
    public Person(String name, GenderType gender, String placeOfBirth, LocalDate dateOfBirth, String placeOfDeath, LocalDate dateOfDeath) {
        this.name = name;
        this.placeOfBirth = placeOfBirth;
        this.placeOfDeath = placeOfDeath;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.dateOfDeath = dateOfDeath;
    }

    public Person(String name, GenderType gender, String placeOfBirth, LocalDate dateOfBirth) {
        this(name, gender, placeOfBirth, dateOfBirth, null, null);
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlaceOfBirth() {
        return placeOfBirth;
    }

    public void setPlaceOfBirth(String placeOfBirth) {
        this.placeOfBirth = placeOfBirth;
    }

    public String getPlaceOfDeath() {
        return placeOfDeath;
    }

    public void setPlaceOfDeath(String placeOfDeath) {
        this.placeOfDeath = placeOfDeath;
    }

    public GenderType getGender() {
        return gender;
    }

    public void setGender(GenderType gender) {
        this.gender = gender;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public LocalDate getDateOfDeath() {
        return dateOfDeath;
    }

    public void setDateOfDeath(LocalDate dateOfDeath) {
        this.dateOfDeath = dateOfDeath;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47 * hash + Objects.hashCode(this.id);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Person other = (Person) obj;
        return Objects.equals(this.id, other.id);
    }

    @Override
    public String toString() {
        return "Person{" + "id=" + id + 
                ", name=" + name + 
                ", placeOfBirth=" + placeOfBirth + 
                ", placeOfDeath=" + placeOfDeath + 
                ", gender=" + gender + 
                ", dateOfBirth=" + dateOfBirth + 
                ", dateOfDeath=" + dateOfDeath + '}';
    }
}
