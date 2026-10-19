package org.ikigaidigital.adapter.web;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Balances are financial data: no browser or proxy may cache them, and browsers must not guess the
 * content type. Set before the chain runs, so error responses carry them too.
 */
@Component
class SecurityHeadersFilter extends OncePerRequestFilter {

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain chain)
      throws ServletException, IOException {
    response.setHeader(HttpHeaders.CACHE_CONTROL, "no-store");
    response.setHeader("X-Content-Type-Options", "nosniff");
    chain.doFilter(request, response);
  }
}
