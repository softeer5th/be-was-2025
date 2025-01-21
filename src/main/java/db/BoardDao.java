package db;

import model.Board;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.Optional;

import static db.DBUtils.close;
import static db.DBUtils.getConnection;

public enum BoardDao {
    BOARDS;

    private static final Logger log = LoggerFactory.getLogger(BoardDao.class);

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
        String sql = "insert into boards(contents, writer, image_path) values (?, ?, ?)";
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try{
            con = getConnection();
            pstmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, board.getContents());
            pstmt.setString(2, board.getWriter());
            pstmt.setString(3, board.getImagePath());
            pstmt.executeUpdate();
            rs = pstmt.getGeneratedKeys();
            long boardId = 0L;
            if(rs.next()){
                boardId = rs.getLong(1);
            }else{
                throw new SQLException("생성된 ID가 없음");
            }
            Board newBoard = new Board(boardId, board.getWriter(), board.getContents(), board.getImagePath());
            return Optional.of(newBoard);
        } catch (SQLException e) {
            log.error("save 예외: ", e);
        }finally {
            close(rs, pstmt, con);
        }
        return Optional.empty();
    }

    public Long getBoardSize() {
        String sql = "select count(*) from boards";
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet resultSet = null;
        try{
            con = getConnection();
            pstmt = con.prepareStatement(sql);

            resultSet = pstmt.executeQuery();
            if(resultSet.next()){
                return resultSet.getLong(1);
            }
        } catch (SQLException e) {
            log.error("find 예외: ", e);
        }finally {
            close(resultSet, pstmt, con);
        }
        return 0L;
    }
}
