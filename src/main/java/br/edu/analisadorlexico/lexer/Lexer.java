package main.java.br.edu.analisadorlexico.lexer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import main.java.br.edu.analisadorlexico.symbol.SymbolTable;

public class Lexer {

    private final Buffer buffer;
    private final Set<String> palavrasChave;
    private final SymbolTable tabelaSimbolos;

    public Lexer(String caminhoArquivo) throws IOException {
        this.buffer = new Buffer(caminhoArquivo);
        this.tabelaSimbolos = new SymbolTable();
        this.palavrasChave = new HashSet<>(Arrays.asList(
                "abstract",
                "assert",
                "boolean",
                "break",
                "byte",
                "case",
                "catch",
                "char",
                "class",
                "const",
                "continue",
                "default",
                "do",
                "double",
                "else",
                "enum",
                "extends",
                "final",
                "finally",
                "float",
                "for",
                "if",
                "implements",
                "import",
                "instanceof",
                "int",
                "interface",
                "long",
                "native",
                "new",
                "package",
                "private",
                "protected",
                "public",
                "return",
                "short",
                "static",
                "strictfp",
                "super",
                "switch",
                "synchronized",
                "this",
                "throw",
                "throws",
                "transient",
                "try",
                "void",
                "volatile",
                "while",
                "true",
                "false",
                "null"
        ));
    }

    public Token nextToken() {
        ignorarEspacosEComentarios();

        if (buffer.isEOF()) {
            return null;
        }

        buffer.resetBegin();
        char caractere = buffer.peekChar();

        if (Character.isLetter(caractere) || caractere == '_') {
            return reconhecerIdentificadorOuPalavraChave();
        }

        if (Character.isDigit(caractere)) {
            return reconhecerNumero();
        }

        if (caractere == '"') {
            return reconhecerString();
        }

        if (caractere == '\'') {
            return reconhecerChar();
        }

        if (ehDelimitador(caractere)) {
            return reconhecerDelimitador();
        }

        if (ehOperador(caractere)) {
            return reconhecerOperador();
        }

        buffer.nextChar();

        return new Token(
                TokenType.ERROR,
                buffer.getLexeme(),
                "Caractere inválido"
        );
    }

    public List<Token> analisar() {
        List<Token> tokens = new ArrayList<>();
        Token token;

        while ((token = nextToken()) != null) {
            tokens.add(token);
        }

        return tokens;
    }

    private void ignorarEspacosEComentarios() {
        boolean encontrouAlgo;

        do {
            encontrouAlgo = false;

            while (!buffer.isEOF() && Character.isWhitespace(buffer.peekChar())) {
                buffer.nextChar();

                encontrouAlgo = true;
            }

            if (!buffer.isEOF() && buffer.peekChar() == '/') {
                buffer.nextChar();

                if (!buffer.isEOF() && buffer.peekChar() == '/') {
                    encontrouAlgo = true;

                    buffer.nextChar();

                    while (!buffer.isEOF() && buffer.peekChar() != '\n' && buffer.peekChar() != '\r') {
                        buffer.nextChar();
                    }

                } else if (!buffer.isEOF() && buffer.peekChar() == '*') {
                    encontrouAlgo = true;
                    buffer.nextChar();
                    boolean fechouComentario = false;

                    while (!buffer.isEOF()) {
                        char atual = buffer.nextChar();

                        if (atual == '*' && !buffer.isEOF() && buffer.peekChar() == '/') {
                            buffer.nextChar();
                            fechouComentario = true;

                            break;
                        }
                    }

                    if (!fechouComentario) {
                        return;
                    }
                } else {
                    buffer.backChar();
                }
            }

        } while (encontrouAlgo && !buffer.isEOF());
    }

    private Token reconhecerIdentificadorOuPalavraChave() {

        while (!buffer.isEOF()) {
            char caractere = buffer.peekChar();

            if (Character.isLetterOrDigit(caractere) || caractere == '_') {

                buffer.nextChar();
            } else {
                break;
            }
        }

        String lexema = buffer.getLexeme();

        if (palavrasChave.contains(lexema)) {
            return new Token(TokenType.KEYWORD, lexema, null);
        }

        tabelaSimbolos.adicionar(lexema);
        return new Token(TokenType.IDENTIFIER, lexema, null);
    }

    private Token reconhecerNumero() {
        boolean possuiPonto = false;
        boolean possuiErro = false;

        while (!buffer.isEOF() && Character.isDigit(buffer.peekChar())) {

            buffer.nextChar();
        }

        if (!buffer.isEOF() && (Character.isLetter(buffer.peekChar()) || buffer.peekChar() == '_')) {
            possuiErro = true;

            while (!buffer.isEOF()) {
                char caractere = buffer.peekChar();

                if (Character.isLetterOrDigit(caractere) || caractere == '_') {
                    buffer.nextChar();
                } else {
                    break;
                }
            }
        }

        if (!buffer.isEOF() && buffer.peekChar() == ',') {
            possuiErro = true;
            buffer.nextChar();

            while (!buffer.isEOF() && Character.isDigit(buffer.peekChar())) {

                buffer.nextChar();
            }
        }

        if (!possuiErro && !buffer.isEOF() && buffer.peekChar() == '.') {
            possuiPonto = true;
            buffer.nextChar();

            if (buffer.isEOF() || !Character.isDigit(buffer.peekChar())) {
                possuiErro = true;
            } else {
                while (!buffer.isEOF() && Character.isDigit(buffer.peekChar())) {

                    buffer.nextChar();
                }
            }
        }

        if (!buffer.isEOF() && buffer.peekChar() == '.') {
            possuiErro = true;
            buffer.nextChar();

            while (!buffer.isEOF() && Character.isDigit(buffer.peekChar())) {

                buffer.nextChar();
            }
        }

        String lexema = buffer.getLexeme();

        if (possuiErro) {
            return new Token(TokenType.ERROR, lexema, "Número inválido");
        }


        if (possuiPonto) {
            return new Token(TokenType.FLOAT, lexema, null);
        }

        return new Token(TokenType.INTEGER, lexema, null);
    }


    private Token reconhecerString() {

        buffer.nextChar();
        boolean fechouString = false;

        while (!buffer.isEOF()) {
            char caractere = buffer.peekChar();

            if (caractere == '\n' || caractere == '\r') {
                break;
            }

            caractere = buffer.nextChar();

            if (caractere == '\\') {
                if (!buffer.isEOF() && buffer.peekChar() != '\n' && buffer.peekChar() != '\r') {
                    buffer.nextChar();
                }

                continue;
            }

            if (caractere == '"') {
                fechouString = true;
                break;
            }
        }

        String lexema = buffer.getLexeme();

        if (!fechouString) {
            return new Token(TokenType.ERROR, lexema, "String não terminada");
        }

        return new Token(TokenType.STRING, lexema, null);
    }

    private Token reconhecerChar() {
        buffer.nextChar();
        boolean fechouChar = false;

        if (!buffer.isEOF()) {
            char caractere = buffer.nextChar();

            if (caractere == '\\') {
                if (!buffer.isEOF()) {
                    buffer.nextChar();
                }
            }

            if (!buffer.isEOF() && buffer.peekChar() == '\'') {
                buffer.nextChar();

                fechouChar = true;
            }
        }

        String lexema = buffer.getLexeme();

        if (!fechouChar) {
            while (!buffer.isEOF() && buffer.peekChar() != '\n' && buffer.peekChar() != '\r') {
                if (buffer.nextChar() == '\'') {
                    break;
                }
            }

            return new Token(TokenType.ERROR, buffer.getLexeme(), "Char inválido ou não terminado");
        }

        return new Token(TokenType.CHAR, lexema, null);
    }

    private Token reconhecerDelimitador() {
        char caractere = buffer.nextChar();

        return new Token(TokenType.DELIMITER, String.valueOf(caractere), null);
    }

    private Token reconhecerOperador() {
        char primeiro = buffer.nextChar();

        if (buffer.isEOF()) {
            return criarOperadorSimples(primeiro);
        }

        char segundo = buffer.peekChar();

        if ((primeiro == '=' && segundo == '=')
                || (primeiro == '!' && segundo == '=')
                || (primeiro == '<' && segundo == '=')
                || (primeiro == '>' && segundo == '=')
                || (primeiro == '&' && segundo == '&')
                || (primeiro == '|' && segundo == '|')
                || (primeiro == '+' && segundo == '=')
                || (primeiro == '-' && segundo == '=')
                || (primeiro == '*' && segundo == '=')
                || (primeiro == '/' && segundo == '=')
                || (primeiro == '%' && segundo == '=')
                || (primeiro == '+' && segundo == '+')
                || (primeiro == '-' && segundo == '-')
                || (primeiro == '&' && segundo == '=')
                || (primeiro == '|' && segundo == '=')
                || (primeiro == '^' && segundo == '=')) {

            buffer.nextChar();

            return new Token(TokenType.OPERATOR, buffer.getLexeme(), null
            );
        }

        return criarOperadorSimples(primeiro);
    }

    private Token criarOperadorSimples(char caractere) {

        if (caractere == '&' || caractere == '|' || caractere == '^') {
            return new Token(TokenType.ERROR, buffer.getLexeme(), "Operador inválido");
        }

        return new Token(TokenType.OPERATOR, String.valueOf(caractere), null);
    }

    private boolean ehDelimitador(char caractere) {
        return caractere == ';'
                || caractere == ','
                || caractere == '.'
                || caractere == '('
                || caractere == ')'
                || caractere == '{'
                || caractere == '}'
                || caractere == '['
                || caractere == ']';
    }

    private boolean ehOperador(char caractere) {
        return caractere == '+'
                || caractere == '-'
                || caractere == '*'
                || caractere == '/'
                || caractere == '%'
                || caractere == '='
                || caractere == '!'
                || caractere == '<'
                || caractere == '>'
                || caractere == '&'
                || caractere == '|'
                || caractere == '^';
    }

    public SymbolTable getTabelaSimbolos() {

        return tabelaSimbolos;
    }
}