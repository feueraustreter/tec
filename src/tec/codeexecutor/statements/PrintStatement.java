package tec.codeexecutor.statements;

import tec.Tec;
import tec.codeexecutor.Executor;
import tec.codeexecutor.Expression;
import tec.codeexecutor.VariableState;
import tec.interfaces.Statement;
import tec.utils.Token;

import java.util.ArrayList;

/**
 * The type Print statement.
 */
public class PrintStatement implements Statement {

	@Override
	public String getName() {
		return "print";
	}

	@Override
	public boolean execute(ArrayList<Token> tokens, VariableState variableState) {

		Expression expression = new Expression(tokens, variableState);
		expression.build();
		if (Tec.debug) {
			System.out.println("Expression " + Tec.expressions + " build time: " + expression.getExpressionTime() + "ms");
			Tec.expressions += 1;
		}
		if (expression.getObject() == null) {
			System.out.println("ERROR: " + expression.getError());
			return false;
		}
		System.out.println(expression.getObject());

		return true;
	}

}
