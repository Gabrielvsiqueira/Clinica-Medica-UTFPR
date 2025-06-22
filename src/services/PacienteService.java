package services;

import dao.PacienteDAO;
import entities.Paciente;

import java.time.LocalDate;
import java.util.List;

public class PacienteService {

    private PacienteDAO pacienteDAO;

    public PacienteService() {
        this.pacienteDAO = new PacienteDAO();
    }

    public void salvarPaciente(Paciente paciente) {
        // Validações de campos obrigatórios
        if (paciente.getNome() == null || paciente.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("O nome do paciente é obrigatório.");
        }
        if (paciente.getDataNascimento() == null) {
            throw new IllegalArgumentException("A data de nascimento do paciente é obrigatória.");
        }
        if (paciente.getDataNascimento().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("A data de nascimento não pode ser futura.");
        }
        if (paciente.getSexo() == null || paciente.getSexo().trim().isEmpty()) {
            throw new IllegalArgumentException("O sexo do paciente é obrigatório.");
        }
        if (paciente.getTelefone() == null || paciente.getTelefone().trim().isEmpty()) {
            throw new IllegalArgumentException("O telefone do paciente é obrigatório.");
        }
        // Mais validações (formato de telefone, email, etc.)

        if (paciente.getId() == null) {
            pacienteDAO.inserir(paciente);
        } else {
            pacienteDAO.atualizar(paciente);
        }
    }

    public Paciente buscarPacientePorId(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID do paciente inválido.");
        }
        return pacienteDAO.buscarPorId(id);
    }

    public List<Paciente> listarTodosPacientes() {
        return pacienteDAO.buscarTodos();
    }

    public void deletarPaciente(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID do paciente inválido para exclusão.");
        }
        // Lógica de negócio: Verificar se o paciente possui consultas ou exames agendados/realizados
        // (Isso exigiria métodos nos DAOs de Consulta e AgendamentoExame para contar por paciente)
        // if (new ConsultaDAO().contarConsultasPorPaciente(id) > 0 || new AgendamentoExameDAO().contarAgendamentosPorPaciente(id) > 0) {
        //     throw new IllegalStateException("Não é possível excluir paciente com histórico de consultas/exames.");
        // }
        pacienteDAO.deletar(id);
    }
}