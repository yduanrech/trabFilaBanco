import java.util.List;
import java.util.Random;

class GeradorClientes extends Thread {
    private List<Cliente> fila;         // fila compartilhada de clientes
    private static Random random = new Random();
    private int tempoSimuladoTotal;     // duração total da simulação em segundos
    
    public GeradorClientes(List<Cliente> fila, int tempoSimuladoTotal) {
        this.fila = fila;
        this.tempoSimuladoTotal = tempoSimuladoTotal;
    }
    
    @Override
    public void run() {
        int tempoAtual = 0;  // tempo simulado que transcorrerá desde o início (11h)
        try {
            while (tempoAtual < tempoSimuladoTotal) {
                // Gera um intervalo aleatório de chegada entre 5 e 50 segundos (simulados)
                int intervalo = 5 + random.nextInt(46);  // [5, 50]
                // Aguarda esse intervalo (em milissegundos reais) para simular a chegada do próximo cliente
                Thread.sleep(intervalo);
                tempoAtual += intervalo;
                if (tempoAtual > tempoSimuladoTotal) {
                    break;  // não cria clientes após o tempo final da simulação
                }
                // Cria um novo cliente com o tempo de chegada atual
                Cliente cliente = new Cliente(tempoAtual);
                // Adiciona o cliente na fila de espera (região crítica sincronizada)
                synchronized (fila) {
                    fila.add(cliente);
                    // Notifica um dos caixas de que um novo cliente chegou
                    fila.notify();
                }
                // Cliente registrado (opcionalmente poderia ser armazenado em lista de todos clientes gerados para estatísticas gerais)
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            // Indica que não haverá mais chegadas de clientes e desperta todos os caixas possivelmente esperando
            synchronized (fila) {
                Principal.aberto = false;
                fila.notifyAll();
            }
        }
    }
}
