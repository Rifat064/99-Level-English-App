# ProGuard rules
-keepattributes *Annotation*, InnerClasses

# kotlinx.serialization
-keepclassmembers class * {
    @kotlinx.serialization.Serializable <fields>;
}
-keepnames @kotlinx.serialization.Serializable class *
-keep class kotlinx.serialization.json.** { *; }

# Supabase & Ktor
-keep class io.ktor.** { *; }
-keep class io.github.jan.supabase.** { *; }

# Core Models (DTOs)
-keep class com.shobdodaily.core.network.model.** { *; }
-keep class com.shobdodaily.core.model.** { *; }

# Ignore Ktor debug missing classes on Android
-dontwarn java.lang.management.**
