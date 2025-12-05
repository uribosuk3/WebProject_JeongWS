package model.dto;

import java.io.Serializable;

/**
 * UsersDTO: users 테이블 레코드를 담는 간단한 JavaBean
 * 필드명은 DAO와 서블릿에서 사용하는 이름(id, pw, name, email, phone, idx)과 일치해야 함.
 */
public class UsersDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private int idx;
    private String id;
    private String pw;
    private String name;
    private String email;
    private String phone;

    public UsersDTO() { }

    // idx
    public int getIdx() {
        return idx;
    }
    public void setIdx(int idx) {
        this.idx = idx;
    }

    // id
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    // pw
    public String getPw() {
        return pw;
    }
    public void setPw(String pw) {
        this.pw = pw;
    }

    // name
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    // email
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    // phone
    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public String toString() {
        return "UsersDTO [idx=" + idx + ", id=" + id + ", name=" + name + ", email=" + email + ", phone=" + phone + "]";
    }
}
