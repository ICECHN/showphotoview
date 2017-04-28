package com.icechn.library.showphoto.Photo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

/**
 * Created by ICE on 2016/12/23.
 */

public class WebPhotoView extends PhotoBase<WebView> {

    public WebPhotoView(Context context) {
        super(context);
    }

    public WebPhotoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WebPhotoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected WebView createPhotoView(Context context, AttributeSet attrs) {
        WebView mWebView = new WebView(context);
        mWebView.setBackgroundColor(Color.BLACK);
        return mWebView;
    }

    @Override
    protected void destroyPhotoView() {
        mInnerPhotoView.clearCache(false);
        mInnerPhotoView.clearHistory();
        mInnerPhotoView.removeAllViews();
        mInnerPhotoView.destroy();
    }

    @SuppressLint({"AddJavascriptInterface", "SetJavaScriptEnabled"})
    @Override
    protected void onDownloadImage(String filePath) {
        mInnerPhotoView.getSettings().setJavaScriptEnabled(true);
        mInnerPhotoView.getSettings().setUseWideViewPort(true);
        mInnerPhotoView.getSettings().setLoadWithOverviewMode(true);
        mInnerPhotoView.getSettings().setBuiltInZoomControls(true);
        mInnerPhotoView.getSettings().setDomStorageEnabled(true);
        mInnerPhotoView.getSettings().setDisplayZoomControls(false);
        mInnerPhotoView.addJavascriptInterface(new Object() {

            @JavascriptInterface
            public void closedialog() {
                if (mClickImageListener != null) {
                    mClickImageListener.onClick(null);
                }
            }
        }, "AndroidShowBigImage");

        mInnerPhotoView.setVerticalScrollBarEnabled(false);
        mInnerPhotoView.setHorizontalScrollBarEnabled(false);

        String str1 = "file://" + filePath;
        String str2 = "<html>\n"
                + "<head>\n"
                + "     <style>\n"
                + "          html,body{background:#000000;margin:0;padding:0;}"
                + "          *{-webkit-tap-highlight-color:rgba(0, 0, 0, 0);}\n"
                + "     </style>\n"
                + "     <script type=\"text/javascript\">\n"
                + "     var imgUrl = \""
                + str1
                + "\";"
                + "     var objImage = new Image();\n"
                + "     var realWidth = 0;\n"
                + "     var realHeight = 0;\n"
                + "\n"
                + "     function onLoad() {\n"
                + "          objImage.onload = function() {\n"
                + "               realWidth = objImage.width;\n"
                + "               realHeight = objImage.height;\n"
                + "\n"
                + "               document.gagImg.src = imgUrl;\n"
                + "               onResize();\n"
                + "          }\n"
                + "          objImage.src = imgUrl;\n"
                + "     }\n"
                + "\n"
                + "     function onResize() {\n"
                + "          var scale = 1;\n"
                + "          var newWidth = document.gagImg.width;\n"
                + "          if (realWidth > newWidth) {\n"
                + "               scale = realWidth / newWidth;\n"
                + "          } else {\n"
                + "               scale = newWidth / realWidth;\n"
                + "          }\n"
                + "\n"
                + "          hiddenHeight = Math.ceil(30 * scale);\n"
                + "          document.getElementById('hiddenBar').style.height = hiddenHeight + \"px\";\n"
                + "          document.getElementById('hiddenBar').style.marginTop = -hiddenHeight + \"px\";\n"
                + "     }\n"
                + "     </script>\n"
                + "</head>\n"
                + "<body onload=\"onLoad()\" onresize=\"onResize()\" onclick=\"AndroidShowBigImage.closedialog();\">\n"
                + "     <table style=\"width: 100%;height:100%;\">\n"
                + "          <tr style=\"width: 100%;\">\n"
                + "               <td valign=\"middle\" align=\"center\" style=\"width: 100%;\">\n"
                + "                    <div style=\"display:block\">\n"
                + "                         <img name=\"gagImg\" src=\"\" width=\"100%\" style=\"\" />\n"
                + "                    </div>\n"
                + "                    <div id=\"hiddenBar\" style=\"position:absolute; width: 0%; background: #000000;\"></div>\n"
                + "               </td>\n"
                + "          </tr>\n"
                + "     </table>\n"
                + "</body>\n"
                + "</html>";
        mInnerPhotoView.loadDataWithBaseURL("file:///android_asset/", str2, "text/html", "utf-8", null);

        mInnerPhotoView.setTag(new Object());
        mInnerPhotoView.setVisibility(View.VISIBLE);
    }

}
