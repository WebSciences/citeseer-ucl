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

import edu.psu.citeseerx.myciteseer.domain.*;
import edu.psu.citeseerx.myciteseer.web.utils.MCSUtils;

import java.util.List;

import java.io.Serializable;

public class UrlSubmissionForm implements Serializable {
    
    private UrlSubmission submission;
    private List submissions;
    
    public UrlSubmissionForm() {
        submission = new UrlSubmission();
        submission.setUsername(MCSUtils.getLoginAccount().getUsername());
    }
    
    public List getPreviousSubmissions() {
        return submissions;
    }
    
    public void setPreviousSubmissions(List submissions) {
        this.submissions = submissions;
    }
    
    public UrlSubmission getUrlSubmission() {
        return submission;
    }
    
    public void setUrlSubmission(UrlSubmission submission) {
        this.submission = submission;
    }
    
}
