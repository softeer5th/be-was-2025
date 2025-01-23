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

    /**
     * 트랜젝션 내에서 작업을 수행하고 커밋한다. 작업 수행 중 예외가 발생하면 롤백한다.
     *
     * @param dao      트랜젝션에 참여할 DAO(트랜젝션에 참여하지 않은 상태)
     * @param function 트랜젝션에 참여한 DAO를 인자로 받아 작업을 수행할 함수
     * @param <T>      DAO의 타입
     * @param <R>      함수의 반환값의 타입
     * @return 함수의 반환값
     */
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

    /**
     * 트랜젝션 내에서 작업을 수행하고 커밋한다. 작업 수행 중 예외가 발생하면 롤백한다.
     *
     * @param dao1     트랜젝션에 참여할 DAO 1(트랜젝션에 참여하지 않은 상태)
     * @param dao2     트랜젝션에 참여할 DAO 2(트랜젝션에 참여하지 않은 상태)
     * @param function 트랜젝션에 참여한 DAO 1,2 를 인자로 받아 작업을 수행할 함수
     * @param <T>      DAO 1의 타입
     * @param <U>      DAO 2의 타입
     * @param <R>      함수의 반환값의 타입
     * @return 함수의 반환값
     */
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
