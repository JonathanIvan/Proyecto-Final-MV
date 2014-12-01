

package com.navi.android.webviewdemo;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.regex.Pattern;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import com.navi.android.webviewdemo.AddFavorito;
import com.navi.android.webviewdemo.Favoritos;

import com.navi.android.webviewdemo.mysql;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebIconDatabase;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebSettings.PluginState;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

public class WebViewDemoActivity extends Activity implements android.view.View.OnClickListener
{
	private WebView webview;

	private ProgressBar progressBar;

	private EditText urlEditText;

	private List<Link> historyStack;

	private ArrayAdapter<Link> dialogArrayAdapter;

	private ImageButton stopButton;
	mysql objcon = new mysql(this, null, null, 1);
	 ImageButton favoritoButton;
	private ImageView faviconImageView;
	
	private static final Pattern urlPattern = Pattern.compile("^(https?|ftp|file)://(.*?)");


	@SuppressWarnings("deprecation")
	@SuppressLint({ "SetJavaScriptEnabled", "NewApi", "ClickableViewAccessibility" })
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		historyStack = new LinkedList<Link>();
		webview = (WebView) findViewById(R.id.webkit);
		faviconImageView = (ImageView) findViewById(R.id.favicon);

		urlEditText = (EditText) findViewById(R.id.url);
		progressBar = (ProgressBar) findViewById(R.id.progressbar);	
		stopButton = ((ImageButton) findViewById(R.id.stop));
		favoritoButton = ((ImageButton) findViewById(R.id.favorito));
		WebIconDatabase.getInstance().open(getDir("icons", MODE_PRIVATE).getPath());		
		
	
		webview.loadUrl("www.debian.org");
		webview.getSettings().setJavaScriptEnabled(true);
		webview.getSettings().setBuiltInZoomControls(true);
		webview.getSettings().setSupportZoom(true);
		favoritoButton.setOnClickListener(this);
		  ArrayList<Favoritos> secciones=
	        		new ArrayList<Favoritos>();
	        
	        try {
	        	objcon = new mysql(this, null, null, 1);
	        	
				SQLiteDatabase db = objcon.getReadableDatabase();
				Cursor rs = db.rawQuery("select * from Favoritos order by nombre ", null);
				//                              nombre
				Favoritos obj;
				while(rs.moveToNext()){
					obj=new Favoritos();
					obj.setNombre(rs.getString(0));
					obj.setUrl(rs.getString(1));
					secciones.add(obj);
					
				}
				
			} catch (Exception e) {
				// TODO: handle exception
			}
	        
	        Spinner sp = (Spinner)findViewById(R.id.spinner1);
	        ArrayAdapter<Favoritos> adaptador=
	        		new ArrayAdapter<Favoritos>(this, android.R.layout.simple_list_item_1,
	        				secciones);
	        
	        sp.setAdapter(adaptador);
	        
	        sp.setOnItemSelectedListener(new OnItemSelectedListener() {
	        	public void onItemSelected(AdapterView<?> parentView, View selectedItemView,
	        			int position, long id){
	        		// mensaje toast
	        		 //  obtiene la informacion del spinner seleccionado
	        		String seleccion= parentView.getItemAtPosition(position).toString();
	        		// creamos una condicion para ver si la opcion esta sola
	        		if(seleccion.trim().length() == 0){
	        		 //si no tiene campos la seleccion no hara nada
	        				
	        			
	    			//	Toast.makeText(getApplicationContext(), "nada por aqui", Toast.LENGTH_SHORT).show();
	    			}else{ // y si el campo no esta vacio
	    				
	    			Toast.makeText(parentView.getContext(), "Has Seleccionado " +seleccion, Toast.LENGTH_SHORT).show(); 
	        		SQLiteDatabase db = objcon.getReadableDatabase();
	    			Cursor c = db.rawQuery("SELECT * FROM Favoritos WHERE nombre='"+ seleccion +"'", null);
	    			if(c.moveToFirst()){
	    				//txturl.setText("");
	    				urlEditText.invalidate();
					urlEditText.setText("");
					
					//se empiezan a mostrar los datos desde la pocision0
					// pero se necesita solo la pocision 1 (url)
					urlEditText.setText(c.getString(1));
					String cargafav = urlEditText.getText().toString();
					webview.loadUrl(cargafav);
	    				
	    				
	    			}
	    			//Toast.makeText(getApplicationContext(), "hola "+ seleccion, Toast.LENGTH_SHORT).show();

	        		
	        		//webview.loadUrl("https://www.google.com.mx/");
	    			}
	    			
	        	}
	        	public void onNothingSelected(AdapterView<?> parentView){
	        		
	        	}
	        	
			});
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO)
		{
			webview.getSettings().setPluginState(PluginState.ON);
		}
		else
		{
			
			webview.getSettings().setPluginsEnabled(true);
		}

		
		webview.setDownloadListener(new CustomDownloadListener());

		webview.setWebViewClient(new CustomWebViewClient());					

		webview.setWebChromeClient(new WebChromeClient()
		{
			@Override
			public void onProgressChanged(WebView view, int progress)
			{
				progressBar.setProgress(0);
				FrameLayout progressBarLayout = (FrameLayout) findViewById(R.id.progressBarLayout);
				progressBarLayout.setVisibility(View.VISIBLE);
				WebViewDemoActivity.this.setProgress(progress * 1000);
				
				TextView progressStatus = (TextView) findViewById(R.id.progressStatus);
				progressStatus.setText(progress + " %");
				progressBar.incrementProgressBy(progress);

				if (progress == 100)
				{
					progressBarLayout.setVisibility(View.GONE);
				}
			}

			@Override
			public void onReceivedTitle(WebView view, String title)
			{
				WebViewDemoActivity.this.setTitle("Browser Navi:- " + WebViewDemoActivity.this.webview.getTitle());
				for(Link link : historyStack)
				{
					if (link.getUrl().equals(WebViewDemoActivity.this.webview.getUrl()))
					{
						link.setTitle(title);
					}
				}
			}
			
			@Override
			public void onReceivedIcon(WebView view, Bitmap icon)
			{	 			
				faviconImageView.setImageBitmap(icon);
				view.getUrl();
				boolean b = false;
				ListIterator<Link> listIterator = historyStack.listIterator();
				while (!b && listIterator.hasNext())
				{
					Link link = listIterator.next();
					if (link.getUrl().equals(view.getUrl()))
					{
						link.setFavicon(icon);
						b = true;
					}
				}
			}

		});
		
	
		webview.setOnTouchListener(new View.OnTouchListener() 
		{ 
			@Override
			public boolean onTouch(View v, MotionEvent event) 
			{
			           switch (event.getAction()) 
			           { 
			               case MotionEvent.ACTION_DOWN: 
			               case MotionEvent.ACTION_UP: 
			                   if (!v.hasFocus()) 
			                   { 
			                       v.requestFocus(); 
			                   }  
			                   break; 
			           } 
			           return false; 
			        }
			
			});

		
		
		
		webview.requestFocus();
	}
	
	
	@Override
	protected Dialog onCreateDialog(int id)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setTitle(getString(R.string.history));		
		builder.setPositiveButton(getString(R.string.clear), new OnClickListener()
		{			
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				historyStack.clear();				
			}
		});
		
		builder.setNegativeButton(R.string.close, null);
		
		dialogArrayAdapter = new ArrayAdapter<Link>(this, R.layout.history, historyStack)
		{
			@SuppressLint("InflateParams") @Override
			public View getView(int position, View convertView, ViewGroup parent)
			{
				//holder pattern
				LinkHolder holder = null;
				if (convertView == null)
				{
					LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
					convertView = inflater.inflate(R.layout.history, null);
					holder = new LinkHolder();
					holder.setUrl((TextView) convertView.findViewById(R.id.textView1));
					holder.setImageView((ImageView) convertView.findViewById(R.id.favicon));

					convertView.setTag(holder);
				}
				else
				{
					holder = (LinkHolder) convertView.getTag();
				}

				Link link = historyStack.get(position);
				
				if (link.getTitle() != null)
				{
					holder.getUrl().setText(link.getTitle());
				}
				else
				{
					holder.getUrl().setText(link.getUrl());
				}
				Bitmap favicon = link.getFavicon();
				if (favicon == null)
				{
					holder.getImageView().setImageDrawable(super.getContext().getResources().getDrawable(R.drawable.favicon_default));
				}
				else
				{
					holder.getImageView().setImageBitmap(favicon);
				}

				return convertView;
			}
		};

		builder.setAdapter(dialogArrayAdapter, new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int item)
			{
				webview.loadUrl(historyStack.get(item).getUrl());
				stopButton.setEnabled(true);
			}

		});

		return builder.create();
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onPrepareDialog(int id, Dialog dialog)
	{
		dialogArrayAdapter.notifyDataSetChanged();
		super.onPrepareDialog(id, dialog);
	}

	// back button
	@Override
	public void onBackPressed()
	{
		if (webview.canGoBack())
		{
			webview.goBack();
		}
		else
		{
			super.onBackPressed();
		}
	}
	
	class CustomWebViewClient extends WebViewClient
	{

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url)
		{
			if (url.endsWith(".mp3") || url.endsWith(".aac"))
			{
				Intent intent = new Intent(Intent.ACTION_VIEW);				
				intent.setDataAndType(Uri.parse(url),"audio/mpeg");
				startActivity(intent);
				return true;
			}
			return false;
		}

		public void onPageStarted(WebView view, String url, Bitmap favicon)
		{
			if (checkConnectivity())
			{
		
				WebViewDemoActivity.this.faviconImageView.setImageDrawable(WebViewDemoActivity.this.getResources().getDrawable(R.drawable.favicon_default));
	
				WebViewDemoActivity.this.urlEditText.setText(url);
				
				
				boolean b = false;
				ListIterator<Link> listIterator = historyStack.listIterator();
				while (listIterator.hasNext() && !b)
				{
					if (listIterator.next().getUrl().equals(url))
					{
						b = true;
						listIterator.remove();
					}
				}
				Link link = new Link(url, favicon);
				historyStack.add(0, link);
	
				stopButton.setEnabled(true);
				updateButtons();			
			}
		}

		@Override
		public void onPageFinished(WebView view, String url)
		{
			stopButton.setEnabled(false);
			updateButtons();
		}

	
		@Override
		public void onReceivedError(WebView view, int errorCode, String description, String failingUrl)
		{
			AlertDialog.Builder builder = new AlertDialog.Builder(WebViewDemoActivity.this);
			builder.setMessage(description).setPositiveButton((R.string.ok), null).setTitle("onReceivedError");
			builder.show();
		}

	}


	public void go(View view)
	{
		
		InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		inputMethodManager.hideSoftInputFromWindow(urlEditText.getWindowToken(), 0);

		if (checkConnectivity())
		{	
			stopButton.setEnabled(true);		
			
			
			if (!urlPattern.matcher(urlEditText.getText().toString()).matches())
			{
				 urlEditText.setText("http://" + urlEditText.getText().toString());
			}
			webview.loadUrl(urlEditText.getText().toString());
		}
	}

	public void back(View view)
	{
		if (checkConnectivity())
		{
			webview.goBack();
		}
	}

	public void forward(View view)
	{
		if (checkConnectivity())
		{
			webview.goForward();
		}
	}

	public void stop(View view)
	{
		webview.stopLoading();
		Toast.makeText(this, getString(R.string.stopping), Toast.LENGTH_LONG).show();
	}

	@SuppressWarnings("deprecation")
	public void history(View view)
	{
		showDialog(0);
	}



	private void updateButtons()
	{
		ImageButton backButton = (ImageButton) WebViewDemoActivity.this.findViewById(R.id.back);

		if (webview.canGoBack())
		{
			backButton.setEnabled(true);
		}
		else
		{
			backButton.setEnabled(false);
		}

		ImageButton forwardButton = (ImageButton) WebViewDemoActivity.this.findViewById(R.id.forward);

		if (webview.canGoForward())
		{
			forwardButton.setEnabled(true);
		}
		else
		{
			forwardButton.setEnabled(false);
		}
	}

	// DOWNLOAD MANAGER WITH ASYNCTASK

	class CustomDownloadListener implements DownloadListener
	{
		public void onDownloadStart(final String url, String userAgent, String contentDisposition, String mimetype, long contentLength)
		{
			AlertDialog.Builder builder = new AlertDialog.Builder(WebViewDemoActivity.this);
			
			builder.setTitle(getString(R.string.download));
			builder.setMessage(getString(R.string.question));
			builder.setCancelable(false).setPositiveButton((R.string.ok), new DialogInterface.OnClickListener()
			{
				public void onClick(DialogInterface dialog, int id)
				{
					new DownloadAsyncTask().execute(url);
				}

			}).setNegativeButton((R.string.cancel), new DialogInterface.OnClickListener()
			{
				public void onClick(DialogInterface dialog, int id)
				{
					dialog.cancel();
				}
			});
			
			builder.create().show();

		}

	}

	private class DownloadAsyncTask extends AsyncTask<String, Void, String>
	{

		@Override
		protected String doInBackground(String... arg0)
		{
			String result = "";
			String url = arg0[0];
			
			if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
			{							
				HttpClient httpClient = new DefaultHttpClient();
				HttpGet httpGet = new HttpGet(url);
				InputStream inputStream = null;
				FileOutputStream fileOutputStream = null;
				try
				{
					HttpResponse httpResponse = httpClient.execute(httpGet);
	
					BufferedHttpEntity bufferedHttpEntity = new BufferedHttpEntity(httpResponse.getEntity());
	
					inputStream = bufferedHttpEntity.getContent();
					
					String fileName = android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/webviewdemo";
					File directory = new File(fileName);
					File file = new File(directory, url.substring(url.lastIndexOf("/")));				
					directory.mkdirs();
	
					fileOutputStream = new FileOutputStream(file);
					ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
					byte[] buffer = new byte[1024];
					int len = 0;
					
					while (inputStream.available() > 0 && (len = inputStream.read(buffer)) != -1)
					{
						byteArrayOutputStream.write(buffer, 0, len);
					}
					
					fileOutputStream.write(byteArrayOutputStream.toByteArray());
					fileOutputStream.flush();
					
					result = getString(R.string.result) + file.getAbsolutePath();
				}
				catch (Exception ex)
				{
					Log.e(WebViewDemoActivity.class.toString(), ex.getMessage(), ex);
					result = ex.getClass().getSimpleName() + " " + ex.getMessage();
				}
				finally
				{
					if (inputStream != null)
					{
						try
						{
							inputStream.close();
						}
						catch (IOException ex)
						{
							Log.e(WebViewDemoActivity.class.toString(), ex.getMessage(), ex);
							result = ex.getClass().getSimpleName() + " " + ex.getMessage();
						}
					}
					if (fileOutputStream != null)
					{
						try
						{
							fileOutputStream.close();
						}
						catch (IOException ex)
						{
							Log.e(WebViewDemoActivity.class.toString(), ex.getMessage(), ex);
							result = ex.getClass().getSimpleName() + " " + ex.getMessage();
						}
					}
				}
			}
			else
			{
				result = getString(R.string.nosd);
			}

			return result;
		}

		@Override
		protected void onPostExecute(String result)
		{
			AlertDialog.Builder builder = new AlertDialog.Builder(WebViewDemoActivity.this);
			builder.setMessage(result).setPositiveButton((R.string.ok), null).setTitle(getString(R.string.download));
			builder.show();

		}

	}
	
	/**
	 * Checks networking status.
	 */
	private boolean checkConnectivity()
	{
		boolean enabled = true;

		ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = connectivityManager.getActiveNetworkInfo();
		
		if ((info == null || !info.isConnected() || !info.isAvailable()))
		{
			enabled = false;
			Builder builder = new Builder(this);
			builder.setIcon(android.R.drawable.ic_dialog_alert);
			builder.setMessage(getString(R.string.noconnection));
			builder.setCancelable(false);
			builder.setNeutralButton(R.string.ok, null);
			builder.setTitle(getString(R.string.error));
			builder.create().show();		
		}
		return enabled;			
	}
	
	public void onClick(View view) {
		
		if(view==favoritoButton)
    	{
String link = urlEditText.getText().toString();
    		
    		Bundle parametros = new Bundle();
    		parametros.putString("nombre", link);
    		
    		Intent fac = new Intent(this, AddFavorito.class);
    		fac.putExtras(parametros);
    		startActivity(fac);
    		
    		
    		
    		
    	}
		
	}

}
