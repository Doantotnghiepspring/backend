package code.security;

import code.exception.ErrorResponse;
import code.exception.ForbiddenException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

  @Autowired
  private CustomUserDetailsService customUserDetailsService;

  @Autowired
  private JwtTokenUtil jwtTokenUtil;

  @Value("${API_KEY}")
  private String apiKeyConfig;

  private static final Logger logger = LoggerFactory.getLogger(JwtRequestFilter.class);

  @Override
  protected void doFilterInternal(HttpServletRequest request,
      HttpServletResponse response,
      FilterChain chain)
      throws ServletException, IOException {
    try {
      final String authorizationHeader = request.getHeader("Authorization");

      String username = null;
      String token = null;

      // Permit all for specific URIs
      if (request.getRequestURI().startsWith("/api/home")
          || request.getRequestURI().startsWith("/ws")
      || request.getRequestURI().startsWith("/uploads")) {
        chain.doFilter(request, response);
        return;
      }

//      Kiểm tra token giao dịch
      if (request.getRequestURI().startsWith("/api/payment")) {
        if (authorizationHeader.contains(apiKeyConfig)) {
//          System.out.println(authorizationHeader);
          chain.doFilter(request, response);
        }
        else {
          throw new ForbiddenException("JWT token API_KEY is missing or invalid" );
        }
        return;
      }

      // Kiểm tra tiêu đề Authorization có tồn tại và có prefix "Bearer"
      if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
        token = authorizationHeader.substring(7); // Lấy token từ tiêu đề
        try {
          username = jwtTokenUtil.extractUsername(token); // Trích xuất username từ token
        } catch (Exception e) {
          // Nếu có lỗi trong khi trích xuất username, trả về lỗi 403
          throw new ForbiddenException("Invalid JWT token");
        }
      }

      // Nếu không có token hoặc username là null, trả về lỗi 403
      if (username == null) {
        throw new ForbiddenException("JWT token is missing or invalid");
      }

      // Nếu chưa có Authentication trong SecurityContext
      if (SecurityContextHolder.getContext().getAuthentication() == null) {
        try {
          UserDetails userDetails = this.customUserDetailsService.loadUserByUsername(username);

          // Xác thực token
          if (jwtTokenUtil.validateToken(token, userDetails.getUsername())) {
            UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());
            authenticationToken.setDetails(
                new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext()
                .setAuthentication(authenticationToken); // Lưu Authentication vào SecurityContext
          } else {
            // Nếu token không hợp lệ, trả về lỗi 403
            throw new ForbiddenException("Invalid JWT token");
          }
        } catch (Exception e) {
          // Nếu không thể xác thực người dùng hoặc có lỗi khác, trả về lỗi 403
          throw new ForbiddenException("Invalid JWT token");
        }
      }

      // Tiếp tục chuỗi filter nếu không có lỗi
      chain.doFilter(request, response);
    } catch (ForbiddenException e) {
      // Nếu ngoại lệ ForbiddenException được ném, trả về mã trạng thái HTTP 403
      HttpServletResponse httpResponse = (HttpServletResponse) response;
      httpResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
      httpResponse.setContentType("application/json");
      httpResponse.setCharacterEncoding("UTF-8");

      // Tạo đối tượng lỗi để trả về dưới dạng JSON
      ErrorResponse errorResponse = new ErrorResponse(HttpStatus.FORBIDDEN, e.getMessage());
      String jsonResponse = new ObjectMapper().writeValueAsString(errorResponse);
      httpResponse.getWriter().write(jsonResponse);
      httpResponse.getWriter().flush();
      return;
    }
  }


}