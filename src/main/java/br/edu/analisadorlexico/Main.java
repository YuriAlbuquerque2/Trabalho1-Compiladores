package main.java.br.edu.analisadorlexico;

import main.java.br.edu.analisadorlexico.lexer.Lexer;
import main.java.br.edu.analisadorlexico.lexer.Token;
import main.java.br.edu.analisadorlexico.lexer.TokenType;
import main.java.br.edu.analisadorlexico.lexer.Buffer;

import java.io.IOException;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException {

        Lexer lexer = new Lexer("arquivos-teste/Erro_identificador.java");

        List<Token> tokens = lexer.analisar();

        System.out.println();
        System.out.println("==============================================================");
        System.out.println("                    ANALISADOR LÉXICO");
        System.out.println("==============================================================");
        System.out.println("Arquivo analisado: " + "arquivos-teste/Teste.java");
        System.out.println("Quantidade de tokens: " + tokens.size());

        imprimirTabelaTokens(tokens);

        lexer.getTabelaSimbolos().imprimir();

        imprimirResumo(tokens);
    }


    private static void imprimirTabelaTokens(List<Token> tokens) {

        System.out.println();
        System.out.println("========================= TABELA DE TOKENS =========================");

        System.out.printf(
                "%-15s %-30s %-25s%n",
                "TIPO",
                "LEXEMA",
                "ATRIBUTO"
        );

        System.out.println(
                "-------------------------------------------------------------------"
        );


        for (Token token : tokens) {

            String lexema = token.getLexema();
            String atributo = token.getAtributo();


            if (atributo == null) {
                atributo = "-";
            }


            System.out.printf(
                    "%-15s %-30s %-25s%n",
                    token.getTipo(),
                    lexema,
                    atributo
            );
        }
    }


    private static void imprimirResumo(List<Token> tokens) {

        int erros = 0;

        int identificadores = 0;
        int palavrasChave = 0;
        int inteiros = 0;
        int floats = 0;
        int chars = 0;
        int strings = 0;
        int operadores = 0;
        int delimitadores = 0;


        for (Token token : tokens) {

            TokenType tipo = token.getTipo();


            switch (tipo) {

                case ERROR:
                    erros++;
                    break;

                case IDENTIFIER:
                    identificadores++;
                    break;

                case KEYWORD:
                    palavrasChave++;
                    break;

                case INTEGER:
                    inteiros++;
                    break;

                case FLOAT:
                    floats++;
                    break;

                case CHAR:
                    chars++;
                    break;

                case STRING:
                    strings++;
                    break;

                case OPERATOR:
                    operadores++;
                    break;

                case DELIMITER:
                    delimitadores++;
                    break;
            }
        }


        System.out.println();
        System.out.println("=========================== RESUMO ===========================");

        System.out.println("Identificadores : " + identificadores);
        System.out.println("Palavras-chave  : " + palavrasChave);
        System.out.println("Inteiros        : " + inteiros);
        System.out.println("Floats          : " + floats);
        System.out.println("Chars           : " + chars);
        System.out.println("Strings         : " + strings);
        System.out.println("Operadores      : " + operadores);
        System.out.println("Delimitadores   : " + delimitadores);
        System.out.println("Erros léxicos   : " + erros);

        System.out.println("==============================================================");
    }
}
