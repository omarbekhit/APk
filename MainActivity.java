package com.example.dynamicwebapp;

import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {
    WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        webView = new WebView(this);
        webView.setWebViewClient(new WebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);
        setContentView(webView);

        // احصل على slug من ملفات BuildConfig (GitHub يقوم بتمريرها)
        String slug = BuildConfig.APP_SLUG;

        // ابدأ تحميل manifest
        new Thread(() -> {
            try {
                // افترض أن السيرفر يرجع manifest من هنا
                String manifestUrl = "https://YOUR_SERVER_URL_HERE/apps/" + slug + "/manifest.json";
                URL url = new URL(manifestUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();
                Scanner scanner = new Scanner(stream).useDelimiter("\\A");
                String manifestJson = scanner.hasNext() ? scanner.next() : "";

                JSONObject manifest = new JSONObject(manifestJson);
                String startUrl = manifest.getString("start_url");

                runOnUiThread(() -> webView.loadUrl(startUrl));

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> webView.loadUrl("https://defaultpage.com"));
            }
        }).start();
    }
}
