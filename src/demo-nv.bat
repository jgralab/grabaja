@echo off
::--- set classpath -------------------------------------------------
set CLASSPATH=.;jgralab\src\.;common\lib\getopt\java-getopt-1.0.13.jar;common\lib\antlr\org.antlr_2.7.6.jar;%CLASSPATH%

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
java -Xmx1G de.uni_koblenz.jgralab.utilities.tg2dot.Tg2Dot --graph ..\testit\type_resolving\TestNestedTypes_lazy.tg --output ..\testit\type_resolving\TestNestedTypes_lazy.dot --domains --edgeattr --rolenames --reversed
java -Xmx1G de.uni_koblenz.jgralab.utilities.tg2dot.Tg2Dot --graph ..\testit\type_resolving\TestNestedTypes_eager.tg --output ..\testit\type_resolving\TestNestedTypes_eager.dot --domains --edgeattr --rolenames --reversed
java -Xmx1G de.uni_koblenz.jgralab.utilities.tg2dot.Tg2Dot --graph ..\testit\type_resolving\TestNestedTypes_complete.tg --output ..\testit\type_resolving\TestNestedTypes_complete.dot --domains --edgeattr --rolenames --reversed
cd ..\testit\type_resolving\
dot -Tpng -O TestNestedTypes_lazy.dot
dot -Tpng -O TestNestedTypes_eager.dot
dot -Tpng -O TestNestedTypes_complete.dot
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

pause

