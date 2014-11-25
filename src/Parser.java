import java.util.ArrayDeque;

/**
 * Created by William Trent Holliday on 10/31/14.
 */
public class Parser {
    /**
     * Block where we initialize all of our constant characters that we will use to check for in
     * the code.
     */
    private static final char PLUS = '+';
    private static final char SUBTRACT = '-';
    private static final char MULTIPLY = '*';
    private static final char DIVIDE = '/';
    private static final char OPEN_PAR = '(';
    private static final char CLOSE_PAR = ')';
    private static ArrayDeque<Character> expression; // Declare variable for queue of characters

    /** Method representing the grammar rule:
     *      E -> TE'
     *
     * @param token the character to process
     */
    private static void ruleE(Character token){
        ruleT(token); // Do T rule
        token = expression.peek(); // Get the next token
        if(token != null) // Check if we have reached end of expression
            ruleE1(token);
    }

    /** Method representing the grammar rule:
     *      T -> FT'
     *
     * @param token the character to process
     */
    private static void ruleT(Character token){
        if(ruleF(token)) {
            token = expression.peek();
            if(token != null) // Check if we have reached end of expression
                ruleT1(token); // Do rule T1 to check for * and /
        }
        else { // If ruleF is false the expression is invalid so we print false and exit
            System.out.println("false");
            System.exit(0);
        }
    }

    /** Method representing the grammar rule:
     *      E' -> +TE' | -TE' | lambda
     *
     * @param token the character to process
     */
    private static void ruleE1(Character token){
        if(token.equals(PLUS) || token.equals(SUBTRACT)){ // Check if token is a + or -
            expression.pop(); // Remove + or -
            token = expression.peek(); // Get next character in expression
            if(token != null) { // null if expression has ended
                ruleT(token);
                token = expression.peek(); // Get next token
                if(token != null) // null if expression has ended
                    ruleE1(token);
            }else{
                System.out.println(false);
                System.exit(0);
            }
        }
    }

    /** Method representing the grammar rule:
     *      T' -> *FT' | /FT' | lambda
     *
     * @param token the character to process
     */
    private static void ruleT1(Character token){
        if(token.equals(MULTIPLY) || token.equals(DIVIDE)) {
            expression.pop(); // remove * or /
            token = expression.peek(); // Get next token
            if(token != null && ruleF(token)) {
                token = expression.peek();
                if (token != null) // null if expression has ended
                    ruleT1(token);
            }
            else{ // If ruleF is false the expression is invalid so we print false and exit
                    System.out.println("false");
                    System.exit(0);
                }
        }
    }

    /** Method representing the grammar rule:
     *      F -> n | (E) | v
     *
     * @param token the character to process
     * @return boolean value: true if the f rule worked and false otherwise
     */
    private static boolean ruleF(Character token){
        if(token.equals(OPEN_PAR)){
            expression.pop(); // remove (
            token = expression.peek(); // Get next token
            if(token != null) // null if expression has ended
                ruleE(token); // do ruleE to check if stuff after parenthesis is valid
            else{ // nothing after opening ( so expression is invalid
                System.out.println("false");
                System.exit(0);
            }
            token = expression.peek(); // get next token
            if(token != null && token.equals(CLOSE_PAR)){ // Check for )
                expression.pop(); // remove )
                // return true since their was a opening and closing
                // parenthesis with a valid expression between them
                return true;
            }
            else { // Expression is invalid since their was an opening parenthesis that was not closed
                System.out.println(false);
                System.exit(0);
            }
        }
        else if(isTerminal(token)){ // Check if the token is a valid terminal
            removeTerminals(); // check if the terminals are in a valid format and remove them
            return true; // only gets here if removeTerminals was successful, so return true
        }
        return false; // Character does not match a ( or a valid terminal so return false
    }

    /** Checks whether the given character is a possible valid terminal.
     * Valid terminals are 0-9, a-zA-Z, and .
     *
     * @param token the character to process
     * @return boolean value: true, if character is a token and false otherwise
     */
    private static boolean isTerminal(Character token){
        return Character.isAlphabetic(token) || Character.isDigit(token) || token.equals('.');
    }

    /** Checks whether the terminal characters are of the right format. Will not
     *  allow letters to follow a '.' Will not allow a letter to follow a digit if
     *  a digit is the first terminal.
     *
     *  Ex.
     *      'a1' is accepted
     *      '1a' is not accepted
     *      '.1' is accepted
     *      '.a' is not accepted
     *
     */
    private static void removeTerminals(){
        // boolean variables to determine if the terminals are in an acceptable format
        boolean seenNumber = false, seenLetter = false, seenDecimal = false;
        while(expression.peek() != null && isTerminal(expression.peek())) { // loop on the terminals
            if(Character.isDigit(expression.peek())) { // check if token is a number
                if (seenLetter) // if we've seen a letter then a number is okay to remove
                    expression.pop();
                else { // Set seenNumber to true so we know we have seen a number
                    seenNumber = true;
                    expression.pop(); // remove number
                }
            }
            else if(Character.isAlphabetic(expression.peek())){ // check if token is a letter
                if(seenNumber || seenDecimal){ // if we have seen a number or a . before, then we cannot see a letter
                    System.out.println("false");
                    System.exit(0);
                }else{ // set that we have seen a letter, and remove letter
                    seenLetter = true;
                    expression.pop();
                }
            }
            else if(expression.peek().equals('.')){ // check if token is a .
                if(seenLetter || seenDecimal){ // if we have already seen a . or a letter then we cannot see a .
                    System.out.println("false");
                    System.exit(0);
                }else{ // remove .
                    seenDecimal = true;
                    expression.pop();
                    Character token = expression.peek();
                    if(token == null || !Character.isDigit(token)){ // if next character is not a
                        System.out.println("false");                // number then expression is invalid
                        System.exit(0);
                    }

                }
            }
        } // End while loop
    }

    public static void main(String[] args){
        String testExpr = "(((1))*(1)/1)";
        expression = new ArrayDeque<Character>(); // Initialize the queue which will hold the characters
        if (args.length != 0) // Check if arguments were given
            testExpr = args[0];
        char[] primitiveArray = testExpr.toCharArray(); // Convert the given string to an array of char
        for(char c : primitiveArray) {
            // This is so we do not add spaces to the queue
            if(c != ' ')
                expression.add(c); // Add character to queue
        }
        ruleE(expression.peek()); // Begin to evaluate the given expression

        if(expression.isEmpty())
            System.out.println(true); // Expression is empty so it is valid
        else
            System.out.println(false); // Expression is not empty so it is invalid
    }
}