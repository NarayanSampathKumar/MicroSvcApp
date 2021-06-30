package com.webstore.core.engine.web.controller;

import java.io.IOException;

import java.io.*;
import java.text.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.webstore.core.uriconstants.ServerUris;
import com.webstore.core.uriconstants.URIConstants;

/**
 * Servlet implementation class CartController
 */
@WebServlet("/cartController/removeCartItems")
public class CartController extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public void logDebug(String type, String entity, String msg)
	{

		String logDateAndTime;
		PrintWriter logWriter;
		String log;
	
		java.util.Date date = new java.util.Date();
		logDateAndTime = getDateAndTime(date);

		try
		{
			String ss = System.getProperty("app.log.path");
			if (ss == null ||  ss.length() <= 0) {
                ss = "/opt/egapp/logs/server.log";
            }
            System.out.println("Log path: "+ss);
			logWriter = new PrintWriter(new FileWriter(ss, true));
		}
		catch (Exception e)
		{
			System.err.println("Cannot open log file ");
			e.printStackTrace();
			return;
		}
		log =
			new String(logDateAndTime + " " + type + " " + entity + " " + msg);
		logWriter.println(log);
		logWriter.close();

	}

	private String getDateAndTime(java.util.Date d)
	{
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		return (sdf.format(d));
	}
       
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String productIdStr = request.getParameter("productId"); 
//		String redirectto =  request.getParameter("redirectto");
		System.out.println("INFO  CART-ACTION-DELETE  There is an item removed from cart. ProductID = "+productIdStr);
		logDebug("INFO", "CART-ACTION-DELETE", "There is an item removed from cart. ProductID = "+productIdStr);
		boolean emptyCart = false;
		int productId = -1;
		if(productIdStr != null){
			try{
				productId = Integer.parseInt(productIdStr);
				
			}
			catch(Exception ex){
				ex.printStackTrace();
			}
		}
		if(productId == -1){
			emptyCart = true;
			request.setAttribute("productIdString", productId);
		}
			int result = -1;
			try{
				RestTemplate restTemplate = new RestTemplate();
				HttpHeaders headers = new HttpHeaders();
				headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);

				Integer cartIdObj = (Integer)request.getSession(false).getAttribute("cartId");
				int cartId = -1;
				if(cartIdObj != null){
					cartId = cartIdObj.intValue();
				}
				JSONObject jsonObj = new JSONObject();
				try {
					jsonObj.put("cartId", cartId);
					jsonObj.put("productId", productId);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				//System.out.println("param jsonObj=>"+jsonObj.toString());
				
				UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(ServerUris.QUOTE_SERVER_URI + URIConstants.DELETE_CART).queryParam("params", jsonObj);	
				HttpEntity<?> entity = new HttpEntity<>(headers);
				HttpEntity<String> returnString = restTemplate.exchange(builder.build().toUri(), HttpMethod.GET, entity, String.class);
				JSONObject returnJsonObj = null;
				System.out.println("return String is= "+returnString);
				try {
					returnJsonObj = new JSONObject(returnString.getBody());
					System.out.println("return JSON Obj = "+ returnJsonObj);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				if(returnJsonObj!=null){
					result = returnJsonObj.getInt("cartId");
					System.out.println("return Json Obj = "+returnJsonObj+" result - "+result);
					if(emptyCart){
						request.getSession(false).setAttribute("cartId", -1);
					}
				}
			}
			catch(Exception e){
				e.printStackTrace();
			}
//			if(redirectto != null){
//				 response.sendRedirect(redirectto);
//			}
//			else{
//				 response.sendRedirect("viewcart.jsp");
//			}

		System.out.println("request = "+request);
	}
}
