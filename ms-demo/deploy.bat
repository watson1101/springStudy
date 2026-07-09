@echo off
REM Deploy script - upload JAR via SCP and restart service via SSH
REM Usage: deploy.bat <jar-path>

setlocal enabledelayedexpansion

REM === CONFIG ===
set "SSH_USER=hong"
set "SSH_HOST=localhost"
set "REMOTE_PATH=/home/hong/apps/msdemo"
REM ==============

set "JAR_FILE=%~1"
set "JAR_NAME=%~nx1"

echo.
echo === Deploy ===
echo   JAR:  %JAR_FILE%
echo   Dest: %SSH_USER%@%SSH_HOST%:%REMOTE_PATH%
echo.

if not exist "%JAR_FILE%" (
    echo [ERROR] JAR not found: %JAR_FILE%
    pause
    exit /b 1
)

echo [1/3] Checking SSH connection...
ssh -o BatchMode=yes -o ConnectTimeout=3 %SSH_USER%@%SSH_HOST% "echo OK" >nul 2>&1
if errorlevel 1 (
    echo [INFO] SSH key not configured, password prompt may appear...
)

echo [2/3] Uploading %JAR_NAME%...
ssh %SSH_USER%@%SSH_HOST% "mkdir -p %REMOTE_PATH%"
scp "%JAR_FILE%" %SSH_USER%@%SSH_HOST%:%REMOTE_PATH%/

echo [3/3] Starting service...
ssh %SSH_USER%@%SSH_HOST% "pkill -f '%JAR_NAME%' 2>/dev/null; sleep 1"
ssh -f %SSH_USER%@%SSH_HOST% "cd '%REMOTE_PATH%' && nohup java -jar '%JAR_NAME%'"

timeout /t 3 /nobreak >nul
ssh %SSH_USER%@%SSH_HOST% "pgrep -f '%JAR_NAME%' >/dev/null 2>&1 && echo OK - service is running || echo FAILED"

echo.
echo === Done ===
echo.