# How to build for free using GitHub (no install needed)

## Step 1 — Create a free GitHub account
Go to https://github.com and sign up. It's free, no credit card.

## Step 2 — Create a new repository
1. Click the **+** icon in the top-right corner
2. Click **New repository**
3. Name it anything, e.g. `seed-xray-mod`
4. Make sure it is set to **Public**
5. Click **Create repository**

## Step 3 — Upload the mod files
1. Click **uploading an existing file** (shown on the empty repo page)
2. Drag ALL files and folders from this ZIP into the upload area:
   - `.github/` folder
   - `gradle/` folder
   - `src/` folder
   - `build.gradle`
   - `settings.gradle`
   - `gradle.properties`
   - `BUILD-AND-INSTALL.bat`
3. Click **Commit changes**

## Step 4 — Wait for the build (~5 minutes)
1. Click the **Actions** tab at the top of the repo
2. You'll see **Build Seed X-Ray Mod** running (yellow circle = in progress)
3. Wait for the green checkmark ✅

## Step 5 — Download your compiled JAR
1. Click the completed build run
2. Scroll down to **Artifacts**
3. Click **seed-xray-mod** to download
4. Extract the ZIP — inside is `seed-xray-1.0.0.jar`

## Step 6 — Install the mod
Copy `seed-xray-1.0.0.jar` to your mods folder:
```
C:\Users\aiden\curseforge\minecraft\Instances\advadvadvadv (1)\mods\
```

Launch Minecraft Forge 1.20.4 and it will load.

---

## Controls
| | |
|---|---|
| **X** key | Toggle ore X-ray on/off |
| `/seedxray` | Same as X key |
| `/seedxray on` | Force on + predict ores |
| `/seedxray off` | Force off |
| `/seedxray refresh` | Refresh after moving |
