package com.netflix.spinnaker.gate.security;

import com.netflix.spinnaker.gate.services.CredentialsService;
import java.util.Collection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/** Returns list of WRITE enabled accounts to populate X-SPINNAKER-ACCOUNTS header. */
@Component
public class AllowedAccountsSupport {
  private final Logger log = LoggerFactory.getLogger(getClass());

  //  private final FiatStatus fiatStatus;

  //  private final FiatPermissionEvaluator fiatPermissionEvaluator;

  private final CredentialsService credentialsService;

  @Autowired
  public AllowedAccountsSupport(CredentialsService credentialsService) {
    this.credentialsService = credentialsService;
  }

  public Collection<String> filterAllowedAccounts(String username, Collection<String> roles) {
    //    if (fiatStatus.isEnabled()) {
    //      UserPermission.View permission =
    //          AuthenticatedRequest.allowAnonymous(
    //              () -> fiatPermissionEvaluator.getPermission(username));
    //      if (permission == null) {
    //        return new ArrayList<>();
    //      }
    //
    //      if (permission.isLegacyFallback()) {
    //        // fetch allowed accounts as if fiat were not enabled (ie. check available roles
    // against
    //        // clouddriver directly)
    //        Collection<String> allowedAccounts = credentialsService.getAccountNames(roles, true);
    //
    //        log.warn(
    //            "Unable to fetch fiat permissions, will fallback to legacy account permissions
    // (user: {}, roles: {}, allowedAccounts: {})",
    //            username,
    //            roles,
    //            allowedAccounts);
    //
    //        return allowedAccounts;
    //      }
    //
    //      return permission.getAccounts().stream()
    //          .filter(v -> v.getAuthorizations().contains(Authorization.WRITE))
    //          .map(Account.View::getName)
    //          .collect(Collectors.toSet());
    //    }

    return credentialsService.getAccountNames(roles);
  }
}
