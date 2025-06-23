package services;

import dao.ExameDAO;
import entities.Exame;
import java.math.BigDecimal;
import java.util.List;

public class ExameService {

    private ExameDAO exameDAO;

    public ExameService() {
        this.exameDAO = new ExameDAO();
    }

    public void salvarExame(Exame exame) {
        if (exame.getNome() == null || exame.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("O nome do exame é obrigatório.");
        }
        if (exame.getValor() == null || exame.getValor().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("O valor do exame deve ser um número positivo.");
        }
        if (exame.getId() == null) {
            exameDAO.cadastrarExame(exame);
        } else {
            exameDAO.atualizarExame(exame);
        }
    }

    public Exame buscarExamePorId(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID do exame inválido.");
        }
        return exameDAO.buscarExameId(id);
    }

    public List<Exame> listarTodosExames() {
        return exameDAO.buscarTodosExames();
    }

    public void deletarExame(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID do exame inválido para exclusão.");
        }
        exameDAO.deletarExame(id);
    }
}