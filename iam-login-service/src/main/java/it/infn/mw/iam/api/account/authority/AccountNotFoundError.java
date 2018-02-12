package it.infn.mw.iam.api.account.authority;

public class AccountNotFoundError extends RuntimeException {

  /**
   * 
   */
  private static final long serialVersionUID = -7259771093968890099L;

  public AccountNotFoundError(String message) {
    super(message);

  }
  
  public static final AccountNotFoundError forUuid(String uuid) {
    return new AccountNotFoundError(String.format("Account not found for id '%s'", uuid));
  }
}
