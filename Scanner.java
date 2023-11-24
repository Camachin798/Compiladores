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
    
    Scanner(String source){
        this.source = source + " ";
    }

    List<Token> scan(){
        String lexema = "";
        String numero = ""; //variables para convertir un numero en texto a un numero en doble  
        String exponente ="";
        String signo = "";
        int estado = 0;
        int contador = 1;
        char c;

        for(int i=0; i<source.length(); i++){
            c = source.charAt(i);

            switch (estado){
                case 0:
                    if(Character.isLetter(c)){ estado = 8; lexema += c; }
                    else if(Character.isDigit(c)){ estado = 9; lexema += c; }
                    /*a partir de aqui se colocan los caracteres que se van leyendo
                    los cuales es posible que se agrupen con otros por lo que se van a otro 
                    estado para validar cada posibilidad e identificar el tipo de toklen que se 
                    debe generar*/
                    else if(c == '<'){ estado = 1; lexema += c; }
                    else if(c == '='){ estado = 2; lexema += c; }
                    else if(c == '>'){ estado = 3; lexema += c; }
                    else if(c == '!'){ estado = 4; lexema += c; }
                    else if(c == '/'){ estado = 5; lexema += c; }
                    else if(c == '"'){ estado =  6; lexema += c; }
                    //caso para generar los tokens de un caracter 
                    //donde se desechan los caracteres no contemplados
                    else{ estado = 7; lexema += c; }
                break;
                case 1:
                    if( c == '='){
                        estado = 1; lexema += c;
                        tokens.add(new Token(TipoToken.LESS_EQUAL, lexema, contador )); contador++;

                        estado = 0;//luego de insertar un token se regresa al estado incial de lectura 
                        lexema = "";//y se vacia la cadena para analizar el nuevo token
                    }
                    else{
                        tokens.add(new Token(TipoToken.LESS, lexema, contador )); contador++; 
                        i--; //cada i-- es donde se leyo un caracter adicional que no pertenece 
                             //al token generado para regresar una posicion en la lectura
                        lexema = ""; estado = 0;
                    } 

                    break;
                //mismo proceso hasta el estado 5
                case 2:
                    if( c == '='){
                        estado = 2; lexema += c;
                        tokens.add( new Token(TipoToken.EQUAL_EQUAL, lexema, contador)); contador++;
                        estado = 0; lexema = "";
                    }
                    else{
                        tokens.add( new Token(TipoToken.EQUAL, lexema, contador)); contador++;
                        i--; lexema = ""; estado = 0;
                    } 

                    break;
                case 3:
                    if( c == '='){
                        estado = 3; lexema += c;
                        tokens.add( new Token(TipoToken.GREATER_EQUAL, lexema, contador)); contador++;
                        estado = 0; lexema = "";
                    }
                    else{
                        tokens.add( new Token(TipoToken.GREATER, lexema, contador)); contador++;
                        i--; lexema = ""; estado = 0;
                    } 

                    break;
                case 4:
                    if( c == '='){
                        estado = 4; lexema += c;
                        tokens.add( new Token(TipoToken.BANG_EQUAL, lexema, contador)); contador++;
                        estado = 0; lexema = "";
                    }
                    else{
                        tokens.add( new Token(TipoToken.BANG, lexema, contador));contador++;
                        i--; lexema = ""; estado = 0;
                    } 

                    break;
                //estado para comentarios
                case 5:
                    //comentarios de linea
                    if( c == '/'){
                        
                        estado = 5; lexema += c;
                        //esto va guardando el mensaje del comentario hasta un salto
                        //de linea o hasta que se termine el archivo
                        i++; c = source.charAt(i);
                        while(c != (char)10 && i < source.length()-1 ){
                            lexema += c; i++; c = source.charAt(i);
                        }
                        lexema = ""; estado = 0;
                    }
                    //comentarios multilinea                  
                    else if( c == '*'){
                        estado = 5; lexema += c;
                        i++; c = source.charAt(i);
                        if(i+1==source.length()){
                    		break;
                    	}
                        //se realiza en un do while ya que el buble se detiene encontrando "*/"
                        //sin embargo un ciclo while normal hacia que la cadena "/*/" abriera y terminara el comentario
                        do{
                            lexema += c; i++; c = source.charAt(i);
                        }while(source.charAt(i-1) != '*' && source.charAt(i) != '/' && i < source.length()-1 );//hastq eu se ecnuentre "*/" o termine el archivo
                        lexema = ""; estado = 0;
                    }
                    else{
                        tokens.add( new Token(TipoToken.SLASH, lexema, contador)); contador++;
                        i--; lexema = ""; estado = 0;
                    }

                    break;
                //codigo para las cadenas que deben empezar con comillas imprime mensaje de error si no se cierran
                case 6:
                    estado = 6; i++; lexema += c;
                    if(i==source.length()){
                        System.out.println("Error encontrado falto cerrar las comillas");
                    	break;
                    }
                    c = source.charAt(i);
                    while( c != (char)10 && c != '"' && i < source.length()-1 ){//hasta un salto de linea, encontrar " o fin de archivo
                        lexema += c; i++; c = source.charAt(i);
                    }
                    if(source.charAt(i) == '"'){//para saber con que caso termino el bucle
                        lexema += c;
                        tokens.add( new Token(TipoToken.STRING, lexema, contador, lexema.substring(1,lexema.length()-1))); contador++;
                        lexema = "";estado = 0;
                    }
                    else{
                        //no se genera el token
                        //aqui se puede mandar un mensaje de error por no cerrar las comillas 
                        System.out.println("Error encontrado falto cerrar las comillas");
                        i--; estado = 0; lexema = "";
                    }                 
                    break;
                //caso para generar los tokens de un caracter 
                //donde se desechan los caracteres no contemplados
                case 7:
                    TipoToken tt11 = palabrasReservadas.get(lexema); estado = 7;
                    if(tt11 != null){
                        tokens.add( new Token(tt11, lexema, contador)); contador++;
                    }
                    lexema = ""; i--; estado = 0;
                break;
                //caso para generar el token de identificadores
                case 8:
                    if(Character.isLetter(c) || Character.isDigit(c)){
                        estado = 8; lexema += c;
                    }
                    else{
                        TipoToken tt1 = palabrasReservadas.get(lexema);

                        if(tt1 == null){
                            tokens.add( new Token(TipoToken.IDENTIFIER, lexema, contador)); contador++;
                        }
                        else{
                            tokens.add( new Token(tt1, lexema, contador)); contador++;
                        }
                        i--; lexema = ""; estado = 0;
                    }
                    break;
                //caso para generar el token de los numeros 
                case 9:
                    //esto va guardando el numero saliendo del caso y volviendo a entrar caracter a caracter
                    //para mantener el formato del profe 
                    if(Character.isDigit(c)){
                        estado = 9; lexema += c;
                    }
                    //esta parte considera que ya se leyo toda la parte entera y empieza la decimal
                    else if(c == '.'){
                        lexema += c; i++; c = source.charAt(i);
                        if(!Character.isDigit(c)){//if para evitar "'numero''.'"
                            System.out.println("Error encontrado se esperaba la parte decimal del numero");
                            estado=0; exponente = ""; numero= ""; lexema =""; signo =""; i--;
                            break;
                        }
                        while(i < source.length()-1 && Character.isDigit(c) ){//esto lee toda la parte decimal
                            lexema += c; i++; c = source.charAt(i);
                        }
                        numero = lexema;//se guarda el numero como flotante a modo de cadena
                        if(source.charAt(i) == 'E'){//empieza la parte exponencial 
                            lexema += c; i++; c = source.charAt(i);
                            if(Character.isDigit(c)){//identifica si el exponente tiene signo o no
                                lexema += c; exponente += c; i++; c = source.charAt(i);
                                while(i < source.length()-1 && Character.isDigit(c) ){//mientras se lea un digito y no termine el archivo
                                    lexema += c; exponente += c; i++; c = source.charAt(i);
                                }
                            }else if (c == '+' || c == '-'){
                                lexema += c; signo += c; i++; c = source.charAt(i);
                                while(i < source.length()-1 && Character.isDigit(c) ){
                                    lexema += c; exponente += c; i++; c = source.charAt(i);
                                }
                            }
                            //genera el token de numero y convierte el texto a numero
                            //problema para manejar exponentes grandes 
                            tokens.add( new Token(TipoToken.NUMBER, lexema, contador, Float.parseFloat(numero)* Math.pow(10,Float.parseFloat(signo+exponente)) )); contador++;
                            estado=0; exponente = ""; numero= ""; lexema =""; signo =""; i--;
                        }
                        else{//caso para manejar numero con punto decimal sin exponente
                            
                            tokens.add( new Token(TipoToken.NUMBER, lexema, contador, Float.parseFloat(lexema) )); contador++;
                            estado=0; exponente = ""; numero= ""; lexema =""; signo =""; i--;
                        }
                    }
                    else if(c == 'E'){//caso para manejar numero entero con exponente
                        numero = lexema; lexema += c; i++; c = source.charAt(i);
                        if(Character.isDigit(c)){
                                lexema += c; exponente += c; i++; c = source.charAt(i);
                                while(i < source.length()-1 && Character.isDigit(c) ){
                                    lexema += c; exponente += c; i++; c = source.charAt(i);
                                }
                            }else if (c == '+' || c == '-'){
                                lexema += c; signo += c; i++; c = source.charAt(i);
                                while(i < source.length()-1 && Character.isDigit(c) ){
                                    lexema += c; exponente += c; i++; c = source.charAt(i);
                                }
                            }
                        tokens.add( new Token(TipoToken.NUMBER, lexema, contador, Integer.valueOf(numero)* Math.pow(10,Float.parseFloat(signo+exponente)))); contador++;
                        estado=0; exponente = ""; numero= ""; lexema =""; signo =""; i--;
                    }
                    else{//caso para manejar numero enteros sin exponente
                        tokens.add( new Token(TipoToken.NUMBER, lexema, contador, Integer.valueOf(lexema))); contador++;
                        estado=0; exponente = ""; numero= ""; lexema =""; signo =""; i--;
                    }
                    break;
            }
        }
        tokens.add(new Token(TipoToken.EOF, "", source.length()));
        return tokens;
    }
}
