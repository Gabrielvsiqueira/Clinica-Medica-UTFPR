package entities;

import java.io.Serializable;
import java.math.BigDecimal;

public class Exame implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id; // Mapeia para cod_exame
    private String nome;
    private BigDecimal valor; // Mapeia para valor (DECIMAL)
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
        Exame other = (Exame) obj;
        if (id == null) {
            if (other.id != null) return false;
        } else if (!id.equals(other.id)) return false;
        return true;
    }
}