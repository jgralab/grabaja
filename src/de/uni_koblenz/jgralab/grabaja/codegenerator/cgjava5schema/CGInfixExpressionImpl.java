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
package de.uni_koblenz.jgralab.grabaja.codegenerator.cgjava5schema;

import java.io.BufferedWriter;
import java.io.IOException;

import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.grabaja.codegenerator.JavaCodeGenerator;
import de.uni_koblenz.jgralab.grabaja.java5schema.impl.std.InfixExpressionImpl;

public class CGInfixExpressionImpl extends InfixExpressionImpl implements
		CGExpression {

	public CGInfixExpressionImpl(int arg0, Graph arg1) {
		super(arg0, arg1);
	}

	// @Override
	public Vertex generateCode(JavaCodeGenerator jcg, BufferedWriter bw,
			int indentLevel) throws IOException {
		if (!jcg.generationWanted(this)) {
			return null;
		}

		boolean isNested = JavaCodeGenerator.isNestedExpression(this);
		if (isNested) {
			bw.append('(');
		}

		// first the LHS (1,1)
		((CGExpression) getFirstIsLeftHandSideOfInfixExpressionIncidence(
				EdgeDirection.IN).getAlpha())
				.generateCode(jcg, bw, indentLevel);

		bw.append(' ');

		switch (_operator) {
		case AND:
			bw.append("&");
			break;
		case ANDASSIGNMENT:
			bw.append("&=");
			break;
		case ASSIGNMENT:
			bw.append("=");
			break;
		case DIVISION:
			bw.append("/");
			break;
		case DIVISIONASSIGNMENT:
			bw.append("/=");
			break;
		case EQUALS:
			bw.append("==");
			break;
		case GREATER:
			bw.append(">");
			break;
		case GREATEREQUALS:
			bw.append(">=");
			break;
		case INSTANCEOF:
			bw.append("instanceof");
			break;
		case LEFTSHIFT:
			bw.append("<<");
			break;
		case LEFTSHIFTASSIGNMENT:
			bw.append("<<=");
			break;
		case LESS:
			bw.append("<");
			break;
		case LESSEQUALS:
			bw.append("<=");
			break;
		case MINUS:
			bw.append("-");
			break;
		case MINUSASSIGNMENT:
			bw.append("-=");
			break;
		case MODULO:
			bw.append("%");
			break;
		case MODULOASSIGNMENT:
			bw.append("%=");
			break;
		case MULTIPLICATION:
			bw.append("*");
			break;
		case MULTIPLICATIONASSIGNMENT:
			bw.append("*=");
			break;
		case OR:
			bw.append("|");
			break;
		case ORASSIGNMENT:
			bw.append("|=");
			break;
		case PLUS:
			bw.append("+");
			break;
		case PLUSASSIGNMENT:
			bw.append("+=");
			break;
		case RIGHTSHIFT:
			bw.append(">>");
			break;
		case RIGHTSHIFTASSIGNMENT:
			bw.append(">>=");
			break;
		case SHORTCIRCUITAND:
			bw.append("&&");
			break;
		case SHORTCIRCUITOR:
			bw.append("||");
			break;
		case UNEQUALS:
			bw.append("!=");
			break;
		case UNSIGNEDRIGHTSHIFT:
			bw.append(">>>");
			break;
		case UNSIGNEDRIGHTSHIFTASSIGNMENT:
			bw.append(">>>=");
			break;
		case XOR:
			bw.append("^");
			break;
		case XORASSIGNMENT:
			bw.append("^=");
			break;
		default:
			throw new RuntimeException("Unknown operator " + _operator + "!!!");
		}

		bw.append(" ");

		Vertex last = null;

		// then the RHS (1,1), but do it conditinally, cause it seems the parser
		// creates broken graphs...
		if (getFirstIsRightHandSideOfInfixExpressionIncidence(EdgeDirection.IN) != null) {
			last = ((CGExpression) getFirstIsRightHandSideOfInfixExpressionIncidence(
					EdgeDirection.IN).getAlpha()).generateCode(jcg, bw,
					indentLevel);
		} else {
			bw.append("/* Missing RHS */");
		}

		if (isNested) {
			bw.append(')');
		}
		return last;
	}
}
