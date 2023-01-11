package ;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DBConn {  //공통모듈
    //DB연결에 필요한 변수 또는 메소드를 정의한다.
    //0. 변수 선언
    protected Connection conn = null;
    protected PreparedStatement pstmt = null;
    protected CallableStatement cstmt = null;  //프로시저를 이용한  statement객체
    protected ResultSet rs = null;  //select문일 때 필요(pstmt 실행하면 rs실행)

    // protected : 같은 패키지이거나 다른 패키지일 경우 상속받아 접근할 수 있다.
    protected String sql = null;
    String url = "jdbc:oracle:thin:@localhost:1521:xe"; // 서버주소, 포트번호, SID(전역데이터베이스)
    String username = "scott";
    String password = "tiger";

    //1. jdbc 드라이브 로딩
    static { //멤버변수와 생성자메소드 실행되기전 먼저 실행된다.
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver"); // 이 경로를 JVM에 등록
        }catch(ClassNotFoundException fnfe) {
            System.out.println("jdbc 드라이브 로딩 예외 발생.." + fnfe.getMessage());
        }
    }

    //2. DB연결
    // db연결(회선 생김)
    public void dbConn() {
        try {
            conn = DriverManager.getConnection(url, username, password);
        } catch (Exception se) {
            System.out.println("DB연결예외 발생.." + se.getMessage());
        }
    }

    //3. DB닫기
    public void dbClose() {
        try {
            //생성된 객체를 역순으로 닫는다.
            if(rs != null) rs.close();
            if(pstmt != null) pstmt.close();
            if(cstmt != null) cstmt.close();
            if(conn != null) conn.close();
        } catch (Exception e) {
            System.out.println("DB닫기 예외발생.." + e.getMessage());
        }
    }
}