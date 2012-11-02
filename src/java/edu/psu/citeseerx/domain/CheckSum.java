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

/**
 * Bean container for document file checksum information.
 *
 * @author Isaac Councill
 * @version $Rev: 668 $ $Date: 2008-07-27 15:25:51 -0400 (Sun, 27 Jul 2008) $
 */
public class CheckSum {

    private String sha1;
    private String doi;
    private String fileType;
    
    public CheckSum() { }
    
    public CheckSum(String sha1, String doi, String fileType) {
        this.sha1 = sha1;
        this.doi = doi;
        this.fileType = fileType;
    }
    
    public String getFileType() {
        return fileType;
    }
    public void setFileType(String fileType) {
        this.fileType = fileType;
    }
    public String getSha1() {
        return sha1;
    }
    public void setSha1(String sha1) {
        this.sha1 = sha1;
    }
    public String getDOI() {
        return doi;
    }
    public void setDOI(String doi) {
        this.doi = doi;
    }
    
}
