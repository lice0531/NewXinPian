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
-keep class com.tencent.mmkv.** { *; }
-dontwarn com.tencent.mmkv.**

# GreenDao
-keep class org.greenrobot.greendao.** { *; }
-dontwarn org.greenrobot.greendao.**

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
