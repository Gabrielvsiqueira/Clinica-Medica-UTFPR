package gui;

import entities.AgendamentoExame;
import entities.Exame;
import services.AgendamentoExameService;
import services.ExameService;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

// Opcional: JDateChooser para seleção de datas (requer jcalendar)
// import com.toedter.calendar.JDateChooser;

public class AgendaExameView extends JDialog {

    private ExameService exameService;
    private AgendamentoExameService agendamentoExameService;

    private JComboBox<Exame> exameComboBox;
    // private JDateChooser dataChooser; // Se usar JDateChooser
    private JTextField dataField; // Alternativa para data
    private JButton buscarButton;

    private JTable agendaTable;
    private AgendaExameTableModel agendaTableModel;

    public AgendaExameView(Frame owner) {
        super(owner, "Agenda de Exames", true);
        this.exameService = new ExameService();
        this.agendamentoExameService = new AgendamentoExameService();
        initComponents();
        setupLayout();
        addListeners();
        loadExames(); // Carrega exames no combobox
        pack();
        setLocationRelativeTo(owner);
    }

    private void initComponents() {
        exameComboBox = new JComboBox<>();
        // dataChooser = new JDateChooser(LocalDate.now().toDate()); // Se usar JDateChooser
        // dataChooser.setDateFormatString("dd/MM/yyyy");
        dataField = new JTextField(LocalDate.now().toString(), 10); // Data atual por padrão
        buscarButton = new JButton("Buscar Agenda");

        agendaTableModel = new AgendaExameTableModel();
        agendaTable = new JTable(agendaTableModel);
        agendaTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    private void setupLayout() {
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        filterPanel.add(new JLabel("Exame:"));
        filterPanel.add(exameComboBox);
        filterPanel.add(new JLabel("Data (AAAA-MM-DD):"));
        // if (dataChooser != null) filterPanel.add(dataChooser); else
        filterPanel.add(dataField);
        filterPanel.add(buscarButton);

        setLayout(new BorderLayout(10, 10));
        add(filterPanel, BorderLayout.NORTH);
        add(new JScrollPane(agendaTable), BorderLayout.CENTER);
    }

    private void addListeners() {
        buscarButton.addActionListener(e -> buscarAgenda());
    }

    private void loadExames() {
        try {
            List<Exame> exames = exameService.listarTodosExames();
            exameComboBox.removeAllItems();
            exameComboBox.addItem(null); // Opção para "Nenhum selecionado"
            for (Exame exame : exames) {
                exameComboBox.addItem(exame);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar lista de exames: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void buscarAgenda() {
        try {
            Exame selectedExame = (Exame) exameComboBox.getSelectedItem();
            if (selectedExame == null) {
                JOptionPane.showMessageDialog(this, "Selecione um exame para buscar a agenda.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }

            LocalDate dataAgenda;
            try {
                // dataAgenda = dataChooser.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate(); // Se usar JDateChooser
                dataAgenda = LocalDate.parse(dataField.getText());
            } catch (DateTimeParseException e) {
                JOptionPane.showMessageDialog(this, "Formato de data inválido. Use AAAA-MM-DD.", "Erro de Formato", JOptionPane.WARNING_MESSAGE);
                return;
            }

            List<AgendamentoExame> agendamentos = agendamentoExameService.buscarAgendaExame(selectedExame.getId(), dataAgenda.atStartOfDay());
            agendaTableModel.setAgendamentos(agendamentos);

            if (agendamentos.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nenhum agendamento encontrado para o exame e data selecionados.", "Informação", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (IllegalArgumentException | IllegalStateException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao buscar agenda do exame: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private class AgendaExameTableModel extends AbstractTableModel {
        private final String[] COLUMNS = {"Paciente", "Horário", "Valor", "Status", "Médico Requisitante"};
        private List<AgendamentoExame> agendamentos;

        public AgendaExameTableModel() {
            this.agendamentos = new ArrayList<>();
        }

        public void setAgendamentos(List<AgendamentoExame> agendamentos) {
            this.agendamentos = agendamentos;
            fireTableDataChanged();
        }

        @Override
        public int getRowCount() {
            return agendamentos.size();
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
            AgendamentoExame agendamento = agendamentos.get(rowIndex);
            switch (columnIndex) {
                case 0: return agendamento.getPaciente() != null ? agendamento.getPaciente().getNome() : "N/A";
                case 1: return agendamento.getDataRealizacao();
                case 2: return agendamento.getValorPago();
                case 3: return agendamento.getStatus();
                case 4: return agendamento.getMedicoRequisitante() != null ? agendamento.getMedicoRequisitante().getNomeCompleto() : "N/A";
                default: return null;
            }
        }
    }
}