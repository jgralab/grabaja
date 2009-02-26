@echo off
echo --- Starting build --------------------------------
echo Adding required directories to the classpath
set CLASSPATH=.;..\..\jgralab\src;..\..\common\lib\getopt\java-getopt-1.0.13.jar;..\..\common\lib\antlr\org.antlr_2.7.6.jar;%CLASSPATH%
echo ---------------------------------------------------
echo Testing javaextractor with some stuff...
java -Xmx768M javaextractor.JavaExtractor ..\testit\fieldresolving\test1_1.java -out ..\testit\fieldresolving\test1.tg -name "Test No. 1" -log ..\testit\fieldresolving\test1 -eager
java -Xmx768M javaextractor.JavaExtractor ..\testit\fieldresolving\test2_1.java ..\testit\fieldresolving\test2_2.java -out ..\testit\fieldresolving\test2.tg -name "Test No. 2" -log ..\testit\fieldresolving\test2 -eager
java -Xmx768M javaextractor.JavaExtractor ..\testit\fieldresolving\test3_1.java ..\testit\fieldresolving\test3_2.java ..\testit\fieldresolving\test3_3.java -out ..\testit\fieldresolving\test3.tg -name "Test No. 3" -log ..\testit\fieldresolving\test3 -eager
java -Xmx768M javaextractor.JavaExtractor ..\testit\fieldresolving\test4_1.java ..\testit\fieldresolving\test4_2.java -out ..\testit\fieldresolving\test4.tg -name "Test No. 4" -log ..\testit\fieldresolving\test4 -eager
java -Xmx768M javaextractor.JavaExtractor ..\testit\fieldresolving\test5_1.java ..\testit\fieldresolving\test5_2.java -out ..\testit\fieldresolving\test5.tg -name "Test No. 5" -log ..\testit\fieldresolving\test5 -eager
java -Xmx768M javaextractor.JavaExtractor ..\testit\fieldresolving\test6_1.java ..\testit\fieldresolving\test6_2.java -out ..\testit\fieldresolving\test6.tg -name "Test No. 6" -log ..\testit\fieldresolving\test6 -eager
echo ---------------------------------------------------
echo Generating .DOT files from extracted graph
java -Xmx768M de.uni_koblenz.jgralab.utilities.tg2dot.Tg2Dot --graph ..\testit\fieldresolving\test1.tg --output ..\testit\fieldresolving\test1.dot --domains --edgeattr --rolenames --reversed
java -Xmx768M de.uni_koblenz.jgralab.utilities.tg2dot.Tg2Dot --graph ..\testit\fieldresolving\test2.tg --output ..\testit\fieldresolving\test2.dot --domains --edgeattr --rolenames --reversed
java -Xmx768M de.uni_koblenz.jgralab.utilities.tg2dot.Tg2Dot --graph ..\testit\fieldresolving\test3.tg --output ..\testit\fieldresolving\test3.dot --domains --edgeattr --rolenames --reversed
java -Xmx768M de.uni_koblenz.jgralab.utilities.tg2dot.Tg2Dot --graph ..\testit\fieldresolving\test4.tg --output ..\testit\fieldresolving\test4.dot --domains --edgeattr --rolenames --reversed
java -Xmx768M de.uni_koblenz.jgralab.utilities.tg2dot.Tg2Dot --graph ..\testit\fieldresolving\test5.tg --output ..\testit\fieldresolving\test5.dot --domains --edgeattr --rolenames --reversed
java -Xmx768M de.uni_koblenz.jgralab.utilities.tg2dot.Tg2Dot --graph ..\testit\fieldresolving\test6.tg --output ..\testit\fieldresolving\test6.dot --domains --edgeattr --rolenames --reversed
echo ---------------------------------------------------
echo Generating .PNG files from .DOT files
dot -Tpng -o..\testit\fieldresolving\test1.png ..\testit\fieldresolving\test1.dot
dot -Tpng -o..\testit\fieldresolving\test2.png ..\testit\fieldresolving\test2.dot
dot -Tpng -o..\testit\fieldresolving\test3.png ..\testit\fieldresolving\test3.dot
dot -Tpng -o..\testit\fieldresolving\test4.png ..\testit\fieldresolving\test4.dot
dot -Tpng -o..\testit\fieldresolving\test5.png ..\testit\fieldresolving\test5.dot
dot -Tpng -o..\testit\fieldresolving\test6.png ..\testit\fieldresolving\test6.dot

::--- general merging & resolving --------------------------------------------
java -Xmx1G javaextractor.JavaExtractor "..\testit\merging" -out ..\testit\merging\merging_lazy.tg
java -Xmx1G javaextractor.JavaExtractor -eager "..\testit\merging" -out ..\testit\merging\merging_eager.tg
java -Xmx1G javaextractor.JavaExtractor -complete "..\testit\merging" -out ..\testit\merging\merging_complete.tg
java -Xmx1G de.uni_koblenz.jgralab.utilities.tg2dot.Tg2Dot --graph ..\testit\merging\merging_lazy.tg --output ..\testit\merging\merging_lazy.dot --domains --edgeattr --rolenames --reversed
java -Xmx1G de.uni_koblenz.jgralab.utilities.tg2dot.Tg2Dot --graph ..\testit\merging\merging_eager.tg --output ..\testit\merging\merging_eager.dot --domains --edgeattr --rolenames --reversed
java -Xmx1G de.uni_koblenz.jgralab.utilities.tg2dot.Tg2Dot --graph ..\testit\merging\merging_complete.tg --output ..\testit\merging\merging_complete.dot --domains --edgeattr --rolenames --reversed
cd ..\testit\merging\
dot -Tpng -omerging_lazy.png merging_lazy.dot
dot -Tpng -omerging_eager.png merging_eager.dot
dot -Tpng -omerging_complete.png merging_complete.dot
cd ..\..\src
::--- type parameters -------------------------------------------------
java -Xmx1G javaextractor.JavaExtractor "..\testit\type_resolving\TestTypeParameters.java" -out ..\testit\type_resolving\TestTypeParameters_lazy.tg
java -Xmx1G javaextractor.JavaExtractor -eager "..\testit\type_resolving\TestTypeParameters.java" -out ..\testit\type_resolving\TestTypeParameters_eager.tg
java -Xmx1G javaextractor.JavaExtractor -complete "..\testit\type_resolving\TestTypeParameters.java" -out ..\testit\type_resolving\TestTypeParameters_complete.tg
java -Xmx1G de.uni_koblenz.jgralab.utilities.tg2dot.Tg2Dot --graph ..\testit\type_resolving\TestTypeParameters_lazy.tg --output ..\testit\type_resolving\TestTypeParameters_lazy.dot --domains --edgeattr --rolenames --reversed
java -Xmx1G de.uni_koblenz.jgralab.utilities.tg2dot.Tg2Dot --graph ..\testit\type_resolving\TestTypeParameters_eager.tg --output ..\testit\type_resolving\TestTypeParameters_eager.dot --domains --edgeattr --rolenames --reversed
java -Xmx1G de.uni_koblenz.jgralab.utilities.tg2dot.Tg2Dot --graph ..\testit\type_resolving\TestTypeParameters_complete.tg --output ..\testit\type_resolving\TestTypeParameters_complete.dot --domains --edgeattr --rolenames --reversed
cd ..\testit\type_resolving\
dot -Tpng -O TestTypeParameters_lazy.dot
dot -Tpng -O TestTypeParameters_eager.dot
dot -Tpng -O TestTypeParameters_complete.dot
cd ..\..\src
::--- nested types -------------------------------------------------
java -Xmx1G javaextractor.JavaExtractor "..\testit\type_resolving\B.java" -out ..\testit\type_resolving\TestNestedTypes_lazy.tg
java -Xmx1G javaextractor.JavaExtractor -eager "..\testit\type_resolving\B.java" -out ..\testit\type_resolving\TestNestedTypes_eager.tg
java -Xmx1G javaextractor.JavaExtractor -complete "..\testit\type_resolving\B.java" -out ..\testit\type_resolving\TestNestedTypes_complete.tg
java -Xmx1G javaextractor.JavaExtractor "..\testit\nested_types" -out ..\testit\nested_types\TestProtectedNestedType_lazy.tg
java -Xmx1G de.uni_koblenz.jgralab.utilities.tg2dot.Tg2Dot --graph ..\testit\type_resolving\TestNestedTypes_lazy.tg --output ..\testit\type_resolving\TestNestedTypes_lazy.dot --domains --edgeattr --rolenames --reversed
java -Xmx1G de.uni_koblenz.jgralab.utilities.tg2dot.Tg2Dot --graph ..\testit\type_resolving\TestNestedTypes_eager.tg --output ..\testit\type_resolving\TestNestedTypes_eager.dot --domains --edgeattr --rolenames --reversed
java -Xmx1G de.uni_koblenz.jgralab.utilities.tg2dot.Tg2Dot --graph ..\testit\type_resolving\TestNestedTypes_complete.tg --output ..\testit\type_resolving\TestNestedTypes_complete.dot --domains --edgeattr --rolenames --reversed
java -Xmx1G de.uni_koblenz.jgralab.utilities.tg2dot.Tg2Dot --graph ..\testit\nested_types\TestProtectedNestedType_lazy.tg --output ..\testit\nested_types\TestProtectedNestedType_lazy.dot --domains --edgeattr --rolenames --reversed
cd ..\testit\type_resolving\
dot -Tpng -O TestNestedTypes_lazy.dot
dot -Tpng -O TestNestedTypes_eager.dot
dot -Tpng -O TestNestedTypes_complete.dot
cd ..\nested_types\
dot -Tpng -O TestProtectedNestedType_lazy.dot
cd ..\..\src
::--- combination of type parameters and nested types -------------------------------------------------
java -Xmx1G javaextractor.JavaExtractor "..\testit\type_resolving\TestCombinationOfNestedTypesAndTypeParameters.java" -out ..\testit\type_resolving\TestCombinationOfNestedTypesAndTypeParameters_lazy.tg
java -Xmx1G javaextractor.JavaExtractor -eager "..\testit\type_resolving\TestCombinationOfNestedTypesAndTypeParameters.java" -out ..\testit\type_resolving\TestCombinationOfNestedTypesAndTypeParameters_eager.tg
java -Xmx1G javaextractor.JavaExtractor -complete "..\testit\type_resolving\TestCombinationOfNestedTypesAndTypeParameters.java" -out ..\testit\type_resolving\TestCombinationOfNestedTypesAndTypeParameters_complete.tg
java -Xmx1G de.uni_koblenz.jgralab.utilities.tg2dot.Tg2Dot --graph ..\testit\type_resolving\TestCombinationOfNestedTypesAndTypeParameters_lazy.tg --output ..\testit\type_resolving\TestCombinationOfNestedTypesAndTypeParameters_lazy.dot --domains --edgeattr --rolenames --reversed
java -Xmx1G de.uni_koblenz.jgralab.utilities.tg2dot.Tg2Dot --graph ..\testit\type_resolving\TestCombinationOfNestedTypesAndTypeParameters_eager.tg --output ..\testit\type_resolving\TestCombinationOfNestedTypesAndTypeParameters_eager.dot --domains --edgeattr --rolenames --reversed
java -Xmx1G de.uni_koblenz.jgralab.utilities.tg2dot.Tg2Dot --graph ..\testit\type_resolving\TestCombinationOfNestedTypesAndTypeParameters_complete.tg --output ..\testit\type_resolving\TestCombinationOfNestedTypesAndTypeParameters_complete.dot --domains --edgeattr --rolenames --reversed
cd ..\testit\type_resolving\
dot -Tpng -O TestCombinationOfNestedTypesAndTypeParameters_lazy.dot
dot -Tpng -O TestCombinationOfNestedTypesAndTypeParameters_eager.dot
dot -Tpng -O TestCombinationOfNestedTypesAndTypeParameters_complete.dot
cd ..\..\src
echo ----------------------------------- Build done! ---
pause
