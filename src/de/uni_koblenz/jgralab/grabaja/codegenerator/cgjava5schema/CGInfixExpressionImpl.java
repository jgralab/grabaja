package de.uni_koblenz.jgralab.grabaja.codegenerator.cgjava5schema;

import java.io.BufferedWriter;
import java.io.IOException;

import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.grabaja.codegenerator.JavaCodeGenerator;
import de.uni_koblenz.jgralab.grabaja.java5schema.impl.InfixExpressionImpl;

public class CGInfixExpressionImpl extends InfixExpressionImpl implements
		CGExpression {

	public CGInfixExpressionImpl(int arg0, Graph arg1) {
		super(arg0, arg1);
	}

	@Override
	public void generateCode(JavaCodeGenerator jcg, BufferedWriter bw, int indentLevel)
			throws IOException {
		boolean isNested = JavaCodeGenerator.isNestedExpression(this);
		if (isNested) {
			bw.append('(');
		}

		// first the LHS (1,1)
		((CGExpression) getFirstIsLeftHandSideOfInfixExpression(
				EdgeDirection.IN).getAlpha()).generateCode(jcg, bw, indentLevel);

		bw.append(' ');

		switch (operator) {
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
			throw new RuntimeException("Unknown operator " + operator + "!!!");
		}

		bw.append(" ");

		// then the RHS (1,1)
		((CGExpression) getFirstIsRightHandSideOfInfixExpression(
				EdgeDirection.IN).getAlpha()).generateCode(jcg, bw, indentLevel);

		if (isNested) {
			bw.append(')');
		}
	}
}
