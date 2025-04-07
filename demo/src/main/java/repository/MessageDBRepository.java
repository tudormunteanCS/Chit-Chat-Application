package repository;

import domain.Message;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MessageDBRepository {
    private final String url;
    private final String username;
    private final String password;

    public MessageDBRepository(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }


    public ArrayList<Message> getAllMesagesOfUsers(String currentUserEmail, String userToChatEmail) {
        ArrayList<Message> messagesBetweenUsers = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            PreparedStatement statement = connection.prepareStatement("select mesaj_from,mesaj_to,mesaj from mesaje where (mesaj_from = ? and mesaj_to = ?) or (mesaj_from = ? and mesaj_to = ?)");
            statement.setString(1,currentUserEmail);
            statement.setString(2,userToChatEmail);
            statement.setString(3,userToChatEmail);
            statement.setString(4,currentUserEmail);
            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next())
            {
                String from = resultSet.getString("mesaj_from");
                String to = resultSet.getString("mesaj_to");
                String mesaj = resultSet.getString("mesaj");
                Message message = new Message(from,to,mesaj);
                messagesBetweenUsers.add(message);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return messagesBetweenUsers;
    }

    public void save(Message mesajNou) {
        try(Connection connection = DriverManager.getConnection(url,username,password)){
            PreparedStatement preparedStatement = connection.prepareStatement("insert into mesaje(mesaj_from,mesaj_to,mesaj) values (?, ?, ?)");
            preparedStatement.setString(1,mesajNou.getFrom());
            preparedStatement.setString(2,mesajNou.getTo());
            preparedStatement.setString(3,mesajNou.getMessage());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
