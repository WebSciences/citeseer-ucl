/*
 * Copyright 2007 Penn State University
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.psu.citeseerx.myciteseer.web;

import java.io.Serializable;

import org.springframework.security.GrantedAuthorityImpl;

import edu.psu.citeseerx.myciteseer.domain.Account;


/**
 * Command object to manipulate/obtain user input to be used by
 * AccountFormController
 * @author Isaac Councill
 * @version $$Rev: 713 $$ $$Date: 2008-09-30 07:21:32 -0400 (Tue, 30 Sep 2008) $$
 */
public class AccountForm implements Serializable {
    
    private final Account account;
    private final boolean newAccount;
    private String repeatedPassword;
    private String captcha;
    
    public AccountForm(Account account) {
        this.account = account;
        this.newAccount = false;
    }
    
    public AccountForm() {
        this.account = new Account();
        this.newAccount = true;
        /* Each new user will have this role to be used
         * by the authorization process when required 
         */
        this.account.addGrantedAuthority(
        		new GrantedAuthorityImpl("ROLE_AUTHENTICATED"));
    }
    
    public Account getAccount() {
        return account;
    }
    
    public boolean isNewAccount() {
        return newAccount;
    }

    public void setRepeatedPassword(String repeatedPassword) {
        this.repeatedPassword = repeatedPassword;
    }
    
    public String getRepeatedPassword() {
        return repeatedPassword;
    }

	public String getCaptcha() {
		return captcha;
	}

	public void setCaptcha(String captcha) {
		this.captcha = captcha;
	}
    
}  //- class AccountForm
