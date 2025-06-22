package services;

import dao.AgendamentoExameDAO;
import dao.ExameDAO;
import dao.MedicoDAO;
import dao.PacienteDAO;
import entities.AgendamentoExame;
import entities.Exame;
import entities.Medico;
import entities.Paciente;

import java.time.LocalDateTime;
import java.util.List;
import java.math.BigDecimal;

public class AgendamentoExameService {

    private AgendamentoExameDAO agendamentoExameDAO;
    private ExameDAO exameDAO;
    private PacienteDAO pacienteDAO;
    private MedicoDAO medicoDAO;

    public AgendamentoExameService() {
        this.agendamentoExameDAO = new AgendamentoExameDAO();
        this.exameDAO = new ExameDAO();
        this.pacienteDAO = new PacienteDAO();
        this.medicoDAO = new MedicoDAO();
    }

    public void agendarExame(AgendamentoExame agendamento) {
        // Validações de campos obrigatórios
        if (agendamento.getExame() == null || agendamento.getExame().getId() == null) {
            throw new IllegalArgumentException("O exame é obrigatório.");
        }
        if (agendamento.getPaciente() == null || agendamento.getPaciente().getId() == null) {
            throw new IllegalArgumentException("O paciente é obrigatório.");
        }
        if (agendamento.getDataRealizacao() == null) {
            throw new IllegalArgumentException("A data e hora da realização do exame são obrigatórias.");
        }
        if (agendamento.getDataRealizacao().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Não é possível agendar exame no passado.");
        }
        if (agendamento.getValorPago() == null || agendamento.getValorPago().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("O valor a ser pago deve ser um número positivo.");
        }

        // Valida se as entidades existem
        Exame exameExistente = exameDAO.buscarPorId(agendamento.getExame().getId());
        if (exameExistente == null) {
            throw new IllegalArgumentException("Exame informado não encontrado.");
        }
        agendamento.setExame(exameExistente);

        Paciente pacienteExistente = pacienteDAO.buscarPorId(agendamento.getPaciente().getId());
        if (pacienteExistente == null) {
            throw new IllegalArgumentException("Paciente informado não encontrado.");
        }
        agendamento.setPaciente(pacienteExistente);

        if (agendamento.getMedicoRequisitante() != null && agendamento.getMedicoRequisitante().getId() != null) {
            Medico medicoRequisitanteExistente = medicoDAO.buscarPorId(agendamento.getMedicoRequisitante().getId());
            if (medicoRequisitanteExistente == null) {
                throw new IllegalArgumentException("Médico requisitante informado não encontrado.");
            }
            agendamento.setMedicoRequisitante(medicoRequisitanteExistente);
        }

        // Lógica de Negócio: Verificar sobreposição de horários para o EXAME ESPECÍFICO
        // Assumindo uma duração padrão para o exame, por exemplo, 60 minutos
        LocalDateTime fimPrevisto = agendamento.getDataRealizacao().plusMinutes(60); // Ajuste a duração se necessário

        if (agendamentoExameDAO.existeSobreposicao(agendamento.getExame().getId(), agendamento.getDataRealizacao(), fimPrevisto, agendamento.getId())) {
            throw new IllegalStateException("Já existe um agendamento para este exame neste horário.");
        }

        // Define o status inicial
        if (agendamento.getId() == null) {
            agendamento.setStatus("Agendado");
            agendamentoExameDAO.inserir(agendamento);
        } else {
            agendamentoExameDAO.atualizar(agendamento);
        }
    }

    public AgendamentoExame buscarAgendamentoExamePorId(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID do agendamento de exame inválido.");
        }
        return agendamentoExameDAO.buscarPorId(id);
    }

    public List<AgendamentoExame> buscarAgendaExame(Integer exameId, LocalDateTime data) {
        if (exameId == null || exameId <= 0) {
            throw new IllegalArgumentException("ID do exame inválido para buscar agenda.");
        }
        if (data == null) {
            throw new IllegalArgumentException("A data para buscar a agenda é obrigatória.");
        }
        return agendamentoExameDAO.buscarPorExameEData(exameId, data);
    }

    public List<AgendamentoExame> buscarHistoricoExamesPaciente(Integer pacienteId) {
        if (pacienteId == null || pacienteId <= 0) {
            throw new IllegalArgumentException("ID do paciente inválido para buscar histórico de exames.");
        }
        return agendamentoExameDAO.buscarHistoricoPaciente(pacienteId);
    }

    public void marcarExameRealizado(Integer agendamentoId) {
        if (agendamentoId == null || agendamentoId <= 0) {
            throw new IllegalArgumentException("ID do agendamento de exame inválido para marcar como realizado.");
        }
        AgendamentoExame agendamento = agendamentoExameDAO.buscarPorId(agendamentoId);
        if (agendamento == null) {
            throw new IllegalArgumentException("Agendamento de exame não encontrado.");
        }
        if (!"Agendado".equals(agendamento.getStatus())) {
            throw new IllegalStateException("O agendamento não está no status 'Agendado' para ser marcado como realizado.");
        }
        agendamento.setStatus("Realizado");
        agendamentoExameDAO.atualizar(agendamento);
    }

    public void cancelarAgendamentoExame(Integer agendamentoId) {
        if (agendamentoId == null || agendamentoId <= 0) {
            throw new IllegalArgumentException("ID do agendamento de exame inválido para cancelar.");
        }
        AgendamentoExame agendamento = agendamentoExameDAO.buscarPorId(agendamentoId);
        if (agendamento == null) {
            throw new IllegalArgumentException("Agendamento de exame não encontrado.");
        }
        if ("Realizado".equals(agendamento.getStatus())) {
            throw new IllegalStateException("Não é possível cancelar um agendamento já realizado.");
        }
        agendamento.setStatus("Cancelado");
        agendamentoExameDAO.atualizar(agendamento);
    }

    public void deletarAgendamentoExame(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID do agendamento de exame inválido para exclusão.");
        }
        // Regra de negócio: pode deletar um agendamento já realizado? Geralmente não.
        AgendamentoExame agendamento = agendamentoExameDAO.buscarPorId(id);
        if (agendamento != null && "Realizado".equals(agendamento.getStatus())) {
            throw new IllegalStateException("Não é possível deletar um agendamento de exame já realizado.");
        }
        agendamentoExameDAO.deletar(id);
    }
}