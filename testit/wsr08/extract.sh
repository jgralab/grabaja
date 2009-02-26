#!/bin/bash
export CLASSPATH=.:../../src:/Users/riediger/src/ist/anatotitan/jgralab/build/jar/jgralab.jar
java -Xmx1G javaextractor.JavaExtractor -out javaexample.tg -name JavaExample *.java
java -Xmx1G de.uni_koblenz.jgralab.utilities.tg2dot.Tg2Dot --graph javaexample.tg --output javaexample.dot --domains --reversed