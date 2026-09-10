<div align="center">
    <img src="https://cdn.rscomeback.fr/bdapi/mark.svg" width="150px" height="150px" />
</div>

# BDApi

BDApi is a small Java library for creating and animating Minecraft block displays on Paper. It supports two backends: real server-side Paper block displays and optional client-side (fake) block displays sent through PacketEvents.

## Requirements

- Paper 1.21.11
- Java 21+
- PacketEvents (optional, only required for the `PACKET_EVENTS` backend)

## Installation

Add the repository and dependency to your plugin:

```kotlin
repositories {
    maven("https://maven.rscomeback.fr/releases")
}

dependencies {
    implementation("fr.redsavant:bdapi:1.0.0-beta.1")
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

The default backend is `PAPER`. You can change it with a configuration:

```java
BDApi.init(this, BDApiConfig.builder()
        .defaultBackend(DisplayBackend.PAPER)
        .build());
```

## Backends

BDApi exposes two backends through the `DisplayBackend` enum:

- `PAPER` — a real server-side Bukkit/Paper `BlockDisplay`. Visible to every eligible player, persists as a world entity, and does not require PacketEvents.
- `PACKET_EVENTS` — a client-side fake `BlockDisplay`. No Bukkit entity exists on the server; the display is sent to specific players as packets. Requires PacketEvents to be installed and initialized.

If the `PACKET_EVENTS` backend is requested while PacketEvents is unavailable, spawning throws an `IllegalStateException` with a clear message. Paper usage never touches PacketEvents classes.

## Usage

Create a block display (defaults to the Paper backend):

```java
DisplayCrate crate = BDApi.get().displays().create()
        .at(location)
        .block(Material.DIAMOND_BLOCK)
        .scale(1.5f)
        .spawn();
```

Select a backend per display:

```java
DisplayCrate crate = BDApi.get().displays().create()
        .backend(DisplayBackend.PACKET_EVENTS)
        .at(location)
        .block(Material.DIAMOND_BLOCK)
        .spawn();
```

## Per-player and global displays

Packet displays can target specific players or all players:

```java
BDApi.get().displays().create()
        .backend(DisplayBackend.PACKET_EVENTS)
        .viewer(player)
        .at(location)
        .block(Material.DIAMOND_BLOCK)
        .spawn();
```

```java
BDApi.get().displays().create()
        .backend(DisplayBackend.PACKET_EVENTS)
        .global()
        .at(location)
        .block(Material.DIAMOND_BLOCK)
        .spawn();
```

Global displays are sent to eligible online players and to players who join later, and viewer state is cleared when players disconnect.

## Visibility

```java
crate.show(player);
crate.hide(player);
crate.addViewer(player);
crate.removeViewer(player);
crate.isVisibleTo(player);
crate.viewers();
```

For the Paper backend these calls are no-ops because Paper displays are global world entities.

## Animations

Animations work on both backends:

```java
crate.animate()
        .moveTo(target)
        .rotateTo(0, 180, 0)
        .duration(2, TimeUnit.SECONDS)
        .easing(Easing.EASE_OUT)
        .play();
```

## Physics

```java
crate.physics()
        .gravity()
        .bounce(0.4)
        .start();
```

BDApi computes the logical movement; the backend decides how the new state reaches the client.

## Coordinate behavior

`.at(location)` sets the display position. By default displays use the `CENTER` anchor: scaling and rotation pivot around the block's center, so a scaled or rotated display stays centered on the same point instead of drifting toward the block corner. For `scale 1` with no rotation this is identical to placing a normal block at the location.

Use `.anchor(Anchor.CORNER)` to keep the raw Minecraft behavior where scaling and rotation pivot around the block's minimum corner.

Both backends share the same coordinate and transform logic, so the same `.at(location)` and transformation produce the same visual result.

Timelines, display groups, transformations, meteor effects and explosion effects are also available.

## Limitations

- The `PACKET_EVENTS` backend requires PacketEvents installed and initialized on the server.
- Client-side displays are not real entities: they do not collide, are not saved to the world, and are only visible to their viewers.
- Per-player visibility applies to the `PACKET_EVENTS` backend only.

## Status

BDApi is currently available as `1.0.0-beta.1`. The API may change between versions.
