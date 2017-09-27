package com.ybveg.auth.token;

import com.alibaba.fastjson.JSONObject;
import com.ybveg.auth.config.TokenProperties;
import com.ybveg.auth.exception.TokenExpiredException;
import com.ybveg.auth.exception.TokenInvalidException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;
import org.apache.commons.lang3.StringUtils;


public class TokenFactory {


  private TokenProperties properties;

  public TokenFactory() {

  }

  public TokenFactory(TokenProperties properties) {
    this.properties = properties;
  }

  /**
   * 创建数据访问Token
   *
   * @param userId 用户ID或者用户名
   * @param data 用户其他数据不要存敏感数据
   */
  public <T> AccessToken createAccessToken(String userId, T data) {

    if (StringUtils.isBlank(userId)) { //
      throw new IllegalArgumentException("Cannot create Token without userId");
    }

    String subjet = JSONObject.toJSONString(data);

    Claims claims = Jwts.claims().setSubject(subjet).setId(UUID());
    claims.put(Token.USER, userId);

    LocalDateTime currentTime = LocalDateTime.now();

    String token = Jwts.builder()
        .setClaims(claims)
        .setIssuer(properties.getIssuer())
        .setIssuedAt(Date.from(currentTime.atZone(ZoneId.systemDefault()).toInstant()))
        .setExpiration(Date.from(currentTime
            .plusMinutes(
                properties.getExpire())  // 数据令牌过期时间
            .atZone(ZoneId.systemDefault()).toInstant()))
        .signWith(SignatureAlgorithm.HS512, properties.getSecert())
        .compact();
    return new AccessToken(token, claims);
  }

  /**
   * 创建数据访问Token
   *
   * @param userId 用户ID或者用户名
   */
  public RefreshToken createRefreshToken(String userId) {
    if (StringUtils.isBlank(userId)) { //
      throw new IllegalArgumentException("Cannot create Token without userId");
    }
    Claims claims = Jwts.claims().setId(UUID());
    claims.put(Token.USER, userId);
    LocalDateTime currentTime = LocalDateTime.now();
    String token = Jwts.builder()
        .setClaims(claims)
        .setIssuer(properties.getIssuer())
        .setIssuedAt(Date.from(currentTime.atZone(ZoneId.systemDefault()).toInstant()))
        .setExpiration(Date.from(currentTime.plusMinutes(properties.getRefreshExpire())  // 刷新令牌过期时间
            .atZone(ZoneId.systemDefault()).toInstant()))
        .signWith(SignatureAlgorithm.HS512, properties.getSecert())
        .compact();
    return new RefreshToken(token, claims);
  }


  /**
   * 验证Toekn
   *
   * @param rawToken token
   */
  public AccessToken parseAccess(String rawToken)
      throws TokenExpiredException, TokenInvalidException {
    Jws<Claims> jws = validToken(rawToken);
    Claims claims = jws.getBody();
    return new AccessToken(rawToken, claims);
  }

  /**
   * 验证刷新令牌
   *
   * @param rawToken token
   */
  public RefreshToken parseRefresh(String rawToken)
      throws TokenExpiredException, TokenInvalidException {
    Jws<Claims> jws = validToken(rawToken);
    Claims claims = jws.getBody();
    return new RefreshToken(rawToken, claims);
  }

  public Jws<Claims> validToken(String rawToken)
      throws TokenExpiredException, TokenInvalidException {
    try {

      return Jwts.parser().setSigningKey(properties.getSecert())
          .parseClaimsJws(rawToken);
    } catch (ExpiredJwtException e) {   // token 过期
      throw new TokenExpiredException("Token Expired", e);
    } catch (UnsupportedJwtException | MalformedJwtException | SignatureException | IllegalArgumentException e) {
      // 不支持的JWT  修改畸形的jwt  签名错误的jwt  参数错误的jwt
      throw new TokenInvalidException("Token Invalid", e);
    }
  }

  /**
   * 获取刷新令牌有效时间
   */
  public int getRefreshExpire() {
    return properties.getRefreshExpire();
  }

  /**
   * 获取数据令牌有效时间
   */
  public int getAccessExpire() {
    return properties.getExpire();
  }

  private String UUID() {
    return UUID.randomUUID().toString();
  }
}
