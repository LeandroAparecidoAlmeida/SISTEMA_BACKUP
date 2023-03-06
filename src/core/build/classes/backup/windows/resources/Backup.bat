@echo off
chcp 1252
goto startapp
:startapp
    start javaw.exe -Xms512m -Xmx2048m -Xss20m -jar "%~dp0Backup.jar"
goto end
:end