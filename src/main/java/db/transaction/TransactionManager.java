package db.transaction;

import util.DBUtil;

import java.sql.Connection;
import java.sql.SQLException;

public class TransactionManager {
    private static final TransactionManager INSTANCE = new TransactionManager();

    private TransactionManager(){}

    public static TransactionManager getInstance(){
        return INSTANCE;
    }

    public Transaction getTransaction() {
        try {
            Connection con = DBUtil.getConnection();
            con.setAutoCommit(false);
            return new Transaction(con);
        }catch(SQLException e){
            throw new IllegalStateException(e);
        }
    }

    public void commit(Transaction transaction){
        try{
            Connection con = transaction.getConnection();
            con.commit();
        }catch(SQLException e){
            throw new IllegalStateException(e);
        }
    }

    public void rollback(Transaction transaction){
        try{
            Connection con = transaction.getConnection();
            con.rollback();
        }catch(SQLException e){
            throw new IllegalStateException(e);
        }
    }
}
