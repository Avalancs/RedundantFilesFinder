plugins {
    java
    application
}

group = "org.avalancs";
version = "1.0";

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17);
    }
}

application {
    mainClass = "org.avalancs.redundantfilesfinder.Main";
    applicationDefaultJvmArgs = listOf("-Dfile.encoding=UTF-8");
}

tasks.withType<JavaCompile>().configureEach {
    options.compilerArgs.add("-Xlint:unchecked");
    options.isDeprecation = true;
    options.encoding = "UTF-8";
}

tasks.named<Jar>("jar").configure {
    archiveFileName.set("RedundantFilesFinder.jar");
}

tasks.test {
    useJUnitPlatform();
}

repositories {
    mavenCentral();
}

dependencies {
    implementation(libs.commons.io);

    testImplementation(libs.junit);
    //testImplementation("org.junit.jupiter:junit-jupiter-params:${junitVersion}");
    //testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:${junitVersion}");
    testImplementation(libs.mockito);
}

// extra files we want to include in the distribution
tasks.register("createExtras") {
    group = "distribution";
    inputs.property("version", version);
    outputs.dir(project.layout.buildDirectory.dir("extras"));

    doLast {
        val versionFile = layout.buildDirectory.file("extras/version.txt").get().asFile;
        versionFile.writeText("Version: ${version}, git: ${getGitHash()}");
    }
}

distributions {
    main {
        contents {
            from(tasks["createExtras"].outputs.files) {
                into("/");
            }
            from(project.files("readme.md", "license.txt")) {
                into("/");
            }
            into("/"); // do not create nested folder inside zip
        }
    }
}

tasks.named<Zip>("distZip").configure {
    archiveFileName.set("RedundantFilesFinder.zip");
    dependsOn(tasks.named("createExtras"));
}

tasks.named<Tar>("distTar").configure {
    enabled = false;
}

fun getGitHash() : String {
    val process : Process =  Runtime.getRuntime().exec("git rev-parse --short HEAD");
    process.waitFor();
    if(process.exitValue() == 0) {
        return String(process.inputStream.readAllBytes());
    } else {
        //logger.log(LogLevel.WARN, String(process.inputStream.readAllBytes()));
        //logger.log(LogLevel.WARN, String(process.errorStream.readAllBytes()));
        logger.log(LogLevel.WARN, "Could not read git commit hash, will return empty string")
        return "";
    }
}