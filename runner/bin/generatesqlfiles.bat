@ECHO OFF
call readprop.bat
echo PRAGMA synchronous=OFF;> %2
chcp 1251>NUL
DIR %1 /S/B/L/a-d | findstr /R ".*bin$" | %seddir% -f static\sedscripts\files.sed >> %2