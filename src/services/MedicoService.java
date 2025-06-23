package services;

import dao.EspecialidadeDAO;
import dao.MedicoDAO;
import entities.Especialidade;
import entities.Medico;
import java.util.List;

public class MedicoService {

    private MedicoDAO medicoDAO;
    private EspecialidadeDAO especialidadeDAO;

    public MedicoService() {
        this.medicoDAO = new MedicoDAO();
        this.especialidadeDAO = new EspecialidadeDAO();
    }

    public void salvarMedico(Medico medico) {
        if (medico.getNomeCompleto() == null || medico.getNomeCompleto().trim().isEmpty()) {
            throw new IllegalArgumentException("O nome completo do médico é obrigatório.");
        }
        if (medico.getEndereco() == null || medico.getEndereco().trim().isEmpty()) {
            throw new IllegalArgumentException("O endereço do médico é obrigatório.");
        }
        if (medico.getTelefone() == null || medico.getTelefone().trim().isEmpty()) {
            throw new IllegalArgumentException("O telefone do médico é obrigatório.");
        }
        if (medico.getCrm() == null) {
            throw new IllegalArgumentException("O CRM do médico é obrigatório e deve ser um número.");
        }
        if (medico.getCrm() <= 0) {
            throw new IllegalArgumentException("O CRM do médico deve ser um número inteiro positivo.");
        }
        if (medico.getEspecialidade() == null || medico.getEspecialidade().getId() == null) {
            throw new IllegalArgumentException("A especialidade do médico é obrigatória.");
        }
        Especialidade especialidadeExistente = especialidadeDAO.buscarEspecialidadeId(medico.getEspecialidade().getId());
        if (especialidadeExistente == null) {
            throw new IllegalArgumentException("Especialidade informada não existe na base de dados.");
        }
        medico.setEspecialidade(especialidadeExistente);
        Medico medicoComMesmoCrm = medicoDAO.buscarPorCrm(medico.getCrm());
        if (medicoComMesmoCrm != null && (medico.getId() == null || !medicoComMesmoCrm.getId().equals(medico.getId()))) {
            throw new IllegalStateException("Já existe um médico cadastrado com este CRM.");
        }
        if (medico.getId() == null) {
            medicoDAO.cadastrarMedico(medico);
        } else {
            medicoDAO.atualizarMedico(medico);
        }
    }

    public Medico buscarMedicoPorId(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID do médico inválido para busca.");
        }
        return medicoDAO.buscarMedicoPorId(id);
    }

    public List<Medico> listarTodosMedicos() {
        return medicoDAO.buscarTodos();
    }

    public void deletarMedico(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID do médico inválido para exclusão.");
        }
        medicoDAO.deletarMedico(id);
    }
}