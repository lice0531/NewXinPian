# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

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
-keep class androidx.core.app.CoreComponentFactory { *; }
# Gson
-keep class com.google.gson.** { *; }
-dontwarn com.google.gson.**

# OkHttp
-keep class okhttp3.** { *; }
-dontwarn okhttp3.**

# ButterKnife
-keep class butterknife.** { *; }
-dontwarn butterknife.**
-keep class butterknife.internal.** { *; }

# MMKV
-keep class com.tencent.** {*;}
-keep class com.tencent.mmkv.** {*;}
# GreenDao
-keep class org.greenrobot.greendao.**{*;}
-keepclassmembers class * extends org.greenrobot.greendao.AbstractDao {
  public static java.lang.String TABLENAME;
 }
-keep class **$Properties{*;}
# DialogPlus
-keep class com.orhanobut.dialogplus.** { *; }
-dontwarn com.orhanobut.dialogplus.**

# Nice Spinner
-keep class com.github.arcadefire.nice.** { *; }
-dontwarn com.github.arcadefire.nice.**

# Logger
-keep class com.orhanobut.logger.** { *; }
-dontwarn com.orhanobut.logger.**

# MPAndroidChart
-keep class com.github.mikephil.charting.** { *; }
-dontwarn com.github.mikephil.charting.**

# SmartRefreshLayout
-keep class com.scwang.smartrefresh.** { *; }
-dontwarn com.scwang.smartrefresh.**

# BaseRecyclerViewAdapterHelper
-keep class com.github.CymChad.** { *; }
-dontwarn com.github.CymChad.**

# WorkManager
-keep class androidx.work.** { *; }
-dontwarn androidx.work.**

# ZXing
-keep class com.google.zxing.** { *; }
-dontwarn com.google.zxing.**

# Hipermission
-keep class me.weyye.hipermission.** { *; }
-dontwarn me.weyye.hipermission.**

# GreenDaoUpgradeHelper
-keep class io.github.yuweiguocn.GreenDaoUpgradeHelper.** { *; }
-dontwarn io.github.yuweiguocn.GreenDaoUpgradeHelper.**
#Bugly
-dontwarn com.tencent.bugly.**
-keep public class com.tencent.bugly.**{*;}
#百度地图
-keep class com.baidu.** {*;}
-keep class vi.com.** {*;}
-keep class com.baidu.vi.** {*;}
-dontwarn com.baidu.**
# 保留所有JNI相关的类和方法
-keepclasseswithmembernames class * {
   native <methods>;
}

# 保留所有的Activity
-keep public class * extends android.app.Activity
#继承activity,application,service,broadcastReceiver,contentprovider....不进行混淆
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
#-keep public class * extends androidx.support.multidex.MultiDexApplication
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class * extends android.view.View
-keep class android.support.** {*;}
-keep class android_serialport_api.** {*;}