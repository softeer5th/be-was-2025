package db;

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
}
