@ECHO OFF
call readprop.bat
call bin\generatesqlcvs.bat "%csvplayers%" %sqlplayers% "static\sedscripts\playercsv.sed"
REM call bin\generatesqlcvs.bat "%csvteams%" %sqlteams% "static\sedscripts\teamscsv.sed"
call bin\generatesqlfiles.bat "%dirfacesorigen%" %sqldir%
call bin\generatesqlmap.bat "%listplayersmap%" %sqlmap%