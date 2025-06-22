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
        // Validação básica: nome não pode ser vazio
        if (especialidade.getNome() == null || especialidade.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("O nome da especialidade não pode ser vazio.");
        }

        // Poderia adicionar validação para nome duplicado aqui também
        // Especialidade existente = especialidadeDAO.buscarPorNome(especialidade.getNome());
        // if (existente != null && (especialidade.getId() == null || !existente.getId().equals(especialidade.getId()))) {
        //     throw new IllegalArgumentException("Já existe uma especialidade com este nome.");
        // }

        if (especialidade.getId() == null) {
            especialidadeDAO.inserir(especialidade);
        } else {
            especialidadeDAO.atualizar(especialidade);
        }
    }

    public Especialidade buscarEspecialidadePorId(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID da especialidade inválido.");
        }
        return especialidadeDAO.buscarPorId(id);
    }

    public List<Especialidade> listarTodasEspecialidades() {
        return especialidadeDAO.buscarTodos();
    }

    public void deletarEspecialidade(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID da especialidade inválido para exclusão.");
        }
        // Lógica de negócio: Verificar se a especialidade está sendo usada por algum médico antes de deletar
        // if (new MedicoDAO().contarMedicosPorEspecialidade(id) > 0) {
        //     throw new IllegalStateException("Não é possível excluir especialidade com médicos associados.");
        // }
        especialidadeDAO.deletar(id);
    }
}