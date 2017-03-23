package me.Fupery.ArtMap.IO.Database;

import me.Fupery.ArtMap.IO.ErrorLogger;

import java.sql.*;

public class SQLiteTable {
    protected static final String sqlError = "Database error,";

    protected final SQLiteDatabase manager;
    protected final String TABLE;
    protected final String creationSQL;

    public SQLiteTable(SQLiteDatabase database, String TABLE, String creationSQL) {
        this.manager = database;
        this.TABLE = TABLE;
        this.creationSQL = creationSQL;
    }

    boolean create() {
        Connection connection = null;
        Statement buildTableStatement = null;

        manager.getLock().lock();
        try {
            connection = manager.getConnection();
            buildTableStatement = connection.createStatement();
            buildTableStatement.executeUpdate(creationSQL);
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM " + TABLE);
            ps.executeQuery();
        } catch (SQLException e) {
            ErrorLogger.log(e, ArtTable.sqlError);
            return false;
        } finally {
            if (buildTableStatement != null) try {
                buildTableStatement.close();
            } catch (SQLException e) {
                ErrorLogger.log(e, ArtTable.sqlError);
            }
            if (connection != null) try {
                connection.close();
            } catch (SQLException e) {
                ErrorLogger.log(e, ArtTable.sqlError);
            }
            manager.getLock().unlock();
        }
        return true;
    }

    protected abstract class QueuedStatement extends ArtTable.QueuedQuery<Boolean> {

        int[] executeBatch(String query) {
            Connection connection = null;
            PreparedStatement statement = null;
            int[] result = new int[0];

            manager.getLock().lock();
            try {
                connection = manager.getConnection();
                statement = connection.prepareStatement(query);
                prepare(statement);
                result = statement.executeBatch();
            } catch (Exception e) {
                ErrorLogger.log(e, sqlError);
            } finally {
                close(connection, statement);
                manager.getLock().unlock();
            }
            return result;
        }

        Boolean read(ResultSet set) throws SQLException {
            return false;//unused
        }

        Boolean execute(String query) {
            Connection connection = null;
            PreparedStatement statement = null;
            boolean result = false;

            manager.getLock().lock();
            try {
                connection = manager.getConnection();
                statement = connection.prepareStatement(query);
                prepare(statement);
                result = (statement.executeUpdate() != 0);
            } catch (Exception e) {
                ErrorLogger.log(e, sqlError);
            } finally {
                close(connection, statement);
                manager.getLock().unlock();
            }
            return result;
        }
    }

    protected abstract class QueuedQuery<T> {

        abstract void prepare(PreparedStatement statement) throws SQLException;

        abstract T read(ResultSet set) throws SQLException;

        void close(Connection connection, PreparedStatement statement) {
            if (statement != null) try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            if (connection != null) try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        T execute(String query) {
            Connection connection = null;
            PreparedStatement statement = null;
            T result = null;

            manager.getLock().lock();
            try {
                connection = manager.getConnection();
                statement = connection.prepareStatement(query);
                prepare(statement);
                result = read(statement.executeQuery());
            } catch (Exception e) {
                ErrorLogger.log(e, sqlError);
            } finally {
                close(connection, statement);
                manager.getLock().unlock();
            }
            return result;
        }
    }
}
