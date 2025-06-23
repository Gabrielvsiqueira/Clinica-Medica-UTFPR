package entities;

import java.math.BigDecimal;

public class Exame  {
    private Integer id;
    private String nome;
    private BigDecimal valor;
    private String orientacoes;

    public Exame() {
    }

    public Exame(Integer id, String nome, BigDecimal valor, String orientacoes) {
        this.id = id;
        this.nome = nome;
        this.valor = valor;
        this.orientacoes = orientacoes;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }
    public String getOrientacoes() { return orientacoes; }
    public void setOrientacoes(String orientacoes) { this.orientacoes = orientacoes; }

    @Override
    public String toString() {
        return "Exame [id=" + id + ", nome=" + nome + ", valor=" + valor + "]";
    }
}