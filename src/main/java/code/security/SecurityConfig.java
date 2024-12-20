package code.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
@Configuration
public class SecurityConfig {

  private final CustomUserDetailsService customUserDetailsService;
  private final JwtRequestFilter jwtRequestFilter;

  public SecurityConfig(CustomUserDetailsService customUserDetailsService, JwtRequestFilter jwtRequestFilter) {
    this.customUserDetailsService = customUserDetailsService;
    this.jwtRequestFilter = jwtRequestFilter;
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .csrf(csrf -> csrf.disable()) // Vô hiệu hóa CSRF (không cần cho API)
        .cors(cors -> cors.configurationSource(corsConfigurationSource())) // Kích hoạt CORS với cấu hình riêng
        .authorizeHttpRequests(authz -> authz
            .requestMatchers("/api/auth/**","api/home/**","/ws/**","/uploads/**").permitAll() // Cho phép truy cập công khai tới các API xác thực (login, register, v.v.)
            .requestMatchers("/api/admin/**").hasRole("admin") // Chỉ admin có thể truy cập
            .requestMatchers("/api/customer/**").hasRole("customer") // Chỉ customer có thể truy cập
            .requestMatchers("/api/payment/**").permitAll()
            .anyRequest().authenticated() // Các yêu cầu khác phải được xác thực
        )
        .sessionManagement(session -> session
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // Sử dụng JWT, không tạo session
        )
        // Thêm filter JWT trước filter xác thực mặc định
        .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class)
    ;

    return http.build();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(); // Mã hóa mật khẩu
  }

  // Khai báo AuthenticationManager để sử dụng cho việc xác thực
  @Bean
  public AuthenticationManager authenticationManager(
      AuthenticationConfiguration authenticationConfiguration) throws Exception {
    return authenticationConfiguration.getAuthenticationManager();
  }

  // Cấu hình CORS cho toàn bộ ứng dụng
  @Bean
  public UrlBasedCorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration config = new CorsConfiguration();
    config.addAllowedOrigin("http://localhost:3000");
    config.addAllowedOrigin("http://127.0.0.1:5500");// Cho phép origin (client frontend)
    config.addAllowedOrigin("https://fe-datn-three.vercel.app");
    config.addAllowedMethod("*"); // Cho phép tất cả các phương thức HTTP (GET, POST, PUT, DELETE, v.v.)
    config.addAllowedHeader("*"); // Cho phép tất cả các header
    config.setAllowCredentials(true); // Cho phép gửi thông tin xác thực (cookies, authorization headers, v.v.)

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", config); // Cấu hình CORS cho tất cả các endpoint
    return source;
  }
}
