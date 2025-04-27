import java.util.*;

public class Principal {
    // Flag estática indicando se o gerador de clientes ainda está ativo (true = ainda podem chegar clientes)
    public static volatile boolean aberto = true;
    // Duração total da simulação de pico (11h às 13h = 2 horas = 7200 segundos simulados)
    private static final int DURACAO_SIMULACAO = 7200;
    
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Informe o número de caixas para a simulação: ");
        int numCaixas = scanner.nextInt();
        if (numCaixas < 1) {
            numCaixas = 1;  // garante ao menos 1 caixa
        }
        
        // Estruturas compartilhadas
        List<Cliente> fila = new LinkedList<>();  // fila de clientes aguardando atendimento
        List<Cliente> atendidos = Collections.synchronizedList(new ArrayList<>());  // lista de clientes atendidos
        
        // Cria e inicia as threads de caixa (caixas de atendimento)
        Caixa[] caixas = new Caixa[numCaixas];
        for (int i = 0; i < numCaixas; i++) {
            caixas[i] = new Caixa(i + 1, fila, atendidos);
            caixas[i].start();
        }
        
        // Cria e inicia a thread geradora de clientes
        GeradorClientes gerador = new GeradorClientes(fila, DURACAO_SIMULACAO);
        gerador.start();
        
        // Aguarda o término da geração de clientes (fim do horário de pico)
        try {
            gerador.join();
        } catch (InterruptedException e) {
            // Se houver interrupção, prossegue para finalizar
        }
        
        // Aguarda todos os caixas terminarem o atendimento (fila esvaziar)
        for (Caixa caixa : caixas) {
            try {
                caixa.join();
            } catch (InterruptedException e) {
                // Em caso de interrupção, continua a finalizar
            }
        }
        
        scanner.close();
        
        // Cálculo das estatísticas após a simulação
        int totalAtendidos = atendidos.size();
        int tempoMaxEspera = 0;
        int tempoMaxAtendimento = 0;
        long somaTempoBanco = 0;
        long somaTempoEspera = 0;
        
        for (Cliente cliente : atendidos) {
            int espera = cliente.getTempoInicioAtendimento() - cliente.getTempoChegada();
            int atendimento = cliente.getTempoFimAtendimento() - cliente.getTempoInicioAtendimento();
            int tempoNoBanco = cliente.getTempoFimAtendimento() - cliente.getTempoChegada();
            
            if (espera > tempoMaxEspera) {
                tempoMaxEspera = espera;
            }
            if (atendimento > tempoMaxAtendimento) {
                tempoMaxAtendimento = atendimento;
            }
            somaTempoBanco += tempoNoBanco;
            somaTempoEspera += espera;
        }
        
        double tempoMedioBanco = 0;
        double tempoMedioEspera = 0;
        if (totalAtendidos > 0) {
            tempoMedioBanco = (double) somaTempoBanco / totalAtendidos;
            tempoMedioEspera = (double) somaTempoEspera / totalAtendidos;
        }
        
        boolean objetivoAtingido = tempoMedioEspera <= 120.0;  // verifica se tempo médio de espera foi menor ou igual a 2 minutos (120s)
        
        // Exibição dos resultados da simulação no console
        System.out.println("\n--- Resultados da Simulação ---");
        System.out.println("Total de clientes atendidos: " + totalAtendidos);
        System.out.println("Tempo máximo de espera na fila: " + tempoMaxEspera + " segundos");
        System.out.println("Tempo máximo de atendimento: " + tempoMaxAtendimento + " segundos");
        System.out.printf("Tempo médio que o cliente ficou no banco: %.2f segundos\n", tempoMedioBanco);
        System.out.printf("Tempo médio de espera na fila: %.2f segundos\n", tempoMedioEspera);
        System.out.println("Objetivo de espera inferior a 2 minutos atingido: " + (objetivoAtingido ? "Sim" : "Não"));
    }
}
