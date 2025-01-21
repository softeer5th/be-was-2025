package db;

import model.Board;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import static db.DBUtils.close;
import static db.DBUtils.getConnection;

public enum BoardDao {
    BOARDS(0);

    private static final Logger log = LoggerFactory.getLogger(BoardDao.class);
    private final AtomicLong boardSize;

    BoardDao(long initValue) {
        this.boardSize = new AtomicLong(initValue);
    }
    /**
     * id와 매칭되는 게시글을 찾아주는 메소드
     * @param boardId 찾고자 하는 board ID
     * @return Optional 로 감싼 board 객체 반환. 해당 ID가 존재하지 않을 경우, 빈 Optional 객체 반환
     */
    public Optional<Board> findById(Long boardId) {
        String sql = "select * from boards where board_id = ?";
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet resultSet = null;
        try{
            con = getConnection();
            pstmt = con.prepareStatement(sql);

            pstmt.setLong(1, boardId);
            resultSet = pstmt.executeQuery();
            if(resultSet.next()){
                Long findBoardId = resultSet.getLong("board_id");
                String findBoardContents = resultSet.getString("contents");
                String findBoardImagePath = resultSet.getString("image_path");
                String findBoardWriter = resultSet.getString("writer");
                return Optional.of(new Board(findBoardId, findBoardWriter,findBoardContents, findBoardImagePath));
            }
        } catch (SQLException e) {
            log.error("find 예외: ", e);
        }finally {
            close(resultSet, pstmt, con);
        }
        return Optional.empty();
    }
    /**
     * 새 게시글을 저장하는 메소드
     * @param board id값이 없는 새로 저장할 board 객체
     * @return
     */
    public Optional<Board> save(Board board){
        String sql = "insert into boards(board_id, contents, writer, image_path) values (?, ?, ?, ?)";
        Connection con = null;
        PreparedStatement pstmt = null;
        try{
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            long boardId = boardSize.incrementAndGet();
            pstmt.setLong(1, boardId);
            pstmt.setString(2, board.getContents());
            pstmt.setString(3, board.getWriter());
            pstmt.setString(4, board.getImagePath());
            pstmt.executeUpdate();
            Board newBoard = new Board(boardId, board.getWriter(), board.getContents(), board.getImagePath());
            return Optional.of(newBoard);
        } catch (SQLException e) {
            log.error("save 예외: ", e);
        }finally {
            close(null, pstmt, con);
        }
        return Optional.empty();
    }

    public Long getBoardSize() {
        return boardSize.get();
    }
}
