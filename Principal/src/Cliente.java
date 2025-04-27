import java.util.List;

class Cliente {
    private int tempoChegada;
    private int tempoInicioAtendimento;
    private int tempoFimAtendimento;
    
    public Cliente(int tempoChegada) {
        this.tempoChegada = tempoChegada;
    }
    
    public int getTempoChegada() {
        return tempoChegada;
    }
    
    public int getTempoInicioAtendimento() {
        return tempoInicioAtendimento;
    }
    
    public int getTempoFimAtendimento() {
        return tempoFimAtendimento;
    }
    
    public void setTempoInicioAtendimento(int tempoInicioAtendimento) {
        this.tempoInicioAtendimento = tempoInicioAtendimento;
    }
    
    public void setTempoFimAtendimento(int tempoFimAtendimento) {
        this.tempoFimAtendimento = tempoFimAtendimento;
    }
}
