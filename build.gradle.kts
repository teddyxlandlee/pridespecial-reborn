plugins {
    id("java")
    id("xland.gradle.forge-init-injector") version "1.2.0"
}

group = "xland.mcmod"
version = project.ext["mod_version"]!!

repositories {
    mavenCentral()
    maven("https://maven.fabricmc.net/") {
        name = "Fabric"
    }
    maven("https://maven.gegy.dev") {
        name = "Gegy Maven" // for PrideLib
    }
    maven("https://libraries.minecraft.net") {
        name = "Mojang Libraries"
    }
}

dependencies {
    implementation("com.google.code.gson:gson:2.8.0")
    implementation("net.fabricmc:sponge-mixin:0.11.4+mixin.0.8.5")
    implementation("org.apache.logging.log4j:log4j-api:2.8.1")
    implementation("io.github.queerbric:pridelib:1.5.1+1.21.9") {
        isTransitive = false
    }
    implementation("com.mojang:datafixerupper:8.0.16")
    compileOnly("org.jetbrains:annotations:26.1.0")
    implementation("org.ow2.asm:asm-commons:9.3")

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testRuntimeOnly("org.apache.logging.log4j:log4j-core:2.20.0")
    testRuntimeOnly("com.google.code.gson:gson:2.13.2")
    testRuntimeOnly("com.mojang:datafixerupper:8.0.16")
    testRuntimeOnly("com.google.code.gson:gson:2.13.2")
}

forgeInitInjector {
    modId = "pridespecial"
    stubPackage = "ER7bEax68TozOW9Y7J2hu"
    neoFlag("post_20_5")
    setClientEntrypoint("ygp/pridespecial/PrideSpecial")
}

tasks.processResources {
    inputs.property("version", project.version)
    filesMatching(listOf("fabric.mod.json", "META-INF/neoforge.mods.toml")) {
        expand("version" to project.version)
    }

    from("LICENSE") {
        rename { "LICENSE_pridespecial" }
    }
}

java.withSourcesJar()

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.release.set(21)
}

tasks.withType<AbstractArchiveTask>().configureEach {
    isPreserveFileTimestamps = false
    isReproducibleFileOrder = true
}

tasks.test {
    useJUnitPlatform()
}