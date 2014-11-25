import java.util.*;

/**
 * Created by Trent Holliday
 * Arithmetic ParserOld
 * Theoretical Foundations
 */
public class ParserOld {
    private static final char PLUS = '+'; // P
    private static final char SUBTRACT = '-'; // S
    private static final char MULTIPLY = '*'; // M
    private static final char DIVIDE = '/'; // D
    private static final char OPEN_PAR = '('; // B1
    private static final char CLOSE_PAR = ')'; // B2
    private static int NUM_OPEN_PAR = 0;

    private static Stack<Character> expr;

    private static Character getNextToken(Stack<Character> expr) throws Exception{
        if (expr.isEmpty()){
            throw new Exception("End of expression");
        }else{
            return expr.pop();
        }
    }

    private static boolean parse(Stack<Character> expr){
        boolean isValid = true;
        while(isValid) {
            try {
                if (expression(getNextToken(expr)) == false) {

                    isValid = false;
                }
            } catch (Exception ex) {
                if(NUM_OPEN_PAR != 0)
                    isValid = false;

                break;
            }
        }
        if(NUM_OPEN_PAR != 0)
            isValid = false;
        return isValid;

    }

    // E rule
    private static boolean expression(Character token){
        //Check if the terminal rule applies
        boolean terminalTrue = terminal(token);
        if(terminalTrue){
            if(!isTerminal(token))
                token = expr.peek();
            if(expr.isEmpty()) {
                return false;
            }
        }

        //Check if the token is a terminal token
        if(isTerminal(token)) {

            if(expr.isEmpty())
                return true;
            //Booleans to decide if terminal is appropriate
            boolean seenLetter = false,seenNumber = false, seenDecimal = false;
            while(isTerminal(token)) {
                if(Character.isAlphabetic(token)) {
                    if (!seenNumber && !seenDecimal)
                        seenLetter = true;
                    else return false;
                }
                else if(Character.isDigit(token)) {
                    seenNumber = true;
                }else if(token.equals('.')){
                    if(seenDecimal)
                        return false;
                    else
                        seenDecimal = true;
                }
                if(seenLetter && token.equals('.'))
                    return false;
                if(isTerminal(expr.peek()))
                    token = expr.pop();
                else
                    token = expr.peek();
            }
            token = expr.peek();
            // See a * or / so do appropriate rules
            if(token.equals(MULTIPLY) || token.equals(DIVIDE)){
                return sign(expr.peek());
            }
            token=expr.peek();
            // See a + or - so do appropriate rules
            if(token.equals(PLUS) || token.equals(SUBTRACT)){
                return xRule(expr.peek());
            }
            return true;
        }
        // Check if token is a + or -
        else if(token.equals(PLUS) || token.equals(SUBTRACT)){

            if(expr.isEmpty()){
                return false;
            }
            return xRule(expr.peek());
        }
        // Check for (
        else if(token.equals(OPEN_PAR)){

            if(expr.isEmpty()){
                return false;
            }
            token = expr.peek();
            if(token.equals(OPEN_PAR))
                token = expr.pop();
            token=expr.peek();
            if(expr.isEmpty()){
                return false;
            }else if(token.equals(CLOSE_PAR))
                return false;
            if(f1Rule(token)){
                if(expr.isEmpty()){
                    return true;
                }

                return xRule(expr.peek());
            }
        }
        if(terminalTrue && !isTerminal(token)) {
            return true;
        }
        else
            return false;
    }

    // X Rule
    private static boolean xRule(Character token){
        // Check for + rule
        if(e1(token)) {
            if(expr.isEmpty()){
                return true;
            }
            if(isTerminal(expr.peek()))
                expr.pop();
            if(expr.isEmpty()) {
                return true;
            }
            xRule(expr.peek());
            return true;
        }
        // Check for - rule
        else if (e2(token)){
            if(expr.isEmpty()){
                return true;
            }
            if(isTerminal(expr.peek()))//!expr.peek().equals(CLOSE_PAR))
                expr.pop();
            if(expr.isEmpty()) {
                return true;
            }
            xRule(expr.peek());
            return true;
        }
        return false;
    }

    // E1 Rule
    private static boolean e1(Character token){

        if (token.equals(PLUS)){
            if(expr.isEmpty()){
                return false;
            }
            expr.pop(); // remove +
            if(expr.isEmpty()){
                return false;
            }
            boolean val = terminal(expr.pop());
            if(val && expr.isEmpty()) {
                return true;
            }else if(expr.isEmpty())
                return false;

            token = expr.peek();
            if(val && expr.isEmpty()) {
                return true;
            }else if(expr.isEmpty())
                return false;

            if(val && isTerminal(token))
                terminal(expr.pop());


            return val;
        }
        return false;
    }

    // E2 Rule
    private static boolean e2(Character token){

        if (token.equals(SUBTRACT)){
            if(expr.isEmpty()){
                return false;
            }
            expr.pop(); // remove -
            if(expr.isEmpty()){
                return false;
            }

            boolean val = terminal(expr.pop());

            if(val && expr.isEmpty()) {
                return true;
            }else if(expr.isEmpty())
                return false;

            token = expr.peek();
            if(val && expr.isEmpty()) {
                return true;
            }else if(expr.isEmpty())
                return false;

            if(val && isTerminal(token))
                terminal(expr.pop());

            return val;
        }
        return false;
    }

    // T rule
    private static boolean terminal(Character token){
        // Check for (
        if(token.equals(OPEN_PAR)) {
            if(expr.isEmpty())
                return false;
            // Looks for ((
            if(expr.peek().equals(OPEN_PAR)) {
                if (f1Rule(expr.pop())) {
                    try {
                        if(expr.peek().equals(PLUS) || expr.peek().equals(SUBTRACT))
                            return xRule(expr.peek());
                        else if(expr.peek().equals(MULTIPLY) || expr.peek().equals(DIVIDE))
                            return sign(expr.peek());

                        return true;
                    }
                    catch(Exception e){
                        // End of stack
                        return true;
                    }
                }else{
                    return false;
                }
            }
            else if(expr.peek().equals(CLOSE_PAR))
                return false;
            if(f1Rule(expr.peek())){
                try {
                    if(expr.peek().equals(PLUS) || expr.peek().equals(SUBTRACT))
                        return xRule(expr.peek());
                    else if(expr.peek().equals(MULTIPLY) || expr.peek().equals(DIVIDE))
                        return sign(expr.peek());

                    return true;
                }
                catch(Exception e){
                    // End of stack
                    return true;
                }
            }else{
                return false;
            }

        }
        else if(isTerminal(token)){
            token = expr.peek();
            if(token.equals(MULTIPLY) || token.equals(DIVIDE))
                return sign(token);
            if(expr.isEmpty())
                return false;
            return true;
        }
        return false;
    }

    // Y rule
    private static boolean sign(Character token){

        // T1 Y
        if(token.equals(MULTIPLY)) {
            if(multiplyRule(token)) {
                token = expr.peek();
                if (token.equals(MULTIPLY) || token.equals(DIVIDE))
                    return sign(token);
                return true;
            }
            return false;

        }else if(token.equals(DIVIDE)){
            if(divideRule(token)) {
                token = expr.peek();
                if (token.equals(MULTIPLY) || token.equals(DIVIDE))
                    return sign(token);
                return true;
            }
            return false;
        }
        return false;
    }

    // T1
    private static boolean multiplyRule(Character token){

        if (token.equals(MULTIPLY)){
            if(expr.isEmpty()){
                return false;
            }
            expr.pop(); // remove *
            if(expr.isEmpty()){
                NUM_OPEN_PAR = -2;
                return false;
            }
            return fRule(expr.peek());
        }
        return false;
    }

    // T2
    private static boolean divideRule(Character token){

        if (token.equals(DIVIDE)){
            if(expr.isEmpty()){
                return false;
            }
            expr.pop(); // remove /
            if(expr.isEmpty()){
                NUM_OPEN_PAR = -2;
                return false;
            }
            return fRule(expr.peek());
        }
        return false;
    }

    // F
    private static boolean fRule(Character token){

        if(isTerminal(token)){
            expr.pop();
            token = expr.peek();
            if(isTerminal(token))
                fRule(token);
            return true;
        }
        if(token.equals(OPEN_PAR)){
            NUM_OPEN_PAR--;
            return f1Rule(expr.pop());
        }
        return false;
    }

    // F1 Rule
    private static boolean f1Rule(Character token){

        NUM_OPEN_PAR++;
        if(expression(token)) {
            if(expr.isEmpty()) {
                return false;
            }
            token = expr.peek();
            if (token.equals(CLOSE_PAR)) {
                NUM_OPEN_PAR--;
                expr.pop(); // remove )
                if(expr.isEmpty()) {
                    return true;
                }
                if(isTerminal(expr.peek()))
                    return false;
                else if(expr.peek().equals(OPEN_PAR)) {
                    NUM_OPEN_PAR = -2;
                    return false;
                }
                return true;
            }
            return true;
        }
        return false;
    }
    // method to check whether a character is a valid terminal symbol
    private static boolean isTerminal(Character token){
        if(Character.isAlphabetic(token) || Character.isDigit(token) || token.equals('.')){
            return true;
        }else
            return false;
    }

    public static void main(String[] args){
        String testExpr = "";
        if(args.length !=0)
            testExpr = args[0];
        else {
            System.out.println("No argument given.");
            System.exit(1);
        }
        char[] primitiveArray = testExpr.toCharArray();
        expr = new Stack<Character>();
        // Populate the stack with the string characters given
        ArrayList<Character> charArray = new ArrayList<Character>();
        for (int i=primitiveArray.length-1; i >= 0; i--){
            if(primitiveArray[i] != ' ')
                charArray.add(Character.valueOf(primitiveArray[i]));
        }

        expr.addAll(charArray);
        boolean isValid = parse(expr);
        System.out.println(isValid);
    }
}
