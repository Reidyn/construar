package com.reidyn.innovandoapps.construccion.Services;
/*
 * Copyright (C) 2015 PICO S.A.C.I.
 * Desarrollador: Marcos Ramirez
 *
 */
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import android.app.IntentService;
import android.content.Intent;

import com.reidyn.innovandoapps.construccion.Conexion;
import com.reidyn.innovandoapps.construccion.Utils.HttpUpFile;

/*************************************************
 * @author windows 8.1                           *
 * IntentService de envio de imagen en segundo   *
 * plano                                         * 
 *************************************************/
public class UpImageService extends IntentService {
	
	private String url;
	private HttpUpFile httpcliente;
	
	public UpImageService() {
		super("UpImageService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		url= Conexion.URLUPIMG;
		File file=new File(intent.getExtras().getString("photopath"));
		List<NameValuePair>parametros=new ArrayList<NameValuePair>();
		parametros.add(new BasicNameValuePair("remision", intent.getExtras().getString("remision")));
		parametros.add(new BasicNameValuePair("idpedido", intent.getExtras().getString("idpedido")));
		parametros.add(new BasicNameValuePair("cantidad", intent.getExtras().getString("cantidad")));
		httpcliente=new HttpUpFile();
		httpcliente.subirFichero(file,parametros,url,intent.getExtras().getInt("tipo"));
	}
}
