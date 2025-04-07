package repository;

import domain.*;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class FriendDBRepository implements Repository<Tuple<Long, Long>, Prietenie> {
    private final String url;
    private final String username;
    private final String password;

    public FriendDBRepository(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }


    @Override
    public Optional<Prietenie> findOne(Tuple<Long, Long> longLongTuple) {
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("select * from friends where id1 = ? and id2 = ?");
        ) {
            statement.setInt(1, Math.toIntExact(longLongTuple.getLeft()));
            statement.setInt(2, Math.toIntExact(longLongTuple.getRight()));
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                Long id1 = resultSet.getLong(1);
                Long id2 = resultSet.getLong(2);
                //salveaza si data
                Date datesql = resultSet.getDate(3);
                LocalDate datejava = datesql.toLocalDate();
                Prietenie prietenie = new Prietenie(id1, id2, datejava);
                return Optional.ofNullable(prietenie);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public Iterable<Prietenie> findAll() {
        Set<Prietenie> prietenii = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("select * from friends");
        ) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Long id1 = resultSet.getLong(1);
                Long id2 = resultSet.getLong(2);
                //salveaza si data
                Date datesql = resultSet.getDate(3);
                LocalDate datejava = datesql.toLocalDate();
                Prietenie prietenie = new Prietenie(id1, id2, datejava);
                prietenii.add(prietenie);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return prietenii;
    }

    @Override
    public Optional<Prietenie> save(Prietenie entity) {
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("INSERT INTO friends(id1,id2,dataprieteniei) values (?,?,?)");
        ) {
            statement.setInt(1, Math.toIntExact(entity.getId1()));
            statement.setInt(2, Math.toIntExact(entity.getId2()));
            statement.setDate(3, Date.valueOf(entity.getDate()));
            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                return Optional.of(entity);
            } else {
                return Optional.empty();
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Prietenie> delete(Tuple<Long, Long> longLongTuple) {
        try(Connection connection = DriverManager.getConnection(url,username,password);
        PreparedStatement statement = connection.prepareStatement("delete from friends where id1 =? and id2 =?")) {
            statement.setInt(1,Math.toIntExact(longLongTuple.getLeft()));
            statement.setInt(2,Math.toIntExact(longLongTuple.getRight()));
            int rowsAffected = statement.executeUpdate();
            if(rowsAffected == 0)
            {
                statement.setInt(2,Math.toIntExact(longLongTuple.getLeft()));
                statement.setInt(1,Math.toIntExact(longLongTuple.getRight()));
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    @Override
    public Prietenie update(Prietenie entity) {
        return null;
    }

    @Override
    public int size() {
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("select count * from friends");
        ) {
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return 0;
    }

    /*
    functie pentru selectarea id-ului din useri cu emailul curent
    functie pentru selectarea tuturor ids din friends diferit de id-ul dorit
    functie pentru parsarea tuturor id-urilor si transformarea acestora in numele,prenumele fiecarui user cu id-ul sau
     */

    public Long getIdOfUserWithEmail(String email) {
        Long id = (long) -1;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("select id from users where email = ?");
        ) {
            statement.setString(1,email);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                id = resultSet.getLong(1);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return id;
    }

    public ArrayList<Tuple<Long,Date>> getAllIdsOfFriends(Long id) {
        ArrayList<Tuple<Long,Date>> friendsIds = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("select * from friends where id1 = ? or id2 =?");
        ) {
            statement.setInt(1, Math.toIntExact(id));
            statement.setInt(2, Math.toIntExact(id));
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                if (resultSet.getLong("id1") != id) {
                    Tuple<Long,Date> tuple = new Tuple<>(resultSet.getLong("id1"),resultSet.getDate("dataprieteniei"));
                    friendsIds.add(tuple);
                }

                if (resultSet.getLong("id2") != id) {
                    Tuple<Long,Date> tuple = new Tuple<>(resultSet.getLong("id2"),resultSet.getDate("dataprieteniei"));
                    friendsIds.add(tuple);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return friendsIds;
    }

    public ArrayList<UserFriend> getAllFriendsOfUserWithEmail(String currentUserEmail) {
        ArrayList<UserFriend> prieteni = new ArrayList<>();
        Long idOfCurrentUser = getIdOfUserWithEmail(currentUserEmail);
        ArrayList<Tuple<Long,Date>> listaTupleIdAndDate = getAllIdsOfFriends(idOfCurrentUser);
        try (Connection connection = DriverManager.getConnection(url,username,password);
        ) {
            PreparedStatement statement = connection.prepareStatement("select first_name,last_name,email from users where id = ?");
            for(Tuple<Long,Date> tuplu : listaTupleIdAndDate)
            {
                statement.setInt(1,Math.toIntExact(tuplu.getLeft()));
                ResultSet resultSet = statement.executeQuery();
                if(resultSet.next())
                {
                    String firstName = resultSet.getString("first_name");
                    String lastName = resultSet.getString("last_name");
                    String email = resultSet.getString("email");
                    UserFriend userFriend = new UserFriend(firstName,lastName, email, tuplu.getRight().toLocalDate());
                    prieteni.add(userFriend);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return prieteni;
    }
}





