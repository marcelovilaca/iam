package it.infn.mw.iam.api.account.group_manager;

import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import it.infn.mw.iam.api.account.authority.AccountNotFoundError;
import it.infn.mw.iam.api.account.group_manager.error.InvalidManagedGroupError;
import it.infn.mw.iam.api.account.group_manager.model.AccountManagedGroupsDTO;
import it.infn.mw.iam.api.common.ErrorDTO;
import it.infn.mw.iam.api.scim.converter.UserConverter;
import it.infn.mw.iam.api.scim.model.ScimUser;
import it.infn.mw.iam.persistence.model.IamAccount;
import it.infn.mw.iam.persistence.model.IamGroup;
import it.infn.mw.iam.persistence.repository.IamAccountRepository;
import it.infn.mw.iam.persistence.repository.IamGroupRepository;

@RestController
public class AccountGroupManagerController {

  final AccountGroupManagerService service;
  final IamAccountRepository accountRepository;
  final IamGroupRepository groupRepository;
  final UserConverter userConverter;

  @Autowired

  public AccountGroupManagerController(AccountGroupManagerService service,
      IamAccountRepository accountRepo, IamGroupRepository groupRepository,
      UserConverter userConverter) {
    this.service = service;
    this.accountRepository = accountRepo;
    this.groupRepository = groupRepository;
    this.userConverter = userConverter;
  }



  @RequestMapping(value = "/iam/account/{accountId}/managed-groups", method = RequestMethod.GET)
  @PreAuthorize("hasRole('ADMIN') or #iam.isUser(#accountId)")
  public AccountManagedGroupsDTO getAccountManagedGroupsInformation(
      @PathVariable String accountId) {
    IamAccount account = accountRepository.findByUuid(accountId)
      .orElseThrow(() -> AccountNotFoundError.forUuid(accountId));

    return service.getManagedGroupInfoForAccount(account);
  }

  @RequestMapping(value = "/iam/account/{accountId}/managed-groups/{groupId}",
      method = RequestMethod.POST)
  @PreAuthorize("hasRole('ADMIN')")
  @ResponseStatus(value = HttpStatus.CREATED)
  public void addManagedGroupToAccount(@PathVariable String accountId,
      @PathVariable String groupId) {

    IamAccount account = accountRepository.findByUuid(accountId)
      .orElseThrow(() -> AccountNotFoundError.forUuid(accountId));

    IamGroup group = groupRepository.findByUuid(groupId)
      .orElseThrow(() -> InvalidManagedGroupError.groupNotFoundException(groupId));

    service.addManagedGroupForAccount(account, group);
  }

  @RequestMapping(value = "/iam/account/{accountId}/managed-groups/{groupId}",
      method = RequestMethod.DELETE)
  @PreAuthorize("hasRole('ADMIN')")
  @ResponseStatus(value = HttpStatus.NO_CONTENT)
  public void removeManagedGroupFromAccount(@PathVariable String accountId,
      @PathVariable String groupId) {

    IamAccount account = accountRepository.findByUuid(accountId)
      .orElseThrow(() -> AccountNotFoundError.forUuid(accountId));

    IamGroup group = groupRepository.findByUuid(groupId)
      .orElseThrow(() -> InvalidManagedGroupError.groupNotFoundException(groupId));

    service.removeManagedGroupForAccount(account, group);
  }

  @RequestMapping(value = "/iam/group/{groupId}/group-managers")
  @PreAuthorize("hasRole('ADMIN') or #iam.isGroupManager(#groupId)")
  public List<ScimUser> getGroupManagersForGroup(@PathVariable String groupId) {
    IamGroup group = groupRepository.findByUuid(groupId)
      .orElseThrow(() -> InvalidManagedGroupError.groupNotFoundException(groupId));

    return service.getGroupManagersForGroup(group)
      .stream()
      .map(userConverter::dtoFromEntity)
      .collect(Collectors.toList());

  }

  @ResponseStatus(value = HttpStatus.BAD_REQUEST)
  @ExceptionHandler(AccountNotFoundError.class)
  public ErrorDTO accountNotFoundError(HttpServletRequest req, Exception ex) {
    return ErrorDTO.fromString(ex.getMessage());
  }

  @ResponseStatus(value = HttpStatus.BAD_REQUEST)
  @ExceptionHandler(InvalidManagedGroupError.class)
  public ErrorDTO invalidManagedGroupError(HttpServletRequest req, Exception ex) {
    return ErrorDTO.fromString(ex.getMessage());
  }

}
