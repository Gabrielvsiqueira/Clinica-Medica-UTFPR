package entities;

import java.io.Serializable;

public class Medico implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id; // Mapeia para medico_id
    private Integer crm; // CRM é INT no seu DB, então use Integer aqui
    private String nomeCompleto; // Mapeia para nome (coluna no DB)
    private String endereco;
    private String telefone; // Mapeia para telefone (STRING para VARCHAR)
    private Especialidade especialidade; // Mapeia para fk_especialidade

    public Medico() {
    }

    public Medico(Integer id, Integer crm, String nomeCompleto, String endereco, String telefone, Especialidade especialidade) {
        this.id = id;
        this.crm = crm;
        this.nomeCompleto = nomeCompleto;
        this.endereco = endereco;
        this.telefone = telefone;
        this.especialidade = especialidade;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getCrm() { return crm; }
    public void setCrm(Integer crm) { this.crm = crm; } // Usar setInt e getInt no DAO
    public String getNomeCompleto() { return nomeCompleto; }
    public void setNomeCompleto(String nomeCompleto) { this.nomeCompleto = nomeCompleto; }
    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }
    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
    public Especialidade getEspecialidade() { return especialidade; }
    public void setEspecialidade(Especialidade especialidade) { this.especialidade = especialidade; }

    @Override
    public String toString() {
        return "Medico [id=" + id + ", crm=" + crm + ", nomeCompleto=" + nomeCompleto + ", especialidade=" + (especialidade != null ? especialidade.getNome() : "N/A") + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((crm == null) ? 0 : crm.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        Medico other = (Medico) obj;
        if (id == null) {
            if (other.id != null) return false;
        } else if (!id.equals(other.id)) return false;
        if (crm == null) {
            if (other.crm != null) return false;
        } else if (!crm.equals(other.crm)) return false;
        return true;
    }
}