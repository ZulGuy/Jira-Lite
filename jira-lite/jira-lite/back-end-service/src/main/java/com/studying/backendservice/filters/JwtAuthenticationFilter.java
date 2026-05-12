package com.studying.backendservice.filters;

import com.studying.backendservice.configurations.TenantContext;
import com.studying.backendservice.services.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtService jwtService;
  private final UserDetailsService userDetailsService;

  @Autowired
  public JwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService) {
    this.jwtService = jwtService;
    this.userDetailsService = userDetailsService;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {
    try {
      final String jwt = getJwtFromCookie(request);
      final String username;

      if (jwt == null) {
        TenantContext.setTenantId(request.getHeader("X-Tenant-Id"));
        filterChain.doFilter(request, response);
        return;
      }

      try {
        username = jwtService.extractUsername(jwt);
      } catch (Exception e) {
        filterChain.doFilter(request, response);
        return;
      }

      String tenant = jwtService.extractAllClaims(jwt).get("tennantId", String.class);

      if (tenant != null) {
        setTenant(tenant);
      } else {
        TenantContext.setTenantId("public");
      }

      if (SecurityContextHolder.getContext().getAuthentication() == null) {
        try {
          UserDetails userDetails = userDetailsService.loadUserByUsername(username);

          if (jwtService.isTokenValid(jwt, userDetails)) {
            UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authToken);
          }
        } catch (UsernameNotFoundException e) {
          filterChain.doFilter(request, response);
          return;
        }
      }

      filterChain.doFilter(request, response);
    } finally {
      TenantContext.clear();
    }
  }

  private String getJwtFromCookie(HttpServletRequest request) {
    if (request.getCookies() != null) {
      for (Cookie cookie : request.getCookies()) {
        if ("jwt".equals(cookie.getName())) {
          return cookie.getValue();
        }
      }
    }
    return null;
  }

  @Autowired
  private JdbcTemplate jdbcTemplate;

  public List<String> getAllTenantSchemas() {
    String sql = "SELECT schema_name FROM information_schema.schemata";
    return jdbcTemplate.queryForList(sql, String.class);
  }

  private void setTenant(String tenant) {
    for (String schema : getAllTenantSchemas()) {
      if (tenant.equals(schema) && !(tenant.equals("pg_catalog")
          || tenant.equals("information_schema") || tenant.equals("pg_toast"))) {
        TenantContext.setTenantId(tenant);
        return;
      }
    }
    TenantContext.setTenantId("public");
  }
}
