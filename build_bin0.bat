echo %cd%
echo 开始编译
"%windir%\system32\cmd.exe" /c gradlew clean
"%windir%\system32\cmd.exe" /c gradlew assembleRelease --stacktrace --info -debug
"%windir%\system32\xcopy.exe" "%cd%\build\intermediates\manifests\release\*.*" "%cd%\bin\*.*" /e /y
"%windir%\system32\xcopy.exe" "%cd%\build\outputs\lint*.*" "%cd%\bin\" /e /y
"%windir%\system32\xcopy.exe" "%cd%\build\outputs\mapping\release\*.txt" "%cd%\bin\proguard\" /e /y
"%windir%\system32\xcopy.exe" "%cd%\build\outputs\*.*" "%cd%\bin\*.*" /e /y
