package koal.glide_demo;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.webkit.ConsoleMessage;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
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

    public static final String TAG = WebViewActivity.class.getSimpleName();
    private TextView mContentTv;
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        WebView webView = findViewById(R.id.webview);
        mContentTv = findViewById(R.id.content_tv);
        mHandler = new Handler(Looper.getMainLooper());

        printMemory();

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

            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                Log.d(TAG, "onConsoleMessage: " + consoleMessage.messageLevel()
                        + " " + consoleMessage.message()
                        + " " + consoleMessage.sourceId()
                        + " " + consoleMessage.lineNumber());
                return super.onConsoleMessage(consoleMessage);
            }
        });

        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                Log.d(TAG, "onPageStarted() called with: view = [" + view + "], url = [" + url + "], favicon = [" + favicon + "]");
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Log.d(TAG, "onPageFinished() called with: view = [" + view + "], url = [" + url + "]");
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                Log.d(TAG, "onReceivedError() called with: view = [" + view + "], request = [" + request + "], error = [" + error + "]");
            }

            @Override
            public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
                super.onReceivedHttpError(view, request, errorResponse);
                Log.d(TAG, "onReceivedHttpError() called with: view = [" + view + "], request = [" + request + "], errorResponse = [" + errorResponse + "]");
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                super.onReceivedSslError(view, handler, error);
                Log.d(TAG, "onReceivedSslError() called with: view = [" + view + "], handler = [" + handler + "], error = [" + error + "]");
            }
        });
    }

    private void printMemory() {
        Runtime runtime = Runtime.getRuntime();
        long maxMemory = runtime.maxMemory();
        long freeMemory = runtime.freeMemory();
        long totalMemory = runtime.totalMemory();
        Log.d("memory", "maxMemory = " + maxMemory + " freeMemory = " + freeMemory + " totalMemory = " + totalMemory);
        // large Heap: maxMemory = 536870912 freeMemory = 531623104 totalMemory = 536870912
        // no large:   maxMemory = 268435456 freeMemory = 263188024 totalMemory = 268435456
        /**
         * [dalvik.vm.heapgrowthlimit]: [256m]
         * [dalvik.vm.heapmaxfree]: [8m]
         * [dalvik.vm.heapminfree]: [512k]
         * [dalvik.vm.heapsize]: [512m]
         * [dalvik.vm.heapstartsize]: [8m]
         * [dalvik.vm.heaptargetutilization]: [0.75]
         */
        ActivityManager am =(ActivityManager)getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        Log.d("todo", "getMemoryClass: " + am.getMemoryClass() + " am.getLargeMemoryClass() = " + am.getLargeMemoryClass());
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