echo %cd%
echo 开始拆分extension aar
cd "%cd%\bin\"
echo 开始拆分国内的aar
mkdir cmadsdk_ext_china_no_gdt
copy cmadsdk_ext_china*.aar  %cd%\cmadsdk_ext_china_no_gdt
cd %cd%\cmadsdk_ext_china_no_gdt
unzip *.aar
rm *.aar
jar -xvf classes.jar
rm classes.jar
cd com
rm -rf qq
cd "%cd%\cmcm\adsdk\adapter"
rm GDT*.class
cd ..\..\..\..\
jar -cvf classes.jar com/
rm -rf com/
cd ..\
7za.exe cmadsdk_ext_china_no_gdt.aar" cmadsdk_ext_china_no_gdt