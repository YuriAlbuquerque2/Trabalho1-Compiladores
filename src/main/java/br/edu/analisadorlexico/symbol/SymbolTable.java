package main.java.br.edu.analisadorlexico.symbol;

import java.util.LinkedHashMap;
import java.util.Map;

public class SymbolTable {

    private final Map<String, Integer> simbolos;

    public SymbolTable() {
        simbolos = new LinkedHashMap<>();
    }

    public void adicionar(String identificador) {

        if (simbolos.containsKey(identificador)) {
            int quantidade = simbolos.get(identificador);
            simbolos.put(identificador, quantidade + 1);
        } else {
            simbolos.put(identificador, 1);
        }
    }

    public Map<String, Integer> getSimbolos() {
        return simbolos;
    }

    public int getOcorrencias(String identificador) {
        if (simbolos.containsKey(identificador)) {
            return simbolos.get(identificador);
        }

        return 0;
    }

    public void imprimir() {

        System.out.println("\n========== TABELA DE SÍMBOLOS ==========");
        System.out.printf("%-25s %-15s%n", "IDENTIFICADOR", "OCORRÊNCIAS");
        System.out.println("-----------------------------------------");

        for (Map.Entry<String, Integer> entrada : simbolos.entrySet()) {
            System.out.printf("%-25s %-15d%n", entrada.getKey(), entrada.getValue());
        }
    }
}