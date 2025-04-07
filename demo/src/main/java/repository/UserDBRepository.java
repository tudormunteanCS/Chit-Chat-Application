package repository;


import domain.UserFriend;
import domain.Utilizator;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class UserDBRepository implements Repository<Long, Utilizator> {
    private final String url;
    private final String username;
    private final String password;

    public UserDBRepository(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    @Override
    public Optional<Utilizator> findOne(Long id) {
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("select * from users where id = ?");
        ) {
            statement.setInt(1, Math.toIntExact(id));
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                String email = resultSet.getString("email");
                String password = resultSet.getString("password");
                Utilizator user = new Utilizator(firstName, lastName, email, password);
                user.setId(id);
                return Optional.ofNullable(user);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    @Override
    public Iterable<Utilizator> findAll() {
        Set<Utilizator> users = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("select * from users");
             ResultSet resultSet = statement.executeQuery();
        ) {
            while (resultSet.next()) {
                Long id = resultSet.getLong(1);
                String firstName = resultSet.getString(2);
                String lastName = resultSet.getString(3);
                String email = resultSet.getString("email");
                String password = resultSet.getString("password");
                Utilizator user = new Utilizator(firstName, lastName, email, password);
                user.setId(id);
                users.add(user);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return users;
    }



    @Override
    public Optional<Utilizator> save(Utilizator entity) {
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("INSERT INTO users(first_name,last_name,email,password) values (?,?,?,?)");

        ) {
            statement.setString(1, entity.getFirstName());
            statement.setString(2, entity.getLastName());
            statement.setString(3, entity.getEmail());
            statement.setString(4, entity.getPassword());
            statement.executeUpdate();
            return Optional.empty(); //null if saved
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public Optional<Utilizator> delete(Long aLong) {
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("delete from users where id = ?")) {
            statement.setInt(1, aLong.intValue());
            Optional<Utilizator> toBeRemovedUser = findOne(Long.valueOf(aLong));
            int deleted = statement.executeUpdate();
            if (deleted == 0)
                return Optional.empty();
            else
                return toBeRemovedUser;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public Utilizator update(Utilizator entity) {
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("update users set first_name = ?, last_name = ?, email = ? where id = ? ");
        ) {
            statement.setString(1, entity.getFirstName());
            statement.setString(2, entity.getLastName());
            statement.setString(3, entity.getEmail());
            statement.setInt(4, entity.getId().intValue());
            statement.executeUpdate();
            return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int size() {
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("select count (*) from users");
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

    public Optional<Utilizator> findOneByEmail(String email) {
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("select * from users where email = ?");) {
            statement.setString(1, email);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                String password = resultSet.getString("password");
                Utilizator user = new Utilizator(firstName, lastName, email, password);
                return Optional.of(user);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    public ArrayList<UserFriend> findAllWithoutCurrentUser(String currentUserEmail) {
        ArrayList<UserFriend> listaUseri = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
        ) {
            PreparedStatement statement = connection.prepareStatement("select first_name,last_name,email from users where email <>?");
            statement.setString(1, currentUserEmail);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                String email = resultSet.getString("email");
                UserFriend userFirstNameLastName = new UserFriend(firstName, lastName, email);
                listaUseri.add(userFirstNameLastName);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return listaUseri;
    }
    public ArrayList<UserFriend> getUsersPaginatedWithoutCurrentUSer(String currentUserEmail,int numberOfRows, int currentPageIndex) {
        ArrayList<UserFriend> listaUseri = new ArrayList<>();
        try
        {
            Connection connection = DriverManager.getConnection(url,username,password);
            int offset = currentPageIndex*numberOfRows;
            PreparedStatement statement = connection.prepareStatement("SELECT first_name, last_name, email FROM users WHERE email <> ? LIMIT " + numberOfRows + " OFFSET " + offset);
            statement.setString(1,currentUserEmail);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                String email = resultSet.getString("email");
                UserFriend userFirstNameLastName = new UserFriend(firstName, lastName, email);
                listaUseri.add(userFirstNameLastName);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return listaUseri;
    }


    public Long findIdByEmail(String email) {
        try (Connection connection = DriverManager.getConnection(url, username, password)
        ) {
            PreparedStatement statement = connection.prepareStatement("select id from users where email = ?");
            statement.setString(1, email);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getLong("id");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return (long) -1;
    }

}
