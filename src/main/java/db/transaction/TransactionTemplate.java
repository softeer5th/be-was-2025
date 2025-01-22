package db.transaction;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

import static util.DBUtil.*;

public class TransactionTemplate {
    private static final TransactionTemplate INSTANCE = new TransactionTemplate();
    private final TransactionManager transactionManager = TransactionManager.getInstance();

    private TransactionTemplate(){
    }

    public static TransactionTemplate getInstance(){
        return INSTANCE;
    }

    public  <R> R execute(BiFunction<Transaction, Object[], R> bizLogic, Object... args){
        Transaction transaction = transactionManager.getTransaction();
        try {
            R result = bizLogic.apply(transaction, args);
            transactionManager.commit(transaction);
            return result;
        }catch(RuntimeException e){
            transactionManager.rollback(transaction);
            throw e;
        }
    }
    public void executeWithoutResult(BiConsumer<Transaction, Object[]> bizLogic, Object... args){
        Transaction transaction = transactionManager.getTransaction();
        try{
            bizLogic.accept(transaction, args);
            transactionManager.commit(transaction);
        }catch(RuntimeException e){
            transactionManager.rollback(transaction);
            throw e;
        }
    }
}
