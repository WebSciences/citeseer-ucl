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
package edu.psu.citeseerx.domain;

import java.util.Date;

/**
 * Bean container for parent URL objects.
 *
 * @author Isaac Councill
 * @version $Rev: 668 $ $Date: 2008-07-27 15:25:51 -0400 (Sun, 27 Jul 2008) $
 */
public class Hub  {
    
    private String url;
    private Date lastCrawled;
    private String repID;
    
    public Date getLastCrawled() {
        return lastCrawled;
    }
    public void setLastCrawled(Date lastCrawled) {
        this.lastCrawled = lastCrawled;
    }
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public String getRepID() {
        return repID;
    }
    public void setRepID(String repID) {
        this.repID = repID;
    }
    
}  //- class Hub
