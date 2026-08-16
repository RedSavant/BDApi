# BDApi

BDApi is a small Java library for creating and animating Minecraft block displays on Paper.

## Requirements

- Paper 1.21.11
- Java 21+

## Installation

Add the repository and dependency to your plugin:

```kotlin
repositories {
    maven("https://maven.rscomeback.fr/releases")
}

dependencies {
    implementation("fr.redsavant:displayapi:1.0.0-beta.1")
}
```

Include BDApi in your plugin JAR with Shadow when building your plugin.

To build the library locally instead:

```bash
./gradlew build
```

## Setup

Initialize BDApi when your plugin starts and shut it down when your plugin stops:

```java
@Override
public void onEnable() {
    BDApi.init(this);
}

@Override
public void onDisable() {
    BDApi.shutdown(true);
}
```

## Usage

Create a block display:

```java
DisplayCrate crate = BDApi.get().displays().create()
        .at(location)
        .block(Material.DIAMOND_BLOCK)
        .scale(1.5f)
        .spawn();
```

Animate it:

```java
crate.animate()
        .moveTo(target)
        .rotateTo(0, 180, 0)
        .duration(2, TimeUnit.SECONDS)
        .easing(Easing.EASE_OUT)
        .play();
```

Add physics:

```java
crate.physics()
        .gravity()
        .bounce(0.4)
        .start();
```

Timelines, display groups, transformations, meteor effects and explosion effects are also available.

## Status

BDApi is currently available as `1.0.0-beta.1`. The API may change between versions.
