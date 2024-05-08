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

  private final CredentialsService credentialsService;

  @Autowired
  public AllowedAccountsSupport(CredentialsService credentialsService) {
    this.credentialsService = credentialsService;
  }

  public Collection<String> filterAllowedAccounts(String username, Collection<String> roles) {

    return credentialsService.getAccountNames(roles);
  }
}
