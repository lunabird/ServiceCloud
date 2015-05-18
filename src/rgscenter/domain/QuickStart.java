/*
 * ====================================================================
 *
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */

package rgscenter.domain;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

public class QuickStart {
	public void post(String[] back) throws IOException{
		String result = new String();
		DefaultHttpClient httpclient = new DefaultHttpClient();
		 HttpPost httpPost = new HttpPost("http://60.5.1.22:8080/gas/AllOthers/setServiceAddress.action");
	        List <NameValuePair> nvps = new ArrayList <NameValuePair>();
	        nvps.add(new BasicNameValuePair("url", back[1]));
//	        nvps.add(new BasicNameValuePair("name", back[0]));
//	        nvps.add(new BasicNameValuePair("version", back[1]));
	        nvps.add(new BasicNameValuePair("id", back[0]));
	        for(NameValuePair i :nvps){
	        	System.out.println(i);
	        }
	        httpPost.setEntity(new UrlEncodedFormEntity(nvps,HTTP.UTF_8));
	        HttpResponse response2 = httpclient.execute(httpPost);

	        try {
	            System.out.println(response2.getStatusLine());
	            HttpEntity entity2 = response2.getEntity();
	            result = EntityUtils.toString(entity2);
	            System.out.println(result);
	            // do something useful with the response body
	            // and ensure it is fully consumed
//	            EntityUtils.consume(entity2);
	        } finally {
	            httpPost.releaseConnection();
	        }
			
	    }
		
	

    public static void main(String[] args) throws Exception {
//        DefaultHttpClient httpclient = new DefaultHttpClient();
//        HttpGet httpGet = new HttpGet("http://targethost/homepage");
//
//        HttpResponse response1 = httpclient.execute(httpGet);

        // The underlying HTTP connection is still held by the response object 
        // to allow the response content to be streamed directly from the network socket. 
        // In order to ensure correct deallocation of system resources 
        // the user MUST either fully consume the response content  or abort request 
        // execution by calling HttpGet#releaseConnection().

//        try {
//            System.out.println(response1.getStatusLine());
//            HttpEntity entity1 = response1.getEntity();
//            // do something useful with the response body
//            // and ensure it is fully consumed
//            EntityUtils.consume(entity1);
//        } finally {
//            httpGet.releaseConnection();
//        }

//        HttpPost httpPost = new HttpPost("http://192.169.10.7:3000/setServiceAddressByNameAndVersion");
//        List <NameValuePair> nvps = new ArrayList <NameValuePair>();
//        nvps.add(new BasicNameValuePair("url", "10.10.10.10"));
//        nvps.add(new BasicNameValuePair("name", "方向海情服务中心"));
//        nvps.add(new BasicNameValuePair("version", "1.0"));
//        for(NameValuePair i :nvps){
//        	System.out.println(i);
//        }
//        httpPost.setEntity(new UrlEncodedFormEntity(nvps,HTTP.UTF_8));
//        HttpResponse response2 = httpclient.execute(httpPost);
//
//        try {
//            System.out.println(response2.getStatusLine());
//            HttpEntity entity2 = response2.getEntity();
//            String result = EntityUtils.toString(entity2);
//            System.out.println(result);
//            // do something useful with the response body
//            // and ensure it is fully consumed
//            EntityUtils.consume(entity2);
//        } finally {
//            httpPost.releaseConnection();
//        }
    }

}
