package services;

import dao.ConsultaDAO;
import dao.MedicoDAO;
import dao.PacienteDAO;
import entities.Consulta;
import entities.Medico;
import entities.Paciente;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class ConsultaService {

    private ConsultaDAO consultaDAO;
    private PacienteDAO pacienteDAO;
    private MedicoDAO medicoDAO;

    public ConsultaService() {
        this.consultaDAO = new ConsultaDAO();
        this.pacienteDAO = new PacienteDAO();
        this.medicoDAO = new MedicoDAO();
    }

    public void agendarConsulta(Consulta consulta) {
        if (consulta.getPaciente() == null || consulta.getPaciente().getId() == null) {
            throw new IllegalArgumentException("O paciente da consulta é obrigatório.");
        }
        if (consulta.getMedico() == null || consulta.getMedico().getId() == null) {
            throw new IllegalArgumentException("O médico da consulta é obrigatório.");
        }
        if (consulta.getDataHora() == null) {
            throw new IllegalArgumentException("A data e hora da consulta são obrigatórias.");
        }
        if (consulta.getDataHora().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Não é possível agendar consulta no passado.");
        }
        Paciente pacienteExistente = pacienteDAO.buscarPacienteId(consulta.getPaciente().getId());
        if (pacienteExistente == null) {
            throw new IllegalArgumentException("Paciente informado não encontrado.");
        }
        consulta.setPaciente(pacienteExistente);

        Medico medicoExistente = medicoDAO.buscarMedicoPorId(consulta.getMedico().getId());
        if (medicoExistente == null) {
            throw new IllegalArgumentException("Médico informado não encontrado.");
        }
        consulta.setMedico(medicoExistente);
        LocalDateTime fimPrevisto = consulta.getDataHora().plusMinutes(30);

        if (consultaDAO.existeSobreposicaoConsulta(consulta.getMedico().getId(), consulta.getDataHora(), fimPrevisto, consulta.getId())) {
            throw new IllegalStateException("O médico já possui uma consulta agendada neste horário.");
        }
        if (consulta.getId() == null) {
            consulta.setStatus("Agendada");
            consultaDAO.cadastrarConsulta(consulta);
        } else {
            consultaDAO.atualizarConsulta(consulta);
        }
    }

    public Consulta buscarConsultaPorId(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID da consulta inválido.");
        }
        return consultaDAO.buscarConsultaId(id);
    }

    public List<Consulta> buscarAgendaMedico(Integer medicoId, LocalDateTime data) {
        if (medicoId == null || medicoId <= 0) {
            throw new IllegalArgumentException("ID do médico inválido para buscar agenda.");
        }
        if (data == null) {
            throw new IllegalArgumentException("A data para buscar a agenda é obrigatória.");
        }
        return consultaDAO.buscarPorMedicoEData(medicoId, data);
    }

    public List<Consulta> buscarHistoricoConsultasPaciente(Integer pacienteId) {
        if (pacienteId == null || pacienteId <= 0) {
            throw new IllegalArgumentException("ID do paciente inválido para buscar histórico.");
        }
        return consultaDAO.buscarHistoricoPaciente(pacienteId);
    }

    public void marcarConsultaConcluida(Integer consultaId) {
        if (consultaId == null || consultaId <= 0) {
            throw new IllegalArgumentException("ID da consulta inválido para marcar como concluída.");
        }
        Consulta consulta = consultaDAO.buscarConsultaId(consultaId);
        if (consulta == null) {
            throw new IllegalArgumentException("Consulta não encontrada.");
        }
        if (!"Agendada".equals(consulta.getStatus())) {
            throw new IllegalStateException("A consulta não está no status 'Agendada' para ser marcada como concluída.");
        }
        consulta.setStatus("Concluída");
        consultaDAO.atualizarConsulta(consulta);
    }

    public void cancelarConsulta(Integer consultaId) {
        if (consultaId == null || consultaId <= 0) {
            throw new IllegalArgumentException("ID da consulta inválido para cancelar.");
        }
        Consulta consulta = consultaDAO.buscarConsultaId(consultaId);
        if (consulta == null) {
            throw new IllegalArgumentException("Consulta não encontrada.");
        }
        if ("Concluída".equals(consulta.getStatus())) {
            throw new IllegalStateException("Não é possível cancelar uma consulta já concluída.");
        }
        consulta.setStatus("Cancelada");
        consultaDAO.atualizarConsulta(consulta);
    }

    public void deletarConsulta(Integer id) throws SQLException, IOException {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID da consulta inválido para exclusão.");
        }
        Consulta consulta = consultaDAO.buscarConsultaId(id);
        if (consulta != null && "Concluída".equals(consulta.getStatus())) {
            throw new IllegalStateException("Não é possível deletar uma consulta já concluída.");
        }
        consultaDAO.deletarConsulta(id);
    }
}