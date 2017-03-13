package com.reidyn.innovandoapps.construccion;
/*
 * Copyright (C) 2015 PICO S.A.C.I.
 * Desarrollador: Marcos Ramirez
 *
 */
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.reidyn.innovandoapps.construccion.Services.UpImageService;

/*******************************************************
 * @author windows 8.1                                 *
 * Activity para la captura de Imagenes                *
 *******************************************************/
public class TakeImagenActivity extends FragmentActivity {
	
	///Objetos view
	private ImageButton imgbtnCapturar;
	private ImageButton imgbtnFolder;
	private LinearLayout layoutPreview;
	private ImageView imgPreview;
	private Button btnEnviar;
	private Button btnVolver;
	private Boolean swtake=false;
	///Variables
	private Uri mCapturedImageURI = null;
	public String path;
	private SharedPreferences prefs;
	private EditText edtRemision;
	private EditText edtCantidad;
	private Bundle bundle;
	private int tipo=0;
	//Constantes
	private static final int TAKE_PHOTO=1;
	private static final int SELECT_PHOTO=2;
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_take_imagen);
        
        prefs=getSharedPreferences("construar",MODE_PRIVATE);
        bundle=getIntent().getExtras();        
        imgbtnCapturar=(ImageButton)findViewById(R.id.imgbtnCapturar);
        imgbtnFolder=(ImageButton)findViewById(R.id.imgbtnFolder);
        layoutPreview=(LinearLayout)findViewById(R.id.layoutPreview);
        edtRemision=(EditText)findViewById(R.id.edtRemision);
        edtCantidad=(EditText)findViewById(R.id.edtCantidad);
        
        imgPreview=(ImageView)findViewById(R.id.imgPreview);
        btnEnviar=(Button)findViewById(R.id.btnEnviar);
        btnVolver=(Button)findViewById(R.id.btnVolver);
        edtCantidad.setText(bundle.getString("cantidad"));
        asignarEventos();
	}
	
	//Asigancionde programacion a objetos
	private void asignarEventos(){
		///CAptura de imagenes por camara
		imgbtnCapturar.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				tipo=1;
				File folder=new File(Environment.getExternalStorageDirectory()+"/DCIM/construar/");
				if(!folder.exists()){
					folder.mkdirs();
				}
				File file=new File(folder + File.separator + "IMG_"+String.valueOf(System.currentTimeMillis())+".jpg");
				path=file.getAbsolutePath();
				mCapturedImageURI = Uri.fromFile(file);
				Intent takePictureIntent =new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
					 SharedPreferences.Editor editor=prefs.edit();
					 editor.putString("photopath",path);
					 editor.commit();
					 takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,mCapturedImageURI);
					 startActivityForResult(takePictureIntent, TAKE_PHOTO);
				 }
			}
		});
		
		//Captura de imagenes por Galeria
		imgbtnFolder.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				tipo=2;
				Intent intent = new Intent();
				intent.setType("image/*");
				intent.setAction(Intent.ACTION_GET_CONTENT);
				startActivityForResult(Intent.createChooser(intent, "SELECCIONAR"), SELECT_PHOTO);
			}
		});
		
		//Envio de Imagen
		btnEnviar.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(swtake){
					if(edtRemision.getText().toString().equals("")){
						new DialogoRemision().show(getSupportFragmentManager(), "");
					}else{
						Bundle parametros=new Bundle();
						parametros.putString("remision", edtRemision.getText().toString());
						parametros.putString("idpedido", bundle.getString("idpedido"));
						parametros.putString("cantidad", bundle.getString("cantidad"));
						parametros.putString("photopath", reducirImagen(prefs.getString("photopath", "")));					
						parametros.putInt("tipo", tipo);
						Log.i("Parametros",parametros.toString());
						startService(new Intent(TakeImagenActivity.this,UpImageService.class).putExtras(parametros));
						layoutPreview.setVisibility(View.GONE);
						finish();
					}
				}
				
			}
		});
		
		//Salir del activity
		btnVolver.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	@SuppressLint("ValidFragment") ////dialogo para confirmar la salidad de la app///////////////////////////////
	private class DialogoRemision extends DialogFragment{//////////////////////////////////////////////////////
		 @Override
			public Dialog onCreateDialog(Bundle savedInstanceState) {
				AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
				builder.setMessage("Salir")
					   .setTitle("Salir de la Aplicacion")
					   .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();
						}
					 });
				return builder.create();
			}
	 }

	
	@Override///Retorno
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
				case TAKE_PHOTO:
					swtake=true;
					layoutPreview.setVisibility(View.VISIBLE);
					vistaPrevia(prefs.getString("photopath", ""));
				break;
				case SELECT_PHOTO:
					swtake=true;
					Uri uri = data.getData();
					String[] projection = { MediaStore.Images.Media.DATA };
					Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
					cursor.moveToFirst();
					int columnIndex = cursor.getColumnIndex(projection[0]);
					String picturePath = cursor.getString(columnIndex);
					cursor.close();
					SharedPreferences.Editor editor=prefs.edit();
					editor.putString("photopath",picturePath);
					editor.commit();
					layoutPreview.setVisibility(View.VISIBLE);
					vistaPrevia(prefs.getString("photopath", ""));
				break;
			}
		}else{
			File file=new File(prefs.getString("photopath", ""));
			if(file.exists()){
				file.delete();
			}
			SharedPreferences.Editor editor=prefs.edit();
			editor.putString("photopath","");
			editor.commit();
		}
	}
	
	//Muestra de vista previa
	private void vistaPrevia(String imagenpath){
		int THUMBSIZEwidth = 0;
		int THUMBSIZEheight = 0;
		float desity=getResources().getDisplayMetrics().density;
		 if(desity>=3.0){
			 THUMBSIZEwidth=600;
			 THUMBSIZEheight=400;
		 }else if(desity==2.0){
			 THUMBSIZEwidth=400;
			 THUMBSIZEheight=300;
		 }else if(desity==1.5){
			 THUMBSIZEwidth=300;
			 THUMBSIZEheight=200;
		 }else{
			 THUMBSIZEwidth=200;
			 THUMBSIZEheight=50;
		 }
		 
		 Bitmap ThumbImage = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(imagenpath), THUMBSIZEwidth, THUMBSIZEheight);
	     imgPreview.setImageBitmap(ThumbImage);
	}
	
	private String reducirImagen(String fichero){
		File folder=new File(Environment.getExternalStorageDirectory()+"/DCIM/construar/send/");
		if(!folder.exists()){
			folder.mkdirs();
		}
		File file=new File(folder + File.separator + "IMG_"+String.valueOf(System.currentTimeMillis())+".jpg");
		path=file.getAbsolutePath();
		int THUMBSIZEwidth =600;
		int THUMBSIZEheight =400;
		Bitmap ThumbImage = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(fichero), THUMBSIZEwidth, THUMBSIZEheight);
		try {
			FileOutputStream fOut = new FileOutputStream(file);
			ThumbImage.compress(Bitmap.CompressFormat.JPEG, 85, fOut);
		    fOut.flush();
		    fOut.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return path;
	}
}
