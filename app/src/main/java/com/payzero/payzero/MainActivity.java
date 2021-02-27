package com.payzero.payzero;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.GeolocationPermissions;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity {
    WebView webview;
    private String weburl = "https://payzero.co.in/main-page/";

    RelativeLayout relativeLayout;
    Button btnNoInternetConnection;
    ProgressBar progressBarweb;
  //uploading file
  private ValueCallback<Uri> mUploadMessage;
    public ValueCallback<Uri[]> uploadMessage;
  public static final int REQUEST_SELECT_FILE = 100;
    private final static int FILECHOOSER_RESULTCODE=1;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppUpdateChecker appUpdateChecker=new AppUpdateChecker(this);  //pass the activity in constructure
        appUpdateChecker.checkForUpdate(false); //mannual check false here


        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        webview = findViewById(R.id.webviewid);
        webview.loadUrl(weburl);

        btnNoInternetConnection = (Button) findViewById(R.id.btnNoconnection);
        relativeLayout = (RelativeLayout) findViewById(R.id.relativelayout);
       // swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
       // swipeRefreshLayout.setColorSchemeColors(Color.BLUE);
      ////  swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
      //////      @Override
           // public void onRefresh() {
             //   webview.reload();
            //}
       // });

        progressBarweb = findViewById(R.id.progressBar);
    //    progressDialog = new ProgressDialog(this);
      //  progressDialog.setMessage("Loading Please Wait");

        WebSettings webSettings = webview.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webview.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
       // webview.getSettings().setCacheMode(webSettings.LOAD_CACHE_ELSE_NETWORK);
        webview.getSettings().setAppCacheEnabled(true);
        webview.getSettings().setGeolocationEnabled(true);
        webview.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webSettings.setDomStorageEnabled(true);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        webSettings.setUseWideViewPort(true);
        webSettings.setSavePassword(true);
        webSettings.setSaveFormData(true);
        webSettings.setEnableSmoothTransition(true);


        checkConnection();

        // webview.loadUrl("http://payzero.co.in/app/#");
      //  webview.setWebViewClient(new myWebClient());
    //    webView.setWebViewClient(new xWebViewClient());

        webview.setWebViewClient(new WebViewClient() {

          /*  @Override
            public void onPageFinished(WebView view, String url) {
                swipeRefreshLayout.setRefreshing(false);
                super.onPageFinished(view, url);
            }*/

            public boolean  shouldOverrideUrlLoading(WebView view, String url) {
                if(url != null && url.startsWith("whatsapp://"))
                {
                    view.getContext().startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse(url)));
                    return true;

                }else
                {
                    return false;
                }
            }
        });

        webview.setWebChromeClient(new WebChromeClient() {
            private String TAG;
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                progressBarweb.setVisibility(View.VISIBLE);
                progressBarweb.setProgress(newProgress);
                setTitle("Loading...");
              //  progressDialog.show();
                if (newProgress == 100) {
                    progressBarweb.setVisibility(view.GONE);
                    setTitle(view.getTitle());
                  //  progressDialog.dismiss();
                }

                super.onProgressChanged(view, newProgress);
            }

            // For 3.0+ Devices (Start)
            // onActivityResult attached before constructor
            protected void openFileChooser(ValueCallback uploadMsg, String acceptType)
            {
                mUploadMessage = uploadMsg;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("image/*");
                startActivityForResult(Intent.createChooser(i, "File Browser"), FILECHOOSER_RESULTCODE);
            }

            // For Lollipop 5.0+ Devices
            public boolean onShowFileChooser(WebView mWebView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams)
            {
                if (uploadMessage != null) {
                    uploadMessage.onReceiveValue(null);
                    uploadMessage = null;
                }

                uploadMessage = filePathCallback;

                Intent intent = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    intent = fileChooserParams.createIntent();
                }
                try
                {
                    startActivityForResult(intent, REQUEST_SELECT_FILE);
                } catch (ActivityNotFoundException e)
                {
                    uploadMessage = null;
                    return false;
                }
                return true;
            }

            //For Android 4.1 only
            protected void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture)
            {
                mUploadMessage = uploadMsg;
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "File Browser"), FILECHOOSER_RESULTCODE);
            }

            protected void openFileChooser(ValueCallback<Uri> uploadMsg)
            {
                mUploadMessage = uploadMsg;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("image/*");
                startActivityForResult(Intent.createChooser(i, "File Chooser"), FILECHOOSER_RESULTCODE);
            }






            @Override
                public void onGeolocationPermissionsShowPrompt (String
                origin, GeolocationPermissions.Callback callback){
                    super.onGeolocationPermissionsShowPrompt(origin, callback);
                    callback.invoke(origin, true, false);

                }


        });
        // reconnect btn
       btnNoInternetConnection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkConnection();
            }
        });


    }

    //file
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (requestCode == REQUEST_SELECT_FILE) {
                if (uploadMessage == null)
                    return;
                uploadMessage.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, intent));
                uploadMessage = null;
            }
        } else if (requestCode == FILECHOOSER_RESULTCODE) {
            if (null == mUploadMessage)
                return;
            // Use MainActivity.RESULT_OK if you're implementing WebView inside Fragment
            // Use RESULT_OK only if you're implementing WebView inside an Activity
            Uri result = intent == null || resultCode != MainActivity.RESULT_OK ? null : intent.getData();
            mUploadMessage.onReceiveValue(result);
            mUploadMessage = null;
        }


    }


    private class xWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if(keyCode == KeyEvent.KEYCODE_BACK && webview.canGoBack()){
            webview.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }




    //main

    protected void onPause() {
        super.onPause();
        //Toast.makeText(this, "Pause", Toast.LENGTH_SHORT).show();
    }

    protected void onResume() {
        super.onResume();
      //  Toast.makeText(this, "Resume", Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onBackPressed() {
     //   if (webview.canGoBack()) {
            webview.goBack();
     //   } else {
         //   AlertDialog.Builder builder = new AlertDialog.Builder(this);
          //  builder.setMessage("")
            //        .setNegativeButton("No", null)
             ////       .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    //    @Override
               //         public void onClick(DialogInterface dialog, int which) {
//                   //     }
                //    }).show();

   //     }


    }
//check internet connection

    public void checkConnection() {

        ConnectivityManager connectivityManager = (ConnectivityManager)
                this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileNetwork = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if (wifi.isConnected()) {
            webview.loadUrl(weburl);

            webview.setVisibility(View.VISIBLE);
            relativeLayout.setVisibility((View.GONE));
        } else if (mobileNetwork.isConnected()) {
            webview.loadUrl(weburl);

            webview.setVisibility(View.VISIBLE);
            relativeLayout.setVisibility((View.GONE));

        } else {
            webview.setVisibility(View.GONE);
            relativeLayout.setVisibility((View.VISIBLE));

        }
    }



}




