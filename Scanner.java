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
                        estado = 13;
                        lexema += c;
                    }
                    else if(Character.isDigit(c)){
                        estado = 15;
                        lexema += c;
                    }
                break;

                case 13:
                    if(Character.isLetterOrDigit(c)){
                        estado = 13;
                        lexema += c;
                    }
                    else{
                        TipoToken tt = palabrasReservadas.get(lexema);

                        if(tt == null){
                            Token t = new Token(TipoToken.IDENTIFIER, lexema);
                            tokens.add(t);
                        }
                        else{
                            Token t = new Token(tt, lexema);
                            tokens.add(t);
                        }

                        estado = 0;
                        lexema = "";
                        i--;

                    }
                    break;
                //caso para generar el token de los numeros 
                case 15:
                    //esto va guardando el numero saliendo del caso y volviendo a entrar caracter a caracter
                    //para mantener el formato del profe 
                    if(Character.isDigit(c)){
                        estado = 15;
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
