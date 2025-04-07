package domain;

import java.time.LocalDate;
import java.util.Objects;


public class Prietenie extends Entity<Tuple<Long, Long>> {

    LocalDate date;
    private Long id1;
    private Long id2;


    public Prietenie(Long id1, Long id2, LocalDate data) {
        this.id1 = id1;
        this.id2 = id2;
        this.date = data;
    }

    /**
     * @return the date when the friendship was created
     */
    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Long getId1() {
        return id1;
    }

    public void setId1(Long id1) {
        this.id1 = id1;
    }

    public Long getId2() {
        return id2;
    }

    public void setId2(Long id2) {
        this.id2 = id2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Prietenie prietenie = (Prietenie) o;
        return Objects.equals(getDate(), prietenie.getDate()) && Objects.equals(getId1(), prietenie.getId1()) && Objects.equals(getId2(), prietenie.getId2());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getDate(), getId1(), getId2());
    }

    @Override
    public String toString() {
        return "Prietenie{" +
                "date=" + date +
                ", id1=" + id1 +
                ", id2=" + id2 +
                '}';
    }
}