@echo off
echo killing !
for /f "tokens=1" %%i in ('jps -m ^|find "api-admin.api-admin"') do (taskkill /F /PID %%i)&&(echo kill %%i) 
echo successed !