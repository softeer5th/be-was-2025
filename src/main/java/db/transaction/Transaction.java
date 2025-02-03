package db.transaction;

import java.sql.Connection;

public class Transaction {
    private Connection con;

    public Transaction(Connection con) {
        this.con = con;
    }

    public Connection getConnection(){
        return con;
    }
}
