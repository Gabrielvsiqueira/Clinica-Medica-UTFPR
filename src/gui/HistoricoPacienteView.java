package gui;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import entities.AgendamentoExame;
import entities.Consulta;
import entities.Paciente;
import services.AgendamentoExameService;
import services.ConsultaService;
import services.PacienteService;

public class HistoricoPacienteView extends JDialog {

    private PacienteService pacienteService;
    private ConsultaService consultaService;
    private AgendamentoExameService agendamentoExameService;

    private JComboBox<Paciente> pacienteComboBox;
    private JButton buscarButton;
    private JButton exportarButton;

    private JTable historicoTable;
    private HistoricoPacienteTableModel historicoTableModel;

    private List<HistoricoItem> historicoCompleto;

    public HistoricoPacienteView(Frame owner) {
        super(owner, "Histórico do Paciente", true);
        this.pacienteService = new PacienteService();
        this.consultaService = new ConsultaService();
        this.agendamentoExameService = new AgendamentoExameService();

        initComponents();
        setupLayout();
        addListeners();
        loadPacientes();

        setSize(800, 600);
        setLocationRelativeTo(owner);
    }

    private void initComponents() {
        pacienteComboBox = new JComboBox<>();
        buscarButton = new JButton("Buscar Histórico");
        exportarButton = new JButton("Exportar Relatório");

        historicoTableModel = new HistoricoPacienteTableModel();
        historicoTable = new JTable(historicoTableModel);
        historicoTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    private void setupLayout() {
        setLayout(new BorderLayout(10, 10));
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        JPanel pacientePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pacientePanel.add(new JLabel("Paciente:"));
        pacienteComboBox.setPreferredSize(new Dimension(700, 25));
        pacientePanel.add(pacienteComboBox);
        topPanel.add(pacientePanel);
        JPanel botoesPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        botoesPanel.add(buscarButton);
        botoesPanel.add(exportarButton);
        topPanel.add(botoesPanel);
        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(historicoTable), BorderLayout.CENTER);
    }

    private void addListeners() {
        buscarButton.addActionListener(e -> buscarHistorico());
        exportarButton.addActionListener(e -> {
            if (historicoCompleto == null || historicoCompleto.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nenhum histórico para exportar.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Salvar relatório");
            if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                try (FileWriter writer = new FileWriter(fileChooser.getSelectedFile())) {
                    for (HistoricoItem item : historicoCompleto) {
                        writer.write(item.toString() + "\n");
                    }
                    JOptionPane.showMessageDialog(this, "Relatório salvo com sucesso!");
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, "Erro ao salvar relatório: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    private void loadPacientes() {
        try {
            List<Paciente> pacientes = pacienteService.listarTodosPacientes();
            pacienteComboBox.removeAllItems();
            pacienteComboBox.addItem(null);
            for (Paciente paciente : pacientes) {
                pacienteComboBox.addItem(paciente);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar pacientes: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void buscarHistorico() {
        try {
            Paciente selectedPaciente = (Paciente) pacienteComboBox.getSelectedItem();
            if (selectedPaciente == null) {
                JOptionPane.showMessageDialog(this, "Selecione um paciente.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            List<Consulta> consultas = consultaService.buscarHistoricoConsultasPaciente(selectedPaciente.getId());
            List<AgendamentoExame> agendamentos = agendamentoExameService.buscarHistoricoExamesPaciente(selectedPaciente.getId());

            historicoCompleto = new ArrayList<>();
            for (Consulta c : consultas) {
                historicoCompleto.add(new HistoricoItem(
                        c.getDataHora(),
                        "Consulta",
                        c.getMedico() != null ? c.getMedico().getNomeCompleto() : "N/A",
                        null,
                        c.getStatus()
                ));
            }
            for (AgendamentoExame ae : agendamentos) {
                historicoCompleto.add(new HistoricoItem(
                        ae.getDataRealizacao(),
                        "Exame: " + (ae.getExame() != null ? ae.getExame().getNome() : "N/A"),
                        ae.getMedicoRequisitante() != null ? ae.getMedicoRequisitante().getNomeCompleto() : "N/A",
                        ae.getValorPago(),
                        ae.getStatus()
                ));
            }
            historicoCompleto.sort(Comparator.comparing(HistoricoItem::getDataHora).reversed());
            historicoTableModel.setHistorico(historicoCompleto);
            if (historicoCompleto.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nenhum histórico encontrado.", "Informação", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao buscar histórico: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static class HistoricoItem {
        LocalDateTime dataHora;
        String tipoEvento;
        String profissionalOuExame;
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
        @Override
        public String toString() {
            return dataHora + " - " + tipoEvento + " - " + profissionalOuExame + " - " +
                    (valor != null ? "R$" + valor : "") + " - " + status;
        }
    }

    private class HistoricoPacienteTableModel extends AbstractTableModel {
        private final String[] COLUMNS = {"Data/Hora", "Tipo", "Profissional/Exame", "Valor", "Status"};
        private List<HistoricoItem> historico = new ArrayList<>();

        public void setHistorico(List<HistoricoItem> historico) {
            this.historico = historico;
            fireTableDataChanged();
        }

        @Override public int getRowCount() { return historico.size(); }
        @Override public int getColumnCount() { return COLUMNS.length; }
        @Override public String getColumnName(int column) { return COLUMNS[column]; }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            HistoricoItem item = historico.get(rowIndex);
            return switch (columnIndex) {
                case 0 -> item.getDataHora();
                case 1 -> item.getTipoEvento();
                case 2 -> item.getProfissionalOuExame();
                case 3 -> item.getValor();
                case 4 -> item.getStatus();
                default -> null;
            };
        }
    }
}
