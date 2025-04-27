import java.util.List;
import java.util.Random;

class Caixa extends Thread {
    private List<Cliente> fila;       // fila compartilhada de clientes aguardando
    private List<Cliente> atendidos;  // lista compartilhada de clientes já atendidos (para estatísticas)
    private int id;
    private static Random random = new Random();
    private int tempoLivre = 0;  // tempo simulado em que este caixa estará livre (inicialmente 0 = 11h)
    
    public Caixa(int id, List<Cliente> fila, List<Cliente> atendidos) {
        this.id = id;
        this.fila = fila;
        this.atendidos = atendidos;
    }
    
    @Override
    public void run() {
        try {
            while (true) {
                Cliente cliente;
                // Região crítica: aguardar ou obter um cliente da fila de forma sincronizada
                synchronized (fila) {
                    // Espera enquanto a fila estiver vazia e ainda houver clientes por chegar
                    while (fila.isEmpty() && Principal.aberto) {
                        fila.wait();  // aguarda notificação de novo cliente ou fim das chegadas
                    }
                    // Se a fila está vazia e não virão mais clientes, encerra o loop (fecha o caixa)
                    if (fila.isEmpty() && !Principal.aberto) {
                        break;
                    }
                    // Remove o próximo cliente da fila para atendimento
                    cliente = fila.remove(0);
                }
                
                // Determina o horário de início do atendimento (maior entre chegada do cliente e quando o caixa ficou livre)
                int chegada = cliente.getTempoChegada();
                int inicioAtendimento = Math.max(chegada, tempoLivre);
                cliente.setTempoInicioAtendimento(inicioAtendimento);
                
                // Gera um tempo de atendimento aleatório entre 30 e 120 segundos (simulados)
                int tempoAtendimento = 30 + random.nextInt(91);  // [30, 120]
                // Calcula o horário de término do atendimento
                int fimAtendimento = inicioAtendimento + tempoAtendimento;
                cliente.setTempoFimAtendimento(fimAtendimento);
                
                // Atualiza o tempo em que este caixa estará livre novamente após atender este cliente
                tempoLivre = fimAtendimento;
                
                // Simula o atendimento do cliente (pausa a thread pelo tempo de atendimento em milissegundos reais)
                Thread.sleep(tempoAtendimento);
                
                // Registra o cliente atendido na lista de atendidos (para cálculo de estatísticas)
                synchronized (atendidos) {
                    atendidos.add(cliente);
                }
            }
        } catch (InterruptedException e) {
            // Em caso de interrupção da thread, encerra a execução do caixa
            Thread.currentThread().interrupt();
        }
    }
}
