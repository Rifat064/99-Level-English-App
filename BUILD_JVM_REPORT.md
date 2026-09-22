# JVM Target Compatibility Report

## Overview
When building the project, Gradle failed on the `:core:core-model:compileKotlin` task with the following error:

```text
Inconsistent JVM-target compatibility detected for tasks 'compileJava' (17) and 'compileKotlin' (21).
```

---

## Root Cause Analysis
1. **Module Type**: `:core:core-model` is a pure Kotlin JVM library (using `plugins { alias(libs.plugins.kotlin.jvm) }`), unlike the rest of the project modules which are Android libraries using the `kotlin-android` plugin.
2. **Java Target Configuration**: `:core:core-model/build.gradle.kts` explicitly configured the Java compiler target to **Java 17**:
   ```kotlin
   java {
       sourceCompatibility = JavaVersion.VERSION_17
       targetCompatibility = JavaVersion.VERSION_17
   }
   ```
3. **Kotlin Target Default**: In Kotlin 2.0+, when targeting a Java 21 JDK environment, the Kotlin JVM plugin defaults its `compileKotlin` JVM target bytecode to **JVM 21** if left unspecified.
4. **Mismatch Conflict**: Because `compileJava` was generating Java 17 bytecode while `compileKotlin` attempted to generate JVM 21 bytecode, Gradle stopped the build due to JVM-target mismatch validation rules.

---

## Resolution
The `:core:core-model/build.gradle.kts` file was updated to explicitly configure the Kotlin compiler's `jvmTarget` to `JVM_17`:

```kotlin
kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}
```

---

## Verification
1. Running `./gradlew :core:core-model:compileKotlin` succeeded cleanly.
2. Running `./gradlew assembleDebug` succeeded with no errors.
