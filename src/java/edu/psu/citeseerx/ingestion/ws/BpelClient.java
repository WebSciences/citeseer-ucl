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
package edu.psu.citeseerx.ingestion.ws;

import javax.xml.namespace.QName;
import javax.xml.rpc.*;
import org.apache.axis.*;
import java.util.*;

import edu.psu.citeseerx.ingestion.IngestionException;
import edu.psu.citeseerx.ingestion.datamappers.*;

/**
 * API class for connecting to a BPEL server to run an ingestion job.
 *
 * @author Isaac Councill
 * @version $Rev: 665 $ $Date: 2008-07-27 13:10:35 -0400 (Sun, 27 Jul 2008) $
 */
public class BpelClient {

    protected Service service;

    protected final QName operationName =
        new QName("http://citeseerx.org/ingestion/wsdl", "ingest");
    protected final QName xsdString =
        new QName("http://www.w3.org/2001/XMLSchema", "string");

    protected String endpointAddress;
    
    /**
     * Sets the URL where the BPEL service can be called.
     * @param endpointAddress
     */
    public void setEndpointAddress(String endpointAddress) {
        this.endpointAddress = endpointAddress;
    }
    
    
    public BpelClient() {
        try {
            ServiceFactory factory = ServiceFactory.newInstance();
            service = factory.createService(null);

        } catch (Exception e) {
            e.printStackTrace();
        }
    
    }  //- BpelClient
    
    
    /**
     * Calls the BPEL engine to ingest a document located at the relative
     * filePath from the specified repository.
     * @param filePath
     * @param repID
     * @return a Document object with parsed metadata.
     * @throws IngestionException
     */
    public edu.psu.citeseerx.domain.Document callService(String filePath,
            String repID) throws IngestionException {
        
        edu.psu.citeseerx.domain.Document doc = null;
        
        try {
            Call call = service.createCall();

            call.setTargetEndpointAddress(endpointAddress);
            call.setOperationName(operationName);

            String headerParse =
                (String)call.invoke(new Object[] {filePath, repID});

            String citeParse   = null;
            String convTrace   = null;
            
            List parts = call.getOutputValues();
            if (parts.size() != 2) {
                //throw
                throw new IngestionException("Expected 4 parts in response, " +
                        "found " + parts.size());
            }
            for (int i=0; i<2; i++) {
                switch(i) {
                case 0:
                    citeParse = new String((byte[])parts.get(i));
                    //System.out.println(1+ " "+citeParse);
                    break;
                case 1:
                    convTrace = (String)parts.get(i);
                    //System.out.println(4+ " "+convTrace);
                    break;
                }
            }

            doc = ParsHedMapper.map(headerParse);
            ParscitMapper.map(doc, citeParse);
            FileInfoMapper.map(doc, convTrace);
            
        } catch (AxisFault f) {
            //f.printStackTrace();
            IngestionException exc = new IngestionException(f.getMessage());
            exc.setStackTrace(f.getStackTrace());
            throw(exc);
        } catch (Exception e) {
            IngestionException exc = new IngestionException(e.getMessage());
            exc.setStackTrace(e.getStackTrace());
            throw(exc);
        }
        
        return doc;
        
    }  //- callService
    
    
    public static void main(String args[]) {
        String filePath = "files/semidef_prog.PDF";
        String repID = "rep1";
        BpelClient client = new BpelClient();
        edu.psu.citeseerx.domain.Document doc =
            client.callService(filePath, repID);
        System.out.println(doc.toXML(true));
    }
    
}  //- class BpelClient
