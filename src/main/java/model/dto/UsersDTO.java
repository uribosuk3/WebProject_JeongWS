package model.dto;

public class UsersDTO {
    
    // 필드 (DB 테이블의 컬럼과 일치)
    private String id;
    private String pw;
    private String name;
    private String email;
    private String phone;
    // 필요한 경우 reg_date 등 추가 필드 선언 가능

    // 기본 생성자 (필수)
    public UsersDTO() {
    }

    // Getter와 Setter
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPw() {
        return pw;
    }

    public void setPw(String pw) {
        this.pw = pw;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    // 디버깅을 위한 toString() 메서드를 추가하는 것이 좋습니다.
    @Override
    public String toString() {
        return "UsersDTO [id=" + id + ", pw=" + pw + ", name=" + name + ", email=" + email + ", phone=" + phone + "]";
    }
}