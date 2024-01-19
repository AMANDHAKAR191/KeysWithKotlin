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
#for google sign in
-keep class com.google.android.gms.auth.*{*;}
#-keep class com.google.android.gms.common.api.*{*;}
-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.internal.** { *; }
-keep class com.google.android.gms.auth.api.signin.** { *; }
-keep class com.google.android.gms.common.api.** { *; }

-keep class com.google.firebase.database.*{*;}
-keep class com.google.firebase.messaging.*{*;}
#for model classses
-keep class com.aman.keys.auth.domain.model.*{*;}
-keep class com.aman.keys.chats.domain.model.*{*;}
-keep class com.aman.keys.notes.domain.model.*{*;}
-keep class com.aman.keys.passwords.domain.model.*{*;}

#for services
-keep class com.aman.keys.notification_service.NotificationReceiver

-keep class com.google.gson.** { *; }
-keep class com.shaded.fasterxml.jackson.** { *; }
-keepnames class com.fasterxml.jackson.** { *; }
-keepnames class javax.servlet.** { *; }
-keepnames class org.ietf.jgss.** { *; }
-dontwarn org.w3c.dom.**
-dontwarn org.joda.time.**
-dontwarn org.shaded.apache.**
-dontwarn org.ietf.jgss.**
-keepattributes Signature
-keepattributes *Annotation*
-keepattributes EnclosingMethod
-keep class sun.misc.Unsafe.* { *; }
-keep class com.google.gson.stream.** { *; }
# Replace this with the actual package name of your model classes
#-keep class com.example.myapp.model.** { *; }
