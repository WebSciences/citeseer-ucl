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
package edu.psu.citeseerx.myciteseer.web.admin;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;
import org.springframework.web.servlet.view.RedirectView;

import edu.psu.citeseerx.myciteseer.domain.Account;
import edu.psu.citeseerx.myciteseer.web.utils.MCSUtils;
import edu.psu.citeseerx.dao2.logic.CSXDAO;

import java.sql.SQLException;

public class EditBannerFormController extends SimpleFormController {

    private CSXDAO csxdao;
    
    public void setCSXDAO(CSXDAO csxdao) {
        this.csxdao = csxdao;
    }
    
    
    public EditBannerFormController() {
        setSessionForm(true);
        setValidateOnBinding(false);
        setCommandName("editBannerForm");
        setFormView("admin/editBanner");
        
    } //- EditBannerFormController


    protected Object formBackingObject(HttpServletRequest request) 
        throws Exception {

        EditBannerForm form = new EditBannerForm();
        form.setBanner(csxdao.getBanner());
        return form;
        
    } //- formBackingObject
    
    
    protected void onBindAndValidate(HttpServletRequest request,
            Object command, BindException errors) throws Exception {

    } //- onBindAndValidate
    

    protected Map referenceData(HttpServletRequest request) throws Exception {
        Map model = new HashMap();
        return model;
        
    }  //- referenceData
    
    
    protected ModelAndView onSubmit(HttpServletRequest request,
            HttpServletResponse response, Object command,
            BindException errors) throws Exception {
        
        EditBannerForm editBannerForm = (EditBannerForm)command;
        Account account = MCSUtils.getLoginAccount();
        if (!account.isAdmin()) {
            return new ModelAndView("adminRequired", new HashMap());
        }
        csxdao.setBanner(editBannerForm.getBanner());
        return new ModelAndView(new RedirectView(getSuccessView()));
        
    } //- onSubmit
    
} //- class EditBannerFormController
