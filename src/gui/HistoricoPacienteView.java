package gui;

import entities.AgendamentoExame;
import entities.Consulta;
import entities.Paciente;
import services.AgendamentoExameService;
import services.ConsultaService;
import services.PacienteService;
import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Comparator;

public class HistoricoPacienteView extends JDialog {

    private PacienteService pacienteService;
    private ConsultaService consultaService;
    private AgendamentoExameService agendamentoExameService;

    private JComboBox<Paciente> pacienteComboBox;
    private JButton buscarButton;

    private JTable historicoTable;
    private HistoricoPacienteTableModel historicoTableModel;

    public HistoricoPacienteView(Frame owner) {
        super(owner, "Histórico do Paciente", true);
        this.pacienteService = new PacienteService();
        this.consultaService = new ConsultaService();
        this.agendamentoExameService = new AgendamentoExameService();
        initComponents();
        setupLayout();
        addListeners();
        loadPacientes(); // Carrega pacientes no combobox
        pack();
        setLocationRelativeTo(owner);
        setSize(800, 600);
    }

    private void initComponents() {
        pacienteComboBox = new JComboBox<>();
        buscarButton = new JButton("Buscar Histórico");

        historicoTableModel = new HistoricoPacienteTableModel();
        historicoTable = new JTable(historicoTableModel);
        historicoTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    private void setupLayout() {
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        filterPanel.add(new JLabel("Paciente:"));
        filterPanel.add(pacienteComboBox);
        filterPanel.add(buscarButton);

        setLayout(new BorderLayout(10, 10));
        add(filterPanel, BorderLayout.NORTH);
        add(new JScrollPane(historicoTable), BorderLayout.CENTER);
    }

    private void addListeners() {
        buscarButton.addActionListener(e -> buscarHistorico());
    }

    private void loadPacientes() {
        try {
            List<Paciente> pacientes = pacienteService.listarTodosPacientes();
            pacienteComboBox.removeAllItems();
            pacienteComboBox.addItem(null); // Opção para "Nenhum selecionado"
            for (Paciente paciente : pacientes) {
                pacienteComboBox.addItem(paciente);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar lista de pacientes: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void buscarHistorico() {
        try {
            Paciente selectedPaciente = (Paciente) pacienteComboBox.getSelectedItem();
            if (selectedPaciente == null) {
                JOptionPane.showMessageDialog(this, "Selecione um paciente para buscar o histórico.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }

            List<Consulta> consultas = consultaService.buscarHistoricoConsultasPaciente(selectedPaciente.getId());
            List<AgendamentoExame> agendamentosExame = agendamentoExameService.buscarHistoricoExamesPaciente(selectedPaciente.getId());

            List<HistoricoItem> historicoCompleto = new ArrayList<>();

            // Adiciona consultas ao histórico
            for (Consulta c : consultas) {
                historicoCompleto.add(new HistoricoItem(
                        c.getDataHora(),
                        "Consulta",
                        c.getMedico() != null ? c.getMedico().getNomeCompleto() : "N/A",
                        null, // Exames não têm valor direto aqui
                        c.getStatus()
                ));
            }

            // Adiciona agendamentos de exames ao histórico
            for (AgendamentoExame ae : agendamentosExame) {
                historicoCompleto.add(new HistoricoItem(
                        ae.getDataRealizacao(),
                        "Exame: " + (ae.getExame() != null ? ae.getExame().getNome() : "N/A"),
                        ae.getMedicoRequisitante() != null ? ae.getMedicoRequisitante().getNomeCompleto() : "N/A",
                        ae.getValorPago(),
                        ae.getStatus()
                ));
            }

            // Ordena o histórico pela data/hora (mais recente primeiro)
            historicoCompleto.sort(Comparator.comparing(HistoricoItem::getDataHora).reversed());

            historicoTableModel.setHistorico(historicoCompleto);

            if (historicoCompleto.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nenhum histórico encontrado para o paciente selecionado.", "Informação", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (IllegalArgumentException | IllegalStateException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao buscar histórico do paciente: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    // Classe auxiliar para combinar consultas e exames no histórico
    private static class HistoricoItem {
        LocalDateTime dataHora;
        String tipoEvento;
        String profissionalOuExame; // Nome do médico ou do exame
        BigDecimal valor;
        String status;

        public HistoricoItem(LocalDateTime dataHora, String tipoEvento, String profissionalOuExame, BigDecimal valor, String status) {
            this.dataHora = dataHora;
            this.tipoEvento = tipoEvento;
            this.profissionalOuExame = profissionalOuExame;
            this.valor = valor;
            this.status = status;
        }

        public LocalDateTime getDataHora() { return dataHora; }
        public String getTipoEvento() { return tipoEvento; }
        public String getProfissionalOuExame() { return profissionalOuExame; }
        public BigDecimal getValor() { return valor; }
        public String getStatus() { return status; }
    }

    private class HistoricoPacienteTableModel extends AbstractTableModel {
        private final String[] COLUMNS = {"Data/Hora", "Tipo", "Profissional/Exame", "Valor", "Status"};
        private List<HistoricoItem> historico;

        public HistoricoPacienteTableModel() {
            this.historico = new ArrayList<>();
        }

        public void setHistorico(List<HistoricoItem> historico) {
            this.historico = historico;
            fireTableDataChanged();
        }

        @Override
        public int getRowCount() {
            return historico.size();
        }

        @Override
        public int getColumnCount() {
            return COLUMNS.length;
        }

        @Override
        public String getColumnName(int column) {
            return COLUMNS[column];
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            HistoricoItem item = historico.get(rowIndex);
            switch (columnIndex) {
                case 0: return item.getDataHora();
                case 1: return item.getTipoEvento();
                case 2: return item.getProfissionalOuExame();
                case 3: return item.getValor();
                case 4: return item.getStatus();
                default: return null;
            }
        }
    }
}