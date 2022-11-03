package koal.glide_demo;

import android.app.AlertDialog;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

/**
 *
 * 如何在pc的Edge里调试Android webView上的js？
 *
 * 1. setWebContentsDebuggingEnabled(true)
 *
 * 2. 开启后，正常可以在pc的Edge里 ，输入"edge://inspect" 中看到：
 *
 *    "WebView in koal.glide_demo (99.0.4844.73)"....
 *
 * 3. 在"源代码"中加入断点；在"控制台"输入命令：console.log(navigator) 查看 ua相关
 *
 *
 *
 * 参考：
 * Carson带你学Android：你要的WebView与 JS 交互方式都在这里了：
 * TODO: "JS通过WebView调用 Android 代码"未验证
 * https://blog.csdn.net/carson_ho/article/details/64904691
 *
 */

public class WebViewActivity extends AppCompatActivity {

    public static final String TAG = "js";
    private TextView mContentTv;
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        WebView webView = findViewById(R.id.webview);
        mContentTv = findViewById(R.id.content_tv);
        mHandler = new Handler(Looper.getMainLooper());

        // 先载入JS代码
        // 格式规定为:file:///android_asset/文件名.html
        webView.loadUrl("file:///android_asset/web/javascript.html");
        // 启用 Android WebView 调试。 支持inspect调试
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }

        /**
         * 1. “JS 调用 Java”
         * 原生通过向JS注入方法,可以添加多个
         *
         * a. 通过 WebView.addJavascriptInterface() 进行对象映射
         * b. 通过 WebViewClient.shouldOverrideUrlLoading()方法回调拦截 url
         * c. 通过 WebChromeClient 的onJsAlert()、onJsConfirm()、onJsPrompt()方法回调拦截JS对话框
         */
        webView.addJavascriptInterface(new DefaultJSInterface("12344", 111), "sapphireWebViewBridge");
        webView.addJavascriptInterface(new DefaultJSInterfaceEx(), "sapphireWebViewBridgeEx");

        WebSettings webSettings = webView.getSettings();
        // 设置与Js交互的权限
        webSettings.setJavaScriptEnabled(true);
        // 设置允许JS弹窗
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);

        Button button = findViewById(R.id.call_js_btn);
        button.setOnClickListener(v ->
                webView.post(()->{
                    /**
                     * 2. “Java 调用 JS”
                     */
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                        // 不能少了"()"!!
                        // 方法一：调用javascript的callJS()方法,拿不到返回值，刷新webview导致效率低
                        webView.loadUrl("javascript:callJS()");
                    } else {
                        //此处为 js 返回的结果
                        // 方法二：可以拿返回值；不刷新webview，效率高；
                        // 因为该方法的执行不会使页面刷新，而第一种方法（loadUrl ）的执行则会
                        webView.evaluateJavascript("javascript:callJS()", value -> {
                            Log.d(TAG, "evaluateJavascript onReceiveValue = [" + value + "]");
                        });
                    }
                })
        );

        // 由于设置了弹窗检验调用结果,所以需要支持js对话框
        // webview只是载体，内容的渲染需要使用webviewChromClient类去实现
        // 通过设置WebChromeClient对象处理JavaScript的对话框
        //设置响应js 的Alert()函数
        webView.setWebChromeClient(new WebChromeClient(){
            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                AlertDialog.Builder b = new AlertDialog.Builder(WebViewActivity.this);
                b.setTitle("Alert");
                b.setMessage(message);
                b.setPositiveButton(android.R.string.ok, (dialog, which) -> result.confirm());
                b.setCancelable(false);
                b.create().show();
                return true;
            }
        });
    }

    private class DefaultJSInterfaceEx {
        public DefaultJSInterfaceEx() {
        }

        @JavascriptInterface
        public void sendEx(String message) {
            Log.d(TAG, "sendEx() called with: message = [" + message + "]");
            mHandler.post(()->{mContentTv.setText(message);});
        }
    }

    private class DefaultJSInterface {
//        private SapphireJsBridgeIdentifier bridgeIdentifier;
        public DefaultJSInterface(String miniAppId, int miniAppType) {
//            bridgeIdentifier = new SapphireJsBridgeIdentifier(miniAppId, 0);
        }

        @JavascriptInterface
        public void send(String message) {
            Log.d(TAG, "send() called with: message = [" + message + "]");
            mHandler.post(()->{mContentTv.setText(message);});
//            mHandler.post(()
//                    -> SapphireBridgeHandler.INSTANCE.processWebViewMessage(
//                    message, bridgeIdentifier));
        }
    }
}