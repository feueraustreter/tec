package tec.codeexecutor.statements;

import tec.codeexecutor.Executor;
import tec.codeexecutor.VariableState;
import tec.interfaces.Statement;
import tec.utils.Token;

import java.util.ArrayList;

public class FuncStatement implements Statement {

    @Override
    public String getName() {
        return "func";
    }

    @Override
    public boolean execute(ArrayList<Token> tokens, VariableState variableState, Executor executor) {
        executor.jumpToClosingBracket();
        return true;
    }
}
