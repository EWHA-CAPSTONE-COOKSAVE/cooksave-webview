package com.example.cooksave_webview

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.result.contract.ActivityResultContracts

class MainActivity : AppCompatActivity() {
    var mFilePathCallback: ValueCallback<Array<Uri>>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val myWebView: WebView = findViewById(R.id.webView)



        myWebView.settings.run {
            javaScriptEnabled = true
            javaScriptCanOpenWindowsAutomatically = true
            setSupportMultipleWindows(true)
            setLoadWithOverviewMode(true) //메타태그 허용
            setDomStorageEnabled(true) // 로컬저장소 허용
        }


        myWebView.webViewClient = WebViewClient()
        myWebView.webChromeClient = WebChromeClient()
        myWebView.loadUrl("https://www.cooksave.co.kr")


        myWebView.webChromeClient = object : WebChromeClient() {
            // For Android 5.0+
            override fun onShowFileChooser(
                webView: WebView?,
                filePathCallback: ValueCallback<Array<Uri>>?,
                fileChooserParams: FileChooserParams?
            ): Boolean {
                // Ensure that there's no existing callback
                mFilePathCallback = filePathCallback

                val intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.addCategory(Intent.CATEGORY_OPENABLE)
                intent.setType("image/*")
                // 파일 1개만 선택 가능하도록 처리
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false)

                resultLauncher.launch(intent)
                return true
            }

        }


    }

    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val resultCode = result.resultCode
            val intent = result.data

            if (resultCode == Activity.RESULT_OK) {
                val clipData = intent?.clipData
                val results = mutableListOf<Uri>()

                if (clipData != null) {
                    for (i in 0 until clipData.itemCount) {
                        val item = clipData.getItemAt(i)
                        results.add(item.uri)
                    }
                } else {
                    intent?.data?.let { results.add(it) }
                }

                mFilePathCallback?.onReceiveValue(results.toTypedArray())
            } else {
                mFilePathCallback?.onReceiveValue(null)
            }

            mFilePathCallback = null
        }



    override fun onBackPressed() { // 뒤로가기 기능 구현

        val myWebView: WebView = findViewById(R.id.webView)
        if(myWebView.canGoBack()){
            myWebView.goBack()
        }else{
            super.onBackPressed()
        }
    }

}