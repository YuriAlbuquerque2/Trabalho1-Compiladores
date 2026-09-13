package main.java.br.edu.analisadorlexico.lexer;

public class Token {
    private TokenType tipo;
    private String lexema;
    private String atributo;

    public Token(TokenType tipo, String lexema, String atributo){
        this.tipo = tipo;
        this.lexema = lexema;
        this.atributo = atributo;
    }

    public TokenType getTipo() {
        return tipo;
    }

    public String getLexema() {
        return lexema;
    }

    public String getAtributo() {
        return atributo;
    }

    @Override
    public String toString() {
        return "Token{" + "tipo=" + tipo + ", lexema='" + lexema + '\'' + ", atributo='" + atributo + '\'' + '}';
    }
}
