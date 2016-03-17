package cz.muni.fi.pv168.familytree;

import java.time.LocalDate;
import java.util.Objects;

/**
 * 
 * 
 */
public class Marriage {
    
    private Long id;
    private LocalDate from, to;
    private Person spouse1, spouse2;

    public Marriage() {
    }
    
    public Marriage(Person spouse1, Person spouse2, LocalDate from, LocalDate to) {
        this.from = from;
        this.to = to;
        this.spouse1 = spouse1;
        this.spouse2 = spouse2;
    }
    
    public Marriage(Person spouse1, Person spouse2, LocalDate from) {
        this(spouse1, spouse2, from, null);
    }

    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getFrom() {
        return from;
    }

    public void setFrom(LocalDate from) {
        this.from = from;
    }

    public LocalDate getTo() {
        return to;
    }

    public void setTo(LocalDate to) {
        this.to = to;
    }

    public Person getSpouse1() {
        return spouse1;
    }

    public void setSpouse1(Person spouse1) {
        this.spouse1 = spouse1;
    }

    public Person getSpouse2() {
        return spouse2;
    }

    public void setSpouse2(Person spouse2) {
        this.spouse2 = spouse2;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 43 * hash + Objects.hashCode(this.id);
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
        final Marriage other = (Marriage) obj;
        return Objects.equals(this.id, other.id);
    }
}
