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
    BOARDS;

    private static AtomicLong boardSize = new AtomicLong(0);
    private static final Logger log = LoggerFactory.getLogger(BoardDao.class);


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

    public Optional<Board> save(Board board){
        String sql = "insert into boards(board_id, contents, writer, image_path) values (?, ?, ?, ?)";
        Connection con = null;
        PreparedStatement pstmt = null;
        try{
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setLong(1, boardSize.incrementAndGet());
            pstmt.setString(2, board.getContents());
            pstmt.setString(3, board.getWriter());
            pstmt.setString(4, board.getImagePath());
            pstmt.executeUpdate();
            return Optional.of(board);
        } catch (SQLException e) {
            log.error("save 예외: ", e);
        }finally {
            close(null, pstmt, con);
        }
        return Optional.empty();
    }

    public static Long getBoardSize() {
        return boardSize.get();
    }
}
