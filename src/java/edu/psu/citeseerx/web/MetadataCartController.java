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
package edu.psu.citeseerx.web;

import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import java.util.*;
import java.io.*;

import edu.psu.citeseerx.domain.ThinDoc;
import edu.psu.citeseerx.utility.BiblioTransformer;

/**
 * Provides model objects to meta cart view.
 * @author Isaac Councill
 * @version $Rev: 810 $ $Date: 2008-12-02 14:05:57 -0500 (Tue, 02 Dec 2008) $
 */
public class MetadataCartController implements Controller {

    public ModelAndView handleRequest(HttpServletRequest request,
            HttpServletResponse response) throws ServletException {

        HashMap<String, Object> model = new HashMap<String, Object>();
        HttpSession session = request.getSession();
        Object obj = session.getAttribute(MetadataCartDWR.CART_ATTR);
        HashMap set = null;
        if (obj != null) {
            set = (HashMap)obj;
        }
        
        boolean error = false;
        String errMsg = "";
        
        String dl = request.getParameter("dl");
        if (dl != null && set != null && dl.equals("bibtex")) {
            try {
                downloadBibTeX(response, set);
                return null;
            } catch (IOException e) {
                e.printStackTrace();
                error = true;
                errMsg = "There was an error downloading your file";
            }
        }
        if (dl != null && set != null && dl.equals("refbib")) {
            try {
                downloadReferBibIX(response, set);
                return null;
            } catch (IOException e) {
                e.printStackTrace();
                error = true;
                errMsg = "There was an error downloading your file";
            }
        }
        
        String del = request.getParameter("del");
        if (del != null && set != null) {
            if (del.equals("all")) {
                set = null;
                session.removeAttribute(MetadataCartDWR.CART_ATTR);
            }
            try {
                Long id = Long.parseLong(del);
                set.remove(id);
            } catch (Exception e) {}
        }
        
        List docs = new ArrayList();
        if (set != null) {
            Iterator it = set.keySet().iterator();
            while(it.hasNext()) {
                docs.add(set.get(it.next()));
            }
        }
        model.put("error", new Boolean(error));
        model.put("errMsg", errMsg);
        model.put("docs", docs);
        model.put("pagetitle", "Metadata Cart");
        model.put("pagedescription", "Metadata Cart");
        model.put("pagekeywords", "Metadata Cart, references, bibtex, referBibIX");
        return new ModelAndView("metacart", model);
        
    }
    
    
    private void downloadBibTeX(HttpServletResponse response, HashMap docs)
    throws IOException {

        StringBuffer buffer = new StringBuffer();
        for (Iterator it = docs.keySet().iterator(); it.hasNext(); ) {
            ThinDoc doc = (ThinDoc)docs.get(it.next());
            buffer.append(BiblioTransformer.toBibTeX(doc));
            buffer.append("\n");
        }
        
        BufferedInputStream input = new BufferedInputStream(
                new ByteArrayInputStream(buffer.toString().getBytes()));
        BufferedOutputStream output =
            new BufferedOutputStream(response.getOutputStream());
        
        response.reset();
        response.setContentType("text/plain");
        response.setHeader("Content-Disposition",
                "attachment; filename=\"metacart.bib\"");

        int contentLength = input.available();
        response.setContentLength(contentLength);
    
        output = new BufferedOutputStream(response.getOutputStream());
        while(contentLength-- > 0) {
            output.write(input.read());
        }
        output.flush();
        
    }  //- download BibTeX
    
    
    private void downloadReferBibIX(HttpServletResponse response, HashMap docs)
    throws IOException {
        
        StringBuffer buffer = new StringBuffer();
        for (Iterator it = docs.keySet().iterator(); it.hasNext(); ) {
            ThinDoc doc = (ThinDoc)docs.get(it.next());
            buffer.append(BiblioTransformer.toReferBibIX(doc));
            buffer.append("\n");
        }
        
        BufferedInputStream input = new BufferedInputStream(
                new ByteArrayInputStream(buffer.toString().getBytes()));
        BufferedOutputStream output =
            new BufferedOutputStream(response.getOutputStream());
        
        response.reset();
        response.setContentType("text/plain");
        response.setHeader("Content-Disposition",
                "attachment; filename=\"metacart.txt\"");

        int contentLength = input.available();
        response.setContentLength(contentLength);
    
        output = new BufferedOutputStream(response.getOutputStream());
        while(contentLength-- > 0) {
            output.write(input.read());
        }
        output.flush();
        
    }  //- download ReferBibIX
    
}  //- class MetadataCartController
