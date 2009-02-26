@echo off
echo --- Starting clear --------------------------------
echo Clearing compiled javaextractor classes
del *.class /s > NUL
echo Deleting generated schema files
rd javaextractor\schema /s /q
echo --- Starting build --------------------------------
echo Adding this directory to the classpath
set CLASSPATH=.;..\..\jgralab\src;..\..\common\lib\getopt\java-getopt-1.0.13.jar;..\..\common\lib\antlr\org.antlr_2.7.6.jar;%CLASSPATH%
echo ---------------------------------------------------
echo Generating lexer and parser
java antlr.Tool -o javaextractor javaextractor\java15.g
echo ---------------------------------------------------
echo Generating treewalker
java antlr.Tool -o javaextractor javaextractor\java15.tree.g
echo ---------------------------------------------------
echo Generating JavaExtractor graph classes
java de.uni_koblenz.jgralab.utilities.tgschema2java.TgSchema2Java -f java5.tg -p .
echo ---------------------------------------------------
echo Building javaextractor
javac javaextractor\*.java
echo ----------------------------------- Build done! ---
pause
