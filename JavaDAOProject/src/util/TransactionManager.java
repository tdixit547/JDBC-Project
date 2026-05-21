package util;

import java.sql.Connection;
import java.sql.SQLException;

public class TransactionManager {

    @FunctionalInterface
    public interface TransactionalOperation<T> {
        T execute() throws Exception;
    }

    @FunctionalInterface
    public interface TransactionalVoidOperation {
        void execute() throws Exception;
    }

    public static void beginTransaction(Connection conn) throws SQLException {
        conn.setAutoCommit(false);
    }

    public static void commitTransaction(Connection conn) throws SQLException {
        conn.commit();
        conn.setAutoCommit(true);
    }

    public static void rollbackTransaction(Connection conn) {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.rollback();
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            System.err.println("[TransactionManager] Rollback failed: " + e.getMessage());
        }
    }

    public static <T> T executeInTransaction(Connection conn, TransactionalOperation<T> operation) throws Exception {
        try {
            beginTransaction(conn);
            T result = operation.execute();
            commitTransaction(conn);
            return result;
        } catch (Exception e) {
            rollbackTransaction(conn);
            throw e;
        }
    }

    public static void executeInTransaction(Connection conn, TransactionalVoidOperation operation) throws Exception {
        try {
            beginTransaction(conn);
            operation.execute();
            commitTransaction(conn);
        } catch (Exception e) {
            rollbackTransaction(conn);
            throw e;
        }
    }
}
