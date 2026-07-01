@echo off
setlocal enabledelayedexpansion
title Seed X-Ray Mod Builder

echo.
echo  ============================================================
echo   Seed X-Ray Mod Builder for Forge 1.20.4
echo  ============================================================
echo.
echo  This script will:
echo    1. Compile the mod source code into a real JAR
echo    2. Copy the JAR to your mods folder
echo.
echo  First run downloads ~500 MB from the internet. Be patient.
echo  Press any key to start, or close this window to cancel.
echo.
pause > nul

REM ── Find Java 17 ────────────────────────────────────────────────────────────
set JAVA_CMD=
where java >nul 2>&1
if %ERRORLEVEL%==0 (
    for /f "tokens=*" %%j in ('java -version 2^>^&1 ^| findstr /i "17\|21"') do (
        set JAVA_CMD=java
    )
)

REM Check common Microsoft JDK location (what the crash log shows)
if "%JAVA_CMD%"=="" (
    for /d %%d in ("C:\Program Files\Microsoft\jdk-17*") do (
        if exist "%%d\bin\java.exe" set JAVA_CMD="%%d\bin\java.exe"
    )
)

REM Check Eclipse Adoptium
if "%JAVA_CMD%"=="" (
    for /d %%d in ("C:\Program Files\Eclipse Adoptium\jdk-17*") do (
        if exist "%%d\bin\java.exe" set JAVA_CMD="%%d\bin\java.exe"
    )
)

REM Check Oracle
if "%JAVA_CMD%"=="" (
    for /d %%d in ("C:\Program Files\Java\jdk-17*") do (
        if exist "%%d\bin\java.exe" set JAVA_CMD="%%d\bin\java.exe"
    )
)

if "%JAVA_CMD%"=="" (
    echo.
    echo  ERROR: Could not find Java 17.
    echo  Please install Java 17 from: https://adoptium.net
    echo  Then re-run this script.
    echo.
    pause
    exit /b 1
)

echo  Found Java: %JAVA_CMD%
set JAVA_HOME=
echo.

REM ── Run Gradle build ────────────────────────────────────────────────────────
echo  Building mod... (this may take several minutes on first run)
echo.

set "JAVA_EXE=%JAVA_CMD%"
call gradlew.bat build 2>&1

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo  ============================================================
    echo   BUILD FAILED. See errors above.
    echo  ============================================================
    echo.
    echo  Common fixes:
    echo    - Make sure you have internet access (Gradle needs to download Forge)
    echo    - Try running as Administrator
    echo    - Delete the .gradle folder and try again
    echo.
    pause
    exit /b 1
)

echo.
echo  ============================================================
echo   BUILD SUCCESSFUL!
echo  ============================================================
echo.

set "JAR_FILE=%~dp0build\libs\seed-xray-1.0.0.jar"
if not exist "%JAR_FILE%" (
    echo  ERROR: JAR not found at expected path: %JAR_FILE%
    pause
    exit /b 1
)

echo  JAR built: %JAR_FILE%
echo.

REM ── Find mods folder ────────────────────────────────────────────────────────
echo  Where do you want to install the mod?
echo.
echo    1. Standard .minecraft mods folder
echo    2. CurseForge instance (you choose which one)
echo    3. I'll copy it myself (skip auto-install)
echo.
set /p CHOICE="Enter 1, 2, or 3: "

if "%CHOICE%"=="1" (
    set "MODS_DIR=%APPDATA%\.minecraft\mods"
    goto :install
)

if "%CHOICE%"=="2" (
    echo.
    echo  CurseForge instances are at:
    echo  %USERPROFILE%\curseforge\minecraft\Instances\
    echo.
    set /p INSTANCE="Enter instance name (e.g. advadvadvadv (1)): "
    set "MODS_DIR=%USERPROFILE%\curseforge\minecraft\Instances\!INSTANCE!\mods"
    goto :install
)

echo.
echo  Skipping auto-install. Copy this file to your mods folder manually:
echo  %JAR_FILE%
echo.
pause
exit /b 0

:install
if not exist "!MODS_DIR!" (
    echo  Creating mods folder: !MODS_DIR!
    mkdir "!MODS_DIR!"
)

echo  Installing to: !MODS_DIR!
copy "%JAR_FILE%" "!MODS_DIR!\seed-xray-1.0.0.jar" > nul

if %ERRORLEVEL%==0 (
    echo.
    echo  ============================================================
    echo   INSTALLED! Launch Minecraft Forge and enjoy.
    echo  ============================================================
    echo.
    echo  Controls:
    echo    X key        = toggle X-ray on/off
    echo    /seedxray    = same as X key
    echo    /seedxray on ^| off ^| refresh
    echo.
) else (
    echo.
    echo  Copy failed. Manually copy this file to your mods folder:
    echo  %JAR_FILE%
    echo.
)

pause
