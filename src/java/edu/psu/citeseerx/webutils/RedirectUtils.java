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

import javax.servlet.http.*;
import java.io.IOException;

public class RedirectUtils {

    public static void redirectAcegiLogin(HttpServletRequest request,
            HttpServletResponse response,
            String username, String password)
    throws IOException {
        String context = request.getContextPath();
        String captcha = request.getParameter("j_captcha_response");
        response.sendRedirect(context+"/j_acegi_security_check?"+
                "j_username="+username+"&j_password="+password+
                "&j_captcha_response="+captcha);
    }
        
    public static void redirectAcegiLogout(HttpServletRequest request,
            HttpServletResponse response)
            throws IOException {
        String context = request.getContextPath();
        response.sendRedirect(context+"/j_acegi_logout");
    }
    
    public static void sendRedirect(HttpServletRequest request,
            HttpServletResponse response, String path) throws IOException {
        String context = request.getContextPath();
        response.sendRedirect(context+path);
    }
    
    public static void sendPermanentRedirect(HttpServletRequest request,
            HttpServletResponse response, String path) throws IOException {
        String context = request.getContextPath();
        response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
        response.addHeader("Location", context+path);
    }
    
    public static void sendDocumentCIDRedirect(HttpServletRequest request,
            HttpServletResponse response, String doi) {
        StringBuffer urlBuf = request.getRequestURL();
        String queryStr = request.getQueryString();
        
        queryStr = queryStr.replaceAll("cid=\\d+", "doi="+doi);
        urlBuf.append("?");
        urlBuf.append(queryStr);

        response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
        response.addHeader("Location", urlBuf.toString());
    }
    
    public static String getBaseUrl(HttpServletRequest request) {
        StringBuilder builder = new StringBuilder();
        builder.append(request.getScheme());
        builder.append("://");
        builder.append(request.getLocalName());
        int port = request.getLocalPort();
        if (port != 80 && port != 443) {
            builder.append(":");
            builder.append(port);
        }
        builder.append(request.getContextPath());
        return builder.toString();
    }
}
