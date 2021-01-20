@echo 切换工作目录
cd /d %~dp0

del /f /a /q "%cd%\product"
del /f /a /q "%cd%\bin"
del /f /a /q "%cd%\build"
del /f /a /q "%cd%\libs"

rd /s /q "%cd%\product"
rd /s /q "%cd%\bin"
rd /s /q "%cd%\build"
rd /s /q "%cd%\libs"

md "%cd%\product"
md "%cd%\product\bin"

echo 更新代码……
call svn revert -R "%cd%"
call svn up "%cd%"
call "%cd%\build_bin0.bat"
Md "%Resultpath%"
"%windir%\system32\xcopy.exe" "%cd%\bin\*.*" /e/y "%Resultpath%"

