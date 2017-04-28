# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\work\android\android-sdk-windows/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

-keepattributes Signature

-dontshrink

-keep class **.BuildConfig{ *; }

-dontwarn com.marsor.common.activities.InstructionActivity
-keep class com.marsor.common.activities.InstructionActivity{ *;}

-dontnote android.support.**

-dontnote com.google.vending.licensing.ILicensingService

-dontnote com.android.vending.licensing.ILicensingService

-dontnote android.widget.DateTimeView

-keep class * implements java.util.Collection

-dontwarn org.slf4j.impl.**
-keep class org.slf4j.impl.**{ *;}

-keepattributes Exceptions,InnerClasses,...
-keep class com.icechn.library.showphoto.ShowPhotoLib{*;}
-keep class com.icechn.library.showphoto.ShowPhotoLib$ShowPhotoLibParam{*;}
-keepclasseswithmembers class com.icechn.library.showphoto.Utils.BaseUtils{
    <fields>;
}
-keepclasseswithmembers class com.icechn.library.showphoto.Photo.MultiPhotoDialog{
    <methods>;
}
-keepclasseswithmembers class com.icechn.library.showphoto.Photo.PhotoInfo{
    <fields>;
}


#--------------- BEGIN: okhttp ----------
-keepattributes Signature
-keepattributes *Annotation*
-keep class okhttp3.** { *; }
-keep class okhttp3.OkHttpClient { *; }
-keep interface okhttp3.** { *; }
-keep class com.squareup.okhttp.** { *; }
-keep interface com.squareup.okhttp.** { *; }
-dontwarn com.squareup.okhttp.**
-dontwarn okhttp3.**
#--------------- END: okhttp ----------

#--------------- BEGIN: okio ----------
-keep class sun.misc.Unsafe { *; }
-dontwarn java.nio.file.*
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
-dontwarn okio.**
#--------------- END: okio ----------
