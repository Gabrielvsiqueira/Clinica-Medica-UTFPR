package entities;

import java.time.LocalDate;

public class Paciente {
    private Integer id;
    private String nome;
    private String foto;
    private LocalDate dataNascimento;
    private String sexo;
    private String endereco;
    private String telefone;
    private String formaPagamento;

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
}