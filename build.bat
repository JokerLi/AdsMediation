@echo «–ªªπ§◊˜ƒø¬º
cd /d %~dp0

set CurrentDate=%date:~0,4%%date:~5,2%%date:~8,2%
set CurrentTime=%time:~0,2%%time:~3,2%%time:~6,2%
set Resultpath="E:\KINGSOFT_DUBA\build\Build_Result\prj\%module_name%\%CurrentDate%.%BUILD_NUMBER%"
set PythonExePath="E:\KINGSOFT_DUBA\build\Build_Tools\Python\python.exe"
set ANT_OPTS=%ANT_OPTS% -Xmx256m
set NDKExePath="D:\install\android-ndk-r10e\ndk-build.cmd"

del /a /q "E:\KINGSOFT_DUBA\build\Build_Result\prj"

rd /s /q "E:\KINGSOFT_DUBA\build\Build_Result\prj"

md "E:\KINGSOFT_DUBA\build\Build_Result\prj"

echo %BUILD_NUMBER% > E:\KINGSOFT_DUBA\build\Build_Files\cmadsdk.txt

rem cmbee±‡“Î
call "%cd%\buildcmbee.bat"

call E:\KINGSOFT_DUBA\build\Build_Files\PutFileToFtp_mdsdk.build.cmd

if ERRORLEVEL 0	EXIT
