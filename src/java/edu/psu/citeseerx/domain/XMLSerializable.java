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

import java.io.*;
import org.jdom.Element;
import org.jdom.JDOMException;

/**
 * Interface to be implemented by domain objects that support bi-directional
 * XML serialization.
 *
 * @author Isaac Councill
 * @version $Rev: 668 $ $Date: 2008-07-27 15:25:51 -0400 (Sun, 27 Jul 2008) $
 */
public interface XMLSerializable {

    public final static boolean INCLUDE_SYS_DATA = true;
    public final static boolean PUBLIC_ONLY = false;
    
    public String toXML(boolean sysData);
    public void buildXML(StringBuffer buffer, boolean sysData);
    public void toXML(OutputStream out, boolean sysData) throws IOException;
    public void fromXML(InputStream in) throws IOException;
    public void fromXML(Element root) throws JDOMException;
    
}
