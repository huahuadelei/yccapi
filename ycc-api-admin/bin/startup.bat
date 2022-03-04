@echo off
rem 获取当前bin目录的上级目录
set "PROJECT_HOME=%~dp0;"
set "PROJECT_HOME=%PROJECT_HOME:~0,-6%"

set "JAVA_SCRIPT=%JAVA_HOME%\bin\java.exe"
set "JAR_NAME=%PROJECT_HOME%\target\ycc-api-admin.jar"
set "CONFIG_HOME=classpath:/,classpath:/config/,file:./,file:./config/"
set "CONFIG_HOME=%CONFIG_HOME%,file:%PROJECT_HOME%/config/"

rem 配置JVM参数
set "VM_OPTIONS=-Xms512m -Xmx700m"
set "VM_OPTIONS=%VM_OPTIONS% -XX:+UseG1GC"
set "VM_OPTIONS=%VM_OPTIONS% -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -XX:+PrintGCApplicationStoppedTime  -Xloggc:%PROJECT_HOME%/logs/gc.log"

rem 设置java.exe命令及参数
set "JAVA_OPTIONS=%JAVA_SCRIPT% %VM_OPTIONS%"
set "JAVA_OPTIONS=%JAVA_OPTIONS% -Dproject.home=%PROJECT_HOME%"
set "JAVA_OPTIONS=%JAVA_OPTIONS% -Dfile.encoding=UTF-8"

rem 执行的jar文件
set "JAVA_OPTIONS=%JAVA_OPTIONS% -jar %JAR_NAME%"
set "JAVA_OPTIONS=%JAVA_OPTIONS% --spring.config.location=%CONFIG_HOME%"

rem 设置程序的启动标识用于关闭时搜索程序的进程ID
set "JAVA_OPTIONS=%JAVA_OPTIONS% api-admin.api-admin"

call %JAVA_OPTIONS%

