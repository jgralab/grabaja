/*
 * JGraLab - The Java Graph Laboratory
 *
 * Copyright (C) 2006-2010 Institute for Software Technology
 *                         University of Koblenz-Landau, Germany
 *                         ist@uni-koblenz.de
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation; either version 3 of the License, or (at your
 * option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see <http://www.gnu.org/licenses>.
 *
 * Additional permission under GNU GPL version 3 section 7
 *
 * If you modify this Program, or any covered work, by linking or combining
 * it with Eclipse (or a modified version of that program or an Eclipse
 * plugin), containing parts covered by the terms of the Eclipse Public
 * License (EPL), the licensors of this Program grant you additional
 * permission to convey the resulting work.  Corresponding Source for a
 * non-source form of such a combination shall include the source code for
 * the parts of JGraLab used as well as that of the covered work.
 */
package de.uni_koblenz.jgralab.grabaja.extractor;

import java.util.ArrayList;
import java.util.Vector;
import java.util.logging.Logger;

import antlr.CommonAST;
import antlr.RecognitionException;
import antlr.collections.AST;
import de.uni_koblenz.jgralab.GraphException;
import de.uni_koblenz.jgralab.GraphIO;
import de.uni_koblenz.jgralab.GraphIOException;
import de.uni_koblenz.jgralab.grabaja.extractor.adapters.JavaLexerAdapter;
import de.uni_koblenz.jgralab.grabaja.extractor.adapters.LexerSharedInputStateAdapter;
import de.uni_koblenz.jgralab.grabaja.extractor.comments.CommentClass;
import de.uni_koblenz.jgralab.grabaja.extractor.parser.JavaRecognizer;
import de.uni_koblenz.jgralab.grabaja.extractor.parser.JavaTreeParser;
import de.uni_koblenz.jgralab.grabaja.extractor.resolvers.FieldResolver;
import de.uni_koblenz.jgralab.grabaja.extractor.resolvers.GlobalTypeSpecificationResolver;
import de.uni_koblenz.jgralab.grabaja.extractor.resolvers.LocalTypeSpecificationResolver;
import de.uni_koblenz.jgralab.grabaja.extractor.resolvers.MethodResolver;
import de.uni_koblenz.jgralab.grabaja.java5schema.Java5;
import de.uni_koblenz.jgralab.grabaja.java5schema.Java5Schema;
import de.uni_koblenz.jgralab.grabaja.java5schema.Program;
import de.uni_koblenz.jgralab.grabaja.java5schema.SourceFile;
import de.uni_koblenz.jgralab.grabaja.java5schema.SourceUsage;
import de.uni_koblenz.jgralab.grabaja.java5schema.TranslationUnit;
import de.uni_koblenz.jgralab.impl.ConsoleProgressFunction;
import de.uni_koblenz.jgralab.schema.exception.SchemaException;

/**
 * Builds the TGraph for a program.
 *
 * @author: abaldauf@uni-koblenz.de
 * @author: ultbreit@uni-koblenz.de
 */
public class GraphBuilder {

	/**
	 * Reference to TGraph for a complete program.
	 */
	protected Java5 programGraph;

	/**
	 * Reference to symbol table of graph.
	 */
	private SymbolTable symbolTable;

	/**
	 * Reference to the top program vertex. Required for easier creation of new
	 * translation units.
	 */
	protected Program programVertex;

	/**
	 * Name that program vertex will have.
	 */
	protected String nameOfProgram;

	/**
	 * Reference to the used logger.
	 */
	private Logger logger;

	/**
	 * Tree walker used to build each translation unit's main structure.
	 */
	private JavaTreeParser treeWalker;

	/**
	 * Creates and initializes an instance of the GraphBuilder.
	 *
	 * @param nameOfProgram
	 *            Name to be used for the extracted software system
	 * @param logger
	 *            The logger instance to be used for the extraction process.
	 */
	public GraphBuilder(String nameOfProgram, Logger logger) {
		this.nameOfProgram = nameOfProgram;
		this.logger = logger;
	}

	/**
	 * Creates and initializes an instance of the TGraph.
	 *
	 * @param nameOfProgram
	 *            The name to be used for the extracted software system
	 * @throws Exception
	 */
	protected void initializeGraph() throws Exception {
		try {
			Java5Schema javaSchema = Java5Schema.instance();
			programGraph = javaSchema.createJava5(nameOfProgram, 1000, 1000);
			logger.info("Created graph " + nameOfProgram);
			// Create basic "head" of graph
			programVertex = programGraph.createProgram();
			programVertex.set_name(nameOfProgram);
		} catch (SchemaException exception) {
			logger.severe(Utilities.stackTraceToString(exception));
		} catch (GraphException exception) {
			logger.severe(Utilities.stackTraceToString(exception));
		}
	}

	/**
	 * Parses a list of files and triggers TGraph building after each parsed
	 * file.
	 *
	 * @param fileList
	 *            A Vector with the names of the files to be parsed, each can be
	 *            relative or absolute.
	 * @throws Exception
	 */
	public void parseFiles(Vector<String> fileList) throws Exception {
		initializeGraph();
		symbolTable = new SymbolTable(programGraph);
		treeWalker = new JavaTreeParser(symbolTable);
		treeWalker.setProgramGraph(programGraph);
		treeWalker.setProgramVertex(programVertex);
		logger.info(fileList.size() + " file(s) to parse.");
		// TODO Next two lines only work if logger has a handler, this has to be
		// outsourced
		// Handler[] handlersOfLogger = logger.getHandlers();
		// handlersOfLogger[0].setLevel(Level.OFF);
		ConsoleProgressFunction progressBar = new ConsoleProgressFunction();
		progressBar.init(fileList.size());
		int parsingSuccessCount = 0;
		int parsingFailedCount = 0;
		// Collect comments found in a file.
		ArrayList<CommentClass> comments = new ArrayList<CommentClass>();
		LocalTypeSpecificationResolver localResolver = new LocalTypeSpecificationResolver(
				symbolTable);
		for (int i = 0; i < fileList.size(); i++) {
			CommonAST ast = parseFile(fileList.get(i), comments);
			if (ast != null) {
				parsingSuccessCount++;
				// logger.info( "Adding " + fileList.get( i ) + " to graph" );
				addTranslationUnit(fileList.get(i), comments, ast);
				localResolver.resolveTypeSpecifications();
				// logger.info( fileList.get( i ) + " added to graph" );
			} else {
				logger.warning(fileList.get(i)
						+ " not added to graph, due to parse error");
				parsingFailedCount++;
			}
			comments.clear(); // Clear comments before parsing next file.
			symbolTable.nextFile(); // Clear type information of just parsed
									// file
			if ((i % progressBar.getUpdateInterval()) == 0) {
				progressBar.progress(1);
			}
		}
		progressBar.finished();
	}

	/**
	 * Parses a file and returns it's AST representation.
	 *
	 * @param fileName
	 *            Name of the file to parse, can be relative or absolute.
	 * @return AST representation of given file, null if parsing failed.
	 */
	private CommonAST parseFile(String fileName,
			ArrayList<CommentClass> comments) {
		try {
			// logger.info( "parsing file: " + fileName );
			// Create the special shared input state that is needed in order to
			// annotate tokens with offset.
			// @TODO make it a singleton
			LexerSharedInputStateAdapter inputState = new LexerSharedInputStateAdapter(
					fileName);
			// Create a lexer which knows the lexer shared input state with
			// offset .
			// @TODO make it a singleton
			JavaLexerAdapter javaLexer = new JavaLexerAdapter(inputState);
			javaLexer
					.setTokenObjectClass("de.uni_koblenz.jgralab.grabaja.extractor.adapters.CommonTokenAdapter"); // Tells
			// lexer
			// to
			// use
			// token
			// with
			// attribute
			// for
			// offset.
			javaLexer.setCommentCollection(comments);
			// javaLexer.setLogger( logger );
			// Create a parser that reads from the lexer.
			// @TODO make it a singleton
			JavaRecognizer javaParser = new JavaRecognizer(javaLexer);
			javaParser
					.setASTNodeClass("de.uni_koblenz.jgralab.grabaja.extractor.adapters.CommonASTAdapter"); // Tells
			// parser
			// to
			// use
			// AST
			// with
			// attribute
			// for
			// offset.
			javaParser.setFilename(fileName);
			javaParser.compilationUnit(); // Start parsing at the
			// compilationUnit rule
			// logger.info( "parsing was successful" );
			return (CommonAST) javaParser.getAST();
		} catch (Exception exception) {
			// errorCount++;
			System.out.println("An exception occured while parsing file "
					+ fileName);
			logger.warning(Utilities.stackTraceToString(exception));
			return null;
		}
	}

	/**
	 * Adds a new translation unit to TGraph.
	 *
	 * @param sourcePath
	 *            The path of the .java file the AST resulted from.
	 * @param comments
	 *            The collected comments of the .java file.
	 * @param sourceAST
	 *            The AST to be converted.
	 */
	private void addTranslationUnit(String sourcePath,
			ArrayList<CommentClass> comments, AST sourceAST) {
		try {
			// Create a new TranslationUnit with the according SourceUsage etc.
			TranslationUnit translationUnitVertex = programGraph
					.createTranslationUnit();
			programGraph.createIsTranslationUnitIn(translationUnitVertex,
					programVertex);
			for (int i = 0; i < comments.size(); i++) {
				CommentClass comment = comments.get(i);
				comment.createTGraphElements(programGraph,
						translationUnitVertex);
			}
			SourceUsage sourceUsageVertex = programGraph.createSourceUsage();
			sourceUsageVertex.set_lengthOfFile(0); // @TODO warum muss das 0
													// sein???
			programGraph.createIsSourceUsageIn(sourceUsageVertex,
					translationUnitVertex);
			SourceFile sourceFileVertex = programGraph.createSourceFile();
			sourceFileVertex.set_name(sourcePath);
			programGraph.createIsSourceFor(sourceFileVertex, sourceUsageVertex);
			programGraph.createIsPrimarySourceFor(sourceFileVertex,
					translationUnitVertex);
			// Pass some references to the treewalker so it can build and attach
			// the graph elements
			treeWalker.setSourceUsage(sourceUsageVertex);
			treeWalker.setTranslationUnit(translationUnitVertex);
			treeWalker.compilationUnit(sourceAST); // Start tree walk.
		} catch (GraphException exception) {
			exception.printStackTrace(System.err);
		} catch (RecognitionException exception) {
			exception.printStackTrace(System.err);
		}
	}

	/**
	 * Executes all the global resolving mechanisms.
	 *
	 * @param mode
	 *            The extraction mode to be used for resolving.
	 */
	public void finalizeGraph(ExtractionMode mode) {
		GlobalTypeSpecificationResolver typeSpecificationResolver = new GlobalTypeSpecificationResolver(
				symbolTable);
		FieldResolver fieldResolver = new FieldResolver(symbolTable);
		MethodResolver methodResolver = new MethodResolver(symbolTable);
		fieldResolver.setMethodResolver(methodResolver);
		methodResolver.setFieldResolver(fieldResolver);
		if (symbolTable.getAmountOfUnresolvedTypeSpecifications() > 0) {
			System.out.println(symbolTable
					.getAmountOfUnresolvedTypeSpecifications()
					+ " type specification(s) to resolve.");
			if (typeSpecificationResolver.resolveTypeSpecifications(mode) == false) {
				logger.warning(symbolTable
						.getAmountOfUnresolvedTypeSpecifications()
						+ " type specification(s) could not be resolved.");
			} else {
				logger.info("Every type specification has been resolved.");
			}
		} else {
			logger.info("No type specifications have to be resolved.");
		}

		if (symbolTable.amountOfFieldAccesses() > 0) {
			System.out.println(symbolTable.amountOfFieldAccesses()
					+ " field accesses to resolve.");
			if (fieldResolver.resolveFields(mode) == false) {
				logger.warning(symbolTable.getAmountOfUnresolvedFieldAccesses()
						+ " field access(es) could not be resolved.");
			} else {
				logger.info("Every field access has been resolved.");
			}
		} else {
			logger.info("No field accesses have to be resolved.");
		}

		if (symbolTable.amountOfMethodInvocations() > 0) {
			System.out.println(symbolTable.amountOfMethodInvocations()
					+ " method invocations to resolve.");
			if (methodResolver.resolveMethods(mode) == false) {
				logger.warning(symbolTable
						.getAmountOfUnresolvedMethodInvocations()
						+ " method invocation(s) could not be resolved.");
			} else {
				logger.info("Every method invocation has been resolved.");
			}
		} else {
			logger.info("No method invocations have to be resolved.");
		}
	}

	/**
	 * Saves the generated TGraph to disk.
	 *
	 * @param targetPath
	 *            The path of the file to write the graph to.
	 */
	public void saveGraphToDisk(String targetPath) {
		try {
			logger.info("Graph consists of " + programGraph.getVCount()
					+ " vertices and " + programGraph.getECount() + " edges");
			logger.info("Saving graph to " + targetPath);
			GraphIO.saveGraphToFile(programGraph, targetPath,
					new ConsoleProgressFunction());
			logger.info("Graph succesfully saved");
		} catch (GraphIOException exception) {
			logger.warning(Utilities.stackTraceToString(exception));
			logger.severe("Graph could not be saved.");
		}
	}

	/**
	 * Gets extracted graph.
	 *
	 * @return Extracted graph.
	 * @throws Exception
	 *             TODO
	 */
	public Java5 getGraph() throws Exception {
		if (this.programGraph != null) {
			return this.programGraph;
		} else {
			throw new Exception("Run parseFiles() first.");
		}
	}
}
