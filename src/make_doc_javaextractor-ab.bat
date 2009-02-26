@echo off
javadoc -J-Xmx768m -author -version -private -subpackages javaextractor -exclude javaextractor.schema;javaextractor.schema.impl -noqualifier all -classpath ".;..\..\jgralab\src;..\..\common\lib\getopt\java-getopt-1.0.13.jar;..\..\common\lib\antlr\org.antlr_2.7.6.jar;%CLASSPATH%" -d ..\doc
pause  