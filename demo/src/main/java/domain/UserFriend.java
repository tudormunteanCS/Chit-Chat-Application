package domain;

import java.time.LocalDate;

public class UserFriend {
    private String firstName;
    private String lastName;
    private String email;
    private LocalDate date;

    public UserFriend(String firstName, String lastName, String email,LocalDate date) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.date = date;
        this.email = email;
    }

    public UserFriend(String firstName, String lastName, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }


    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return firstName + " " + lastName ;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}
