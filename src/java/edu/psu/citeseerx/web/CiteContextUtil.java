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

import edu.psu.citeseerx.dao2.logic.CiteClusterDAO;
import edu.psu.citeseerx.utility.SafeText;

public class CiteContextUtil {

    private CiteClusterDAO citedao;
    
    public void setCiteClusterDAO(CiteClusterDAO citedao) {
        this.citedao = citedao;
    }
    
    
    private String premark = "=-=";
    private String postmark = "-=-";
    private String startTag = "<em>";
    private String endTag = "</em>";
    
    public void setPremark(String premark) {
        this.premark = premark;
    }
    
    public void setPostmark(String postmark) {
        this.postmark = postmark;
    }
    
    public void setStartTag(String startTag) {
        this.startTag = startTag;
    }
    
    public void setEndTag(String endTag) {
        this.endTag = endTag;
    }
    
    public String getContext(Long citing, Long cited) {
        String context = "No context found.";
        try {
            String rContext = citedao.getContext(citing, cited);
            if (rContext != null) {
                context = SafeText.cleanXML(rContext);
                context = context.replace(premark, startTag);
                context = context.replace(postmark, endTag);
                context = "..."+context+"...";
            }
        } catch (Exception e) { };

        return context;
        
    }  //- getContext
    
}  //- class CiteContextUtil
