package main.java.br.edu.analisadorlexico.lexer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Buffer {

    private char[] buffer1;
    private char[] buffer2;
    private static final int tamanhoBuffer = 1024;
    private static final char EOF = '\0';
    private String conteudo;
    private int begin;
    private int forward;
    private int tamanhoArquivo;
    private int inicioBuffer1;
    private int inicioBuffer2;
    private int quantidadeBuffer1;
    private int quantidadeBuffer2;


    public Buffer(String caminhoArquivo) throws IOException {

        Path path = Path.of(caminhoArquivo);
        this.conteudo = Files.readString(path);
        this.buffer1 = new char[tamanhoBuffer];
        this.buffer2 = new char[tamanhoBuffer];
        this.tamanhoArquivo = conteudo.length();
        this.begin = 0;
        this.forward = 0;
        this.inicioBuffer1 = 0;
        this.inicioBuffer2 = 0;
        this.quantidadeBuffer1 = 0;
        this.quantidadeBuffer2 = 0;

        carregarBuffers(0);
    }


    private void carregarBuffers(int inicio) {

        int restante = tamanhoArquivo - inicio;

        if (restante <= 0) {
            quantidadeBuffer1 = 0;
            quantidadeBuffer2 = 0;
            return;
        }

        quantidadeBuffer1 = Math.min(restante, tamanhoBuffer);

        conteudo.getChars(inicio, inicio + quantidadeBuffer1, buffer1, 0);

        inicioBuffer1 = inicio;
        int inicioSegundoBuffer = inicio + quantidadeBuffer1;
        int restanteSegundoBuffer = tamanhoArquivo - inicioSegundoBuffer;

        if (restanteSegundoBuffer > 0) {
            quantidadeBuffer2 = Math.min(restanteSegundoBuffer, tamanhoBuffer);

            conteudo.getChars(inicioSegundoBuffer, inicioSegundoBuffer + quantidadeBuffer2, buffer2, 0);

            inicioBuffer2 = inicioSegundoBuffer;
        } else {
            quantidadeBuffer2 = 0;
            inicioBuffer2 = inicioSegundoBuffer;
        }
    }

    public char nextChar() {

        if (forward >= tamanhoArquivo) {
            return EOF;
        }

        if (forward >= inicioBuffer1 && forward < inicioBuffer1 + quantidadeBuffer1) {
            int posicaoLocal = forward - inicioBuffer1;
            char caractere = buffer1[posicaoLocal];
            forward++;
            return caractere;
        }


        if (forward >= inicioBuffer2 && forward < inicioBuffer2 + quantidadeBuffer2) {
            int posicaoLocal = forward - inicioBuffer2;
            char caractere = buffer2[posicaoLocal];
            forward++;
            return caractere;
        }

        carregarBuffers(forward);

        int posicaoLocal = forward - inicioBuffer1;
        char caractere = buffer1[posicaoLocal];
        forward++;
        return caractere;
    }

    public char peekChar() {
        if (forward >= tamanhoArquivo) {
            return EOF;
        }

        if (forward >= inicioBuffer1 && forward < inicioBuffer1 + quantidadeBuffer1) {
            int posicaoLocal = forward - inicioBuffer1;
            return buffer1[posicaoLocal];
        }

        if (forward >= inicioBuffer2 && forward < inicioBuffer2 + quantidadeBuffer2) {
            int posicaoLocal = forward - inicioBuffer2;
            return buffer2[posicaoLocal];
        }

        carregarBuffers(forward);
        return buffer1[0];
    }

    public void resetBegin() {
        begin = forward;
    }

    public String getLexeme() {

        StringBuilder lexema = new StringBuilder();
        int posicao = begin;

        while (posicao < forward) {
            if (posicao >= inicioBuffer1 && posicao < inicioBuffer1 + quantidadeBuffer1) {
                int posicaoLocal = posicao - inicioBuffer1;

                lexema.append(buffer1[posicaoLocal]);
            } else if (posicao >= inicioBuffer2 && posicao < inicioBuffer2 + quantidadeBuffer2) {
                int posicaoLocal = posicao - inicioBuffer2;

                lexema.append(buffer2[posicaoLocal]);
            } else {
                lexema.append(conteudo.charAt(posicao));
            }
            posicao++;
        }

        return lexema.toString();
    }

    public int getBegin() {
        return begin;
    }

    public int getForward() {
        return forward;
    }

    public boolean isEOF() {
        return forward >= tamanhoArquivo;
    }

    public void backChar() {
        if (forward > 0) {
            forward--;
        }
    }
}
