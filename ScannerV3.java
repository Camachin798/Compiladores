import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Scanner {

    private static final Map<String, TipoToken> palabrasReservadas;

    static {
        palabrasReservadas = new HashMap<>();
        palabrasReservadas.put("and",    TipoToken.AND);
        palabrasReservadas.put("else",   TipoToken.ELSE);
        palabrasReservadas.put("false",  TipoToken.FALSE);
        palabrasReservadas.put("for",    TipoToken.FOR);
        palabrasReservadas.put("fun",    TipoToken.FUN);
        palabrasReservadas.put("if",     TipoToken.IF);
        palabrasReservadas.put("null",   TipoToken.NULL);
        palabrasReservadas.put("or",     TipoToken.OR);
        palabrasReservadas.put("print",  TipoToken.PRINT);
        palabrasReservadas.put("return", TipoToken.RETURN);
        palabrasReservadas.put("true",   TipoToken.TRUE);
        palabrasReservadas.put("var",    TipoToken.VAR);
        palabrasReservadas.put("while",  TipoToken.WHILE);
        //aunque no sean una palabra el caracter no tiene combinacion con otro por lo que 
        //para no tener que incluirlo en el switch se coloca aqui para ahorrar codigo
        palabrasReservadas.put("(",  TipoToken.LEFT_PAREN);
        palabrasReservadas.put(")",  TipoToken.RIGHT_PAREN);
        palabrasReservadas.put("{",  TipoToken.LEFT_BRACE);
        palabrasReservadas.put("}",  TipoToken.RIGHT_BRACE);
        palabrasReservadas.put(",",  TipoToken.COMMA);
        palabrasReservadas.put(".",  TipoToken.DOT);
        palabrasReservadas.put("-",  TipoToken.MINUS);
        palabrasReservadas.put("+",  TipoToken.PLUS);
        palabrasReservadas.put(";",  TipoToken.SEMICOLON);
        palabrasReservadas.put("*",  TipoToken.STAR);

    }

    private final String source;

    private final List<Token> tokens = new ArrayList<>();
    
    public Scanner(String source){
        this.source = source + " ";
    }

    public List<Token> scan() throws Exception {
        String lexema = "";
        String numero = ""; //variables para convertir un numero en texto a un numero en doble  
        String exponente ="";
        String signo = "";
        int estado = 0;
        char c;

        for(int i=0; i<source.length(); i++){
            c = source.charAt(i);

            switch (estado){
                case 0:
                    if(Character.isLetter(c)){
                        estado = 8;
                        lexema += c;
                    }
                    else if(Character.isDigit(c)){
                        estado = 9;
                        lexema += c;
                    }
                    /*a partir de aqui se colocan los caracteres que se van leyendo
                    los cuales es posible que se agrupen con otros por lo que se van a otro 
                    estado para validar cada posibilidad e identificar el tipo de toklen que se 
                    debe generar*/
                    else if(c == '<'){
                        estado = 1;
                        lexema += c;
                    }
                    else if(c == '='){
                        estado = 2;
                        lexema += c;
                    }
                    else if(c == '>'){
                        estado = 3;
                        lexema += c;
                    }
                    else if(c == '!'){
                        estado = 4;
                        lexema += c;
                    }
                    else if(c == '/'){
                        estado = 5;
                        lexema += c;
                    }
                    else if(c == '"'){
                        estado = 6;
                        lexema += c;
                    }
                    //caso para generar los tokens de un caracter 
                    //donde se desechan los caracteres no contemplados
                    else{
                        estado = 7;
                        lexema += c;
                    }
                break;
                case 1:
                    if( c == '='){
                        estado = 1;
                        lexema += c; 
                        Token t = new Token(TipoToken.LESS_EQUAL, lexema);
                        tokens.add(t);
                
                        estado = 0;//luego de insertar un token se regresa al estado incial de lectura 
                        lexema = "";//y se vacia la cadena para analizar el nuevo token
                    }
                    else{
                        Token t = new Token(TipoToken.LESS, lexema);
                        tokens.add(t);
                        
                        i--; //cada i-- es donde se leyo un caracter adicional que no pertenece 
                             //al token generado para regresar una posicion en la lectura
                        lexema = "";
                        estado = 0;
                    } 

                    break;
                //mismo proceso hasta el estado 5
                case 2:
                    if( c == '='){
                        
                        estado = 2;
                        lexema += c;
                        
                        Token t = new Token(TipoToken.EQUAL_EQUAL, lexema);
                        tokens.add(t);
                
                        estado = 0;
                        lexema = "";
                    }
                    else{
                        Token t = new Token(TipoToken.EQUAL, lexema);
                        tokens.add(t);
                        
                        i--;
                        lexema = "";
                        estado = 0;
                    } 

                    break;
                case 3:
                    if( c == '='){
                        
                        estado = 3;
                        lexema += c;
                        
                        Token t = new Token(TipoToken.GREATER_EQUAL, lexema);
                        tokens.add(t);
                
                        estado = 0;
                        lexema = "";
                    }
                    else{
                        Token t = new Token(TipoToken.GREATER, lexema);
                        tokens.add(t);
                        
                        i--;
                        lexema = "";
                        estado = 0;
                    } 

                    break;
                case 4:
                    if( c == '='){
                        
                        estado = 4;
                        lexema += c;
                        
                        Token t = new Token(TipoToken.BANG_EQUAL, lexema);
                        tokens.add(t);
                
                        estado = 0;
                        lexema = "";
                    }
                    else{
                        Token t = new Token(TipoToken.BANG, lexema);
                        tokens.add(t);
                        
                        i--;
                        lexema = "";
                        estado = 0;
                    } 

                    break;
                //estado para comentarios
                case 5:
                    //comentarios de linea
                    if( c == '/'){
                        
                        estado = 5;
                        lexema += c;

                        //si se quiere guardar un token de comentario:

                        //Token t = new Token(TipoToken.LINE_COMMENT, lexema);
                        //tokens.add(t);
                        //lexema = "";

                        //esto va guardando el mensaje del comentario hasta un salto
                        //de linea o hasta que se termine el archivo
                        i++;
                        c = source.charAt(i);
                        while(c != (char)10 && i < source.length()-1 ){
                            lexema += c;
                            i++;
                            c = source.charAt(i);
                        }

                        //Token t1 = new Token(TipoToken.COMMENT, lexema);
                        //tokens.add(t1);
                        lexema = "";
                        estado = 0;
                    }
                    //comentarios multilinea                  
                    else if( c == '*'){
                        estado = 5;
                        lexema += c;

                        //Token t = new Token(TipoToken.MULTILINE_COMMENT, lexema);
                        //tokens.add(t);
                        //lexema = "";

                        i++;
                        c = source.charAt(i);

                        if(i+1==source.length()){
                    		break;
                    	}
                        //se realiza en un do while ya que el buble se detiene encontrando "*/"
                        //sin embargo un ciclo while normal hacia que la cadena "/*/" abriera y terminara el comentario
                        do{
                            lexema += c;
                            i++;
                            c = source.charAt(i);
                        }while(source.charAt(i-1) != '*' && source.charAt(i) != '/' && i < source.length()-1 );//hastq eu se ecnuentre "*/" o termine el archivo
                        //Token t2 = new Token(TipoToken.COMMENT, lexema.substring(0,lexema.length()-1) );
                        //tokens.add(t2);
                        lexema = "";
                        estado = 0;
                        
                    }
                    else{
                        Token t = new Token(TipoToken.SLASH, lexema);
                        tokens.add(t);
                        
                        i--;
                        lexema = "";
                        estado = 0;
                    }

                    break;
                //codigo para las cadenas que deben empezar con comillas genera token invalido si no se cierran
                case 6:
                    estado = 6;

                    i++;
                    lexema += c;

                    if(i==source.length()){
                    	break;
                    }

                    c = source.charAt(i);
                    while( c != (char)10 && c != '"' && i < source.length()-1 ){//hasta un salto de linea, encontrar " o fin de archivo
                        lexema += c;
                        i++;
                        c = source.charAt(i);
                    }
                    if(source.charAt(i) == '"'){//para saber con que caso termino el bucle
                        lexema += c;
                        Token t = new Token(TipoToken.STRING, lexema,lexema.substring(1,lexema.length()-1));
                        tokens.add(t);
                        lexema = "";
                        estado = 0;

                    }
                    else{
                        //si no se tiene que utilizar token invalido se comentar las siguientes 2 lineas
                        //con esto las cadenas sin cerrar " desaparecen
                        //Token t = new Token(TipoToken.INVALID, lexema);
                        //tokens.add(t);
                        i--;
                        estado = 0;
                        lexema = "";
                    }                 

                    break;
                //caso para generar los tokens de un caracter 
                //donde se desechan los caracteres no contemplados
                case 7:
                    TipoToken tt11 = palabrasReservadas.get(lexema);
                    estado = 7;

                    if(tt11 != null){
                        Token t11 = new Token(tt11, lexema);
                        tokens.add(t11);
                    }

                    lexema = "";
                    i--;
                    estado = 0;

                break;
                //caso para generar el token de identificadores
                case 8:
                    if(Character.isLetter(c) || Character.isDigit(c)){
                        estado = 8;
                        lexema += c;
                    }
                    else{
                        TipoToken tt1 = palabrasReservadas.get(lexema);

                        if(tt1 == null){
                            Token t4 = new Token(TipoToken.IDENTIFIER, lexema);
                            tokens.add(t4);
                        }
                        else{
                            Token t5 = new Token(tt1, lexema);
                            tokens.add(t5);
                        }

                        i--;
                        lexema = "";
                        estado = 0;
                    }
                    break;
                //caso para generar el token de los numeros 
                case 9:
                    //esto va guardando el numero saliendo del caso y volviendo a entrar caracter a caracter
                    //para mantener el formato del profe 
                    if(Character.isDigit(c)){
                        estado = 9;
                        lexema += c;
                    }
                    //esta parte considera que ya se leyo toda la parte entera y empieza la decimal
                    else if(c == '.'){
                        lexema += c;

                        i++;
                        c = source.charAt(i);
                        while(i < source.length()-1 && Character.isDigit(c) ){//esto lee toda la parte decimal
                            lexema += c;
                            i++;
                            c = source.charAt(i);
                        }
                        numero = lexema;//se guarda el numero como flotante a modo de cadena
                        if(source.charAt(i) == 'E'){//empieza la parte exponencial 
                            lexema += c; 
                            i++;
                            c = source.charAt(i);
                            if(Character.isDigit(c)){//identifica si el exponente tiene signo o no
                                lexema += c;
                                exponente += c;
                                i++;
                                c = source.charAt(i);
                                while(i < source.length()-1 && Character.isDigit(c) ){//mientras se lea un digito y no termine el archivo
                                    lexema += c;
                                    exponente += c;
                                    i++;
                                    c = source.charAt(i);
                                }
                            }else if (c == '+' || c == '-'){
                                lexema += c;
                                signo += c;
                                i++;
                                c = source.charAt(i);
                                while(i < source.length()-1 && Character.isDigit(c) ){
                                    lexema += c;
                                    exponente += c;
                                    i++;
                                    c = source.charAt(i);
                                }
                            }
                            //genera el token de numero y convierte el texto a numero
                            //problema para manejar exponentes grandes 
                            Token t69 = new Token(TipoToken.NUMBER, lexema,Float.parseFloat(numero)* Math.pow(10,Float.parseFloat(signo+exponente)) );
                            tokens.add(t69);
                            estado=0;
                            exponente = "";
                            numero= "";
                            lexema ="";
                            signo ="";
                            i--;
                        }
                        else{//caso para manejar numero con punto decimal sin exponente
                            Token t6 = new Token(TipoToken.NUMBER, lexema,Float.parseFloat(lexema+"0") );//se agrega el 0 para considerar los casos donde se recibe "numero."
                            tokens.add(t6);
                            estado=0;
                            exponente = "";
                            numero= "";
                            lexema ="";
                            signo ="";
                            i--;
                        }
                    }
                    else if(c == 'E'){//caso para manejar numero entero con exponente
                        numero = lexema;
                        lexema += c; 
                        i++;
                        c = source.charAt(i);
                        if(Character.isDigit(c)){
                                lexema += c;
                                exponente += c;
                                i++;
                                c = source.charAt(i);
                                while(i < source.length()-1 && Character.isDigit(c) ){
                                    lexema += c;
                                    exponente += c;
                                    i++;
                                    c = source.charAt(i);
                                }
                            }else if (c == '+' || c == '-'){
                                lexema += c;
                                signo += c;
                                i++;
                                c = source.charAt(i);
                                while(i < source.length()-1 && Character.isDigit(c) ){
                                    lexema += c;
                                    exponente += c;
                                    i++;
                                    c = source.charAt(i);
                                }
                            }
                        Token t69 = new Token(TipoToken.NUMBER, lexema, Integer.valueOf(numero)* Math.pow(10,Float.parseFloat(signo+exponente)));
                        tokens.add(t69);
                        estado=0;
                        exponente = "";
                        numero= "";
                        lexema ="";
                        signo ="";
                        i--;
                    }
                    else{//caso para manejar numero enteros sin exponente
                        Token t7 = new Token(TipoToken.NUMBER, lexema, Integer.valueOf(lexema));
                        tokens.add(t7);

                        estado=0;
                        exponente = "";
                        numero= "";
                        lexema ="";
                        signo ="";
                        i--;
                    }
                    break;
            }
        }

        return tokens;
    }
}
