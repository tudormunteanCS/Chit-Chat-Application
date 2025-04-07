package service;

import domain.*;

import repository.FriendDBRepository;
import repository.UserDBRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class ServiceFriend {

    FriendDBRepository repo_db = new FriendDBRepository("jdbc:postgresql://localhost:5432/dbMAP", "postgres", "telefon99");
    UserDBRepository repoUserDb = new UserDBRepository("jdbc:postgresql://localhost:5432/dbMAP", "postgres", "telefon99");

    public void addFriendship(Long id1, Long id2) {
        if (id1.equals(id2))
            throw new IllegalArgumentException("ids must be different");
        LocalDate datejava = LocalDate.now();
        Prietenie prietenie = new Prietenie(id1, id2, datejava);
        repo_db.save(prietenie);

    }
    public void removeFriendship(Long id1, Long id2) {
        Tuple<Long,Long> tuple = new Tuple<>(id1,id2);
        repo_db.delete(tuple);

    }




//    private boolean isSameMonth(LocalDate dataPrietenie, String luna) {
//        // Convertim luna din format String la tipul enum Month
//        int lunaCautata = Integer.parseInt(luna);
//
//        // Verificăm dacă dataPrietenie aparține aceleiași luni cu luna specificată
//        return dataPrietenie.getMonthValue() == lunaCautata;
//    }


//    public ArrayList<UsersShown> getFriendsFromMonthOfUser(Long id, String luna) {
//        Iterable<Prietenie> prietenii = repo_db.findAll();
//        List<Prietenie> prieteniiList = StreamSupport.stream(prietenii.spliterator(), false)
//                .filter(prietenie -> prietenie.getId1().equals(id) || prietenie.getId2().equals(id))
//                .filter(prietenie -> isSameMonth(prietenie.getDate(), luna))
//                .collect(Collectors.toList());
//
//        //am creeat o lista de prieteni specifici
//        //acum trebuie sa le caut numele si prenumele si afisez o lista finala
//        ArrayList<UsersShown> prieteniiFinale = new ArrayList<>();
//        for (Prietenie prietenie : prieteniiList) {
//            if (prietenie.getId1() != id) {
//                Optional<Utilizator> prieten = repoUserDb.findOne(prietenie.getId1());
//                UsersShown userDTO = new UsersShown(prieten.get().getFirstName(), prieten.get().getLastName(), prietenie.getDate());
//                prieteniiFinale.add(userDTO);
//            } else {
//                Optional<Utilizator> prieten = repoUserDb.findOne(prietenie.getId2());
//                UsersShown userDTO = new UsersShown(prieten.get().getFirstName(), prieten.get().getLastName(), prietenie.getDate());
//                prieteniiFinale.add(userDTO);
//            }
//        }
//        return prieteniiFinale;
//    }

    public Set<Long> getALlFriendsOfUser(Long id) {
        return null;


    }




    public ArrayList<UserFriend> getAllFriendsOfUserWithEmail(String currentUserEmail) {
        return repo_db.getAllFriendsOfUserWithEmail(currentUserEmail);
    }

    public Long getIdByEmail(String currentUserEmail) {
        return repo_db.getIdOfUserWithEmail(currentUserEmail);
    }
}
