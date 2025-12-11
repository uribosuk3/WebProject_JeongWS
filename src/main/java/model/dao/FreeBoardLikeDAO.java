package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import model.dto.FreeBoardLikeDTO;
import util.DBConn;

public class FreeBoardLikeDAO {
    // 💡 board_like 테이블에 사용할 board_type 상수 정의
    private static final String BOARD_TYPE = "free";

    private static FreeBoardLikeDAO instance = new FreeBoardLikeDAO();
    public static FreeBoardLikeDAO getInstance() {
        return instance;
    }
    private FreeBoardLikeDAO() {}

    /**
     * [1. 추천 상태 확인] 사용자가 해당 게시글에 추천을 했는지 확인합니다.
     * 💡 board_like 테이블과 board_type 조건을 사용합니다.
     * @param board_idx 게시글 번호
     * @param user_idx 사용자 번호
     * @return 추천 기록이 있으면 해당 idx, 없으면 0
     */
    public int checkLike(int board_idx, int user_idx) {
        int likeIdx = 0;
        // 💡 board_type 조건 추가 및 테이블 이름 수정
        String sql = "SELECT idx FROM board_like WHERE board_type = ? AND board_idx = ? AND user_idx = ?"; 

        try (Connection conn = DBConn.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, BOARD_TYPE); // 💡 board_type 설정
            ps.setInt(2, board_idx);
            ps.setInt(3, user_idx);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    likeIdx = rs.getInt("idx");
                }
            }
        } catch (SQLException e) {
            System.err.println("추천 상태 확인 중 DB 오류 발생: " + e.getMessage());
            e.printStackTrace();
        }
        return likeIdx;
    }
    
    /**
     * [2. 추천 등록] 새로운 추천 기록을 DB에 삽입하고, 게시글의 likes 카운트를 증가시킵니다.
     * 💡 board_like 테이블, seq_like_idx 시퀀스, board_type을 사용합니다.
     */
    public boolean insertLike(FreeBoardLikeDTO dto) {
        Connection conn = null;
        PreparedStatement ps1 = null;
        PreparedStatement ps2 = null;
        boolean result = false;

        try {
            conn = DBConn.getConnection();
            conn.setAutoCommit(false); // 트랜잭션 시작

            // 1) 추천 테이블에 기록 추가
            // 💡 테이블 이름/시퀀스 이름 수정, board_type 필드 추가
            String sql1 = "INSERT INTO board_like (idx, board_type, board_idx, user_idx) "
                         + "VALUES (seq_like_idx.NEXTVAL, ?, ?, ?)"; 
            ps1 = conn.prepareStatement(sql1);
            ps1.setString(1, BOARD_TYPE); // 💡 board_type 값 설정
            ps1.setInt(2, dto.getBoard_idx());
            ps1.setInt(3, dto.getUser_idx());
            
            int affectedRows1 = ps1.executeUpdate();

            // 2) 게시글 테이블의 likes 카운트 증가 (free_board 테이블 유지)
            String sql2 = "UPDATE free_board SET likes = likes + 1 WHERE idx = ?";
            ps2 = conn.prepareStatement(sql2);
            ps2.setInt(1, dto.getBoard_idx());
            
            int affectedRows2 = ps2.executeUpdate();
            
            if (affectedRows1 == 1 && affectedRows2 == 1) {
                conn.commit(); // 커밋
                result = true;
            } else {
                conn.rollback(); // 롤백
            }
        } catch (SQLException e) {
            System.err.println("추천 등록 중 DB 오류 발생: " + e.getMessage());
            try { if (conn != null) conn.rollback(); } catch (SQLException rollbackE) { rollbackE.printStackTrace(); }
            e.printStackTrace();
        } finally {
            try { if (ps2 != null) ps2.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (ps1 != null) ps1.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (conn != null) conn.setAutoCommit(true); conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
        return result;
    }

    /**
     * [3. 추천 취소] 추천 기록을 삭제하고, 게시글의 likes 카운트를 감소시킵니다.
     * 💡 board_like 테이블을 사용합니다.
     */
    public boolean deleteLike(int like_idx, int board_idx) {
        Connection conn = null;
        PreparedStatement ps1 = null;
        PreparedStatement ps2 = null;
        boolean result = false;

        try {
            conn = DBConn.getConnection();
            conn.setAutoCommit(false); // 트랜잭션 시작

            // 1) 추천 테이블에서 기록 삭제
            // 💡 테이블 이름 수정
            String sql1 = "DELETE FROM board_like WHERE idx = ?";
            ps1 = conn.prepareStatement(sql1);
            ps1.setInt(1, like_idx);
            
            int affectedRows1 = ps1.executeUpdate();

            // 2) 게시글 테이블의 likes 카운트 감소 (free_board 테이블 유지)
            String sql2 = "UPDATE free_board SET likes = likes - 1 WHERE idx = ?";
            ps2 = conn.prepareStatement(sql2);
            ps2.setInt(1, board_idx);
            
            int affectedRows2 = ps2.executeUpdate();
            
            if (affectedRows1 == 1 && affectedRows2 == 1) {
                conn.commit(); // 커밋
                result = true;
            } else {
                conn.rollback(); // 롤백
            }
        } catch (SQLException e) {
            System.err.println("추천 취소 중 DB 오류 발생: " + e.getMessage());
            try { if (conn != null) conn.rollback(); } catch (SQLException rollbackE) { rollbackE.printStackTrace(); }
            e.printStackTrace();
        } finally {
            try { if (ps2 != null) ps2.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (ps1 != null) ps1.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (conn != null) conn.setAutoCommit(true); conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
        return result;
    }
}