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
        // Validações
        if (exame.getNome() == null || exame.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("O nome do exame é obrigatório.");
        }
        if (exame.getValor() == null || exame.getValor().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("O valor do exame deve ser um número positivo.");
        }

        // Validação de nome único (opcional)
        // Exame existente = exameDAO.buscarPorNome(exame.getNome());
        // if (existente != null && (exame.getId() == null || !existente.getId().equals(exame.getId()))) {
        //     throw new IllegalArgumentException("Já existe um exame com este nome.");
        // }

        if (exame.getId() == null) {
            exameDAO.inserir(exame);
        } else {
            exameDAO.atualizar(exame);
        }
    }

    public Exame buscarExamePorId(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID do exame inválido.");
        }
        return exameDAO.buscarPorId(id);
    }

    public List<Exame> listarTodosExames() {
        return exameDAO.buscarTodos();
    }

    public void deletarExame(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID do exame inválido para exclusão.");
        }
        // Lógica de negócio: Verificar se o exame possui agendamentos
        // if (new AgendamentoExameDAO().contarAgendamentosPorExame(id) > 0) {
        //     throw new IllegalStateException("Não é possível excluir exame com agendamentos associados.");
        // }
        exameDAO.deletar(id);
    }
}