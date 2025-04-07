package repository;

import java.sql.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

public abstract class AbstractDBRepository<K, E> {
    private final String url;
    private final String username;
    private final String password;

    public AbstractDBRepository(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    protected abstract String getTableName();

    protected abstract E createEntityFromResultSet(ResultSet resultSet) throws SQLException;

    protected abstract void setStatementParameters(PreparedStatement statement, K key) throws SQLException;

    public E findOne(K key) {
        String query = "SELECT * FROM " + getTableName() + " WHERE id = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(query)
        ) {
            setStatementParameters(statement, key);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return createEntityFromResultSet(resultSet);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null; // Return null if not found
    }

    public Iterable<E> findAll() {
        Set<E> entities = new HashSet<>();
        String query = "SELECT * FROM " + getTableName();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()
        ) {
            while (resultSet.next()) {
                entities.add(createEntityFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return entities;
    }

    public E save(E entity) {
        String query = getSaveQuery(); // Subclasses will provide the specific query
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(query)
        ) {
            setSaveStatementParameters(statement, entity);
            statement.executeUpdate();
            return entity;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    protected abstract String getSaveQuery();

    protected abstract void setSaveStatementParameters(PreparedStatement statement, E entity) throws SQLException;

    public E delete(K key) {
        String query = "DELETE FROM " + getTableName() + " WHERE id = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(query)
        ) {
            setStatementParameters(statement, key);
            E toBeRemovedEntity = findOne(key);
            statement.executeUpdate();
            return toBeRemovedEntity;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public E update(E entity) {
        String query = "UPDATE " + getTableName() + " SET ... WHERE id = ?"; // Adjust query as needed
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(query)
        ) {
            setUpdateStatementParameters(statement, entity); // Implement this method in subclasses
            statement.executeUpdate();
            return entity;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    protected abstract void setUpdateStatementParameters(PreparedStatement statement, E entity) throws SQLException;

    public int size() {
        String query = "SELECT COUNT(*) FROM " + getTableName();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()
        ) {
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return 0;
    }
}
