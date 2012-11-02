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
package edu.psu.citeseerx.webutils;

import java.io.*;
import java.util.HashMap;
import javax.servlet.*;
import javax.servlet.http.*;

public class SimpleDownloadLimitFilter implements Filter {
    
    private int limit = 1000;
    private String redirectUrl = "";
    
    private HashMap<String,Integer> dlCounts = new HashMap<String,Integer>();
    private String[] allowedUserAgents = new String[0];
    
    public void init(FilterConfig config) throws ServletException {
        
        String redirectUrlStr = config.getInitParameter("redirectUrl");
        if (redirectUrlStr != null) {
            redirectUrl = redirectUrlStr;
        } else {
            System.err.println("SimpleDownloadLimitFilter: no redirectUrl " +
                    "specified!");
        }
        
        String limitStr = config.getInitParameter("limit");
        try {
            limit = Integer.parseInt(limitStr);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("SimpleDownloadLimitFilter: +" +
                    "Invalid limit specified: "+limitStr);
            System.err.println("Using default limit of "+limit);
        }
        String allowed = config.getInitParameter("allowedUserAgents");
        if (allowed != null) {
            allowedUserAgents = allowed.split(",");
            for (int i=0; i<allowedUserAgents.length; i++) {
                String str = allowedUserAgents[i];
                str = str.trim();
                str = str.toLowerCase();
                allowedUserAgents[i] = str;
            }
        }
        
    }  //- init
    
    
    public void doFilter(ServletRequest request,
            ServletResponse response, FilterChain chain)
    throws IOException, ServletException {

        if (allowedUserAgent(request)) {
            chain.doFilter(request, response);
            return;
        }
        
        if (downloadsExceeded(request)) {
            if (response instanceof HttpServletResponse) {
                HttpServletResponse hreq = (HttpServletResponse)response;
                hreq.setStatus(403);
                hreq.sendRedirect(redirectUrl);
            }
            return;
        }
        chain.doFilter(request, response);
        
    }  //- doFilter
    
    
    private synchronized boolean downloadsExceeded(ServletRequest request) {

        updateFlushTime();
        String ipaddr = request.getRemoteAddr();

        if (dlCounts.containsKey(ipaddr)) {
            Integer dlc = dlCounts.get(ipaddr);
            if (dlc >= limit) {
                return true;
            }
            dlCounts.put(ipaddr, dlc+1);
        } else {
            dlCounts.put(ipaddr, new Integer(1));
        }
        return false;
        
    }  //- downloadsExceeded
        
    
    private long lastFlushTime = System.currentTimeMillis();
    private static final long ONE_DAY = 1000*60*60*24;
    
    private void updateFlushTime() {
        long currentTime = System.currentTimeMillis();
        if (currentTime-lastFlushTime >= ONE_DAY) {
            dlCounts.clear();
            lastFlushTime = currentTime;
        }
        
    }  //- updateFlushTime
    
    
    private boolean allowedUserAgent(ServletRequest request) {

        if (request instanceof HttpServletRequest) {
            HttpServletRequest hreq = (HttpServletRequest)request;
            String useragent = hreq.getHeader("User-Agent");
            String user = (useragent != null) ? useragent.toLowerCase() : "";
            for (int i=0; i<allowedUserAgents.length; i++) {
                if (user.indexOf(allowedUserAgents[i]) != -1) {
                    return true;
                }
            }
            
        }
        return false;
        
    }  //- allowedUserAgent

    
    public void destroy() {}

}  //- class SimpleDownloadFilter
