package domain;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Utilizator extends Entity<Long> {
    private String firstName;
    private String lastName;
    private Set<Long> friendsIDS = new HashSet<>();
    private String email;
    private String password;


    public Utilizator(String firstName, String lastName, String email, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getEmail() {
        return email;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Set<Long> getFriends() {
        return friendsIDS;
    }

    @Override
    public String toString() {
        return "Utilizator{" + "id=" + getId() + '\'' +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", friends=" + friendsIDS +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Utilizator)) return false;
        Utilizator that = (Utilizator) o;
        return getFirstName().equals(that.getFirstName()) &&
                getLastName().equals(that.getLastName()) &&
                getFriends().equals(that.getFriends());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getFirstName(), getLastName(), getFriends());
    }

    public boolean addFriend(Long id) {
        boolean ok;
        ok = friendsIDS.add(id);
        if (!ok) {
            return false;
        }
        return true;
    }

    public boolean removeFriend(Long id) {
        boolean ok;
        ok = friendsIDS.remove(id);
        if (!ok) {
            return false;
        }
        return true;
    }
}