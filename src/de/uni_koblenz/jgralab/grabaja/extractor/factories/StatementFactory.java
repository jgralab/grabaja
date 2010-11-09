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
package de.uni_koblenz.jgralab.grabaja.extractor.factories;

import java.util.HashMap;

import antlr.collections.AST;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.grabaja.extractor.SymbolTable;
import de.uni_koblenz.jgralab.grabaja.extractor.Utilities;
import de.uni_koblenz.jgralab.grabaja.java5schema.Assert;
import de.uni_koblenz.jgralab.grabaja.java5schema.Block;
import de.uni_koblenz.jgralab.grabaja.java5schema.Break;
import de.uni_koblenz.jgralab.grabaja.java5schema.Case;
import de.uni_koblenz.jgralab.grabaja.java5schema.Catch;
import de.uni_koblenz.jgralab.grabaja.java5schema.Continue;
import de.uni_koblenz.jgralab.grabaja.java5schema.Default;
import de.uni_koblenz.jgralab.grabaja.java5schema.DoWhile;
import de.uni_koblenz.jgralab.grabaja.java5schema.EmptyStatement;
import de.uni_koblenz.jgralab.grabaja.java5schema.Expression;
import de.uni_koblenz.jgralab.grabaja.java5schema.For;
import de.uni_koblenz.jgralab.grabaja.java5schema.ForEachClause;
import de.uni_koblenz.jgralab.grabaja.java5schema.ForHead;
import de.uni_koblenz.jgralab.grabaja.java5schema.Identifier;
import de.uni_koblenz.jgralab.grabaja.java5schema.If;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsAttachedTo;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsBodyOfCatch;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsBodyOfFinally;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsBodyOfTry;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsBreakTargetOf;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsCaseConditionOf;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsCaughtExceptionOf;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsConditionOfAssert;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsConditionOfDoWhile;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsConditionOfIf;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsConditionOfWhile;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsContinueTargetOf;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsElseOf;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsEnumerableOf;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsForConditionOf;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsHandlerOf;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsHeadOfFor;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsIteratorOf;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsLabelNameOf;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsLoopBodyOfDoWhile;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsLoopBodyOfFor;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsLoopBodyOfWhile;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsMessageOf;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsMonitorOf;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsParameterOfForEachClause;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsReturnedBy;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsRunVariableInitializationOf;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsStatementOfBody;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsStatementOfCase;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsStatementOfDefaultCase;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsSwitchArgumentOf;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsSynchronizedBodyOf;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsThenOf;
import de.uni_koblenz.jgralab.grabaja.java5schema.IsThrownExceptionOf;
import de.uni_koblenz.jgralab.grabaja.java5schema.Java5;
import de.uni_koblenz.jgralab.grabaja.java5schema.Label;
import de.uni_koblenz.jgralab.grabaja.java5schema.ParameterDeclaration;
import de.uni_koblenz.jgralab.grabaja.java5schema.Return;
import de.uni_koblenz.jgralab.grabaja.java5schema.Statement;
import de.uni_koblenz.jgralab.grabaja.java5schema.Switch;
import de.uni_koblenz.jgralab.grabaja.java5schema.Synchronized;
import de.uni_koblenz.jgralab.grabaja.java5schema.Throw;
import de.uni_koblenz.jgralab.grabaja.java5schema.TraditionalForClause;
import de.uni_koblenz.jgralab.grabaja.java5schema.Try;
import de.uni_koblenz.jgralab.grabaja.java5schema.While;

/**
 * Provides functionality for creating statement elements in graph.
 * 
 * @author: abaldauf@uni-koblenz.de
 */
public class StatementFactory extends SubgraphFactory {

	/**
	 * Stores the defined labels of the current file. @TODO move to symbol table
	 */
	private HashMap<String, Label> labels;

	/**
	 * Instantiates and initializes an instance.
	 * 
	 * @param programGraph
	 *            The graph to be used.
	 * @param symbolTable
	 *            The symbol table to be used.
	 */
	public StatementFactory(Java5 pg, SymbolTable symbolTable) {
		programGraph = pg;
		this.symbolTable = symbolTable;
	}

	/**
	 * Creates a vertex for an empty statement.
	 * 
	 * @return The created vertex.
	 */
	public EmptyStatement createEmptyStatement() {
		if (!symbolTable.hasEmptyStatement()) {
			symbolTable.setEmptyStatement(programGraph.createEmptyStatement());
		}
		return symbolTable.getEmptyStatement();
	}

	// @TODO refactor into methods
	/**
	 * Creates an edge between a statement and the element (Block, case,
	 * default) it belongs to.
	 * 
	 * @param statementVertex
	 *            The statement.
	 * @param parentVertex
	 *            The element the statement belongs to.
	 * @param beginAST
	 *            The AST element representing the first element of the
	 *            statement.
	 * @param endAST
	 *            The AST element representing the last element of the
	 *            statement.
	 */
	public void attachStatement(Statement statementVertex, Vertex parentVertex,
			AST beginAST, AST endAST) {
		if (parentVertex instanceof Block) {
			IsStatementOfBody isStatementOfBodyEdge = programGraph
					.createIsStatementOfBody(statementVertex,
							(Block) parentVertex);
			Utilities.fillEdgeAttributesFromASTDifference(
					isStatementOfBodyEdge, beginAST, endAST);
		} else if (parentVertex instanceof Case) {
			IsStatementOfCase isStatementOfCaseEdge = programGraph
					.createIsStatementOfCase(statementVertex,
							(Case) parentVertex);
			Utilities.fillEdgeAttributesFromASTDifference(
					isStatementOfCaseEdge, beginAST, endAST);
		} else if (parentVertex instanceof Default) {
			IsStatementOfDefaultCase isStatementOfDefaultCaseEdge = programGraph
					.createIsStatementOfDefaultCase(statementVertex,
							(Default) parentVertex);
			Utilities.fillEdgeAttributesFromASTDifference(
					isStatementOfDefaultCaseEdge, beginAST, endAST);
		}
	}

	/**
	 * Creates an edge between a run variable initialization statement and it's
	 * "for" statement.
	 * 
	 * @param statementVertex
	 *            The statement.
	 * @param forHeadVertex
	 *            The head of the "for" statement.
	 * @param beginAST
	 *            The AST element representing the first element of the
	 *            statement.
	 * @param endAST
	 *            The AST element representing the last element of the
	 *            statement.
	 */
	public void attachRunVariableInitialization(Statement statementVertex,
			TraditionalForClause forHeadVertex, AST beginAST, AST endAST) {
		IsRunVariableInitializationOf isRunVariableInitializationOfEdge = programGraph
				.createIsRunVariableInitializationOf(statementVertex,
						forHeadVertex);
		Utilities.fillEdgeAttributesFromASTDifference(
				isRunVariableInitializationOfEdge, beginAST, endAST);
	}

	/**
	 * Creates an edge between a "for" condition expression and it's "for"
	 * statement.
	 * 
	 * @param expressionVertex
	 *            The expression.
	 * @param forHeadVertex
	 *            The head of the "for" statement.
	 * @param beginAST
	 *            The AST element representing the first element of the
	 *            expression.
	 * @param endAST
	 *            The AST element representing the last element of the
	 *            expression.
	 */
	public void attachForCondition(Expression expressionVertex,
			TraditionalForClause forHeadVertex, AST beginAST, AST endAST) {
		IsForConditionOf isForConditionOfEdge = programGraph
				.createIsForConditionOf(expressionVertex, forHeadVertex);
		Utilities.fillEdgeAttributesFromASTDifference(isForConditionOfEdge,
				beginAST, endAST);
	}

	/**
	 * Creates an edge between a head of a "for" statement and it's "for"
	 * statement.
	 * 
	 * @param forHeadVertex
	 *            The head of the "for" statement.
	 * @param forVertex
	 *            The "for" statement.
	 * @param beginAST
	 *            The AST element representing the first element of the head.
	 * @param endAST
	 *            The AST element representing the last element of the head.
	 */
	public void attachForHead(ForHead forHeadVertex, For forVertex,
			AST beginAST, AST endAST) {
		IsHeadOfFor isHeadOfForEdge = programGraph.createIsHeadOfFor(
				forHeadVertex, forVertex);
		Utilities.fillEdgeAttributesFromASTDifference(isHeadOfForEdge,
				beginAST, endAST);
	}

	/**
	 * Creates an edge between a body of a "for" statement and it's "for"
	 * statement.
	 * 
	 * @param statementVertex
	 *            The body of the "for" statement (block or single statement).
	 * @param forVertex
	 *            The "for" statement.
	 * @param beginAST
	 *            The AST element representing the first element of the body.
	 * @param endAST
	 *            The AST element representing the last element of the body.
	 */
	public void attachLoopBody(Statement statementVertex, For forVertex,
			AST beginAST, AST endAST) {
		IsLoopBodyOfFor isLoopBodyOfForEdge = programGraph
				.createIsLoopBodyOfFor(statementVertex, forVertex);
		Utilities.fillEdgeAttributesFromASTDifference(isLoopBodyOfForEdge,
				beginAST, endAST);
	}

	/**
	 * Creates an edge between a "for" enumerable expression and it's "for"
	 * statement.
	 * 
	 * @param expressionVertex
	 *            The expression.
	 * @param forHeadVertex
	 *            The head of the "for" statement.
	 * @param beginAST
	 *            The AST element representing the first element of the
	 *            expression.
	 * @param endAST
	 *            The AST element representing the last element of the
	 *            expression.
	 */
	public void attachEnumeratable(Expression expressionVertex,
			ForEachClause forHeadVertex, AST beginAST, AST endAST) {
		IsEnumerableOf isEnumerableOfEdge = programGraph.createIsEnumerableOf(
				expressionVertex, forHeadVertex);
		Utilities.fillEdgeAttributesFromASTDifference(isEnumerableOfEdge,
				beginAST, endAST);
	}

	/**
	 * Creates an edge between a "for" parameter declaration and it's "for"
	 * statement.
	 * 
	 * @param parameterDeclarationVertex
	 *            The parameter declaration.
	 * @param forHeadVertex
	 *            The head of the "for" statement.
	 * @param beginAST
	 *            The AST element representing the first element of the
	 *            parameter declaration.
	 * @param endAST
	 *            The AST element representing the last element of the parameter
	 *            declaration.
	 */
	public void attachParameter(
			ParameterDeclaration parameterDeclarationVertex,
			ForEachClause forHeadVertex, AST beginAST, AST endAST) {
		IsParameterOfForEachClause isParameterOfForEachClauseEdge = programGraph
				.createIsParameterOfForEachClause(parameterDeclarationVertex,
						forHeadVertex);
		Utilities.fillEdgeAttributesFromASTDifference(
				isParameterOfForEachClauseEdge, beginAST, endAST);
	}

	/**
	 * Creates an edge between a "for" iterator expression and it's "for"
	 * statement.
	 * 
	 * @param expressionVertex
	 *            The expression.
	 * @param traditionalForClauseVertex
	 *            The head of the "for" statement.
	 * @param beginAST
	 *            The AST element representing the first element of the
	 *            expression.
	 * @param endAST
	 *            The AST element representing the last element of the
	 *            expression.
	 */
	public void attachIterator(Expression expressionVertex,
			TraditionalForClause traditionalForClauseVertex, AST beginAST,
			AST endAST) {
		IsIteratorOf isIteratorOfEdge = programGraph.createIsIteratorOf(
				expressionVertex, traditionalForClauseVertex);
		Utilities.fillEdgeAttributesFromASTDifference(isIteratorOfEdge,
				beginAST, endAST);
	}

	/**
	 * Creates an edge between a caught exception parameter declaration and it's
	 * "catch" statement.
	 * 
	 * @param parameterDeclarationVertex
	 *            The parameter declaration.
	 * @param catchVertex
	 *            The "catch" statement.
	 * @param beginAST
	 *            The AST element representing the first element of the
	 *            parameter declaration.
	 * @param endAST
	 *            The AST element representing the last element of the parameter
	 *            declaration.
	 */
	public void attachCaughtException(
			ParameterDeclaration parameterDeclarationVertex, Catch catchVertex,
			AST beginAST, AST endAST) {
		IsCaughtExceptionOf isCaughtExceptionOfEdge = programGraph
				.createIsCaughtExceptionOf(parameterDeclarationVertex,
						catchVertex);
		Utilities.fillEdgeAttributesFromASTDifference(isCaughtExceptionOfEdge,
				beginAST, endAST);
	}

	/**
	 * Creates an edge between a catch body and it's "catch" statement.
	 * 
	 * @param blockVertex
	 *            The catch body.
	 * @param catchVertex
	 *            The "catch" statement.
	 * @param beginAST
	 *            The AST element representing the first element of the catch
	 *            body.
	 * @param endAST
	 *            The AST element representing the last element of the catch
	 *            body.
	 */
	public void attachBodyOfCatch(Block blockVertex, Catch catchVertex,
			AST beginAST, AST endAST) {
		IsBodyOfCatch isBodyOfCatchEdge = programGraph.createIsBodyOfCatch(
				blockVertex, catchVertex);
		Utilities.fillEdgeAttributesFromASTDifference(isBodyOfCatchEdge,
				beginAST, endAST);
	}

	/**
	 * Creates a vertex for a try statement. Also creates an edge between its
	 * try body and itself.
	 * 
	 * @param blockVertex
	 *            The try body.
	 * @param beginAST
	 *            The AST element representing the first element of the try
	 *            body.
	 * @param endAST
	 *            The AST element representing the last element of the try body.
	 * @return The created vertex.
	 */
	public Try createTry(Block blockVertex, AST beginAST, AST endAST) {
		Try tryVertex = programGraph.createTry();
		IsBodyOfTry isBodyOfTryEdge = programGraph.createIsBodyOfTry(
				blockVertex, tryVertex);
		Utilities.fillEdgeAttributesFromASTDifference(isBodyOfTryEdge,
				beginAST, endAST);
		return tryVertex;
	}

	/**
	 * Creates an edge between a catch statement and it's try statement.
	 * 
	 * @param catchVertex
	 *            The catch statement.
	 * @param tryVertex
	 *            The try statement.
	 * @param beginAST
	 *            The AST element representing the first element of the catch
	 *            statement.
	 * @param endAST
	 *            The AST element representing the last element of the catch
	 *            statement.
	 */
	public void attachHandler(Catch catchVertex, Try tryVertex, AST beginAST,
			AST endAST) {
		IsHandlerOf isHandlerOfEdge = programGraph.createIsHandlerOf(
				catchVertex, tryVertex);
		Utilities.fillEdgeAttributesFromASTDifference(isHandlerOfEdge,
				beginAST, endAST);
	}

	/**
	 * Creates an edge between a finally body and it's try statement.
	 * 
	 * @param blockVertex
	 *            The finally body.
	 * @param tryVertex
	 *            The try statement.
	 * @param beginAST
	 *            The AST element representing the first element of the finally
	 *            body.
	 * @param endAST
	 *            The AST element representing the last element of the finally
	 *            body.
	 */
	public void attachFinally(Block blockVertex, Try tryVertex, AST beginAST,
			AST endAST) {
		IsBodyOfFinally isBodyOfFinallyEdge = programGraph
				.createIsBodyOfFinally(blockVertex, tryVertex);
		Utilities.fillEdgeAttributesFromASTDifference(isBodyOfFinallyEdge,
				beginAST, endAST);
	}

	/**
	 * Creates an edge between a "case" condition expression and it's "case"
	 * statement.
	 * 
	 * @param expressionVertex
	 *            The expression.
	 * @param caseVertex
	 *            The "case" statement.
	 * @param beginAST
	 *            The AST element representing the first element of the
	 *            expression.
	 * @param endAST
	 *            The AST element representing the last element of the
	 *            expression.
	 */
	public void attachCaseCondition(Expression expressionVertex,
			Case caseVertex, AST beginAST, AST endAST) {
		IsCaseConditionOf isCaseConditionOfEdge = programGraph
				.createIsCaseConditionOf(expressionVertex, caseVertex);
		Utilities.fillEdgeAttributesFromASTDifference(isCaseConditionOfEdge,
				beginAST, endAST);
	}

	/**
	 * Creates an edge between a synchronized body and it's synchronized
	 * statement.
	 * 
	 * @param blockVertex
	 *            The synchronized body.
	 * @param synchronizedVertex
	 *            The synchronized statement.
	 * @param beginAST
	 *            The AST element representing the first element of the
	 *            synchronized body.
	 * @param endAST
	 *            The AST element representing the last element of the
	 *            synchronized body.
	 */
	public void attachSynchronizedBody(Block blockVertex,
			Synchronized synchronizedVertex, AST beginAST, AST endAST) {
		IsSynchronizedBodyOf isSynchronizedBodyOfEdge = programGraph
				.createIsSynchronizedBodyOf(blockVertex, synchronizedVertex);
		Utilities.fillEdgeAttributesFromASTDifference(isSynchronizedBodyOfEdge,
				beginAST, endAST);
	}

	/**
	 * Creates an edge between a "synchronized" monitor expression and it's
	 * synchronized statement.
	 * 
	 * @param expressionVertex
	 *            The expression.
	 * @param synchronizedVertex
	 *            The synchronized statement.
	 * @param beginAST
	 *            The AST element representing the first element of the
	 *            expression.
	 * @param endAST
	 *            The AST element representing the last element of the
	 *            expression.
	 */
	public void attachMonitor(Expression expressionVertex,
			Synchronized synchronizedVertex, AST beginAST, AST endAST) {
		IsMonitorOf isMonitorOfEdge = programGraph.createIsMonitorOf(
				expressionVertex, synchronizedVertex);
		Utilities.fillEdgeAttributesFromASTDifference(isMonitorOfEdge,
				beginAST, endAST);
	}

	/**
	 * Creates an edge between a thrown exception expression and it's throw
	 * statement.
	 * 
	 * @param expressionVertex
	 *            The expression.
	 * @param throwVertex
	 *            The throw statement.
	 * @param beginAST
	 *            The AST element representing the first element of the
	 *            expression.
	 * @param endAST
	 *            The AST element representing the last element of the
	 *            expression.
	 */
	public void attachThrownException(Expression expressionVertex,
			Throw throwVertex, AST beginAST, AST endAST) {
		IsThrownExceptionOf isThrownExceptionOfEdge = programGraph
				.createIsThrownExceptionOf(expressionVertex, throwVertex);
		Utilities.fillEdgeAttributesFromASTDifference(isThrownExceptionOfEdge,
				beginAST, endAST);
	}

	/**
	 * Creates an edge between a switch argument expression and it's switch
	 * statement.
	 * 
	 * @param expressionVertex
	 *            The expression.
	 * @param switchVertex
	 *            The switch statement.
	 * @param beginAST
	 *            The AST element representing the first element of the
	 *            expression.
	 * @param endAST
	 *            The AST element representing the last element of the
	 *            expression.
	 */
	public void attachSwitchArgument(Expression expressionVertex,
			Switch switchVertex, AST beginAST, AST endAST) {
		IsSwitchArgumentOf isSwitchArgumentOfEdge = programGraph
				.createIsSwitchArgumentOf(expressionVertex, switchVertex);
		Utilities.fillEdgeAttributesFromASTDifference(isSwitchArgumentOfEdge,
				beginAST, endAST);
	}

	/**
	 * Creates an edge between a returned argument expression and it's return
	 * statement.
	 * 
	 * @param expressionVertex
	 *            The expression.
	 * @param returnVertex
	 *            The return statement.
	 * @param beginAST
	 *            The AST element representing the first element of the
	 *            expression.
	 * @param endAST
	 *            The AST element representing the last element of the
	 *            expression.
	 */
	public void attachReturn(Expression expressionVertex, Return returnVertex,
			AST beginAST, AST endAST) {
		IsReturnedBy isReturnedByEdge = programGraph.createIsReturnedBy(
				expressionVertex, returnVertex);
		Utilities.fillEdgeAttributesFromASTDifference(isReturnedByEdge,
				beginAST, endAST);
	}

	/**
	 * Creates an edge between a "do...while" condition expression and it's
	 * "do...while" statement.
	 * 
	 * @param expressionVertex
	 *            The expression.
	 * @param doWhileVertex
	 *            The "do...while" statement.
	 * @param beginAST
	 *            The AST element representing the first element of the
	 *            expression.
	 * @param endAST
	 *            The AST element representing the last element of the
	 *            expression.
	 */
	public void attachConditionOfDoWhile(Expression expressionVertex,
			DoWhile doWhileVertex, AST beginAST, AST endAST) {
		IsConditionOfDoWhile isConditionOfDoWhileEdge = programGraph
				.createIsConditionOfDoWhile(expressionVertex, doWhileVertex);
		Utilities.fillEdgeAttributesFromASTDifference(isConditionOfDoWhileEdge,
				beginAST, endAST);
	}

	/**
	 * Creates an edge between a body of a "do...while" statement and it's
	 * "do...while" statement.
	 * 
	 * @param statementVertex
	 *            The body of the "do...while" statement (block or single
	 *            statement).
	 * @param doWhileVertex
	 *            The "do...while" statement.
	 * @param beginAST
	 *            The AST element representing the first element of the body.
	 * @param endAST
	 *            The AST element representing the last element of the body.
	 */
	public void attachLoopBodyOfDoWhile(Statement statementVertex,
			DoWhile doWhileVertex, AST beginAST, AST endAST) {
		IsLoopBodyOfDoWhile isLoopBodyOfDoWhileEdge = programGraph
				.createIsLoopBodyOfDoWhile(statementVertex, doWhileVertex);
		Utilities.fillEdgeAttributesFromASTDifference(isLoopBodyOfDoWhileEdge,
				beginAST, endAST);
	}

	/**
	 * Creates an edge between a body of a "while" statement and it's "while"
	 * statement.
	 * 
	 * @param statementVertex
	 *            The body of the "while" statement (block or single statement).
	 * @param whileVertex
	 *            The "while" statement.
	 * @param beginAST
	 *            The AST element representing the first element of the body.
	 * @param endAST
	 *            The AST element representing the last element of the body.
	 */
	public void attachLoopBodyOfWhile(Statement statementVertex,
			While whileVertex, AST beginAST, AST endAST) {
		IsLoopBodyOfWhile isLoopBodyOfWhileEdge = programGraph
				.createIsLoopBodyOfWhile(statementVertex, whileVertex);
		Utilities.fillEdgeAttributesFromASTDifference(isLoopBodyOfWhileEdge,
				beginAST, endAST);
	}

	/**
	 * Creates an edge between a "while" condition expression and it's "while"
	 * statement.
	 * 
	 * @param expressionVertex
	 *            The expression.
	 * @param whileVertex
	 *            The "while" statement.
	 * @param beginAST
	 *            The AST element representing the first element of the
	 *            expression.
	 * @param endAST
	 *            The AST element representing the last element of the
	 *            expression.
	 */
	public void attachConditionOfWhile(Expression expressionVertex,
			While whileVertex, AST beginAST, AST endAST) {
		IsConditionOfWhile isConditionOfWhileEdge = programGraph
				.createIsConditionOfWhile(expressionVertex, whileVertex);
		Utilities.fillEdgeAttributesFromASTDifference(isConditionOfWhileEdge,
				beginAST, endAST);
	}

	/**
	 * Creates an edge between a "else" statement and it's "if" statement.
	 * 
	 * @param statementVertex
	 *            The statement.
	 * @param ifVertex
	 *            The "if" statement.
	 * @param beginAST
	 *            The AST element representing the first element of the
	 *            statement.
	 * @param endAST
	 *            The AST element representing the last element of the
	 *            statement.
	 */
	public void attachElse(Statement statementVertex, If ifVertex,
			AST beginAST, AST endAST) {
		IsElseOf isElseOfEdge = programGraph.createIsElseOf(statementVertex,
				ifVertex);
		// this is in case we want the "else" string to be part of the position
		// informations
		// fillEdgeAttributesFromASTDifference( isElseOfEdge, elseBegin,
		// currentEndAST );
		// but as this is a statement/compound, I think "else" should not be a
		// part of it
		Utilities.fillEdgeAttributesFromASTDifference(isElseOfEdge, beginAST,
				endAST);
	}

	/**
	 * Creates an edge between a "then" statement and it's "if" statement.
	 * 
	 * @param statementVertex
	 *            The statement.
	 * @param ifVertex
	 *            The "if" statement.
	 * @param beginAST
	 *            The AST element representing the first element of the
	 *            statement.
	 * @param endAST
	 *            The AST element representing the last element of the
	 *            statement.
	 */
	public void attachThen(Statement statementVertex, If ifVertex,
			AST beginAST, AST endAST) {
		IsThenOf isThenOfEdge = programGraph.createIsThenOf(statementVertex,
				ifVertex);
		Utilities.fillEdgeAttributesFromASTDifference(isThenOfEdge, beginAST,
				endAST);
	}

	/**
	 * Creates an edge between a "if" condition expression and it's "if"
	 * statement.
	 * 
	 * @param expressionVertex
	 *            The expression.
	 * @param ifVertex
	 *            The "if" statement.
	 * @param beginAST
	 *            The AST element representing the first element of the
	 *            expression.
	 * @param endAST
	 *            The AST element representing the last element of the
	 *            expression.
	 */
	public void attachConditionOfIf(Expression expressionVertex, If ifVertex,
			AST beginAST, AST endAST) {
		IsConditionOfIf isConditionOfIfEdge = programGraph
				.createIsConditionOfIf(expressionVertex, ifVertex);
		Utilities.fillEdgeAttributesFromASTDifference(isConditionOfIfEdge,
				beginAST, endAST);
	}

	/**
	 * Creates an edge between a "assert" condition expression and it's "assert"
	 * statement.
	 * 
	 * @param expressionVertex
	 *            The expression.
	 * @param assertVertex
	 *            The "assert" statement.
	 * @param beginAST
	 *            The AST element representing the first element of the
	 *            expression.
	 * @param endAST
	 *            The AST element representing the last element of the
	 *            expression.
	 */
	public void attachConditionOfAssert(Expression expressionVertex,
			Assert assertVertex, AST beginAST, AST endAST) {
		IsConditionOfAssert isConditionOfAssertEdge = programGraph
				.createIsConditionOfAssert(expressionVertex, assertVertex);
		Utilities.fillEdgeAttributesFromASTDifference(isConditionOfAssertEdge,
				beginAST, endAST);
	}

	/**
	 * Creates an edge between a "assert" message expression and it's "assert"
	 * statement.
	 * 
	 * @param expressionVertex
	 *            The expression.
	 * @param assertVertex
	 *            The "assert" statement.
	 * @param beginAST
	 *            The AST element representing the first element of the
	 *            expression.
	 * @param endAST
	 *            The AST element representing the last element of the
	 *            expression.
	 */
	public void attachMessage(Expression expressionVertex, Assert assertVertex,
			AST beginAST, AST endAST) {
		IsMessageOf isMessageOfEdge = programGraph.createIsMessageOf(
				expressionVertex, assertVertex);
		Utilities.fillEdgeAttributesFromASTDifference(isMessageOfEdge,
				beginAST, endAST);
	}

	// @TODO move labels to symbol table
	/**
	 * Creates a vertex for a label. Also creates an edge between the following
	 * statement and the label.
	 * 
	 * @param statementVertex
	 *            The statement.
	 * @param name
	 *            The label's name.
	 * @param ast
	 *            The AST element representing the label's identifier.
	 * @param beginAST
	 *            The AST element representing the first element of the
	 *            statement.
	 * @param endAST
	 *            The AST element representing the last element of the
	 *            statement.
	 * @return The created vertex.
	 */
	public Label createLabel(Statement statementVertex, AST ast, AST beginAST,
			AST endAST) {
		if (labels == null) {
			labels = new HashMap<String, Label>();
		}
		Label labelVertex = createLabel(ast.getText(), ast);
		IsAttachedTo isAttachedToEdge = programGraph.createIsAttachedTo(
				statementVertex, labelVertex);
		Utilities.fillEdgeAttributesFromASTDifference(isAttachedToEdge,
				beginAST, endAST);
		return labelVertex;
	}

	/**
	 * Creates an edge between a label and it's "break" statement. Also creates
	 * the required label vertex.
	 * 
	 * @param breakVertex
	 *            The "break" statement.
	 * @param ast
	 *            The AST element representing the label's identifier.
	 */
	public void createLabel(Break breakVertex, AST ast) {
		if (labels == null) {
			labels = new HashMap<String, Label>();
		}
		Label labelVertex = createLabel(ast.getText(), ast);
		IsBreakTargetOf isBreakTargetOfEdge = programGraph
				.createIsBreakTargetOf(labelVertex, breakVertex);
		Utilities.copyEdgeAttributes(
				labelVertex.getFirstIsStatementOfIncidence(),
				isBreakTargetOfEdge);
		Identifier identifierVertex = (Identifier) labelVertex
				.getFirstIsLabelNameOfIncidence().getAlpha();
		IsLabelNameOf isLabelNameOfEdge = programGraph.createIsLabelNameOf(
				identifierVertex, labelVertex);
		Utilities.fillEdgeAttributesFromAST(isLabelNameOfEdge, ast);
	}

	/**
	 * Creates an edge between a label and it's "continue" statement. Also
	 * creates the required label vertex.
	 * 
	 * @param continueVertex
	 *            The "continue" statement.
	 * @param ast
	 *            The AST element representing the label's identifier.
	 */
	public void createLabel(Continue continueVertex, AST ast) {
		if (labels == null) {
			labels = new HashMap<String, Label>();
		}
		Label labelVertex = createLabel(ast.getText(), ast);
		IsContinueTargetOf isContinueTargetOfEdge = programGraph
				.createIsContinueTargetOf(labelVertex, continueVertex);
		Utilities.copyEdgeAttributes(
				labelVertex.getFirstIsStatementOfIncidence(),
				isContinueTargetOfEdge);
		Identifier identifierVertex = (Identifier) labelVertex
				.getFirstIsLabelNameOfIncidence().getAlpha();
		IsLabelNameOf isLabelNameOfEdge = programGraph.createIsLabelNameOf(
				identifierVertex, labelVertex);
		Utilities.fillEdgeAttributesFromAST(isLabelNameOfEdge, ast);
	}

	/**
	 * Creates a vertex for a label.
	 * 
	 * @param name
	 *            The label's name.
	 * @param ast
	 *            The AST element representing the label's identifier.
	 * @return The created vertex.
	 */
	private Label createLabel(String name, AST ast) {
		if (labels == null) {
			labels = new HashMap<String, Label>();
		}
		if (labels.containsKey(name)) {
			return labels.get(name);
		}
		Label labelVertex = programGraph.createLabel();
		Identifier identifierVertex = programGraph.createIdentifier();
		identifierVertex.set_name(name);
		IsLabelNameOf isLabelNameOf = programGraph.createIsLabelNameOf(
				identifierVertex, labelVertex);
		Utilities.fillEdgeAttributesFromAST(isLabelNameOf, ast);
		labels.put(name, labelVertex);
		return labelVertex;
	}

	/**
	 * Resets the defined labels of the current file.
	 */
	public void reset() {
		if (labels != null) {
			labels.clear();
		}
	}

}
