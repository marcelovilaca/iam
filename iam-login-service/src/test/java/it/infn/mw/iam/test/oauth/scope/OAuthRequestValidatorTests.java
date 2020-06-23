/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). 2016-2019
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package it.infn.mw.iam.test.oauth.scope;

import static com.google.common.collect.Sets.newHashSet;
import static it.infn.mw.iam.core.oauth.scope.matchers.StringEqualsScopeMatcher.stringEqualsMatcher;
import static it.infn.mw.iam.core.oauth.scope.matchers.StructuredPathScopeMatcher.structuredPathMatcher;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.oauth2.common.exceptions.InvalidScopeException;
import org.springframework.security.oauth2.provider.AuthorizationRequest;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.TokenRequest;

import com.google.common.collect.Sets;

import it.infn.mw.iam.core.oauth.scope.matchers.ScopeMatcherOAuthRequestValidator;
import it.infn.mw.iam.core.oauth.scope.matchers.ScopeMatcherRegistry;

@RunWith(MockitoJUnitRunner.class)
public class OAuthRequestValidatorTests {

  @Spy
  AuthorizationRequest authzRequest = new AuthorizationRequest();

  @Mock
  TokenRequest tokenRequest;

  @Mock
  ClientDetails client;

  @Mock
  ScopeMatcherRegistry registry;

  ScopeMatcherOAuthRequestValidator validator;

  @Before
  public void setup() {

    when(client.getClientId()).thenReturn("exampleClient");
    when(client.getScope()).thenReturn(newHashSet("openid", "profile"));
    authzRequest.setScope(Sets.newHashSet("openid"));
    when(registry.findMatchersForClient(client))
      .thenReturn(newHashSet(stringEqualsMatcher("openid"), stringEqualsMatcher("profile")));
    validator = new ScopeMatcherOAuthRequestValidator(registry);
  }

  @Test
  public void testSimpleValidationSuccess() {
    validator.validateScope(authzRequest, client);
  }

  @Test(expected = InvalidScopeException.class)
  public void testSimpleValidationFailure() {
    authzRequest.setScope(Sets.newHashSet("openid", "storage.read:/"));
    validator.validateScope(authzRequest, client);
  }

  @Test
  public void testStructuredScopeValidationSuccess() {

    authzRequest.setScope(Sets.newHashSet("openid", "storage.read:/subdir", "storage.read:/"));
    when(registry.findMatchersForClient(client)).thenReturn(
        newHashSet(stringEqualsMatcher("openid"), structuredPathMatcher("storage.read", "/")));
    
    validator.validateScope(authzRequest, client);

  }
  
  @Test(expected = InvalidScopeException.class)
  public void testStructuredScopeValidationFailure() {

    authzRequest.setScope(Sets.newHashSet("openid", "storage.read:/subdir"));
    when(registry.findMatchersForClient(client)).thenReturn(
        newHashSet(stringEqualsMatcher("openid"), structuredPathMatcher("storage.read", "/other")));
    
    validator.validateScope(authzRequest, client);

  }

}
