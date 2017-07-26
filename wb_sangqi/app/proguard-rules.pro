# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in E:\sdk/tools/proguard/proguard-android.txt
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

################################### 混淆配置 start ###########################################
#指定代码的压缩级别
-optimizationpasses 5
#包明不混合大小写
-dontusemixedcaseclassnames
#不去忽略非公共的库类
-dontskipnonpubliclibraryclasses
#优化  不优化输入的类文件
-dontoptimize
#预校验
-dontpreverify
#混淆时是否记录日志
-verbose
# 混淆时所采用的算法
-optimizations !code/simplification/arithmetic,!code/simplification/cast,!field/*,!class/merging/*
#-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*
#忽略警告
-ignorewarning
################################### 混淆配置 end ############################################


################## 记录生成的日志数据,gradle build时在本项目根目录输出         #################
####### 输出文件夹 build/outputs/mapping
#apk 包内所有 class 的内部结构
-dump class_files.txt
#未混淆的类和成员
-printseeds seeds.txt
#列出从 apk 中删除的代码
-printusage unused.txt
#混淆前后的映射
-printmapping mapping.txt
################## 记录生成的日志数据，gradle build时 在本项目根目录输出-end    #################


################## 混淆保护自己项目的部分代码以及引用的第三方jar包library start ##################
# 保护注解
-keepattributes *Annotation*
# 保护support v4 包
-dontwarn android.support.v4.app.**
-keep class android.support.v4.app.**{ *; }
# 保护JS接口
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}
##保持 native 方法不被混淆
-keepclasseswithmembernames class * {
    native <methods>;
}
##保持 Parcelable 不被混淆
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}
##保持 Serializable 不被混淆
-keepnames class * implements java.io.Serializable
#
#保持 Serializable 不被混淆并且enum 类也不被混淆
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}
#避免混淆泛型 如果混淆报错建议关掉
#–keepattributes Signature
# webview + js
-keepattributes *JavascriptInterface*

# okHttp3
-dontwarn com.squareup.okhttp3.**
-keep class com.squareup.okhttp3.**{ *;}
-dontwarn okio.**

# Retrofit
-dontwarn retrofit2.**
-keep class retrofit2.**{ *; }
-keepattributes Signature
-keepattributes Exceptions

# Butter Knife
-keep class butterknife.**{ *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewBinder{ *; }
-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}
-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}

# RxJava RxAndroid
-dontwarn sun.misc.**
-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
   long producerIndex;
   long consumerIndex;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode producerNode;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueConsumerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode consumerNode;
}

# Gson
-keepattributes Signature
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.stream.** { *; }

# Glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}

# tinker
-dontwarn com.tencent.tinker.anno.AnnotationProcessor
-keep @com.tencent.tinker.anno.DefaultLifeCycle public class *
-keep public class * extends android.app.Application {*;}
-keep public class com.tencent.tinker.loader.app.ApplicationLifeCycle {*;}
-keep public class * implements com.tencent.tinker.loader.app.ApplicationLifeCycle {*;}
-keep public class com.tencent.tinker.loader.TinkerLoader {*;}
-keep public class * extends com.tencent.tinker.loader.TinkerLoader {*;}
-keep public class com.tencent.tinker.loader.TinkerTestDexLoad {*;}
-keep class com.tencent.tinker.loader.**

# litpal
-dontwarn org.litepal.**
-keep class org.litepal.** { *; }

#保护MVVM框架
#-keep class com.arialyy.frame.**{*;}
-keepclassmembers class * extends com.arialyy.frame.module.AbsModule{
    public <init>(android.content.Context);
}
-keepclassmembers class * extends com.arialyy.frame.core.AbsActivity{
    protected void dataCallback(int, java.lang.Object);
}
-keepclassmembers class * extends com.arialyy.frame.core.AbsPopupWindow{
    protected void dataCallback(int, java.lang.Object);
}
-keepclassmembers class * extends com.arialyy.frame.core.AbsFragment{
    protected void dataCallback(int, java.lang.Object);
}
-keepclassmembers class * extends com.arialyy.frame.core.AbsDialogFragment{
    protected void dataCallback(int, java.lang.Object);
}
-keepclassmembers class * extends com.arialyy.frame.core.AbsAlertDialog{
    protected void dataCallback(int, java.lang.Object);
}
-keepclassmembers class * extends com.arialyy.frame.core.AbsDialog{
    protected void dataCallback(int, java.lang.Object);
}

# 保护 aria
-dontwarn com.arialyy.aria.**
-keep class com.arialyy.aria.**{*;}

# 保护databinding
#-dontwarn android.databinding.**
#-keep class android.databinding.**{*;}

################## 混淆保护自己项目的部分代码以及引用的第三方jar包library-end ##################


################################### 混淆保护自己的代码 start #################################
-keepnames class com.utstar.appstoreapplication.activity.StartAppActivity
-keep class com.utstar.appstoreapplication.activity.service.DayTaskRemoteService{*;}
-keep class com.utstar.appstoreapplication.activity.entity.**{*;}
-keep class com.ut_sdk.day_task.DayTaskEntity{*;}
-keep class com.ut_sdk.day_task.IDayTaskInterface{*;}
-keep class com.ut_sdk.pay.PayEntity{*;}
-keep class com.ut_sdk.pay.IWBPay{*;}

-keep public class com.utstar.baseplayer.activity.** { *; }
-keep class com.utstar.baseplayer.activity.** { *; }
################################### 混淆保护自己的代码  end  #################################



###################################### tinker 混淆 start ####################################
#your dex.loader pattern here
-keep class tinker.sample.android.app.SampleApplication

###################################### tinker 混淆 end   ####################################

