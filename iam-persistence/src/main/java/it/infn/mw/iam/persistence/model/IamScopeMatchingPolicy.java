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
package it.infn.mw.iam.persistence.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;


@Embeddable
public class IamScopeMatchingPolicy {
  
  @Enumerated(EnumType.STRING)
  @Column(name = "m_type", nullable = false, length = 6)
  IamScopePolicy.MatchingPolicy type;

  @Column(name = "m_param", nullable = true, length = 256)
  String matchParam;

  public IamScopeMatchingPolicy() {
    // empty ctor
  }

  public IamScopePolicy.MatchingPolicy getType() {
    return type;
  }

  public void setType(IamScopePolicy.MatchingPolicy type) {
    this.type = type;
  }

  public String getMatchParam() {
    return matchParam;
  }

  public void setMatchParam(String matchParam) {
    this.matchParam = matchParam;
  }
  
}
