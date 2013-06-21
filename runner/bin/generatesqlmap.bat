@ECHO OFF
call readprop.bat
echo PRAGMA synchronous=OFF;> %2
chcp 1251>NUL
%seddir% -f static\sedscripts\mapfile.sed <%1 >> %2