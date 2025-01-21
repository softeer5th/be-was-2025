package db;

public interface TransactionalDao<THIS> {

    // Transaction에 참여하는 새로운 DAO 객체를 생성한다.
    public THIS joinTransaction(Transaction transaction);
}
