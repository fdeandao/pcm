@ECHO OFF
call ..\readprop.bat
For /f "tokens=2-4 delims=/ " %%a in ('date /t') do (set mydate=%%c-%%a-%%b)
For /f "tokens=1-2 delims=/:" %%a in ('time /t') do (set mytime=%%a%%b)
java -cp lib\sqlite4java.jar -jar pcmserver.jar -h 127.0.0.1 -p 4555 -d ..\static\www\> ..\logs\%mydate%_%mytime%
REM > ..\logs\%mydate%_%mytime%