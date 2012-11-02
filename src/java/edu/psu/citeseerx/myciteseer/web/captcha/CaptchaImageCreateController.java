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
package edu.psu.citeseerx.myciteseer.web.captcha;

import org.springframework.web.servlet.mvc.*;
import org.springframework.web.servlet.*;
import org.springframework.beans.factory.*;

import com.octo.captcha.service.image.*;

import javax.servlet.http.*;
import javax.servlet.*;

import java.io.*;
import java.awt.image.*;

import com.sun.image.codec.jpeg.*;


public class CaptchaImageCreateController
implements Controller, InitializingBean{

    private ImageCaptchaService jcaptchaService;
    
    public ModelAndView handleRequest(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        
        byte[] captchaChallengeAsJpeg = null;
        ByteArrayOutputStream jpegOutputStream = new ByteArrayOutputStream();
        String captchaId = request.getSession().getId();
        
        BufferedImage challenge =
            jcaptchaService.getImageChallengeForID(captchaId,
                    request.getLocale());
        
        JPEGImageEncoder jpegEncoder =
            JPEGCodec.createJPEGEncoder(jpegOutputStream);
        jpegEncoder.encode(challenge);
        
        captchaChallengeAsJpeg = jpegOutputStream.toByteArray();
        
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
        response.setContentType("image/jpeg");
        ServletOutputStream responseOutputStream = response.getOutputStream();
        responseOutputStream.write(captchaChallengeAsJpeg);
        responseOutputStream.flush();
        responseOutputStream.close();
        return null;
            
    }  //- handleRequest
    
    
    public void setJcaptchaService(ImageCaptchaService jcaptchaService) {
        this.jcaptchaService = jcaptchaService;
    }
    
    
    public void afterPropertiesSet() throws Exception {
        if (jcaptchaService == null) {
            throw new RuntimeException("Image captcha service wasn't set!");
        }
    }
    
}  //- class CaptchaImageCreateController
