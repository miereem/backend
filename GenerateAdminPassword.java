import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Утилита для генерации BCrypt хеша пароля администратора
 * 
 * Запустите этот класс, чтобы получить BCrypt хеш для вашего пароля
 * Затем используйте этот хеш в SQL запросе add_admin.sql
 */
public class GenerateAdminPassword {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        // Замените "admin123" на ваш желаемый пароль
        String password = "admin123";
        String hashedPassword = encoder.encode(password);
        
        System.out.println("Пароль: " + password);
        System.out.println("BCrypt хеш: " + hashedPassword);
        System.out.println("\nИспользуйте этот хеш в SQL запросе:");
        System.out.println("INSERT INTO users (username, password) VALUES ('admin', '" + hashedPassword + "');");
    }
}

