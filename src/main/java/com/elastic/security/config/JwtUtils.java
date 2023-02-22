package com.elastic.security.config;

import com.elastic.common.util.Utils;
import io.jsonwebtoken.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtils {
  private static final Logger LOGGER = LogManager.getLogger(JwtUtils.class);

  @Value("${fulfilman.app.jwtSecret}")
  private String jwtSecret;

  @Value("${fulfilman.app.jwtExpirationDay}")
  private int jwtExpirationDay;

  public String generateJwtToken(Authentication authentication) {

      User userPrincipal = (User)authentication.getPrincipal();

      return Jwts.builder().setSubject((userPrincipal.getUsername())).setIssuedAt(new Date())
          .setExpiration(Utils.addDays(new Date(), jwtExpirationDay)).signWith(SignatureAlgorithm.HS512, jwtSecret)
          .compact();
  }

  public String getUserNameFromJwtToken(String token) {
      return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
  }

  public boolean validateJwtToken(String authToken) {
      try {
        Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
        return true;
      } catch (SignatureException e) {
          LOGGER.error("Invalid JWT signature: {}", e.getMessage());
      } catch (MalformedJwtException e) {
          LOGGER.error("Invalid JWT token: {}", e.getMessage());
      } catch (ExpiredJwtException e) {
          LOGGER.error("JWT token is expired: {}", e.getMessage());
      } catch (UnsupportedJwtException e) {
          LOGGER.error("JWT token is unsupported: {}", e.getMessage());
      } catch (IllegalArgumentException e) {
          LOGGER.error("JWT claims string is empty: {}", e.getMessage());
      }

      return false;
  }
}
