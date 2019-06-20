package tec.calculator;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Calculator {

    private History calculations = new History();
    private String lastCalculation = "";
    private Double lastAnswer = 0.0;

    private ArrayList<Object> objects;
    private ArrayList<Integer> priorities;

    private long calcTime1 = 0;
    private long calcTime2 = 0;

    public Calculator() { }

    public Calculator(History history) {
        calculations = history;
    }

    public String getLastCalculation() {
        return lastCalculation;
    }

    public History getHistory() {
        return calculations;
    }

    public long getCalcTime(String mode) {
        if (mode.equals("ms")) {
            return calcTime1;
        } else if (mode.equals("ns")) {
            return calcTime2;
        } else if (mode.equals("s")) {
            return calcTime1 / 1000;
        }
        return calcTime1;
    }

    public long getCalcTime() {
        return calcTime1;
    }

    private void time() {
        calcTime1 -= System.currentTimeMillis();
        calcTime2 -= System.nanoTime();
        calcTime1 *= -1;
        calcTime2 *= -1;
    }

    public double calc(String s) {
        calcTime1 = System.currentTimeMillis();
        calcTime2 = System.nanoTime();

        if (s.equals("?")) {
            System.out.println("");
            System.out.println("calculator.Calculator:");
            System.out.println("- Priority 1:");
            System.out.println("-- + -");
            System.out.println("- Priority 2:");
            System.out.println("-- * / % !");
            System.out.println("- Priority 3:");
            System.out.println("-- ^ root");
            System.out.println("- Priority 4:");
            System.out.println("-- ( ) |");
            System.out.println("");
            System.out.println("calculator.History:");
            calculations.print();
            System.out.println("");
            time();
            return 0;
        }
        if (s.toLowerCase().equals("history")) {
            System.out.println("");
            System.out.println("calculator.History:");
            System.out.println(calculations.toString());
            System.out.println("");
            time();
            return 0;
        }

        if (s.equals("=")) {
            if (lastCalculation.length() != 0) {
                s = lastCalculation;
            } else {
                time();
                throw new IllegalArgumentException("No Last Calculation");
            }
        }

        String st = s;
        s = s.replaceAll("ans", "(" + lastAnswer + ")");
        if (calculations.contains(s)) {
            time();
            return calculations.get(s);
        } else {
            ArrayList<Object> objects = formatString(s);
            ArrayList<Integer> priorities = priorityArray(objects);
            this.objects = objects;
            this.priorities = priorities;

            deleteNull();
            removeNotNeded();

            if (objects.size() == 1) {
                Double d = calculate();
                lastAnswer = d;
                lastCalculation = st;
                time();
                return d;
            } else {
                Double d = calculate();
                lastAnswer = d;
                calculations.put(s, d);
                lastCalculation = st;
                time();
                return d;
            }
        }
    }

    private ArrayList<Object> formatString(String s) {
        Pattern pattern = Pattern.compile("[0-9] +[0-9]");
        Matcher matcher = pattern.matcher(s);
        if (matcher.find()) {
            System.out.println(s);
            time();
            throw new IllegalArgumentException("No Spaces in Numbers");
        }
        char[] chars = s.replaceAll("root", "r")
                .replaceAll("pi", Math.PI + "").replaceAll("lspeed", "299792458").replaceAll("e", Math.E + "")
                .replaceAll("sin", "s").replaceAll("cos", "c").replaceAll("tan", "t")
                .replaceAll(" ", "")
                .toCharArray();
        ArrayList<Object> objects = new ArrayList<>();
        StringBuilder st = new StringBuilder();
        for (char c : chars) {
            if ((c >= '0' && c <= '9') || c == 'E' || c == '.') {
                st.append(c);
            } else {

                objects = minusCheck(objects, st);

                if (c == '(' && st.length() != 0) {
                    objects.add("*");
                }
                try {
                    if (c == '(' && st.length() == 0 && objects.get(objects.size() - 1).toString().equals("-") && !(objects.get(objects.size() - 2) instanceof Double)) {
                        objects.set(objects.size() - 1, "(");
                        objects.add(Double.parseDouble("-1"));
                        objects.add(")");
                        objects.add("*");
                    }
                } catch (ArrayIndexOutOfBoundsException e) {
                    try {
                        if (c == '(' && st.length() == 0 && objects.get(objects.size() - 1).toString().equals("-")) {
                            objects.set(objects.size() - 1, "(");
                            objects.add(Double.parseDouble("-1"));
                            objects.add(")");
                            objects.add("*");
                        }
                    } catch (IndexOutOfBoundsException ex) {

                    }
                }
                if (c == 'r' && st.length() == 0) {
                    objects.add(Double.parseDouble("2"));
                }
                if ((c == 's' || c == 'c' || c == 't') && st.length() == 0) {
                    objects.add(Double.parseDouble("1"));
                }
                st = new StringBuilder();
                objects.add(c + "");
                // NUMBER REGEX
                // (([\\-]?[0-9]*)([.][0-9]*)?)(([E]([\\-]?[0-9]*))?)
            }
        }
        objects = minusCheck(objects, st);
        return objects;
    }

    private ArrayList<Object> minusCheck(ArrayList<Object> objects, StringBuilder st) {

        if (st.length() != 0) {
            try {
                if (objects.get(objects.size() - 1).toString().equals("-")) {
                    try {
                        if (objects.get(objects.size() - 2).toString().matches("[+]|[\\-]|[*]|[/]|[%]|[\\^]|[(]|[r]")) {
                            if (objects.get(objects.size() - 3).toString().equals("|")) {

                            }
                            objects.remove(objects.size() - 1);
                            objects.add(Double.parseDouble("-" + st.toString()));
                        } else {
                            objects.add(Double.parseDouble(st.toString()));
                        }
                    } catch (IndexOutOfBoundsException e) {
                        objects.remove(objects.size() - 1);
                        objects.add(Double.parseDouble("-" + st.toString()));
                    }
                } else {
                    objects.add(Double.parseDouble(st.toString()));
                }
            } catch (IndexOutOfBoundsException e) {
                objects.add(Double.parseDouble(st.toString()));
            }
        }

        return objects;
    }

    private ArrayList<Object> minusCheck(ArrayList<Object> objects, int index, boolean absolute) {
        try {
            if (objects.get(index + 1).toString().equals("-")) {
                if (absolute && objects.get(index).toString().equals("|")) {
                    if (objects.get(index + 2) instanceof Double) {
                        objects.set(index + 2, (double)objects.get(index + 2) * -1);
                        objects.set(index + 1, "");
                    }
                }
            }
        } catch (IndexOutOfBoundsException e) {

        }
        return objects;
    }

    private ArrayList<Integer> priorityArray(ArrayList<Object> objects) {
        int k = 0;
        int i = 0;
        int j = 0;
        ArrayList<Integer> priorities = new ArrayList<>();
        ArrayList<Boolean> absolute = new ArrayList<>();
        absolute.add(false);
        for (Object object : objects) {
            if (object.toString().matches("\\(")) {
                i += 4;
                j++;
                absolute.add(false);
            } else if (object.toString().matches("\\)")) {
                i -= 4;
                j--;
                if (absolute.get(absolute.size() - 1)) {
                    time();
                    throw new IllegalArgumentException("Absolute Did not close in this Layer");
                } else {
                    absolute.remove(absolute.size() - 1);
                }
            }
            if (i < 0) {
                throw new IllegalArgumentException("Bracket Closed without opening one");
            }
            if (object.toString().matches("[+]|[\\-]")) {
                priorities.add(i + 1);
            } else if (object.toString().matches("[*]|[/]|[%]|[!]")) {
                priorities.add(i + 2);
            } else if (object.toString().matches("[\\^]|[r]|[s]|[c]|[t]")) {
                priorities.add(i + 3);
            } else if (object.toString().matches("[(]|[)]")) {
                priorities.add(0);
            } else if (object.toString().matches("[|]")) {
                if (absolute.get(absolute.size() - 1)) {
                    i -= 5;
                    absolute.set(absolute.size() - 1, false);
                } else {
                    i += 5;
                    absolute.set(absolute.size() - 1, true);
                }
                priorities.add(0);
                objects = minusCheck(objects, k, absolute.get(absolute.size() - 1));
            }
            else {
                priorities.add(-1);
            }
            k++;
        }
        if (i != 0) {
            time();
            throw new IllegalArgumentException("Brackets did not close. Missing: " + j);
        }
        return priorities;
    }

    private void deleteNull() {
        for (int i = objects.size() - 1; i > 0; i--) {
            if (objects.get(i).toString().equals("")) {
                priorities.remove(i);
                objects.remove(i);
            }
        }
    }

    private double calculate() {
        removeNotNeded();
        if (objects.size() == 1) {
            return (double) objects.get(0);
        }

        int hP = 0;
        int cP = 0;
        for (int i = 0; i < priorities.size(); i++) {
            if (cP < priorities.get(i)) {
                hP = i;
                cP = priorities.get(i);
            }
        }

        remove(hP, calcCO(hP));

        if (objects.size() > 1) {
                calculate();
        }
        return (double) objects.get(0);
    }

    private double calcCO(int hP) {
        String op = (String) objects.get(hP + 0);
        try {
            if (priorities.get(hP - 1) == -1 && priorities.get(hP + 1) == -1) {
                double d1 = (double) objects.get(hP - 1);
                double d2 = (double) objects.get(hP + 1);
                if (op.equals("+")) {
                    return d1 + d2;
                } else if (op.equals("-")) {
                    return d1 - d2;
                } else if (op.equals("*")) {
                    return d1 * d2;
                } else if (op.equals("/")) {
                    return d1 / d2;
                } else if (op.equals("%")) {
                    return d1 % d2;
                } else if (op.equals("^")) {
                    return Math.pow(d1, d2);
                } else if (op.equals("r")) {
                    if (d1 == 2) {
                        return Math.sqrt(d2);
                    }
                    return Math.pow(Math.E, Math.log(d2) / d1);
                } else if (op.equals("s")) {
                    return d1 * Math.sin(d2);
                } else if (op.equals("c")) {
                    return d1 * Math.cos(d2);
                } else if (op.equals("t")) {
                    return d1 * Math.tan(d2);
                }
            }
        } catch (IndexOutOfBoundsException e) {

        }
        if (op.equals("!") && priorities.get(hP - 1) == -1) {
            double d1 = (double) objects.get(hP - 1);
            String[] s = (d1 + "").split("\\.");
            char[] chars = s[1].toCharArray();
            if (!(chars.length == 1 && (chars[0] + "").equals("0"))) {
                time();
                throw new IllegalArgumentException("Only Integers allowed in factorial");
            }
            for (int i = (int) d1 - 1; i > 0; i--) {
                d1 *= i;
            }
            return d1;
        }
        time();
        throw new IllegalArgumentException("Operation not recognized or Factor is missing");
    }

    private void remove(int hP, double output) {
        boolean b = false;
        if (objects.get(hP).toString().equals("!")) {
            b = true;
        }
        objects.set(hP, output);
        priorities.set(hP, -1);

        if (!b) {
            objects.remove(hP + 1);
            priorities.remove(hP + 1);
        }
        objects.remove(hP - 1);
        priorities.remove(hP - 1);

        removeNotNeded();
    }

    private void removeNotNeded() {
        for (int i = objects.size() - 1; i > 0; i--) {
            removeBrackets(i);
            removeAbsolute(i);
        }
    }

    private void removeBrackets(int hP) {
        try {
            if (objects.get(hP).toString().equals(")") && objects.get(hP - 2).toString().equals("(")) {
                objects.remove(hP);
                priorities.remove(hP);
                objects.remove(hP - 2);
                priorities.remove(hP - 2);
            }
        } catch (IndexOutOfBoundsException e) {

        }
    }

    private void removeAbsolute(int hP) {
        try {
            if (objects.get(hP).toString().equals("|") && objects.get(hP - 2).toString().equals("|")) {
                objects.remove(hP);
                priorities.remove(hP);
                if ((double)objects.get(hP - 1) < 0) {
                    objects.set(hP - 1, (double) objects.get(hP - 1) * -1);
                }
                objects.remove(hP - 2);
                priorities.remove(hP - 2);
            }
        } catch (IndexOutOfBoundsException e) {

        }
    }

}