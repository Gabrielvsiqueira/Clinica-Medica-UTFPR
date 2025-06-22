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
        // Validações de campos obrigatórios (String)
        if (medico.getNomeCompleto() == null || medico.getNomeCompleto().trim().isEmpty()) {
            throw new IllegalArgumentException("O nome completo do médico é obrigatório.");
        }
        if (medico.getEndereco() == null || medico.getEndereco().trim().isEmpty()) {
            throw new IllegalArgumentException("O endereço do médico é obrigatório.");
        }
        if (medico.getTelefone() == null || medico.getTelefone().trim().isEmpty()) {
            throw new IllegalArgumentException("O telefone do médico é obrigatório.");
        }

        // --- VALIDAÇÃO CORRETA PARA CRM (Integer) ---
        if (medico.getCrm() == null) {
            throw new IllegalArgumentException("O CRM do médico é obrigatório e deve ser um número.");
        }
        if (medico.getCrm() <= 0) {
            throw new IllegalArgumentException("O CRM do médico deve ser um número inteiro positivo.");
        }
        // --- FIM DA VALIDAÇÃO CORRETA PARA CRM ---

        // Validação da Especialidade
        if (medico.getEspecialidade() == null || medico.getEspecialidade().getId() == null) {
            throw new IllegalArgumentException("A especialidade do médico é obrigatória.");
        }

        // Valida se a especialidade existe no banco de dados
        Especialidade especialidadeExistente = especialidadeDAO.buscarPorId(medico.getEspecialidade().getId());
        if (especialidadeExistente == null) {
            throw new IllegalArgumentException("Especialidade informada não existe na base de dados.");
        }
        // Garante que o objeto Especialidade dentro de Medico esteja completo com os dados do banco
        medico.setEspecialidade(especialidadeExistente);

        // Validação de CRM único (regra de negócio)
        Medico medicoComMesmoCrm = medicoDAO.buscarPorCrm(medico.getCrm());
        // Se encontrou um médico com o mesmo CRM E não é o próprio médico que está sendo atualizado
        if (medicoComMesmoCrm != null && (medico.getId() == null || !medicoComMesmoCrm.getId().equals(medico.getId()))) {
            throw new IllegalStateException("Já existe um médico cadastrado com este CRM.");
        }

        // Executa a operação de persistência (inserir ou atualizar)
        if (medico.getId() == null) {
            medicoDAO.inserir(medico);
        } else {
            medicoDAO.atualizar(medico);
        }
    }

    public Medico buscarMedicoPorId(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID do médico inválido para busca.");
        }
        return medicoDAO.buscarPorId(id);
    }

    public List<Medico> listarTodosMedicos() {
        return medicoDAO.buscarTodos();
    }

    public void deletarMedico(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID do médico inválido para exclusão.");
        }
        // Regra de negócio: Um médico não pode ser deletado se tiver consultas ou exames agendados/concluídos
        // (Isso exigiria métodos em ConsultaDAO e AgendamentoExameDAO, como:
        // if (new ConsultaDAO().contarConsultasPorMedico(id) > 0 || new AgendamentoExameDAO().contarAgendamentosPorMedico(id) > 0) {
        //     throw new IllegalStateException("Não é possível excluir médico com histórico de consultas/exames.");
        // }
        medicoDAO.deletar(id);
    }
}