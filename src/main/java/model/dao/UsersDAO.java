package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import model.dto.UsersDTO;
import util.DBConn; // DBConn 클래스를 import 합니다.

public class UsersDAO {
    
    // 싱글톤 패턴
    private static UsersDAO instance = new UsersDAO();

    private UsersDAO() {}

    public static UsersDAO getInstance() {
        return instance;
    }

    private Connection getConnection() throws SQLException {
        // 💡 DBConn 클래스의 정적 메서드를 호출하여 연결을 얻습니다.
        return DBConn.getConnection();
    }
    
    // 회원가입 메서드 (생략하지 않고 유지)
    public boolean insertUser(UsersDTO dto) {
        String sql = "INSERT INTO users (idx, id, pw, name, email, phone) VALUES (seq_users_idx.nextval, ?, ?, ?, ?, ?)";
        int result = 0;
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
                
            pstmt.setString(1, dto.getId());
            pstmt.setString(2, dto.getPw());
            pstmt.setString(3, dto.getName());
            pstmt.setString(4, dto.getEmail());
            pstmt.setString(5, dto.getPhone());
                
            result = pstmt.executeUpdate();
            return result > 0;
        } catch (Exception e) {
            System.err.println("DEBUG: DB 삽입 실패! 에러 발생:");
            e.printStackTrace(); 
            return false;
        }
    }
    
    // 로그인 메서드 (유지)
    public UsersDTO login(String id, String pw) {
        UsersDTO user = null;
        String sql = "SELECT idx, id, pw, name, email, phone FROM users WHERE id = ? AND pw = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
                
            pstmt.setString(1, id);
            pstmt.setString(2, pw);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    user = new UsersDTO();
                    user.setIdx(rs.getInt("idx"));
                    user.setId(rs.getString("id"));
                    user.setPw(rs.getString("pw")); 
                    user.setName(rs.getString("name"));
                    user.setEmail(rs.getString("email"));
                    user.setPhone(rs.getString("phone"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return user;
    }
    
    // 아이디 중복 확인 메서드 (유지)
    public boolean isIdDuplicate(String id) {
        String sql = "SELECT COUNT(*) FROM users WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
                
            pstmt.setString(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // ⭐ ID로 사용자 정보 조회 (UpdateServlet에서 세션 갱신을 위해 사용)
    public UsersDTO getUserById(String id) {
        String sql = "SELECT idx, id, pw, name, email, phone FROM users WHERE id = ?";
        UsersDTO user = null;
        
        try (Connection conn = getConnection();
              PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    user = new UsersDTO();
                    user.setIdx(rs.getInt("idx")); 
                    user.setId(rs.getString("id"));
                    user.setPw(rs.getString("pw")); 
                    user.setName(rs.getString("name"));
                    user.setEmail(rs.getString("email"));
                    user.setPhone(rs.getString("phone"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return user;
    }

    // 🏆 사용자 정보 수정 (비밀번호 선택적 업데이트 로직)
    public boolean updateUser(UsersDTO dto) {
        // dto.getPw()가 비어있지 않은지 확인합니다.
        // UpdateServlet에서 비밀번호를 입력하지 않았으면 DTO에 기존 비밀번호를 설정했으므로, 
        // 여기서는 그냥 UPDATE 쿼리를 실행합니다.
        
        String sql = "UPDATE users SET pw = ?, name = ?, email = ?, phone = ? WHERE id = ?";
        int result = 0;
        
        try (Connection conn = getConnection();
              PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            // 💡 UpdateServlet에서 이미 비밀번호가 비어있으면 기존 PW를 DTO에 넣도록 처리했으므로, 
            //    여기서는 DTO에 있는 값을 그대로 사용합니다.
            pstmt.setString(1, dto.getPw()); 
            pstmt.setString(2, dto.getName());
            pstmt.setString(3, dto.getEmail());
            pstmt.setString(4, dto.getPhone());
            pstmt.setString(5, dto.getId()); 
            
            result = pstmt.executeUpdate();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result > 0;  
    }

    // 사용자 고유번호(idx)로 정보 조회 (유지)
    public UsersDTO selectUserByIdx(int idx) {
        String sql = "SELECT idx, id, pw, name, email, phone FROM users WHERE idx = ?";
        UsersDTO user = null;
        
        try (Connection conn = getConnection();
              PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idx);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    user = new UsersDTO();
                    user.setIdx(rs.getInt("idx"));  
                    user.setId(rs.getString("id"));
                    user.setPw(rs.getString("pw")); 
                    user.setName(rs.getString("name"));
                    user.setEmail(rs.getString("email"));
                    user.setPhone(rs.getString("phone"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return user;
    }

    // 사용자 고유번호(idx)로 이름만 조회 (유지)
    public String selectNameByIdx(int idx) {
        String sql = "SELECT name FROM users WHERE idx = ?";
        String userName = null;
        
        try (Connection conn = getConnection();
              PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idx);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    userName = rs.getString("name"); 
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return userName;
    }
}