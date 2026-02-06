@echo off
echo Fixing merge conflict markers in Java files...
echo.

cd /d "%~dp0"

REM Remove conflict markers from SecurityConfig.java
powershell -Command "(Get-Content 'src\main\java\com\student\management\config\SecurityConfig.java' -Raw) -replace '(?m)^<<<<<<<.*?[\r\n]+', '' -replace '(?m)^=======[\r\n]+', '' -replace '(?m)^>>>>>>>.*?[\r\n]+', '' | Set-Content 'src\main\java\com\student\management\config\SecurityConfig.java'"

REM Remove conflict markers from DashboardController.java
powershell -Command "(Get-Content 'src\main\java\com\student\management\controller\DashboardController.java' -Raw) -replace '(?m)^<<<<<<<.*?[\r\n]+', '' -replace '(?m)^=======[\r\n]+', '' -replace '(?m)^>>>>>>>.*?[\r\n]+', '' | Set-Content 'src\main\java\com\student\management\controller\DashboardController.java'"

REM Remove conflict markers from DataInitializer.java
powershell -Command "(Get-Content 'src\main\java\com\student\management\config\DataInitializer.java' -Raw) -replace '(?m)^<<<<<<<.*?[\r\n]+', '' -replace '(?m)^=======[\r\n]+', '' -replace '(?m)^>>>>>>>.*?[\r\n]+', '' | Set-Content 'src\main\java\com\student\management\config\DataInitializer.java'"

REM Remove conflict markers from AuthController.java
powershell -Command "(Get-Content 'src\main\java\com\student\management\controller\AuthController.java' -Raw) -replace '(?m)^<<<<<<<.*?[\r\n]+', '' -replace '(?m)^=======[\r\n]+', '' -replace '(?m)^>>>>>>>.*?[\r\n]+', '' | Set-Content 'src\main\java\com\student\management\controller\AuthController.java'"

echo.
echo ========================================
echo Conflict markers removed from all files
echo ========================================
echo.
echo Now trying to compile...
echo.

call mvnw.cmd clean compile

pause
