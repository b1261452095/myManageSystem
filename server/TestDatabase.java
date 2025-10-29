import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * 数据库连接测试
 */
public class TestDatabase {
    public static void main(String[] args) {
        String url = "jdbc:mysql://14.103.206.253:3306/admin_system?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai";
        String username = "root";
        String password = "b1261452095";
        
        try {
            System.out.println("=== 测试数据库连接 ===");
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(url, username, password);
            System.out.println("✅ 数据库连接成功！");
            
            // 查询用户表
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT id, username, status, LEFT(password, 20) as pwd FROM sys_user WHERE username = 'admin'");
            
            System.out.println("\n=== 用户信息 ===");
            if (rs.next()) {
                System.out.println("ID: " + rs.getLong("id"));
                System.out.println("用户名: " + rs.getString("username"));
                System.out.println("状态: " + rs.getInt("status"));
                System.out.println("密码(前20位): " + rs.getString("pwd") + "...");
            } else {
                System.out.println("❌ 未找到 admin 用户！");
            }
            
            rs.close();
            stmt.close();
            conn.close();
            
        } catch (Exception e) {
            System.out.println("❌ 数据库连接失败！");
            e.printStackTrace();
        }
    }
}

