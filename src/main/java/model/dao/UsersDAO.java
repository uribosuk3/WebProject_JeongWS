package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import model.dto.UsersDTO;

public class UsersDAO {
    
    // 싱글톤 패턴
    private static UsersDAO instance = new UsersDAO();

    private UsersDAO() {}

    public static UsersDAO getInstance() {
        return instance;
    }

    // =========================================================
    // 💡 DB 연결 메서드 (Context/DataSource 방식 사용 가정)
    // 실제 환경에 맞게 getConnection() 메서드가 구현되어 있어야 합니다.
    // =========================================================
    private Connection getConnection() throws SQLException {
        try {
            Context initCtx = new InitialContext();
            Context envCtx = (Context) initCtx.lookup("java:comp/env");
            // 💡 "jdbc/mydb"는 web.xml 또는 context.xml에 설정된 리소스 이름입니다.
            DataSource ds = (DataSource) envCtx.lookup("jdbc/mydb"); 
            return ds.getConnection();
        } catch (Exception e) {
            System.err.println("DB 연결 오류: " + e.getMessage());
            throw new SQLException("데이터베이스 연결 실패", e);
        }
    }
    
    // =========================================================
    // 🔔 MypageServlet에서 필요한 메서드 1: ID로 사용자 정보 조회
    // =========================================================
    /**
     * ID를 사용하여 특정 사용자 정보를 조회합니다.
     * @param id 조회할 사용자의 ID
     * @return UsersDTO 객체 (사용자가 없을 경우 null 반환)
     */
    public UsersDTO getUserById(String id) {
        String sql = "SELECT id, pw, name, email, phone FROM users WHERE id = ?";
        UsersDTO user = null;
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    user = new UsersDTO();
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

    // =========================================================
    // 🔔 MypageServlet에서 필요한 메서드 2: 사용자 정보 수정
    // =========================================================
    /**
     * 사용자 정보를 업데이트합니다. (ID는 수정하지 않음)
     * @param dto 업데이트할 정보가 담긴 UsersDTO 객체
     * @return 업데이트 성공 여부 (boolean)
     */
    public boolean updateUser(UsersDTO dto) {
        // 비밀번호, 이름, 이메일, 전화번호를 업데이트합니다.
        String sql = "UPDATE users SET pw = ?, name = ?, email = ?, phone = ? WHERE id = ?";
        int result = 0;
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, dto.getPw());
            pstmt.setString(2, dto.getName());
            pstmt.setString(3, dto.getEmail());
            pstmt.setString(4, dto.getPhone());
            pstmt.setString(5, dto.getId()); // WHERE 조건에 사용
            
            result = pstmt.executeUpdate();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result > 0; 
    }
    
    // =========================================================
    // 기존에 구현했던 메서드들 (예시)
    // =========================================================
    
    // 회원가입 메서드
    public boolean insertUser(UsersDTO dto) {
        // ... (회원가입 로직 구현)
        String sql = "INSERT INTO users (id, pw, name, email, phone) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, dto.getId());
            pstmt.setString(2, dto.getPw());
            pstmt.setString(3, dto.getName());
            pstmt.setString(4, dto.getEmail());
            pstmt.setString(5, dto.getPhone());
            
            return pstmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // 로그인 메서드
    public UsersDTO login(String id, String pw) {
        // ... (로그인 로직 구현)
        String sql = "SELECT id, pw, name, email, phone FROM users WHERE id = ? AND pw = ?";
        UsersDTO user = null;
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, id);
            pstmt.setString(2, pw);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    user = new UsersDTO();
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

    // 아이디 중복 확인 메서드
    public boolean isIdDuplicate(String id) {
        // ... (중복 확인 로직 구현)
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
}