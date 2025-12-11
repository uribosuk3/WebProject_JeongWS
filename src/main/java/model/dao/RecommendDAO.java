package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import util.DBConn; // 프로젝트의 DB 연결 클래스를 사용합니다.

public class RecommendDAO {

    private static RecommendDAO instance = new RecommendDAO();
    public static RecommendDAO getInstance() {
        return instance;
    }
    private RecommendDAO() {}
    
    private static final String LIKE_TABLE = "board_like";
    
    /**
     * [좋아요 토글 및 상태/추천수 반환]
     * @param type 게시판 종류 ('free' 사용)
     * @param boardIdx 게시글 번호
     * @param userIdx 사용자 번호
     * @return 성공 여부, 현재 좋아요 상태(isLiked), 새로운 추천수(newCount)를 담은 Map
     */
    public Map<String, Object> toggleLike(String type, int boardIdx, int userIdx) {
        
        Map<String, Object> result = new HashMap<>();
        Connection conn = null;
        
        try {
            conn = DBConn.getConnection();
            conn.setAutoCommit(false); 

            // 1. 현재 좋아요 상태 확인 (SELECT)
            String checkSql = "SELECT COUNT(*) FROM " + LIKE_TABLE 
                            + " WHERE board_type = ? AND board_idx = ? AND user_idx = ?";
            boolean isLiked = false;
            
            try (PreparedStatement psCheck = conn.prepareStatement(checkSql)) {
                psCheck.setString(1, type);
                psCheck.setInt(2, boardIdx);
                psCheck.setInt(3, userIdx);
                
                try (ResultSet rs = psCheck.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        isLiked = true;
                    }
                }
            }

            // 2. 좋아요 기록 테이블(board_like) 토글 및 free_board 추천수 업데이트 SQL 준비
            String actionSql = "";
            String updateSql = "";
            
            if (isLiked) {
                // 이미 좋아요 했으면: 취소 (DELETE)
                actionSql = "DELETE FROM " + LIKE_TABLE 
                          + " WHERE board_type = ? AND board_idx = ? AND user_idx = ?";
                updateSql = "UPDATE free_board SET recommend_count = recommend_count - 1 WHERE idx = ?";
                isLiked = false;
            } else {
                // 좋아요 기록 없으면: 등록 (INSERT)
                actionSql = "INSERT INTO " + LIKE_TABLE 
                          + " (idx, board_type, board_idx, user_idx) VALUES (seq_like_idx.NEXTVAL, ?, ?, ?)";
                updateSql = "UPDATE free_board SET recommend_count = recommend_count + 1 WHERE idx = ?";
                isLiked = true;
            }

            // 3. 기록 테이블 처리 (INSERT 또는 DELETE)
            try (PreparedStatement psAction = conn.prepareStatement(actionSql)) {
                psAction.setString(1, type);
                psAction.setInt(2, boardIdx);
                psAction.setInt(3, userIdx);
                psAction.executeUpdate();
            }

            // 4. 게시글 테이블의 추천수 업데이트
            try (PreparedStatement psUpdate = conn.prepareStatement(updateSql)) {
                psUpdate.setInt(1, boardIdx);
                psUpdate.executeUpdate();
            }
            
            // 5. 최종 추천수 조회
            String countSql = "SELECT recommend_count FROM free_board WHERE idx = ?";
            int newCount = 0;
            try (PreparedStatement psCount = conn.prepareStatement(countSql)) {
                psCount.setInt(1, boardIdx);
                try (ResultSet rs = psCount.executeQuery()) {
                    if (rs.next()) {
                        newCount = rs.getInt(1);
                    }
                }
            }
            
            conn.commit(); // 트랜잭션 커밋
            
            // 6. 결과 맵에 저장
            result.put("success", true);
            result.put("isLiked", isLiked);
            result.put("newCount", newCount);
            
        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException rollbackE) {
                rollbackE.printStackTrace();
            }
            System.err.println("좋아요 토글 중 DB 오류 발생: " + e.getMessage());
            e.printStackTrace();
            result.put("success", false);
        } finally {
            try {
                if (conn != null) conn.close();
            } catch (SQLException closeE) {
                closeE.printStackTrace();
            }
        }
        return result;
    }
}