package model.dao;

import java.sql.*;
import util.DBConn;
import model.dto.UsersDTO;

public class UsersDAO {

    // ✔ 로그인
    public UsersDTO login(String id, String pw) {
        String sql = "SELECT * FROM users WHERE id=? AND pw=?";
        try {
            Connection conn = DBConn.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, id);
            ps.setString(2, pw);
            ResultSet rs = ps.executeQuery();

            if(rs.next()) {
                UsersDTO dto = new UsersDTO();
                dto.setIdx(rs.getInt("idx"));
                dto.setId(rs.getString("id"));
                dto.setPw(rs.getString("pw"));
                dto.setName(rs.getString("name"));
                dto.setEmail(rs.getString("email"));
                dto.setPhone(rs.getString("phone"));
                return dto;
            }
        } 
        catch(Exception e) { e.printStackTrace(); }
        return null;
    }

    // ✔ 회원가입
    public boolean insertUser(UsersDTO u) {
        String sql = "INSERT INTO users(idx, id, pw, name, email, phone) VALUES(users_seq.nextval,?,?,?,?,?)";

        try {
            Connection conn = DBConn.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setString(1, u.getId());
            ps.setString(2, u.getPw());
            ps.setString(3, u.getName());
            ps.setString(4, u.getEmail());
            ps.setString(5, u.getPhone());

            return ps.executeUpdate() == 1;

        } catch(Exception e) { 
            e.printStackTrace(); 
        }
        return false;
    }

}
