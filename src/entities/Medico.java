package entities;

public class Medico {
    private Integer id;
    private Integer crm;
    private String nomeCompleto;
    private String endereco;
    private String telefone;
    private Especialidade especialidade;

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
    public void setCrm(Integer crm) { this.crm = crm; }
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
}