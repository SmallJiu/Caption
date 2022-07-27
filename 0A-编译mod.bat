@echo off
rmdir /q /s build\classes
rmdir /q /s build\dependency-cache
rmdir /q /s build\resources
rmdir /q /s build\retromapping
rmdir /q /s build\sources
rmdir /q /s build\taskLogs
rmdir /q /s build\tmp
goto start

:start
gradlew build
pause
