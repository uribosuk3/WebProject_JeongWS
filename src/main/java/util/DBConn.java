package util; // util 패키지에 생성

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConn {

    // JDBC 연결 정보를 환경에 맞게 수정하세요.
    // 프로젝트명: webproject_db, 비밀번호: 1234
    private static final String DRIVER = "oracle.jdbc.OracleDriver";
    private static final String URL = "jdbc:oracle:thin:@localhost:1521:xe"; // XE 환경 기준
    private static final String USER = "webproject_db"; // Oracle 계정명
    private static final String PASS = "1234"; // Oracle 비밀번호

    // 정적 블록: 클래스가 메모리에 로드될 때 드라이버를 단 한 번 로드
    static {
        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            System.out.println("Oracle 드라이버 로드 실패!");
            e.printStackTrace();
        }
    }

    /**
     * 데이터베이스 연결(Connection) 객체를 반환합니다.
     * @return Connection 객체
     * @throws SQLException 연결 실패 시 예외 발생
     */
    public static Connection getConnection() throws SQLException {
        // DriverManager를 사용하여 DB 연결을 맺고 반환
        return DriverManager.getConnection(URL, USER, PASS);
    }
}

