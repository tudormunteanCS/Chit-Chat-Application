package repository;

import domain.RequestedFriend;
import domain.RequestingFriend;

import java.sql.*;
import java.util.ArrayList;

public class PendingFriendsDBRepository {
    private final String url;
    private final String username;
    private final String password;

    public PendingFriendsDBRepository(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    public void save(Long fromId, Long toId) {
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement preparedStatement = connection.prepareStatement("insert into cereri_de_prietenie(from_id,to_id,status) values(?, ?, ?)")
        ) {
            preparedStatement.setInt(1, Math.toIntExact(fromId));
            preparedStatement.setInt(2, Math.toIntExact(toId));
            preparedStatement.setString(3, "Pending");
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public ArrayList<RequestedFriend> findAllMyRequestedFriends(long myId) {
        ArrayList<RequestedFriend> requestedFriends = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement preparedStatement = connection.prepareStatement("select to_id,status from cereri_de_prietenie where from_id = ?")
        ) {
            preparedStatement.setInt(1,Math.toIntExact(myId));
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                int idOfRequestedFriend = resultSet.getInt("to_id");
                String status = resultSet.getString("status");
                PreparedStatement statement = connection.prepareStatement("select first_name,last_name,email from users where id = ?");
                statement.setInt(1, idOfRequestedFriend);
                ResultSet resultSetOfUser = statement.executeQuery();
                if (resultSetOfUser.next()) {
                    String firstName = resultSetOfUser.getString("first_name");
                    String lastName = resultSetOfUser.getString("last_name");
                    String email = resultSetOfUser.getString("email");
                    RequestedFriend requestedFriend = new RequestedFriend(firstName,lastName,email,status);
                    requestedFriends.add(requestedFriend);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return requestedFriends;
    }

    public ArrayList<RequestingFriend> findAllMyRequestingFriends(long myId) {
        ArrayList<RequestingFriend> requestingFriends = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement preparedStatement = connection.prepareStatement("select from_id,status from cereri_de_prietenie where to_id = ? and status = 'Pending'")
        ) {
            preparedStatement.setInt(1,Math.toIntExact(myId));
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                int idOfRequestingFriend = resultSet.getInt("from_id");
                PreparedStatement statement = connection.prepareStatement("select first_name,last_name,email from users where id = ?");
                statement.setInt(1, idOfRequestingFriend);
                ResultSet resultSetOfUser = statement.executeQuery();
                if (resultSetOfUser.next()) {
                    String firstName = resultSetOfUser.getString("first_name");
                    String lastName = resultSetOfUser.getString("last_name");
                    String email = resultSetOfUser.getString("email");
                    RequestingFriend requestingFriend = new RequestingFriend(firstName,lastName,email);
                    requestingFriends.add(requestingFriend);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return requestingFriends;
    }

    public void updateStatusToApproved(long idFriendReqGetter, long idFriendReqSender) {
        try(Connection connection = DriverManager.getConnection(url,username,password)) {
            PreparedStatement statement = connection.prepareStatement("update cereri_de_prietenie set status = 'Approved' where from_id = ? and to_id = ?");
            statement.setInt(1,Math.toIntExact(idFriendReqSender));
            statement.setInt(2,Math.toIntExact(idFriendReqGetter));
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateStatusToDeclined(long idFriendReqGetter, long idFriendReqSender) {
        try(Connection connection = DriverManager.getConnection(url,username,password)) {
            PreparedStatement statement = connection.prepareStatement("update cereri_de_prietenie set status = 'Declined' where from_id = ? and to_id = ?");
            statement.setInt(1,Math.toIntExact(idFriendReqSender));
            statement.setInt(2,Math.toIntExact(idFriendReqGetter));
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
