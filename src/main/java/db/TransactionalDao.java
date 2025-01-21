package db;

/**
 * Transaction에 참여할 수 있는 DAO
 */
public interface TransactionalDao<THIS extends TransactionalDao> {

    /**
     * Transaction에 참여하는 DAO 객체를 반환한다.
     *
     * @param transaction 참여할 Transaction 객체
     * @return Transaction에 참여하는 새로운 DAO 객체
     */
    THIS joinTransaction(Transaction transaction);
}
