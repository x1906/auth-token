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
   * @param id 用户ID或者用户名
   * @param data 用户其他数据不要存敏感数据
   */
  public <T> AccessToken createAccessToken(String id, T data) {

    if (StringUtils.isBlank(id)) { //
      throw new IllegalArgumentException("Cannot create Token without id");
    }

    String subjet = JSONObject.toJSONString(data);

    Claims claims = Jwts.claims().setSubject(subjet);
    claims.put(Token.JWT_TOKEN, id);

    LocalDateTime currentTime = LocalDateTime.now();

    String token = Jwts.builder()
        .setClaims(claims)
        .setIssuer(properties.getIssuer())
        .setIssuedAt(Date.from(currentTime.atZone(ZoneId.systemDefault()).toInstant()))
        .setExpiration(Date.from(currentTime
            .plusMinutes(
                properties.getExpire())  //给token过期设置双倍时间 当token 超过这个时间的一般要求刷新token 在拦截器里实现
            .atZone(ZoneId.systemDefault()).toInstant()))
        .signWith(SignatureAlgorithm.HS512, properties.getSecert())
        .compact();
    return new AccessToken(token, claims);
  }

  /**
   * 验证或者刷新token 如果未刷新token  返回null
   *
   * @param rawToken token
   * @return 新的token
   */
  public AccessToken parseToken(String rawToken)
      throws TokenExpiredException, TokenInvalidException {
    Jws<Claims> jws = validToken(rawToken);
    Claims claims = jws.getBody();
    Date iat = claims.getIssuedAt();
    long time = new Date().getTime() - iat.getTime();
    if ((time / 1000 / 60) > properties.getRefreshExpire()) {  //令牌可用时间 超过过期时间 需要刷新
      AccessToken newToken = createAccessToken(claims.get(Token.JWT_TOKEN).toString(),
          claims.getSubject());
      newToken.setRefresh(true);
      return newToken;
    }
    return new AccessToken(rawToken, claims);
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
}
