@echo off
echo Launching builds in separate PowerShell windows

:: Launch Model build
start cmd /k "cd model && echo 'Building Model' && mvn clean install"

echo Model build in process...
timeout /t 12
:: Launch Kjar build
start cmd /k "cd kjar && echo 'Building Kjar' && mvn clean install"

echo Kjar build in process...
timeout /t 12
:: Launch Service build
start cmd /k "cd service && echo 'Building Service' && mvn clean package"

echo Service build in process...
echo All builds launched!
pause
