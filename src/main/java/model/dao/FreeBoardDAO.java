package model.dao; // 💡 [수정] dao 패키지가 model.dao에 있으므로 수정 (프로젝트 구조 반영)

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.sql.SQLException; 

import model.dto.FreeBoardDTO; 
import util.DBConn; 

public class FreeBoardDAO {

    private static FreeBoardDAO instance = new FreeBoardDAO();
    public static FreeBoardDAO getInstance() {
        return instance;
    }
    private FreeBoardDAO() {}

    public List<FreeBoardDTO> selectList() { 
        List<FreeBoardDTO> boardList = new ArrayList<>(); 
        
        String sql = "SELECT idx, user_idx, title, content, postdate, views, likes " +
                     "FROM free_board " +
                     "ORDER BY idx DESC"; 

        try (Connection conn = DBConn.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
             
            while (rs.next()) {
                FreeBoardDTO dto = new FreeBoardDTO(); 
                
                dto.setIdx(rs.getInt("idx"));
                dto.setUser_idx(rs.getInt("user_idx"));
                dto.setTitle(rs.getString("title"));
                dto.setContent(rs.getString("content"));
                dto.setPostdate(rs.getDate("postdate")); 
                dto.setViews(rs.getInt("views"));
                dto.setLikes(rs.getInt("likes"));
                
                boardList.add(dto);
            }

        } 
        catch (SQLException e) { 
            System.out.println("자유게시판 목록 조회 중 DB 오류 발생: " + e.getMessage());
            e.printStackTrace();
        }
        
        return boardList;
    }
}