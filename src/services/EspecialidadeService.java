package services;

import dao.EspecialidadeDAO;
import entities.Especialidade;
import java.util.List;

public class EspecialidadeService {

    private EspecialidadeDAO especialidadeDAO;

    public EspecialidadeService() {
        this.especialidadeDAO = new EspecialidadeDAO();
    }

    public void salvarEspecialidade(Especialidade especialidade) {
        if (especialidade.getNome() == null || especialidade.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("O nome da especialidade não pode ser vazio.");
        }
        if (especialidade.getId() == null) {
            especialidadeDAO.cadastrarEspecialidade(especialidade);
        } else {
            especialidadeDAO.atualizarEspecialidade(especialidade);
        }
    }

    public Especialidade buscarEspecialidadePorId(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID da especialidade inválido.");
        }
        return especialidadeDAO.buscarEspecialidadeId(id);
    }

    public List<Especialidade> listarTodasEspecialidades() {
        return especialidadeDAO.buscarTodos();
    }

    public void deletarEspecialidade(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID da especialidade inválido para exclusão.");
        }
        especialidadeDAO.deletarEspecialidade(id);
    }
}