package entities;

public class Especialidade {
    private Integer id;
    private String nome;

    public Especialidade() {
    }

    public Especialidade(Integer id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public String getNome() {
        return nome;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }

    @Override
    public String toString() {
        return "Especialidade [id=" + id + ", nome=" + nome + "]";
    }

}