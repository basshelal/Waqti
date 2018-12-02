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

-keep class com.woxthebox.draglistview.** { *; }

# mandatory proguard rules for cache2k to keep the core implementation
-dontwarn org.slf4j.**
-dontwarn javax.cache.**
-keep interface org.cache2k.spi.Cache2kCoreProvider
-keep public class * extends org.cache2k.spi.Cache2kCoreProvider

# optional proguard rules for cache2k, to keep XML configuration code
# if only programmatic configuration is used, these rules may be ommitted
-keep interface org.cache2k.core.spi.CacheConfigurationProvider
-keep public class * extends org.cache2k.core.spi.CacheConfigurationProvider
-keepclassmembers public class * extends org.cache2k.configuration.ConfigurationBean {
    public void set*(...);
    public ** get*();
}