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

# Uncomment this to debug
#-keepnames class **

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

-keep class * extends com.google.protobuf.GeneratedMessageLite { *; }
-keepclassmembers class com.vladsch.flexmark.** { *; }
-keep class kotlin.Metadata { *; }
-keepclassmembers class com.potatosheep.kite.core.model.** { *; }
-keepclassmembers class com.potatosheep.kite.core.data.model.** { *; }
-keepclassmembers class com.potatosheep.kite.core.network.model.NetworkInstance { *; }
-keep class com.potatosheep.kite.core.common.enums.SortOption$Search
-keep class com.potatosheep.kite.core.common.enums.SortOption$Timeframe
-dontwarn com.google.devtools.ksp.processing.SymbolProcessorProvider
