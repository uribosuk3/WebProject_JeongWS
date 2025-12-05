package membership; 

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import util.DBConn;

public class MemberDAO {
    
    private static MemberDAO instance;

    private MemberDAO() {
    }

    public static MemberDAO getInstance() {
        if (instance == null) {
            instance = new MemberDAO();
        }
        return instance;
    }

    public int insertMember(MemberDTO dto) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        int result = 0;
        
        // 💡 member_table은 사용하시는 실제 테이블 이름으로 수정하세요.
        String sql = "INSERT INTO member_table (id, pw, name, email) VALUES (?, ?, ?, ?)";
        
        try {
             conn = DBConn.getConnection(); 
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, dto.getId());
            pstmt.setString(2, dto.getPw());
            pstmt.setString(3, dto.getName());
            pstmt.setString(4, dto.getEmail());
            
            result = pstmt.executeUpdate();
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // DBConn.close(pstmt, conn); // 💡 자원 반납
        }
        return result;
    }

    /**
     * 3. 로그인: ID와 PW가 일치하는 회원 정보를 조회합니다.
     * @param id 입력된 ID
     * @param pw 입력된 PW
     * @return 일치하는 회원이 있으면 MemberDTO 객체, 없으면 null 반환
     */
    public MemberDTO loginMember(String id, String pw) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        MemberDTO member = null;

        String sql = "SELECT * FROM member_table WHERE id = ? AND pw = ?";
        
        try {
            // conn = DBConn.getConnection(); // 💡 DBConn 클래스를 이용한 연결
            // ************ DB 연결 코드를 실제 환경에 맞게 작성하세요. ************
             conn = DBConn.getConnection(); 
            // ***************************************************************
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, id);
            pstmt.setString(2, pw);
            rs = pstmt.executeQuery(); // 쿼리 실행

            if (rs.next()) {
                // ID와 PW가 일치하는 회원이 존재하면 DTO에 정보를 담아 리턴
                member = new MemberDTO();
                member.setId(rs.getString("id"));
                member.setPw(rs.getString("pw"));
                member.setName(rs.getString("name"));
                member.setEmail(rs.getString("email"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // DBConn.close(rs, pstmt, conn); // 💡 자원 반납
        }
        return member;
    }
}