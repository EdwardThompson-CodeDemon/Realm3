// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    id("maven-publish")

}

group = "com.github.EdwardThompson-CodeDemon"
version = "1.0.17"

subprojects {
    plugins.withId("maven-publish") {
        group = rootProject.group
        version = rootProject.version

        afterEvaluate {
            publishing {
                publications {
                    create<MavenPublication>("release") {
                        from(components["release"])
                        groupId = rootProject.group.toString()
                        artifactId = project.name

                    }
                }
            }
        }
    }
}