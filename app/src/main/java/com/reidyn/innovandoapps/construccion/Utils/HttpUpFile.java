package com.reidyn.innovandoapps.construccion.Utils;
/*
 * Copyright (C) 2015 PICO S.A.C.I.
 * Desarrollador: Marcos Ramirez
 *
 */
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import android.util.Log;
/*******************************************************
 * @author windows 8.1
 * Clase para el envio de imagenes por http
 ********************************************************/
public class HttpUpFile {
	
	private DefaultHttpClient httpCliente;
	private HttpPost httppost;
	
	public HttpUpFile(){
		httpCliente=new DefaultHttpClient();
	}
	
	public boolean subirFichero(File file,List<NameValuePair>parametros,String urlWebserver,int tipo){
		HttpResponse result;
		String response="";
		try {
			httpCliente.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
			httppost=new HttpPost(urlWebserver);
			MultipartEntity multipartEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE); 
			switch (tipo) {
				case 1:
					multipartEntity.addPart("foto", new FileBody(file));
				break;
				case 2:
					//multipartEntity.addPart("adjuntar", new FileBody(file));
					multipartEntity.addPart("foto", new FileBody(file));
				break;
			}
			if(parametros!=null){
				for(int i=0;i<parametros.size();i++){
					multipartEntity.addPart(parametros.get(i).getName(),new StringBody(parametros.get(i).getValue()));
				}
			}
			httppost.setEntity(multipartEntity);
			result=httpCliente.execute(httppost);
			response=parseResponse(result.getEntity().getContent());
			Log.i("REsponse", response.toString());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return false;
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	private String parseResponse(InputStream in){
		StringBuilder sb = new StringBuilder();;
		try {
			BufferedReader reader=new BufferedReader(new InputStreamReader(in,"iso-8859-1"),8);	
			String line = null;
			while ((line = reader.readLine()) != null) {
	                sb.append(line);
	        }
			in.close();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		 
		return sb.toString();
	}
}
