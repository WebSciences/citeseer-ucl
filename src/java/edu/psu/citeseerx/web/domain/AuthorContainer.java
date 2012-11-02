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
package edu.psu.citeseerx.web.domain;

import java.io.Serializable;

public class AuthorContainer implements Serializable {

    private String name;
    private String affil;
    private String address;
    private String email;
    private String order;
    
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public String getAffil() {
        return affil;
    }
    public void setAffil(String affil) {
        this.affil = affil;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getOrder() {
        return order;
    }
    public void setOrder(String order) {
        this.order = order;
    }
    
    private boolean deleted = false;
    
    public boolean getDeleted() {
        return deleted;
    }
    
    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
    
}
