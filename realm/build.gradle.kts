plugins {
    alias(libs.plugins.android.library)
    id("maven-publish")
}
group = "com.github.EdwardThompson-CodeDemon"
version = "1.0.16"
android {
    namespace = "com.realm"
    compileSdk = 34
    publishing {
        singleVariant("release") {
            withSourcesJar()
        }
    }

    defaultConfig {
        minSdk = 24
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(files("libs\\additionnal.jar"))
    implementation(files("libs\\androidprintsdk.jar"))
    implementation(files("libs\\btprintsdk.jar"))
    implementation(files("libs\\bugly_crash_release__2.1.3.jar"))
    implementation(files("libs\\com.datecs.fiscalprinter.jar"))
    implementation(files("libs\\DecodeWlt.jar"))
    implementation(files("libs\\dpuareu.jar"))
    implementation(files("libs\\jsch-0.1.52.jar"))
    implementation(files("libs\\MorphoSmart_SDK_6.45.0.0.jar"))
    implementation(files("libs\\printersdk.jar"))
    implementation(files("libs\\TrustFinger_v2.1.0.1.jar"))
    implementation(project(":mail"))
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    implementation("com.github.EdwardThompson-CodeDemon.SystemRealm:Annotations:0.0.50")
    implementation("net.zetetic:android-database-sqlcipher:4.5.0")
    implementation("com.amitshekhar.android:android-networking:1.0.2")
    implementation("org.apache.commons:commons-io:1.3.2")
    implementation("com.google.guava:guava:33.4.0-android")
    implementation("net.lingala.zip4j:zip4j:2.11.5")
    implementation("com.gemalto.wsq:wsq-android:1.2")
    implementation("com.github.bumptech.glide:glide:4.16.0")
    implementation("com.google.android.gms:play-services-maps:11.+")
    implementation("de.hdodenhof:circleimageview:2.1.0")
    implementation("com.xiaofeng.android:flowlayoutmanager:1.2.3.2")
    implementation("com.github.shuhart:stepview:1.5.1")

}


//tasks.register("bundleReleaseAar") {
//    doLast {
//        val aarFile = file("$buildDir/outputs/aar/${project.name}-release.aar")
//        copy {
//            from(zipTree(aarFile))
//            into("path/to/your/output/directory")
//        }
//    }
//}


//afterEvaluate {
//    publishing {
//        publications {
//            create<MavenPublication>("Realm") {
//                from(components["release"])
////                artifactId = "Realm"
//                pom { packaging = "aar" }
//            }
//        }
//    }
//}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                from(components["release"])
                groupId = "com.github.EdwardThompson-CodeDemon"
                artifactId = "Realm3"
//                version = "1.0.15"

                // Use the layout property to specify the path to the AAR file
//                artifact(layout.buildDirectory.file("outputs/aar/realm-release.aar"))
            }
        }
    }
}
//
//configure<PublishingExtension> {
//    publications.create<MavenPublication>("Realm") {
////        groupId = "com.github.EdwardThompson-CodeDemon"
//        groupId = "com.realm"
//        artifactId = "realm"
//        version = "1.0.15"
////        pom.packaging = "aar"
////        artifact(layout.buildDirectory.file("outputs/aar/${project.name}-release.aar"))
////        artifact("$buildDir/outputs/aar/${project.name}-release.aar")
////        artifact("$buildDir/libs/MyPlugin.jar")
//
//    }
////    repositories {
////        mavenLocal()
////    }
//}

