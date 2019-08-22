package tec.codeexecutor;

import tec.Tec;
import tec.utils.DebugHandler;
import tec.utils.DebugLevel;
import tec.utils.Token;

import java.util.ArrayList;

/**
 * The expression class.
 * This is the core for handling statements.
 */
public class Expression {

    private ArrayList<Token> tokens;
    private VariableState variableState;
    private Executor executor;

	/**
	 * Instantiates a new expression.
	 *
	 * @param tokens        the tokens
	 * @param variableState the variable state
	 */
	public Expression(ArrayList<Token> tokens, VariableState variableState, Executor executor) {
        this.tokens = tokens;
        this.variableState = variableState;
        this.executor = executor;
    }

    /**
     * The Output string (if the output was equal to a string value)
     */
    private String outputString;
    /**
     * The Output boolean (if the output was equal to a boolean value)
     */
    private Boolean outputBoolean;
    /**
     * The Output object (if the output was equal to a boolean value)
     */
    private Object outputObject;

    /**
     * The Expression time in milliseconds.
     */
    private long expressionTime;
    /**
     * The Plus time.
     */
    private long plusTime;
    /**
     * The Type.
     */
    private String type;
    /**
     * The Error.
     */
    private String error;

	/**
	 * Build the expression.
	 */
	public void build() {
        expressionTime = System.currentTimeMillis();
        if (tokens.size() == 1) {
            tokens = replace(tokens);
            if (!tokens.isEmpty()) {
                type = tokens.get(0).getKey();
                outputObject = tokens.get(0).getVal();
                if (type.equals("bol")) {
                    if (tokens.get(0).getVal().toString().equals("true")) {
                        outputBoolean = true;
                    } else {
                        outputBoolean = false;
                    }
                }
            }
        } else if (isLogic()) {
            booleanOutput();
            outputObject = outputBoolean;
        } else {
            stringOutput();
            if (type != null) {
                if (type.equals("num")) {
                    outputObject = Double.parseDouble(outputString);
                } else if (type.equals("int")) {
                    outputObject = Integer.parseInt(outputString);
                } else {
                    outputObject = outputString;
                }
            } else {
                outputObject = outputString;
            }
        }
        expressionTime -= System.currentTimeMillis();
        expressionTime *= -1;
		DebugHandler debug = new DebugHandler(DebugLevel.NONE, "Expression " + Tec.expressions + " built.", (int) expressionTime);
		debug.send();
		debug = new DebugHandler(DebugLevel.NORMAL, "Expression " + Tec.expressions + " built.\nErrors detected while compiling: " + error + "\nExpression as string: " + this.toString());
		debug.send();
		debug = new DebugHandler(DebugLevel.ADVANCED, "Expression " + Tec.expressions + " built.\nErrors detected while compiling: " + error + "\nExpression as string: " + this.toString() + "Expression advanced info: " + this.advancedInfo().toString(), (int) expressionTime);
		debug.send();
        Tec.expressions += 1;
    }

    private int getClosingBracket(int i) {
	    if (!(tokens.get(i).getKey().equals("STb") && tokens.get(i).getVal().equals("("))) {
	        return i;
        }

	    int bracket = 1;
	    i++;
	    for (int j = i; j < tokens.size(); j++) {
            if (tokens.get(j).getKey().equals("STb") && tokens.get(j).getVal().equals("(")) {
                bracket++;
            }
            if (tokens.get(j).getKey().equals("STb") && tokens.get(j).getVal().equals(")")) {
                bracket--;
            }
            if (bracket == 0) {
                i = j;
                break;
            }
        }
	    return i;
    }

    private ArrayList<Token> getRange(int start, int stop) {
	    ArrayList<Token> newTokens = new ArrayList<>();
	    for (int i = start; i <= stop; i++) {
	        newTokens.add(tokens.get(i));
        }
	    return newTokens;
    }

    private ArrayList<Token> replace(ArrayList<Token> tokens) {
	    if (executor != null) {
            for (int i = 0; i < tokens.size(); i++) {
                if (!tokens.get(i).getKey().equals("COD")) {
                    continue;
                }
                if (i < tokens.size() - 1 && !(tokens.get(i + 1).getKey().equals("STb") && tokens.get(i + 1).getVal().equals("("))) {
                    continue;
                }
                int j = i + 1;
                if (!(j < tokens.size())) {
                    continue;
                }

                int c = getClosingBracket(j);

                ArrayList<Token> func = getRange(i, c);

                String funcName = func.get(0).getVal().toString();
                func.remove(0);

                boolean b = executor.runFunction(funcName, func);
                Token t = executor.getFuncReturn();

                if (b) {
                    for (int k = c; k > i; k--) {
                        tokens.remove(k);
                    }
                    tokens.set(i, t);
                }

            }
        }

        if (variableState != null) {
            for (int i = 0; i < tokens.size(); i++) {
                if (i > 1 && tokens.get(i - 1).getKey().equals("SEP")) {
                    continue;
                }
                if (tokens.get(i).getKey().equals("COD")) {
                    if (variableState.isVariable(tokens.get(i).getVal().toString())) {
                        tokens.set(i, new Token(variableState.getVarType(tokens.get(i).getVal().toString()), variableState.getVarValue(tokens.get(i).getVal().toString())));
                    } else {
                        error = "It is not allowed to have Statements in Expressions or the Variable is not assigned";
                        return new ArrayList<>();
                    }
                }
            }
        }

        return tokens;
    }

    private void booleanOutput() {
        type = "bol";
        if (tokens.size() == 1 && tokens.get(0).getKey().equals("bol")) {
            outputBoolean = (boolean)tokens.get(0).getVal();
            return;
        }

        tokens = replace(tokens);

        ArrayList<Token> compareToken = new ArrayList<>();
        ArrayList<Integer> priority = new ArrayList<>();
        int p = 0;
        ArrayList<ArrayList<Token>> lineToken = new ArrayList<>();

        ArrayList<Token> nTokens = new ArrayList<>();

        if (tokens.size() == 0) {
            return;
        }

        for (Token token : tokens) {
            if (token.getKey().equals("LOb")) {
                if (token.getVal().toString().equals(">>")) {
                    p--;
                } else if (token.getVal().toString().equals("<<")) {
                    p++;
                }
                continue;
            }
            if (token.getKey().equals("LOG")) {
                compareToken.add(token);
                priority.add(p);
                lineToken.add(nTokens);
                nTokens = new ArrayList<>();
            } else {
                nTokens.add(token);
            }
        }

        if (nTokens.size() > 0) {
            lineToken.add(nTokens);
        }

        ArrayList<Boolean> booleans = new ArrayList<>();

        try {
            for (ArrayList<Token> lTokens : lineToken) {
                boolean b = booleanOutput(lTokens);
                booleans.add(b);
            }
        } catch (NullPointerException e) {
            error = "This Boolean Expression does not have any Compares";
            return;
        }

        if (compareToken.size() == 1 && booleans.size() == 1 && compareToken.get(0).getVal().toString().equals("!!")) {
            booleans.set(0, !booleans.get(0));
        }
        while (booleans.size() > 1) {
            int index = getTop(priority);

            String logic = compareToken.get(index).getVal().toString();
            Boolean b1 = booleans.get(index);
            Boolean b2 = booleans.get(index + 1);

            if (logic.equals("!!")) {
                priority.set(index, -1);
                booleans.set(index, !booleans.get(index));
                compareToken.remove(index);
                continue;
            }

            if (logic.equals("||")) {
                priority.set(index, -1);
                booleans.remove(index + 1);
                compareToken.remove(index);
                if (b1 || b2) {
                    booleans.set(index, true);
                } else {
                    booleans.set(index, false);
                }
            }
            if (logic.equals("&&")) {
                priority.set(index, -1);
                booleans.remove(index + 1);
                compareToken.remove(index);
                if (b1 && b2) {
                    booleans.set(index, true);
                } else {
                    booleans.set(index, false);
                }
            }

            if (logic.equals("!&")) {
                priority.set(index, -1);
                booleans.remove(index + 1);
                compareToken.remove(index);
                if (b1 && b2) {
                    booleans.set(index, false);
                } else {
                    booleans.set(index, true);
                }
            }
            if (logic.equals("n|")) {
                priority.set(index, -1);
                booleans.remove(index + 1);
                compareToken.remove(index);
                if (b1 || b2) {
                    booleans.set(index, false);
                } else {
                    booleans.set(index, true);
                }
            }
            if (logic.equals("x|")) {
                priority.set(index, -1);
                booleans.remove(index + 1);
                compareToken.remove(index);
                if ((b1 || b2) && !(b1 && b2)) {
                    booleans.set(index, true);
                } else {
                    booleans.set(index, false);
                }
            }
            if (logic.equals("xn")) {
                priority.set(index, -1);
                booleans.remove(index + 1);
                compareToken.remove(index);
                if ((b1 || b2) && !(b1 && b2)) {
                    booleans.set(index, false);
                } else {
                    booleans.set(index, true);
                }
            }
        }

        outputBoolean = booleans.get(0);
    }

    private int getTop(ArrayList<Integer> priorities) {
        int index = 0;
        int pri = 0;
        for (int i = 0; i < priorities.size(); i++) {
            if (pri < priorities.get(i)) {
                index = i;
                pri = priorities.get(i);
            }
        }
        return index;
    }

    private boolean booleanOutput(ArrayList<Token> tokens) {
        boolean b = false;
        ArrayList<Token> tokens1 = new ArrayList<>();
        ArrayList<Token> tokens2 = new ArrayList<>();
        Token compareToken = null;

        for (Token token : tokens) {
            if (token.getKey().equals("COM")) {
                b = true;
                compareToken = token;
                continue;
            }
            if (b) {
                tokens2.add(token);
            } else {
                tokens1.add(token);
            }
        }

        if (compareToken == null) {
            if (tokens1.size() == 1 && tokens1.get(0).getKey().equals("bol")) {
                return (boolean)tokens1.get(0).getVal();
            }

            throw new NullPointerException();
        }

        Expression expression1 = new Expression(tokens1, variableState, executor);
        expression1.build();

        Expression expression2 = new Expression(tokens2, variableState, executor);
        expression2.build();

        plusTime += expression1.getExpressionTime();
        plusTime += expression2.getExpressionTime();

        Object s1 = expression1.getObject();
        Object s2 = expression2.getObject();
        String t1 = expression1.getType();
        String t2 = expression2.getType();

        String compare = compareToken.getVal().toString();

        if (t2.equals("typ") && compare.equals("typeof")) {
            if (t1.equals(tokens2.get(0).getVal().toString())) {
                return true;
            } else {
                return false;
            }
        }

        if (t2.equals("typ") && compare.equals("canbe")) {
            String typeTo = tokens2.get(0).getVal().toString();
            if (typeTo.equals("str")) {
                return true;
            }
            if (typeTo.equals("int")) {
                try {
                    Integer.parseInt(s1.toString());
                    return true;
                } catch (Exception e) {

                }
                try {
                    if (s1.toString().startsWith("##")) {
                        Integer.parseInt(s1.toString().substring(2), 16);
                        return true;
                    } else {
                        return false;
                    }
                } catch (Exception e2) {

                }
                return false;
            }
            if (typeTo.equals("num")) {
                try {
                    Double.parseDouble(s1.toString());
                    return true;
                } catch (Exception e) {
                    return false;
                }
            }
            if (typeTo.equals("bol")) {
                return true;
            }
            if (typeTo.equals("chr")) {
                return true;
            }
        }

        if (compare.equals("==")) {
            if (s1.equals(s2) && t1.equals(t2)) {
                return true;
            } else {
                return false;
            }
        }
        if (compare.equals("!=")) {
            if (!s1.equals(s2) || !t1.equals(t2)) {
                return true;
            } else {
                return false;
            }
        }

        if ((t1.equals("str") && t2.equals("str")) || (t1.equals("str") && t2.equals("chr"))) {
            if (compare.equals("equals")) {
                if (s1.toString().equals(s2.toString())) {
                    return true;
                } else {
                    return false;
                }
            }
            if (compare.equals("equalsIgnoreCase")) {
                if (s1.toString().equalsIgnoreCase(s2.toString())) {
                    return true;
                } else {
                    return false;
                }
            }
            if (compare.equals("startsWith")) {
                if (s1.toString().startsWith(s2.toString())) {
                    return true;
                } else {
                    return false;
                }
            }
            if (compare.equals("endsWith")) {
                if (s1.toString().endsWith(s2.toString())) {
                    return true;
                } else {
                    return false;
                }
            }
            if (compare.equals("contains")) {
                if (s1.toString().contains(s2.toString())) {
                    return true;
                } else {
                    return false;
                }
            }
            if (compare.equals("containsIgnoreCase")) {
                if (s1.toString().toLowerCase().contains(s2.toString().toLowerCase())) {
                    return true;
                } else {
                    return false;
                }
            }
        }
        if (t1.equals("num") && t2.equals("num")) {
            if (compare.equals(">")) {
                if ((double)s1 > (double)s2) {
                    return true;
                } else {
                    return false;
                }
            }
            if (compare.equals("<")) {
                if ((double)s1 < (double)s2) {
                    return true;
                } else {
                    return false;
                }
            }
            if (compare.equals(">=")) {
                if ((double)s1 >= (double)s2) {
                    return true;
                } else {
                    return false;
                }
            }
            if (compare.equals("<=")) {
                if ((double)s1 <= (double)s2) {
                    return true;
                } else {
                    return false;
                }
            }
        }
        if (t1.equals("int") && t2.equals("num")) {
            if (compare.equals(">")) {
                if ((int)s1 > (double)s2) {
                    return true;
                } else {
                    return false;
                }
            }
            if (compare.equals("<")) {
                if ((int)s1 < (double)s2) {
                    return true;
                } else {
                    return false;
                }
            }
            if (compare.equals(">=")) {
                if ((int)s1 >= (double)s2) {
                    return true;
                } else {
                    return false;
                }
            }
            if (compare.equals("<=")) {
                if ((int)s1 <= (double)s2) {
                    return true;
                } else {
                    return false;
                }
            }
        }
        if (t1.equals("int") && t2.equals("int")) {
            if (compare.equals(">")) {
                if ((int)s1 > (int)s2) {
                    return true;
                } else {
                    return false;
                }
            }
            if (compare.equals("<")) {
                if ((int)s1 < (int)s2) {
                    return true;
                } else {
                    return false;
                }
            }
            if (compare.equals(">=")) {
                if ((int)s1 >= (int)s2) {
                    return true;
                } else {
                    return false;
                }
            }
            if (compare.equals("<=")) {
                if ((int)s1 <= (int)s2) {
                    return true;
                } else {
                    return false;
                }
            }
        }
        if (t1.equals("num") && t2.equals("int")) {
            if (compare.equals(">")) {
                if ((double)s1 > (int)s2) {
                    return true;
                } else {
                    return false;
                }
            }
            if (compare.equals("<")) {
                if ((double)s1 < (int)s2) {
                    return true;
                } else {
                    return false;
                }
            }
            if (compare.equals(">=")) {
                if ((double)s1 >= (int)s2) {
                    return true;
                } else {
                    return false;
                }
            }
            if (compare.equals("<=")) {
                if ((double)s1 <= (int)s2) {
                    return true;
                } else {
                    return false;
                }
            }
        }

        return false;
    }

    private boolean isOnlyInt(ArrayList<Token> tokens) {
	    for (Token token : tokens) {
	        if (token.getKey().equals("num")) {
	            return false;
            }
        }
	    return true;
    }

    private void stringFunctions() {
        for (int i = 0; i < tokens.size() - 2; i++) {
            Token tok1 = tokens.get(i);
            Token tok2 = tokens.get(i + 1);
            Token tok3 = tokens.get(i + 2);

            if (!tok1.getKey().equals("str") || !tok2.getKey().equals("SEP") || !tok2.getVal().toString().equals(":") || !tok3.getKey().equals("COD")) {
                continue;
            }

            if (tok3.getVal().toString().equals("length")) {
                int lenght = tok1.getVal().toString().length();
                tokens.set(i, new Token("int", lenght));
                tokens.remove(i + 1);
                tokens.remove(i + 1);
            }

            if (tok3.getVal().toString().equals("trim")) {
                String trim = tok1.getVal().toString().trim();
                tokens.set(i, new Token("str", trim));
                tokens.remove(i + 1);
                tokens.remove(i + 1);
            }

            if (tok3.getVal().toString().equals("toUpperCase")) {
                String toUpperCase = tok1.getVal().toString().toUpperCase();
                tokens.set(i, new Token("str", toUpperCase));
                tokens.remove(i + 1);
                tokens.remove(i + 1);
            }

            if (tok3.getVal().toString().equals("toLowerCase")) {
                String toLowerCase = tok1.getVal().toString().toLowerCase();
                tokens.set(i, new Token("str", toLowerCase));
                tokens.remove(i + 1);
                tokens.remove(i + 1);
            }
        }
    }

    private void stringOutput() {

	    tokens = replace(tokens);
	    if (tokens.size() == 0) {
	        return;
        }

	    stringFunctions();

        if (isCalculation(tokens)) {
            Calculator calculator = new Calculator(tokens);
            Object b = calculator.calculate();
            outputString = b.toString();
            if (b instanceof Integer) {
                type = "int";
            } else if (b instanceof Double) {
                type = "num";
            } else if (b instanceof Long) {
                type = "lon";
            }
            return;
        }

        int startIndex = 0;

        while (startIndex != -1) {
            int start = getOpenBracket(startIndex, tokens);
            ArrayList<Token> tokens = getTokensToClosingBracket(this.tokens, start);
            if (isCalculation(tokens)) {
                Calculator calculator = new Calculator(tokens);
                Object b = calculator.calculate();

                String t = "";
                int i = 0;
                double d = 0;
                long l = 0;

                if (b instanceof Integer) {
                    t = "int";
                    i = (int)b;
                } else if (b instanceof Double) {
                    t = "num";
                    d = (double)b;
                } else if (b instanceof Long) {
                    t = "lon";
                    l = (long)b;
                }

                Token token = null;
                if (t.equals("int")) {
                    token = new Token(t, i);
                } else if (t.equals("num")) {
                    token = new Token(t, d);
                } else if (t.equals("lon")) {
                    token = new Token(t, l);
                }

                removeToClosingBracket(this.tokens, start);
                if (token != null) {
                    this.tokens.add(start, token);
                }
            } else {
                startIndex = start;
                if (startIndex != -1) {
                    startIndex++;
                }
            }
        }

        stringFunctions();

        for (int i = tokens.size() - 1; i >= 0; i--) {
            if (tokens.get(i).getKey().equals("STb")) {
                tokens.remove(i);
            }
        }

        for (int i = 0; i < tokens.size()-1; i += 2) {
            if (!(tokens.get(i).isType() && tokens.get(i + 1).getKey().equals("OPE") && tokens.get(i + 1).getVal().equals("+"))) {
                error = "To many or to few Operators";
                return;
            }
        }

        type = "str";
        outputString = toStringOutput();
    }

    private String toStringOutput() {
        StringBuilder st = new StringBuilder();
        for (Token token : tokens) {
            if (token.getKey().equals("OPE") && token.getVal().equals("+")) {
                continue;
            }
            if (token.getKey().equals("num")) {
                st.append(token.getVal().toString());
                continue;
            }
            st.append(token.getVal().toString());
        }
        return st.toString();
    }


	/**
	 * Gets an expressed string (if the output was equal to a string value)
	 *
	 * @return the expressed string.
	 */
	public String getString() {
        if (outputString == null && error == null) {
            error = "This Expression was not detected as a String Expression";
        }
        return outputString;
    }

	/**
	 * Gets boolean.
	 *
	 * @return the boolean
	 */
	public Boolean getBoolean() {
        if (outputBoolean == null && error == null) {
            error = "This Expression was not detected as a Boolean Expression";
        }
        return outputBoolean;
    }

	/**
	 * Gets object.
	 *
	 * @return the object
	 */
	public Object getObject() {
        return outputObject;
    }

	/**
	 * Gets error.
	 *
	 * @return the error
	 */
	public String getError() {
        return error;
    }

	/**
	 * Gets expression time.
	 *
	 * @return the expression time
	 */
	public long getExpressionTime() {
        return expressionTime + plusTime;
    }

	/**
	 * Gets type.
	 *
	 * @return the type
	 */
	public String getType() {
        return type;
    }


    private int getOpenBracket(int start, ArrayList<Token> tokens) {
        for (int i = start; i < tokens.size(); i++) {
            if (tokens.get(i).getKey().equals("STb") && tokens.get(i).getVal().equals("(")) {
                return i;
            }
        }
        return -1;
    }

    private ArrayList<Token> getTokensToClosingBracket(ArrayList<Token> tokens, int start) {
        if (start == -1) {
            start++;
        }
        ArrayList<Token> arrayList = new ArrayList<>();
        boolean st = false;
        int brCount = 0;
        while (!st || (brCount != 0 && start < tokens.size())) {
            if (tokens.get(start).getKey().equals("STb") && tokens.get(start).getVal().toString().equals("(")) {
                brCount++;
            }
            if (brCount != 0) {
                arrayList.add(tokens.get(start));
            }
            if (tokens.get(start).getKey().equals("STb") && tokens.get(start).getVal().toString().equals(")")) {
                brCount--;
            }
            st = true;
            start++;
        }
        return arrayList;
    }

    private ArrayList<Token> removeToClosingBracket(ArrayList<Token> tokens, int start) {
        int brCount = 0;
        boolean st = false;
        int stop = start;
        while (!st || (brCount != 0 && stop < tokens.size())) {
            if (tokens.get(stop).getKey().equals("STb") && tokens.get(stop).getVal().toString().equals("(")) {
                brCount++;
            }
            if (tokens.get(stop).getKey().equals("STb") && tokens.get(stop).getVal().toString().equals(")")) {
                brCount--;
            }
            st = true;
            stop++;
        }
        stop--;
        while (stop >= start) {
            tokens.remove(stop);
            stop--;
        }
        return tokens;
    }


    private boolean isLogic() {
        for (Token token : tokens) {
            if (token.getKey().equals("LOG")) {
                return true;
            }
            if (token.getKey().equals("COM")) {
                return true;
            }
        }
        if (tokens.size() == 1) {
            if (tokens.get(0).getKey().equals("bol")) {
                return true;
            }
        }
        return false;
    }

    private boolean isCalculation(ArrayList<Token> tokens) {
        if (tokens.size() == 0) {
            return false;
        }
        for (int i = 0; i < tokens.size(); i++) {
            String token = tokens.get(i).getKey();
            if (!token.equals("int") && !token.equals("num") && !token.equals("lon") && !token.equals("OPE") && !token.equals("STb")) {
                return false;
            }
        }
        return true;
    }


    private boolean advancedInfo = false;

	/**
	 * Advanced info expression.
	 *
	 * @return the expression
	 */
	public ExpressionAdvancedInfo advancedInfo() {
        advancedInfo = true;
		ExpressionAdvancedInfo advancedObject = new ExpressionAdvancedInfo();
		advancedObject.set(InfoID.TOKENS, tokens);
        return advancedObject;
    }

    @Override
    public String toString() {
        if (advancedInfo) {
            advancedInfo = false;
            return "Expression{" +
                    "tokens=" + tokens +
                    ", outputString='" + outputString + '\'' +
                    ", outputBoolean=" + outputBoolean +
                    ", outputObject=" + outputObject +
                    ", expressionTime=" + expressionTime +
                    ", plusTime=" + plusTime +
                    ", type='" + type + '\'' +
                    ", error='" + error + '\'' +
                    '}';
        } else {
            return "Expression{" +
                    "outputString='" + outputString + '\'' +
                    ", outputBoolean=" + outputBoolean +
                    ", outputObject=" + outputObject +
                    ", expressionTime=" + expressionTime +
                    ", plusTime=" + plusTime +
                    ", type='" + type + '\'' +
                    ", error='" + error + '\'' +
                    '}';
        }
    }
}