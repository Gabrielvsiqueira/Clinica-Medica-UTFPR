package entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AgendamentoExame implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id; // Mapeia para agendamento_id
    private Exame exame; // Mapeia para fk_exames
    private Paciente paciente; // Mapeia para fk_paciente
    private Medico medicoRequisitante; // Mapeia para fk_medico
    private LocalDateTime dataRealizacao;
    private BigDecimal valorPago; // Mapeia para valor_exame
    private String status; // Mapeia para status_exame

    public AgendamentoExame() {
    }

    public AgendamentoExame(Integer id, Exame exame, Paciente paciente, Medico medicoRequisitante,
                            LocalDateTime dataRealizacao, BigDecimal valorPago, String status) {
        this.id = id;
        this.exame = exame;
        this.paciente = paciente;
        this.medicoRequisitante = medicoRequisitante;
        this.dataRealizacao = dataRealizacao;
        this.valorPago = valorPago;
        this.status = status;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Exame getExame() { return exame; }
    public void setExame(Exame exame) { this.exame = exame; }
    public Paciente getPaciente() { return paciente; }
    public void setPaciente(Paciente paciente) { this.paciente = paciente; }
    public Medico getMedicoRequisitante() { return medicoRequisitante; }
    public void setMedicoRequisitante(Medico medicoRequisitante) { this.medicoRequisitante = medicoRequisitante; }
    public LocalDateTime getDataRealizacao() { return dataRealizacao; }
    public void setDataRealizacao(LocalDateTime dataRealizacao) { this.dataRealizacao = dataRealizacao; }
    public BigDecimal getValorPago() { return valorPago; }
    public void setValorPago(BigDecimal valorPago) { this.valorPago = valorPago; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return "AgendamentoExame [id=" + id + ", exame=" + (exame != null ? exame.getNome() : "N/A") +
                ", paciente=" + (paciente != null ? paciente.getNome() : "N/A") +
                ", dataRealizacao=" + dataRealizacao + ", status=" + status + ", valorPago=" + valorPago + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        AgendamentoExame other = (AgendamentoExame) obj;
        if (id == null) {
            if (other.id != null) return false;
        } else if (!id.equals(other.id)) return false;
        return true;
    }
}