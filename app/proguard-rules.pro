# Add project specific ProGuard rules here.

# ---- Kotlin ----
-keepclassmembers class **$WhenMappings {
    <fields>;
}
-keep class kotlin.Metadata { *; }

# ---- Firebase Firestore (data classes mapped via @DocumentId / @PropertyName) ----
-keepattributes Signature
-keepattributes *Annotation*
-keepattributes EnclosingMethod
-keepattributes InnerClasses

# Keep all domain model classes (Firestore deserialization needs their fields/constructors)
-keep class com.openclassroom.p15.domain.model.** { *; }

# ---- Firebase Auth / Firebase UI ----
-keep class com.google.firebase.** { *; }
-keep class com.firebase.ui.** { *; }
-dontwarn com.google.firebase.**

# ---- Hilt / Dagger ----
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep @dagger.hilt.android.lifecycle.HiltViewModel class * { *; }
-keep @dagger.hilt.InstallIn class * { *; }
-keep @javax.inject.Singleton class * { *; }
-dontwarn dagger.hilt.**

# ---- Coil (image loading) ----
-dontwarn coil.**

# ---- Coroutines ----
-keepclassmembers class kotlinx.coroutines.** {
    volatile <fields>;
}
-dontwarn kotlinx.coroutines.**

# ---- Google Play Services ----
-keep class com.google.android.gms.** { *; }
-dontwarn com.google.android.gms.**

# ---- Keep line numbers for crash reports ----
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile
