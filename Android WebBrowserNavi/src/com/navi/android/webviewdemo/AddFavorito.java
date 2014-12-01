package com.navi.android.webviewdemo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.LabeledIntent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class AddFavorito extends Activity implements OnClickListener {
	
	SQLiteDatabase db;
	Button btvolver, btlisto;
	EditText txtnombre, txtlink2;
	
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_favorito);
        
        //clase DbHelper
        
        
        
        btvolver = (Button)findViewById(R.id.btcancelar);
        btlisto = (Button)findViewById(R.id.btlisto);
        
        btvolver.setOnClickListener(this);
        btlisto.setOnClickListener(this);
        
        Bundle parametros = this.getIntent().getExtras();
        String nombre = parametros.getString("nombre");
        
        txtnombre = (EditText)findViewById(R.id.txt_nombre_fav);
        TextView campo_texto = (TextView)findViewById(R.id.txtlink2);
        campo_texto.setText(nombre);
        
        
        
       /* 
        db=openOrCreateDatabase("proyecto", Context.MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS navegador(nombrefav VARCHAR, urlfav VARCHAR ); ");
*/
	}
	
	
	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		if(view==btlisto)
    	{
    		this.insert();
    		
    		
    		
    		
    	}else if(view==btvolver)
    	{
    		Intent ppal = new Intent(this,WebViewDemoActivity.class);
    		startActivity(ppal);
    	}
		
		
		
	}
	
	
	//  metodo que guarda los datos  una vez presionado el boton
	private void insert()
    { 
		txtlink2 = (EditText)findViewById(R.id.txtlink2);
		
		if( txtnombre.getText().toString().trim().length()==0||
			txtlink2.getText().toString().trim().length()==0 )
		     {
			Toast.makeText(getApplicationContext(), "Por favor llena todos los campos! ", Toast.LENGTH_LONG).show();
		     }else{
		    	 
			mysql objcon = new mysql(this, null, null, 1);
    	
			SQLiteDatabase db = objcon.getReadableDatabase();	     
		
			db.execSQL("INSERT INTO Favoritos VALUES('"+txtnombre.getText()+"', '"+txtlink2.getText()+"' );");
			Toast.makeText(getApplicationContext(),"Se Agrego: "+txtnombre.getText()+ " a favoritos!", Toast.LENGTH_LONG).show();
			Intent ppal = new Intent(this, WebViewDemoActivity.class);
			startActivity(ppal);  }
    }
	
}
	

