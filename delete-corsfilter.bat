@echo off
echo Deleting CorsFilter.java...
del /F /Q "src\main\java\com\student\management\config\CorsFilter.java"
if exist "src\main\java\com\student\management\config\CorsFilter.java" (
    echo ERROR: File still exists!
) else (
    echo SUCCESS: CorsFilter.java deleted!
)

echo.
echo Cleaning Maven build...
call mvnw.cmd clean

echo.
echo Done! Now run: mvnw.cmd spring-boot:run
pause
