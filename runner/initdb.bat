@ECHO OFF
call readprop.bat
TYPE %sqloriscchema%>%sqlscchema%
echo INSERT INTO CONFIG (FILEDIR) VALUES ('%dirfacesorigen%');>>%sqlscchema%
%sqlitedir% %dbpcm% < %sqlscchema%
%sqlitedir% %dbpcm% < %sqldir%
%sqlitedir% %dbpcm% < %sqlmap%
%sqlitedir% %dbpcm% < %sqlplayers%
%sqlitedir% %dbpcm% < %sqlteams%
%sqlitedir% %dbpcm% < %sqlpostinstall%