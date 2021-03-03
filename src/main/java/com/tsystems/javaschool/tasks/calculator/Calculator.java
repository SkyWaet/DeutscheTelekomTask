package com.tsystems.javaschool.tasks.calculator;

import java.util.*;


public class Calculator {
    /**
     * Check if argument is digit
     *
     * @param sym symbol to be checked<br>
     * @return <code>true</code> if symbol is digit, <code>false</code> otherwise
     */
    private boolean isDigit(char sym) {
        return Character.isDigit(sym);
    }


    /**
     * Check if argument is one of the operators: +,-,*,/
     *
     * @param sym symbol to be checked<br>
     * @return <code>true</code> if symbol is operator, <code>false</code> otherwise
     */
    private boolean isOperator(char sym) {
        return (sym == '+') || (sym == '-') || (sym == '*') || (sym == '/');
    }


    /**
     * Find the priority of the operator
     *
     * @param operator char which contains one of the symbols +,-.*,/
     * @return 0 for + and -, 1 otherwise
     */
    private int getPriority(char operator) {
        if (operator == '+' || operator == '-') {
            return 0;
        } else {
            return 1;
        }
    }

    /**
     * Parses string into ArrayDeque of tokens: numbers, operators and ()-brackets
     *
     * @param str mathematical statement containing digits, '.' (dot) as decimal mark,
     *            parentheses, operations signs '+', '-', '*', '/'<br>
     *            Example: <code>(1 + 38) * 4.5 - 1 / 2.</code>
     * @return ArrayDeque containing result of parsing or null if statement is invalid
     */
    private ArrayDeque<String> parseString(String str) {
        ArrayDeque<String> parsedString = new ArrayDeque<>();
        int len = str.length();
        int i = 0;
        int numDots = 0;
        StringBuilder buff = new StringBuilder();
        while (i < len) {
            char elem = str.charAt(i);

            if (isDigit(elem)) {
                char curr = elem;
                /*
                  join all consistent digits and dots in one float number
                 */
                while ((isDigit(curr) || curr == '.')) {
                    if (curr == '.') {
                        numDots++;
                        /*
                          there should be not more than one dot in the number
                          also two dots could not stand nearby
                         */
                        if (numDots > 1) {
                            System.out.println("Too many dots = " + numDots);
                            return null;
                        }
                    }
                    buff.append(curr);
                    i++;
                    if (i >= len) {
                        break;
                    }
                    curr = str.charAt(i);
                }
                /*
                  Numbers like *. are treated as *.0 (For example: 4. -> 4.0 )
                 */
                if (buff.charAt(buff.length() - 1) == '.') {
                    buff.append('0');
                }
                parsedString.add(buff.toString());
                buff = new StringBuilder();
                numDots = 0;
            } else {
                /*
                  Two operators could not stand nearby
                 */
                if (isOperator(elem) && !parsedString.isEmpty() && isOperator(parsedString.peekLast().charAt(0))) {
                    System.out.println("Two operators nearby: " + parsedString.peekLast() + " and " + elem);
                    return null;
                }
                parsedString.add("" + elem);
                i++;
            }
        }
        return parsedString;
    }

    /**
     * Convert parsed string to postfix notation using Dijkstra Shunting-yard algorithm
     *
     * @param parsedString ArrayDeque made by parseString() method
     * @return Queue of operations in postfix notation as ArrayDeque or null if statement is invalid:
     * 1) Restricted Symbols were found
     * 2) There were found unclosed '(' or ')'     *
     */
    private ArrayDeque<String> polishNotation(ArrayDeque<String> parsedString) {
        ArrayDeque<String> output = new ArrayDeque<>();
        Stack<Character> operators = new Stack<>();
        while (!parsedString.isEmpty()) {
            String elem = parsedString.pollFirst();
            /*
              numbers should be put to the queue
             */
            if (elem.matches("[-+]?[0-9]*\\.?[0-9]+")) {
                output.offer(elem);
            } else if (elem.matches("[+\\-*/]")) {
                /*
                  all operators from operations stack with higher priority
                  than current token should be put to the output queue
                  */
                while (!operators.isEmpty() && isOperator(operators.peek()) && getPriority(operators.peek()) >= getPriority(elem.charAt(0))) {
                    output.offer("" + operators.pop());
                }
                /*
                  current token should be pushed to the stack
                  */
                operators.push(elem.charAt(0));
            } else if (elem.charAt(0) == '(') {
                /*
                  left bracket should simply be pushed to the stack
                 */
                operators.push(elem.charAt(0));
            } else if (elem.charAt(0) == ')') {
                /*
                  all operators from the stack should be put
                  to the output queue while '(' is not on the top
                  of the stack
                 */
                while (!operators.isEmpty() && operators.peek() != '(') {
                    output.offer("" + operators.pop());
                }
                /*
                  if operators stack is empty, the '(' was not found
                 */
                if (operators.isEmpty()) {
                    System.out.println("Unclosed )");
                    return null;
                }
                operators.pop();
            } else {
                /*
                  Current element is one of restricted symbols
                 */
                System.out.println("Restricted Symbol " + elem);
                return null;
            }
        }
        /*
         * all operators from the stack should be put to the  output queue
         */
        while (!operators.isEmpty()) {
            /*
             * if '(' is on the top of the stack, there were not corresponding
             * ')' in the initial string
             */
            if (operators.peek() == '(') {
                System.out.println("Unclosed ( ");
                return null;
            }
            output.offer("" + operators.pop());
        }
        return output;
    }


    /**
     * Perform calculations using postfix notation
     *
     * @param polishNot Queue as ArrayDeque representing the postfix notation
     *                  of the initial string (polishNotation() method output)
     * @return string value containing result of calculation or null if statement is invalid
     */
    private String calculate(ArrayDeque<String> polishNot) {
        Stack<String> container = new Stack<>();
        /*
         * the stack is used to perform calculations
         */
        while (!polishNot.isEmpty()) {
            String elem = polishNot.poll();

            if (elem.matches("[-+]?[0-9]*\\.?[0-9]+")) {
                /*
                 * numbers are pushed directly to the stack
                 */
                container.push(elem);
            } else {
                /*
                 * two last elements are popped from the stack in order
                 * to perform operation. The result of the operation is
                 * pushed to the stack
                 */
                double second = Double.parseDouble(container.pop());
                double first = Double.parseDouble(container.pop());
                switch (elem) {
                    case "+":
                        container.push(Double.toString(second + first));
                        break;
                    case "-":
                        container.push(Double.toString(first - second));
                        break;
                    case "*":
                        container.push(Double.toString(first * second));
                        break;
                    case "/":
                        if (second == 0) {
                            System.out.println("Division by zero");
                            return null;
                        }
                        container.push(Double.toString(first / second));
                        break;
                }
            }
        }
        /*
         * The result is on top of the stack.
         * If result could be casted to integer (=*.0), it should
         * be rounded. Else the original value should be returned
         */
        double lastElem = Double.parseDouble(container.pop());
        int ans = (int) Math.round(lastElem);

        return lastElem == (double) ans ? Integer.toString(ans) : Double.toString(lastElem);
    }

    /**
     * Evaluate statement represented as string.
     *
     * @param statement mathematical statement containing digits, '.' (dot) as decimal mark,
     *                  parentheses, operations signs '+', '-', '*', '/'<br>
     *                  Example: <code>(1 + 38) * 4.5 - 1 / 2.</code>
     * @return string value containing result of evaluation or null if statement is invalid
     */
    public String evaluate(String statement) {
        /*
         * Check if initial string is empty or null
         */
        if (statement == null || statement.length() == 0) {
            System.out.println("Empty string or null");
            return null;
        }
        /*
         * Parse initial string
         */
        ArrayDeque<String> parsedString = this.parseString(statement);
        /*
         * if some errors were found while parsing, the parsedString will be null
         */
        if (parsedString == null) {
            return null;
        }
        /*
         * Find the postfix notation of the parsedString
         */
        ArrayDeque<String> polishForm = this.polishNotation(parsedString);
        /*
         * if some errors were found while prefix notation creation, the polishForm will be null
         */
        if (polishForm == null) {
            return null;
        }
        /*
         * Use polishForm to calculate value
         */
        return calculate(polishForm);
    }
}
