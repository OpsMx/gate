package com.opsmx.spinnaker.gate;

import java.util.Objects;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Transient;
import org.springframework.security.core.authority.AuthorityUtils;

/*
   A marker for Authentications that should never be stored across requests, for example a bearer token authentication
*/
@Transient
public class CustomApiTokenAuthentication extends AbstractAuthenticationToken {

  private String apiToken;
  private String username;

  /*
   GrantedAuthority as being a "permission" or a "right". Those "permissions" are (normally) expressed as
   strings (with the getAuthority() method). Those strings let you identify the permissions and let your
   voters decide if they grant access to something.
  */
  public CustomApiTokenAuthentication(String apiToken, boolean authenticated) {
    super(AuthorityUtils.NO_AUTHORITIES);
    this.apiToken = apiToken;
    setAuthenticated(authenticated);
  }

  public CustomApiTokenAuthentication(String apiToken) {
    super(AuthorityUtils.NO_AUTHORITIES);
    this.apiToken = apiToken;
    setAuthenticated(false);
  }

  public CustomApiTokenAuthentication() {
    super(AuthorityUtils.NO_AUTHORITIES);
    setAuthenticated(false);
  }

  @Override
  public Object getCredentials() {
    return apiToken;
  }

  /*
   The identity of the principal being authenticated. In the case of an authentication
   request with username and password, this would be the username.
  */
  @Override
  public Object getPrincipal() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    CustomApiTokenAuthentication that = (CustomApiTokenAuthentication) o;
    return Objects.equals(username, that.username);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), username);
  }
}
