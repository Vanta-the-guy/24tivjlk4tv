# Seed X-Ray — Forge Mod (1.20.4-49.2.0)

Predicts ore locations from the world seed and renders colored outlines through walls.
No scanning. Reads seed → runs same RNG Minecraft uses → draws results.

## Requirements to compile
- **Java 17** (must be on your PATH)
- **Internet** (Gradle downloads Forge toolchain on first run, ~1-2 GB)

## How to build (Windows)
```
gradlew.bat build
```

## How to build (Mac/Linux)
```
chmod +x gradlew
./gradlew build
```

The compiled mod JAR will be at:
```
build/libs/seed-xray-1.0.0.jar
```

Place that JAR into your `.minecraft/mods/` folder.
**You need Forge 49.2.0 for 1.20.4 — no other mods required.**

## Controls

| Action | Default |
|--------|---------|
| Toggle on/off | `X` key |
| Toggle via chat | `/seedxray` |
| Force on | `/seedxray on` |
| Force off | `/seedxray off` |
| Refresh after moving | `/seedxray refresh` |

## Ore colors

| Color | Ore |
|-------|-----|
| 💠 Cyan | Diamond |
| 🟡 Gold | Gold |
| 🔴 Red | Redstone |
| 🟢 Green | Emerald |
| 🔵 Blue | Lapis |
| 🟠 Orange | Ancient Debris |
| 🟫 Brown | Copper |
| ⬜ Grey | Iron / Coal |

## Why the converter site doesn't work for mods

The ZIP-to-JAR converter just renames the file. That works for **resource packs** but
NOT for mods — a mod JAR must contain compiled `.class` files. You must run
`gradlew build` with Java 17 to produce a real compiledmod JAR.

## Resource pack

Use `SeedXray-ResourcePack.zip` (separate download) for instant X-ray without compilation.
Drop it in `.minecraft/resourcepacks/` and enable it in Options → Resource Packs.
