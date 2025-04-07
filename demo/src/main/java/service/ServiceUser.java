package service;

import domain.Graph;
import domain.UserFriend;
import domain.Utilizator;
import repository.UserDBRepository;
import validators.UtilizatorValidator;

import java.util.ArrayList;
import java.util.Optional;

public class ServiceUser {
    UserDBRepository repo_db = new UserDBRepository("jdbc:postgresql://localhost:5432/dbMAP", "postgres", "telefon99");
    UtilizatorValidator validator = new UtilizatorValidator();

    public void addUser(String firstName, String lastName,String email,String password) {

        Utilizator user = new Utilizator(firstName, lastName,email,password);

        validator.validate(user);

        repo_db.save(user);
    }

    public void deleteUser(Long id) {
        repo_db.delete(id);
    }

    public Iterable<Utilizator> getAll() {
        return repo_db.findAll();
    }


    public int numarComunitati() {
        Graph graph = new Graph(repo_db.size());
        repo_db.findAll().forEach(user -> user.getFriends().forEach(id -> graph.addEdge(user.getId().intValue(), (long) id.intValue())));
        return graph.countConnectedComponents();
    }

    public void updateUser(Utilizator toBeUpdated) {
        validator.validate(toBeUpdated);
        repo_db.update(toBeUpdated);
    }

    public Optional<Utilizator> getUserByEmail(String email) {
        return repo_db.findOneByEmail(email);
    }

    public ArrayList<UserFriend> getNamesOfOtherUsers(String currentUserEmail) {
        return repo_db.findAllWithoutCurrentUser(currentUserEmail);
    }
    public Long getIdByEmail(String email)
    {
        return repo_db.findIdByEmail(email);
    }

    public ArrayList<UserFriend> getUsersPaginated(String currentUserEmail, int numberOfUsersToBeShown, int currentPageIndex) {
        return repo_db.getUsersPaginatedWithoutCurrentUSer(currentUserEmail,numberOfUsersToBeShown,currentPageIndex);
    }

    public int getSizeOfUsers() {
        return repo_db.size();
    }
}
