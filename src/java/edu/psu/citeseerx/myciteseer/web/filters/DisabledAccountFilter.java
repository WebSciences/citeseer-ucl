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
package edu.psu.citeseerx.myciteseer.web.filters;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;

import edu.psu.citeseerx.myciteseer.domain.Account;
import edu.psu.citeseerx.myciteseer.web.utils.MCSUtils;

public class DisabledAccountFilter implements Filter {


    public void init(FilterConfig config) throws ServletException {
        //this.config = config;        
    }
    
    public void destroy() { }
    
    
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain) throws ServletException, IOException {

        Account account = MCSUtils.getLoginAccount();
        if (account == null || !account.isEnabled()) {
            String context = ((HttpServletRequest)request).getContextPath();
            HttpServletResponse hres = (HttpServletResponse)response;
            hres.sendRedirect(context+"/messages/account_disabled.html");
            return;
        }
        chain.doFilter(request, response);
        
    }  //- doFilter
    
}  //- class IncompleteAccountFilter
