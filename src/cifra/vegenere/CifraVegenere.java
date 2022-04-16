package cifra.vegenere;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CifraVegenere {

    private static final String NOME_ARQUIVO_ENTRADA = "textoIngles.txt";
    private static final String NOME_TEXTO_SAIDA = "textoDescriptografado.txt";
    private static final Integer tamanhoDeCaracteresQueAchaveDeveConter = 25;
    private static final Integer tamanhoDeslocamentoParaDescobrirAchave = 2;
    private static final Integer tamanhoTotalDoAlfabeto = 26;

    public static void main(String[] args) throws IOException {
        String texto = fazAleituraDotexto();
        int tamanhoChave = encontrarTamanhoDaChaveParaCadaTexto(texto);
        ArrayList<String> listaChave = encontrarChave(tamanhoChave, texto);
        StringBuilder textoDescriptografado = realizaAdescriptografiaDoTexto(listaChave, tamanhoChave, texto);
        imprimirNoArquivoTextoDescriptografado(textoDescriptografado);
    }

    private static String fazAleituraDotexto() throws IOException {
        FileInputStream stream = new FileInputStream(NOME_ARQUIVO_ENTRADA);
        InputStreamReader reader = new InputStreamReader(stream);
        BufferedReader br = new BufferedReader(reader);
        return br.readLine();
    }

    private static int encontrarTamanhoDaChaveParaCadaTexto(String texto) {
        ArrayList<Double> listaIndiceCoincidencia = new ArrayList<>();
        listaIndiceCoincidencia.add(0.0);
        for (int i = 1; i <= tamanhoDeCaracteresQueAchaveDeveConter; i++) {
            ArrayList<String> lista = quebraStringPorTamanhoChave(i, tamanhoDeslocamentoParaDescobrirAchave, texto);
            ArrayList<Double> frequencia = calculaFrequencia(lista);
            Double resultado = calculaIndicdeCoincidencia(frequencia, lista.size());
            listaIndiceCoincidencia.add(resultado);
        }
        Double maiorValorInidiceCoincidencia = Collections.max(listaIndiceCoincidencia);
        return listaIndiceCoincidencia.indexOf(maiorValorInidiceCoincidencia);
    }

    private static ArrayList<String> quebraStringPorTamanhoChave(int tamanhoChave, int inicio, String texto) {
        ArrayList<String> returnString = new ArrayList<>();
        for (int i = inicio; i < texto.length(); i = i + tamanhoChave) {
            returnString.add(String.valueOf(texto.charAt(i)));
        }
        return returnString;
    }

    private static ArrayList<Double> calculaFrequencia(ArrayList<String> lista) {
        int[] quantidades = new int[tamanhoTotalDoAlfabeto];
        ArrayList<Double> listaFrequencia = new ArrayList<>();
        for (String linha : lista) {
            for (char c : linha.toUpperCase().toCharArray()) {
                int indice = c - 65;
                if (indice >= 0 && indice < tamanhoTotalDoAlfabeto) {
                    quantidades[indice] += 1;
                }
            }
        }

        int i = 0;
        while (i < quantidades.length) {
            listaFrequencia.add((double) quantidades[i]);
            i++;
        }

        return listaFrequencia;
    }

    private static Double calculaIndicdeCoincidencia(List listaFrequencia, int total) {
        double result = 0.0;
        for (int i = 0; i < listaFrequencia.size(); i++) {
            double f = (double) listaFrequencia.get(i);
            double calculo = f * (f - (1));
            result = result + calculo;
        }
        double divisor = (double) total * ((double) total - (1));
        return result / (divisor);
    }

    public static ArrayList<String> encontrarChave(int tamanhoChave, String texto) {
        ArrayList<Integer> listaPosicaoDaChaveNoAlfabeto = new ArrayList<>();
        int i = 0;
        while (i < tamanhoChave) {
            ArrayList<String> lista = quebraStringPorTamanhoChave(tamanhoChave, i, texto);
            ArrayList<Double> frequenciaChave = calculaFrequencia(lista);
            double maiorValor = Collections.max(frequenciaChave);
            int maiorIndice = frequenciaChave.indexOf(maiorValor);
            listaPosicaoDaChaveNoAlfabeto.add(maiorIndice);
            i++;
        }
        return calculaDeslocamenteChave(listaPosicaoDaChaveNoAlfabeto);
    }

    private static ArrayList<String> calculaDeslocamenteChave(ArrayList<Integer> listaPosicaoDaChaveNoAlfabeto) {
        Map<Integer, String> listaAlfabeto = new HashMap<>();
        ArrayList<String> chaveDescriptografada = new ArrayList<>();
        StringBuilder senhaEncontrada = new StringBuilder();
        deParaIntegerToStringAlfabeto(listaAlfabeto);
        for (Integer integer : listaPosicaoDaChaveNoAlfabeto) {
            chaveDescriptografada.add(String.valueOf(integer - 4));
        }
        for (String s : chaveDescriptografada) {
            senhaEncontrada.append(listaAlfabeto.get(Integer.parseInt(s)));
        }
        return chaveDescriptografada;
    }

    public static StringBuilder realizaAdescriptografiaDoTexto(ArrayList<String> listaChave, int tamanhoChave, String texto) {
        ArrayList<String> lista = IntStream.range(0, texto.length()).mapToObj(i -> String.valueOf(texto.charAt(i))).collect(Collectors.toCollection(ArrayList::new));
        ArrayList<String> listaChaveTamanhoTexto = new ArrayList<>();
        {
            int i = 0;
            while (i < lista.size()) {
                if (i == tamanhoChave) {
                    i = 0;
                }
                listaChaveTamanhoTexto.add(listaChave.get(i));
                if (listaChaveTamanhoTexto.size() == lista.size()) {
                    break;
                }
                i++;
            }
        }

        Map<String, Integer> listaAlfabetoStringInteger = new HashMap<>();
        Map<Integer, String> listaAlfabetoIntegerString = new HashMap<>();
        deParaStringToIntegerAlfabeto(listaAlfabetoStringInteger);
        deParaIntegerToStringAlfabeto(listaAlfabetoIntegerString);
        StringBuilder texteDescriptografado = new StringBuilder();

        for (int i = 0; i < listaChaveTamanhoTexto.size(); i++) {
            int calculo = listaAlfabetoStringInteger.get(lista.get(i)) - Integer.parseInt(listaChaveTamanhoTexto.get(i));
            if (calculo < 0) {
                texteDescriptografado.append(listaAlfabetoIntegerString.get(calculo + tamanhoTotalDoAlfabeto));
            } else {
                texteDescriptografado.append(listaAlfabetoIntegerString.get(calculo));
            }
        }
        return texteDescriptografado;
    }

    private static void imprimirNoArquivoTextoDescriptografado(StringBuilder textoDescriptografado) {
        try {
            BufferedWriter buffWrite = new BufferedWriter(new FileWriter(NOME_TEXTO_SAIDA));
            buffWrite.append(String.valueOf(textoDescriptografado));
            buffWrite.close();
            System.out.println("Arquivo " + NOME_TEXTO_SAIDA + " descriptrografado com sucesso!");
        } catch (Exception e) {
            System.out.println("Erro ao descriptrografar o arquivo!");
        }
    }

    private static void deParaIntegerToStringAlfabeto(Map<Integer, String> listaAlfabeto) {
        listaAlfabeto.put(0, "a");
        listaAlfabeto.put(1, "b");
        listaAlfabeto.put(2, "c");
        listaAlfabeto.put(3, "d");
        listaAlfabeto.put(4, "e");
        listaAlfabeto.put(5, "f");
        listaAlfabeto.put(6, "g");
        listaAlfabeto.put(7, "h");
        listaAlfabeto.put(8, "i");
        listaAlfabeto.put(9, "j");
        listaAlfabeto.put(10, "k");
        listaAlfabeto.put(11, "l");
        listaAlfabeto.put(12, "m");
        listaAlfabeto.put(13, "n");
        listaAlfabeto.put(14, "o");
        listaAlfabeto.put(15, "p");
        listaAlfabeto.put(16, "q");
        listaAlfabeto.put(17, "r");
        listaAlfabeto.put(18, "s");
        listaAlfabeto.put(19, "t");
        listaAlfabeto.put(20, "u");
        listaAlfabeto.put(21, "v");
        listaAlfabeto.put(22, "w");
        listaAlfabeto.put(23, "x");
        listaAlfabeto.put(24, "y");
        listaAlfabeto.put(25, "z");
    }

    private static void deParaStringToIntegerAlfabeto(Map<String, Integer> listaAlfabeto) {
        listaAlfabeto.put("a", 0);
        listaAlfabeto.put("b", 1);
        listaAlfabeto.put("c", 2);
        listaAlfabeto.put("d", 3);
        listaAlfabeto.put("e", 4);
        listaAlfabeto.put("f", 5);
        listaAlfabeto.put("g", 6);
        listaAlfabeto.put("h", 7);
        listaAlfabeto.put("i", 8);
        listaAlfabeto.put("j", 9);
        listaAlfabeto.put("k", 10);
        listaAlfabeto.put("l", 11);
        listaAlfabeto.put("m", 12);
        listaAlfabeto.put("n", 13);
        listaAlfabeto.put("o", 14);
        listaAlfabeto.put("p", 15);
        listaAlfabeto.put("q", 16);
        listaAlfabeto.put("r", 17);
        listaAlfabeto.put("s", 18);
        listaAlfabeto.put("t", 19);
        listaAlfabeto.put("u", 20);
        listaAlfabeto.put("v", 21);
        listaAlfabeto.put("w", 22);
        listaAlfabeto.put("x", 23);
        listaAlfabeto.put("y", 24);
        listaAlfabeto.put("z", 25);
    }
}
