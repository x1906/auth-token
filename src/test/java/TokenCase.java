import com.ybveg.auth.config.TokenProperties;
import com.ybveg.auth.token.Token;
import com.ybveg.auth.token.TokenFactory;
import org.junit.Before;
import org.junit.Test;

/**
 * @auther zbb
 * @create 2017/9/28
 */
public class TokenCase {

  private TokenProperties properties;

  private TokenFactory factory;

  private final String rawToken = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ7XCJkYXRhQ29kZVwiOlwiXCIsXCJkZXB0SWRcIjpcIjAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwXCIsXCJkZXB0VHlwZVwiOlwiU1lTXCIsXCJpZFwiOlwiMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDBcIn0iLCJqdGkiOiI2ZGE2ZTI4MC1mYjRlLTQ3YzMtOWY4ZC02OWNmNGJmOWZiNjYiLCJ1c2VyX2lkIjoiMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAiLCJpc3MiOiJodHRwOi8vd3d3LnlidmVnLmNvbSIsImlhdCI6MTUwNjU2NTI0NSwiZXhwIjoxNTA2NTY4ODQ1fQ.JOOnk48wSCrbyNdBvMlGtIsiYrcl9ot4zlLmByXf-WTgH2PB2-ii6Abv6VeGlxghf4cnYoMNuS2Rtlll5ZahRA";

  @Before
  public void init() {
    properties = new TokenProperties();
    properties.setExpire(60);
    properties.setRefreshExpire(10080);
    properties.setSecert("p1OuR6dGrxWnIe1BJ3x3IOftxCoh6nby");
    properties.setIssuer("http://www.ybveg.com");
    factory = new TokenFactory(properties);

  }


  @Test
  public void parse() {
    Token token = factory.parseAccess(rawToken);
    System.out.println(token.getId());
  }
}
