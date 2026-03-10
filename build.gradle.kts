// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    id("com.google.gms.google-services") version "4.4.4" apply false
    alias(libs.plugins.hilt.android.plugin) apply false
    alias(libs.plugins.kotlin.kapt) apply false
    id("org.sonarqube") version "5.1.0.4882"
}

sonarqube {
    properties {
        property("sonar.projectKey", "DamienA24_p15-openclassroom")
        property("sonar.organization", "damiena24")
        property("sonar.host.url", "https://sonarcloud.io")
        property("sonar.coverage.jacoco.xmlReportPaths", "app/build/reports/jacoco/jacocoTestReport/jacocoTestReport.xml")
        property("sonar.java.binaries", "app/build/tmp/kotlin-classes/debug")
        property("sonar.qualitygate.wait", "true")
        property("sonar.exclusions", listOf(
            "**/R.class", "**/R\$*.class", "**/BuildConfig.*", "**/Manifest*.*",
            "**/*_MembersInjector.*", "**/Dagger*Component*.*", "**/*_Factory.*", "**/ComposableSingletons*"
        ))
    }
}