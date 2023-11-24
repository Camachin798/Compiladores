import java.util.List;

public class ASDR implements Parser{

    private int i = 0;
    private boolean hayErrores = false;
    private Token preanalisis;
    private final List<Token> tokens;


    public ASDR(List<Token> tokens){
        this.tokens = tokens;
        preanalisis = this.tokens.get(i);
    }

    @Override
    public boolean parse() {
        PROGRAM();

        if(preanalisis.tipo == TipoToken.EOF && !hayErrores){
            System.out.println("Todo bien");
            return  true;
        }else {
            System.out.println("codea bien, inutil");
        }
        return false;
    }

    // PROGRAM -> DECLARATION
    private void PROGRAM(){
        DECLARATION();
    }

    // DECLARATION -> FUN_DECL DECLARATION | VAR_DECL DECLARATION | STATEMENT DECLARATION | /e
    private void DECLARATION(){
        if(hayErrores)
            return;

        if(preanalisis.tipo == TipoToken.FUN){
            FUN_DECL();
            DECLARATION();
        }
        else if (preanalisis.tipo == TipoToken.VAR) {
            VAR_DECL();
            DECLARATION();
        }
        else if ( preanalisis.tipo == TipoToken.BANG || 
        		  preanalisis.tipo == TipoToken.MINUS || 
        		  preanalisis.tipo == TipoToken.TRUE || 
        		  preanalisis.tipo == TipoToken.FALSE || 
        		  preanalisis.tipo == TipoToken.NULL || 
        		  preanalisis.tipo == TipoToken.NUMBER || 
        		  preanalisis.tipo == TipoToken.STRING || 
        		  preanalisis.tipo == TipoToken.IDENTIFIER || 
        		  preanalisis.tipo == TipoToken.LEFT_PAREN || 
        		  preanalisis.tipo == TipoToken.FOR || 
        		  preanalisis.tipo == TipoToken.IF || 
        		  preanalisis.tipo == TipoToken.PRINT || 
        		  preanalisis.tipo == TipoToken.RETURN || 
        		  preanalisis.tipo == TipoToken.WHILE || 
        		  preanalisis.tipo == TipoToken.LEFT_BRACE ) {
            STATEMENT();
            DECLARATION();
        }
    }
    //FUN_DECL -> fun FUNCTION
    private void FUN_DECL(){
    	if(hayErrores)
            return;

        if(preanalisis.tipo == TipoToken.FUN){
            match(TipoToken.FUN);
            FUNCTION();
        }
        else{
            hayErrores = true;
            System.out.println("Se esperaba 'fun'");
        }
    }
    //VAR_DECL -> var id VAR_INIT ;
    private void VAR_DECL(){
    	if(hayErrores)
            return;

        if(preanalisis.tipo == TipoToken.VAR){
            match(TipoToken.VAR);
            match(TipoToken.IDENTIFIER);
            VAR_INIT();
            match(TipoToken.SEMICOLON);
        }
        else{
            hayErrores = true;
            System.out.println("Se esperaba 'var'");
        }
    }
    //VAR_INIT -> = EXPRESSION | /e
    private void VAR_INIT(){
    	if(hayErrores)
            return;

        if(preanalisis.tipo == TipoToken.EQUAL){
            match(TipoToken.EQUAL);
            EXPRESSION();
        }
    }
    // STATEMENT -> EXPR_STMT | FOR_STMT | IF_STMT | PRINT_STMT | RETURN_STMT | WHILE_STMT | BLOCK
    
    private void STATEMENT(){
        if(hayErrores)
            return;

        if(preanalisis.tipo == TipoToken.BANG || 
        	preanalisis.tipo == TipoToken.MINUS || 
        	preanalisis.tipo == TipoToken.TRUE || 
        	preanalisis.tipo == TipoToken.FALSE || 
        	preanalisis.tipo == TipoToken.NULL || 
        	preanalisis.tipo == TipoToken.NUMBER || 
        	preanalisis.tipo == TipoToken.STRING || 
        	preanalisis.tipo == TipoToken.IDENTIFIER || 
        	preanalisis.tipo == TipoToken.LEFT_PAREN){
            EXPR_STMT();
        }
        else if (preanalisis.tipo == TipoToken.FOR) {
            FOR_STMT();
        }
        else if ( preanalisis.tipo == TipoToken.IF ) {
            IF_STMT();
        }
        else if ( preanalisis.tipo == TipoToken.PRINT ) {
            PRINT_STMT();
        }
        else if ( preanalisis.tipo == TipoToken.RETURN ) {
            RETURN_STMT();
        }
        else if ( preanalisis.tipo == TipoToken.WHILE ) {
            WHILE_STMT();
        }
        else if ( preanalisis.tipo == TipoToken.LEFT_BRACE ) {
            BLOCK();
        }
        else{
            hayErrores = true;
            System.out.println("Se esperaba '!' or '-' or 'true' or 'false' or 'null' or 'number' or 'string' or 'id' or '(' or 'for' or 'if' or 'print' or 'return' or 'while' or '{' ");
        }
    }
    //EXPR_STMT -> EXPRESSION ;
    private void EXPR_STMT(){
    	if(hayErrores)
            return;

        if(preanalisis.tipo == TipoToken.BANG || 
        	preanalisis.tipo == TipoToken.MINUS || 
        	preanalisis.tipo == TipoToken.TRUE || 
        	preanalisis.tipo == TipoToken.FALSE || 
        	preanalisis.tipo == TipoToken.NULL || 
        	preanalisis.tipo == TipoToken.NUMBER || 
        	preanalisis.tipo == TipoToken.STRING || 
        	preanalisis.tipo == TipoToken.IDENTIFIER || 
        	preanalisis.tipo == TipoToken.LEFT_PAREN){
            EXPRESSION();
        	match(TipoToken.SEMICOLON);
        }
        else{
            hayErrores = true;
            System.out.println("Se esperaba '!' or '-' or 'true' or 'false' or 'null' or 'number' or 'string' or 'id' or '(' ");
        }
    }
    //FOR_STMT -> for ( FOR_STMT_1 FOR_STMT_2 FOR_STMT_3 ) STATEMENT
    private void FOR_STMT(){
    	if(hayErrores)
            return;

        if(preanalisis.tipo == TipoToken.FOR){
            match(TipoToken.FOR);
            match(TipoToken.LEFT_PAREN);
            FOR_STMT_1();
            FOR_STMT_2(); 
            FOR_STMT_3();
            match(TipoToken.RIGHT_PAREN);
            STATEMENT();
        }
        else{
            hayErrores = true;
            System.out.println("Se esperaba 'for' ");
        }
    }
    //FOR_STMT_1 -> VAR_DECL | EXPR_STMT | ;
    private void FOR_STMT_1(){
    	if(hayErrores)
            return;

        if(preanalisis.tipo == TipoToken.VAR){
            VAR_DECL();
        }
        else if(preanalisis.tipo == TipoToken.BANG || 
        		preanalisis.tipo == TipoToken.MINUS || 
        		preanalisis.tipo == TipoToken.TRUE || 
        		preanalisis.tipo == TipoToken.FALSE || 
        		preanalisis.tipo == TipoToken.NULL || 
        		preanalisis.tipo == TipoToken.NUMBER || 
        		preanalisis.tipo == TipoToken.STRING || 
        		preanalisis.tipo == TipoToken.IDENTIFIER || 
        		preanalisis.tipo == TipoToken.LEFT_PAREN){
            EXPR_STMT();
        }
        else if(preanalisis.tipo == TipoToken.SEMICOLON){
        	match(TipoToken.SEMICOLON);
        }
        else{
            hayErrores = true;
            System.out.println("Se esperaba 'var' or '!' or '-' or 'true' or 'false' or 'null' or 'number' or 'string' or 'id' or '(' or ';' ");
        }
    }
    //FOR_STMT_2 -> EXPRESSION; | ;
    private void FOR_STMT_2(){
    	if(hayErrores)
            return;

        if(preanalisis.tipo == TipoToken.BANG || 
        	preanalisis.tipo == TipoToken.MINUS || 
        	preanalisis.tipo == TipoToken.TRUE || 
        	preanalisis.tipo == TipoToken.FALSE || 
        	preanalisis.tipo == TipoToken.NULL || 
        	preanalisis.tipo == TipoToken.NUMBER || 
        	preanalisis.tipo == TipoToken.STRING || 
        	preanalisis.tipo == TipoToken.IDENTIFIER || 
        	preanalisis.tipo == TipoToken.LEFT_PAREN){
            EXPRESSION();
        	match(TipoToken.SEMICOLON);
        }
        else if(preanalisis.tipo == TipoToken.SEMICOLON){
        	match(TipoToken.SEMICOLON);
        }
        else{
            hayErrores = true;
            System.out.println("Se esperaba '!' or '-' or 'true' or 'false' or 'null' or 'number' or 'string' or 'id' or '(' or ';' ");
        }
    }
    //FOR_STMT_3 -> EXPRESSION
    private void FOR_STMT_3(){
    	if(hayErrores)
            return;

        if(preanalisis.tipo == TipoToken.BANG || 
        	preanalisis.tipo == TipoToken.MINUS || 
        	preanalisis.tipo == TipoToken.TRUE || 
        	preanalisis.tipo == TipoToken.FALSE || 
        	preanalisis.tipo == TipoToken.NULL || 
        	preanalisis.tipo == TipoToken.NUMBER || 
        	preanalisis.tipo == TipoToken.STRING || 
        	preanalisis.tipo == TipoToken.IDENTIFIER || 
        	preanalisis.tipo == TipoToken.LEFT_PAREN){
            EXPRESSION();
        }
    }
    //IF_STMT -> if (EXPRESSION) STATEMENT ELSE_STATEMENT
    private void IF_STMT(){
    	if(hayErrores)
            return;

        if(preanalisis.tipo == TipoToken.IF){
            match(TipoToken.IF);
            match(TipoToken.LEFT_PAREN);
            EXPRESSION();
        	match(TipoToken.RIGHT_PAREN);
        	STATEMENT();
        	ELSE_STATEMENT();
        }
        else{
            hayErrores = true;
            System.out.println("Se esperaba 'if' ");
        }
    }
    //ELSE_STATEMENT -> else STATEMENT | /e
    private void ELSE_STATEMENT(){
    	if(hayErrores)
            return;

        if(preanalisis.tipo == TipoToken.ELSE){
            match(TipoToken.ELSE);
        	STATEMENT();
        }
    }
    //PRINT_STMT -> print EXPRESSION;
    private void PRINT_STMT(){
    	if(hayErrores)
            return;

        if(preanalisis.tipo == TipoToken.PRINT){
            match(TipoToken.PRINT);
            EXPRESSION();
        	match(TipoToken.SEMICOLON);
        }
        else{
            hayErrores = true;
            System.out.println("Se esperaba 'print' ");
        }
    }
    //RETURN_STMT -> return RETURN_STMT_OPC;
    private void RETURN_STMT(){
    	if(hayErrores)
            return;

        if(preanalisis.tipo == TipoToken.RETURN){
            match(TipoToken.RETURN);
            RETURN_STMT_OPC();
        	match(TipoToken.SEMICOLON);
        }
        else{
            hayErrores = true;
            System.out.println("Se esperaba 'return' ");
        }
    }


    private void match(TipoToken tt){
        if(preanalisis.tipo == tt){
            i++;
            preanalisis = tokens.get(i);
        }
        else{
            hayErrores = true;
            System.out.println("Error encontrado");
            //System.out.println("Error encontrado en el token "+ preanalisis.tipo+ " "+tokens.get(i).posicion + " debio ser " + tt);
        }

    }

}
