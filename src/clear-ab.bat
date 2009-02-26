@echo off
echo --- Starting clear --------------------------------
echo Clearing compiled jgralab classes
del ..\..\jgralab\src\*.class /s
echo ---------------------------------------------------
echo Clearing compiled javaextractor classes
del *.class /s
echo ----------------------------------- Clear done! ---
pause
