package code.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class StaticResourceConfig implements WebMvcConfigurer {

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    // Cấu hình để Spring Boot phục vụ các file từ thư mục uploads
    registry.addResourceHandler("/uploads/**")
        .addResourceLocations("file:G:/DATN/DATN/uploads/");  // Thay đổi đường dẫn cho đúng với thư mục của bạn
  }
}
