package entities;
import java.io.Serializable;
import java.time.LocalDate;

public class Paciente implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id; // Mapeia para paciente_id
    private String nome;
    private String foto; // Mapeia para foto (STRING para caminho/URL)
    private LocalDate dataNascimento; // Mapeia para data_nascimento
    private String sexo;
    private String endereco;
    private String telefone; // Mapeia para telefone (STRING para VARCHAR)
    private String formaPagamento; // Mapeia para pagamento (coluna no DB)

    public Paciente() {
    }

    public Paciente(Integer id, String nome, String foto, LocalDate dataNascimento, String sexo, String endereco, String telefone, String formaPagamento) {
        this.id = id;
        this.nome = nome;
        this.foto = foto;
        this.dataNascimento = dataNascimento;
        this.sexo = sexo;
        this.endereco = endereco;
        this.telefone = telefone;
        this.formaPagamento = formaPagamento;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getFoto() { return foto; }
    public void setFoto(String foto) { this.foto = foto; }
    public LocalDate getDataNascimento() { return dataNascimento; }
    public void setDataNascimento(LocalDate dataNascimento) { this.dataNascimento = dataNascimento; }
    public String getSexo() { return sexo; }
    public void setSexo(String sexo) { this.sexo = sexo; }
    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }
    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
    public String getFormaPagamento() { return formaPagamento; }
    public void setFormaPagamento(String formaPagamento) { this.formaPagamento = formaPagamento; }

    @Override
    public String toString() {
        return "Paciente [id=" + id + ", nome=" + nome + ", dataNascimento=" + dataNascimento + ", sexo=" + sexo + ", telefone=" + telefone + "]";
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
        Paciente other = (Paciente) obj;
        if (id == null) {
            if (other.id != null) return false;
        } else if (!id.equals(other.id)) return false;
        return true;
    }
}