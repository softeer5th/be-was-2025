package db;

import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * 트랜젝션을 생성하는 팩토리 인터페이스
 */
public interface TransactionFactory {
    /**
     * 트랜젝션을 생성한다.
     *
     * @return 생성된 트랜젝션
     */
    Transaction createTransaction();

    default <T extends TransactionalDao, R> R runInTransaction(TransactionalDao<T> dao, Function<T, R> function) {
        Transaction tx = createTransaction();
        T transactionalDao = dao.joinTransaction(tx);
        try {
            tx.begin();
            R result = function.apply(transactionalDao);
            tx.commit();
            return result;
        } catch (Exception e) {
            tx.rollback();
            throw new RuntimeException(e);
        } finally {
            tx.close();
        }
    }

    default <T extends TransactionalDao, U extends TransactionalDao, R> R runInTransaction(TransactionalDao<T> dao1, TransactionalDao<U> dao2, BiFunction<T, U, R> function) {
        Transaction tx = createTransaction();
        T transactionalDao1 = dao1.joinTransaction(tx);
        U transactionalDao2 = dao2.joinTransaction(tx);
        try {
            tx.begin();
            R result = function.apply(transactionalDao1, transactionalDao2);
            tx.commit();
            return result;
        } catch (Exception e) {
            tx.rollback();
            throw new RuntimeException(e);
        } finally {
            tx.close();
        }
    }
}
