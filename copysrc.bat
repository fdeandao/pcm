@ECHO OFF
DEL /S /F /Q %CD%\server\
DEL /S /F /Q %CD%\runner\
MKDIR %CD%\runner\generate
MKDIR %CD%\runner\logs
MKDIR %CD%\runner\inputdata
xcopy /yE C:\Users\koferdo\Documents\NetBeansProjects\pcm\src %CD%\server\
xcopy /y C:\myapps\peswork\conf.properties %CD%\runner\
xcopy /y C:\myapps\peswork\initdb.bat %CD%\runner\
xcopy /y C:\myapps\peswork\initsql.bat %CD%\runner\
xcopy /y C:\myapps\peswork\readprop.bat %CD%\runner\
xcopy /yE C:\myapps\peswork\bin %CD%\runner\bin\
xcopy /yE C:\myapps\peswork\sed %CD%\runner\sed\
xcopy /yE C:\myapps\peswork\sqlite %CD%\runner\sqlite\
xcopy /yE C:\myapps\peswork\static %CD%\runner\static\
DEL /F /Q %CD%\runner\bin\pcmserver.jar