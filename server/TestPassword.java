import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 密码测试工具
 * 用于验证密码加密和匹配
 */
public class TestPassword {
    
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        // 测试密码
        String rawPassword = "123456";
        
        // 数据库中的加密密码
        String encodedPassword = "$2a$10$Pbj5a11BoFK1mpU093YnGOZiiXRDqCwgw3sMqbC8HQLDnD2x6JDHq";
        
        // 验证密码是否匹配
        boolean matches = encoder.matches(rawPassword, encodedPassword);
        
        System.out.println("=== 密码验证结果 ===");
        System.out.println("原始密码: " + rawPassword);
        System.out.println("加密密码: " + encodedPassword);
        System.out.println("验证结果: " + (matches ? "✅ 匹配成功" : "❌ 匹配失败"));
        
        // 生成新的加密密码（用于测试）
        String newEncodedPassword = encoder.encode(rawPassword);
        System.out.println("\n=== 新生成的加密密码 ===");
        System.out.println("新密码: " + newEncodedPassword);
        System.out.println("新密码验证: " + (encoder.matches(rawPassword, newEncodedPassword) ? "✅ 成功" : "❌ 失败"));
    }
}
