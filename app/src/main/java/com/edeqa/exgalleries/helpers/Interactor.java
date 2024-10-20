package com.edeqa.exgalleries.helpers;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.JsPromptResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.edeqa.exgalleries.R;
import com.edeqa.exgalleries.interfaces.TaskCompletedInterface;

@SuppressLint("JavascriptInterface")
public class Interactor {

	public static final String SIGNATURE = "Interactor";

	public WebView browser;
	protected Context context;
	protected String script;
	protected Handler handler;
	private Object monitor = new Object();
	private boolean ready = false;
	private InteractorJSMethods javaScriptMethods;

	public Interactor(Context mContext, String javascript_code) {
		context = mContext;
		script = javascript_code;

		if(script==null || script.length()==0)
			try {
				throw new Exception("NOT FOUND SCRIPT DATA");
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				script="";
			}
		
		handler = new Handler(context.getMainLooper());
		handler.post(interactorRunnable);

		synchronized (monitor) {//waiting for loading and finishing page
			while (!ready) {
                try {
                    monitor.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
		}

	}

	private Runnable interactorRunnable = new Runnable() {
		@SuppressLint("SetJavaScriptEnabled")
		@Override
		public void run() {

			browser = new WebView(context);
			browser.setVisibility(View.INVISIBLE);
			browser.setId(0X100);
			browser.setScrollContainer(false);
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
					RelativeLayout.LayoutParams.MATCH_PARENT, 50);
			params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
			browser.setLayoutParams(params);
			browser.setWebViewClient(new wvClient());

			browser.getSettings().setJavaScriptEnabled(true);
			browser.getSettings()
					.setUserAgentString("Mozilla/5.0 (Windows NT 6.2; WOW64; rv:16.0) Gecko/20100101 Firefox/16.0");

			browser.clearCache(true);
			
			javaScriptMethods = new InteractorJSMethods(context);
			
			browser.setWebChromeClient(new WebChromeClient() {
				public void onProgressChanged(WebView view, int progress) {
					if(progress==100){
						ready = true;
						synchronized (monitor){monitor.notify();}
					}
				}

				@Override
				public boolean onJsPrompt(WebView view, String url, String message, String defaultValue,
						JsPromptResult result) {

					if (TextUtils.isEmpty(message) || !message.startsWith(SIGNATURE))
						return false;

					String json = message.substring(SIGNATURE.length());
					try {
						JSONObject info = new JSONObject(json);
						JSONArray params = info.getJSONArray("params");
						int len = params.length();
						Object[] paramValues = new Object[len];
						for (int i = 0; i < len; ++i) {
							Object obj = params.opt(i);
							paramValues[i] = obj;
						}

						Method method = InteractorReflection.findOnlyMethod(javaScriptMethods.getClass(), info.getString("method"));
						Object ret = method.invoke(javaScriptMethods, paramValues);

						JSONObject res = new JSONObject();
						res.put("result", ret);
						result.confirm(res.toString());
					} catch (JSONException e) {
						javaScriptMethods.m_error_message = "Couldn't parse rpc call: " + json;
						result.confirm(null);
						System.out.println("JSONException with method: "+message+". Exception: "+e.getMessage());
					} catch (NoSuchMethodException e) {
						javaScriptMethods.m_error_message = "Can't find method: " + e.getMessage() + " on data " + json;
						result.confirm(null);
						System.out.println("NoSuchMethodException with method: "+message+". Exception: "+e.getMessage());
					} catch (InvocationTargetException e) {
						javaScriptMethods.m_error_message = "Can't invoke method: " + e.getMessage() + " on data " + json;
						result.confirm(null);
						System.out.println("InvocationTargetException with method: "+message+". Exception: "+e.getMessage());
					} catch (IllegalAccessException e) {
						javaScriptMethods.m_error_message = "Can't access method: " + e.getMessage() + " on data " + json;
						result.confirm(null);
						System.out.println("IllegalAccessException with method: "+message+". Exception: "+e.getMessage());
					} catch (IllegalArgumentException e) {
						javaScriptMethods.m_error_message = "Wrong number of arguments: " + e.getMessage() + " on data " + json;
						result.confirm(null);
						System.out.println("IllegalArgumentException with method: "+message+". Exception: "+e.getMessage());
					}
					return true;
				};
			});

			browser.addJavascriptInterface(javaScriptMethods, SIGNATURE);

			String api_content = null;
			try {
				// get input stream for text
				InputStream is = context.getAssets().open("api.html");
				// check size
				int size = is.available();
				// create buffer for IO
				byte[] buffer = new byte[size];
				// get data to buffer
				is.read(buffer);
				// close stream
				is.close();
				// set result to TextView
				api_content = new String(buffer);
			} catch (IOException ex) {
				Log.e("API_ERROR", ex.getMessage());
				return;
			}

			api_content = api_content.replace("%ANDROID_VERSION%", String.valueOf(Build.VERSION.SDK_INT));
			api_content = api_content.replace("%JAVASCRIPT%", script);

			browser.loadDataWithBaseURL("file:///android_asset/", api_content, "text/html", "UTF-8", null);
		}

		final class wvClient extends WebViewClient {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				return true;
			}
		}
	};

	public boolean isReady(){
		return javaScriptMethods.isReady(); 
	}

	public void setCallback(TaskCompletedInterface object){
		javaScriptMethods.setCallback(object);
	}
	
	public void callMethod(String method){
		browser.loadUrl("javascript:"+method+";api_done(\"done:"+method+"\");");
	}
	
	public void callFinish(){
		javaScriptMethods.Finish();
	}
	
	public void showCounter(){

		handler = new Handler(context.getMainLooper());
		handler.post(new Runnable(){
			@Override
		public void run(){
				
				if (InteractorJSMethods.getCounter() > 0)
					Toast.makeText(context,
							context.getString(R.string.counter_new_pictures, InteractorJSMethods.getCounter()),
							Toast.LENGTH_SHORT).show();
				else
					Toast.makeText(context, context.getString(R.string.no_new_pictures),Toast.LENGTH_SHORT).show();
			}
		});

	}
	
	public void destroy() {
		// if(webView!=null)webView.destroy();
		// if(storiesList!=null)storiesList.destroy();
	}


	
}
