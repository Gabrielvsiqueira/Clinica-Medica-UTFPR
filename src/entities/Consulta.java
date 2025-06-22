package entities;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Consulta implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id; // Mapeia para id_consulta
    private Paciente paciente; // Mapeia para fk_paciente
    private Medico medico; // Mapeia para fk_medico
    private LocalDateTime dataHora; // Mapeia para horario_consulta
    private String status;

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
        Consulta other = (Consulta) obj;
        if (id == null) {
            if (other.id != null) return false;
        } else if (!id.equals(other.id)) return false;
        return true;
    }
}