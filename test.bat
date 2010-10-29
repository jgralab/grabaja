set CLASSPATH=.;..\jgralab\build\jar\jgralab.jar;..\common\lib\getopt\java-getopt-1.0.13.jar;..\common\lib\antlr\org.antlr_2.7.6.jar;build\jar\grabaja.jar%CLASSPATH%
java de.uni_koblenz.jgralab.grabaja.extractor.JavaExtractor testit/Strange.java -out lazy.tg
java de.uni_koblenz.jgralab.utilities.tg2dot.Tg2Dot --graph lazy.tg --output lazy.dot --domains --edgeattr --rolenames --reversed
dot -Tpng -olazy.png lazy.dot
pause