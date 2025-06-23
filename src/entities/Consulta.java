package entities;

import java.time.LocalDateTime;

public class Consulta {
    private Integer id;
    private LocalDateTime dataHora;
    private String status;
    private Paciente paciente;
    private Medico medico;

    public Consulta() {
    }

    public Consulta(Integer id, Paciente paciente, Medico medico, LocalDateTime dataHora, String status) {
        this.id = id;
        this.paciente = paciente;
        this.medico = medico;
        this.dataHora = dataHora;
        this.status = status;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Paciente getPaciente() { return paciente; }
    public void setPaciente(Paciente paciente) { this.paciente = paciente; }
    public Medico getMedico() { return medico; }
    public void setMedico(Medico medico) { this.medico = medico; }
    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return "Consulta [id=" + id + ", paciente=" + (paciente != null ? paciente.getNome() : "N/A") +
                ", medico=" + (medico != null ? medico.getNomeCompleto() : "N/A") +
                ", dataHora=" + dataHora + ", status=" + status + "]";
    }
}