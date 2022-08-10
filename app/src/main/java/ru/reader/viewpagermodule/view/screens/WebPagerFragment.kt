package ru.reader.viewpagermodule.view.screens


import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import android.widget.ProgressBar
import android.widget.Toast
import butterknife.BindView
import butterknife.ButterKnife

import ru.reader.viewpagermodule.R
import ru.reader.viewpagermodule.view.adapters.BookBodyData
import ru.reader.viewpagermodule.view.adapters.BookStateForBundle


class WebPagerFragment : Fragment() {


    @BindView(R.id.webview_pager)
    lateinit var webViewPag: WebView

    @BindView(R.id.progress_webview_pager)
    lateinit var progressLoading: ProgressBar

    lateinit var parentActivity: MainActivity


    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view0 = inflater.inflate(R.layout.fragment_web_pager, container, false)
        ButterKnife.bind(this, view0)
        parentActivity = activity as MainActivity

        val arg = arguments?.getSerializable(BOOK_BUNDLE) as BookStateForBundle?
        arg?.let {
            Log.d("MyLog", arg.toString())
        }

        val myWebClient = object : WebViewClient() {
            override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
                super.onReceivedError(view, request, error)
                Log.d("MyLog", "error loading webPage ${error?.description}")
                if (error?.description == "net::ERR_INTERNET_DISCONNECTED") {
                    Toast.makeText(view?.context, "Ошибка какая то", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                view?.visibility = View.VISIBLE
                progressLoading.visibility = View.GONE
            }
        }

        view0.apply {
            progressLoading.visibility = View.VISIBLE
            webViewPag.apply {
                webViewClient = myWebClient
                webChromeClient = WebChromeClient()
                settings.apply {
                    setSupportZoom(true)

                    javaScriptEnabled = true
                    allowFileAccess = false

                    builtInZoomControls = true
                    domStorageEnabled = true
                    loadWithOverviewMode = true
                    useWideViewPort = true
                    defaultTextEncodingName = "utf-8"
                    setAppCacheEnabled(true)
                    loadsImagesAutomatically = true
                    mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                }
                setBackgroundColor(resources.getColor(android.R.color.transparent))

                //loadData("")

            }
        }

        return view0
    }


}
