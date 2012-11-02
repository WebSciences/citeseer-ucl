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

import edu.psu.citeseerx.dao2.logic.CSXDAO;

import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import java.io.*;

public class FileDownloadController implements Controller {
    
    private CSXDAO csxdao;
    
    public void setCSXDAO (CSXDAO csxdao) {
        this.csxdao = csxdao;
    }
    
    public ModelAndView handleRequest(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        
        String doi = request.getParameter("doi");
        String rep = request.getParameter("rep");
        String type = request.getParameter("type");
        
        if (doi == null || type == null) {
            
        }
        
        BufferedInputStream input = null;
        BufferedOutputStream output = null;
        
        try {
            response.reset();
            if (type.equalsIgnoreCase("pdf")) {
                response.setContentType("application/pdf");
                response.setHeader("Content-Disposition",
                        "attachment; filename=\""+doi+".pdf\"");
            }
        
            FileInputStream in = csxdao.getFileInputStream(doi, rep, type);
            input = new BufferedInputStream(in);
        
            int contentLength = input.available();
            response.setContentLength(contentLength);
        
            output = new BufferedOutputStream(response.getOutputStream());
            while(contentLength-- > 0) {
                output.write(input.read());
            }
            output.flush();

        } catch (IOException e) {
            throw e;
        } finally {
            try { input.close(); } catch (Exception exc) {}
            try { output.close(); } catch (Exception exc) {}
        }
        
        return null;
        
    }  //- handleRequest

}  //- class FileDownloadController
