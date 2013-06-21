@ECHO OFF
call readprop.bat
echo PRAGMA synchronous=OFF;> %2
chcp 65001>NUL
%seddir% -f %3 <%1 >> %2