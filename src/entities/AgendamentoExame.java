package entities;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AgendamentoExame {

    private Integer id;
    private Exame exame;
    private Paciente paciente;
    private Medico medicoRequisitante;
    private LocalDateTime dataRealizacao;
    private BigDecimal valorPago;
    private String status;

    public AgendamentoExame() {
    }

    public AgendamentoExame(Integer id, Exame exame, Paciente paciente, Medico medicoRequisitante, LocalDateTime dataRealizacao, BigDecimal valorPago, String status) {
        this.id = id;
        this.exame = exame;
        this.paciente = paciente;
        this.medicoRequisitante = medicoRequisitante;
        this.dataRealizacao = dataRealizacao;
        this.valorPago = valorPago;
        this.status = status;
    }

    public Integer getId() {return id;}
    public void setId(Integer id) {this.id = id;}
    public Exame getExame() {return exame;}
    public void setExame(Exame exame) {this.exame = exame;}
    public Paciente getPaciente() {return paciente;}
    public void setPaciente(Paciente paciente) {this.paciente = paciente;}
    public Medico getMedicoRequisitante() {return medicoRequisitante;}
    public void setMedicoRequisitante(Medico medicoRequisitante) {this.medicoRequisitante = medicoRequisitante;}
    public LocalDateTime getDataRealizacao() {return dataRealizacao;}
    public void setDataRealizacao(LocalDateTime dataRealizacao) {this.dataRealizacao = dataRealizacao;}
    public BigDecimal getValorPago() {return valorPago;}
    public void setValorPago(BigDecimal valorPago) {this.valorPago = valorPago;}
    public String getStatus() {return status;}
    public void setStatus(String status) {this.status = status;}

    @Override
    public String toString() {
        return "AgendamentoExame [id=" + id + ", exame=" + (exame != null ? exame.getNome() : "N/A") +
                ", paciente=" + (paciente != null ? paciente.getNome() : "N/A") +
                ", dataRealizacao=" + dataRealizacao + ", status=" + status + ", valorPago=" + valorPago + "]";
    }
}