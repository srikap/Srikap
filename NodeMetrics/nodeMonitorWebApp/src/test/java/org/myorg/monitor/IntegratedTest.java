package org.myorg.monitor;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.MediaType;

import org.json.simple.JSONObject;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class IntegratedTest {
	public static void main(String[] args){
		
		
		// CREATE REQUEST
		
		
		//curl -X POST -A 'Accept:application/json' -H 'Content-type:text/plain'  
		//-d'abc,def' 'http://localhost:8080/nodeMonitor/rest/nodeMetrics/nodeNames'
		
		String requestMetricsUrl = "http://localhost:8080/nodeMonitor/rest/nodeMetrics/nodeNames";

		Client client = Client.create();
		WebResource webResource = client.resource(requestMetricsUrl);
		ClientResponse response = webResource
				.accept(MediaType.APPLICATION_JSON)
				.post(ClientResponse.class);
		
		
		
		
		

		// GET REQUEST STATUS
		//curl -X GET -A 'Accept:application/json' -H 'Content-type:text/plain'  
				//-d'abc,def' 'http://localhost:8080/nodeMonitor/rest/nodeMetrics/request'

		String requestStatusUrl = "http://localhost:8080/nodeMonitor/rest/nodeMetrics/request/";

		String requestId="";
		requestStatusUrl = requestStatusUrl + requestId;
		webResource = client.resource(requestStatusUrl);
		response = webResource
				.accept(MediaType.APPLICATION_JSON)
				.get(ClientResponse.class);
		String output = response.getEntity(String.class);
		System.out.println(output);
		System.out.println("ResponseCode:");



	}

}
