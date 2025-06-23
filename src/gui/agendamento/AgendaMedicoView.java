package gui.agendamento;

import entities.Consulta;
import entities.Medico;
import services.ConsultaService;
import services.MedicoService;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class AgendaMedicoView extends JDialog {
    private MedicoService medicoService;
    private ConsultaService consultaService;
    private JComboBox<Medico> medicoComboBox;
    private JTextField dataField;
    private JButton buscarButton;

    private JTable agendaTable;
    private AgendaMedicoTableModel agendaTableModel;

    public AgendaMedicoView(Frame owner) {
        super(owner, "Agenda do Médico", true);
        this.medicoService = new MedicoService();
        this.consultaService = new ConsultaService();
        initComponents();
        setupLayout();
        addListeners();
        loadMedicos();
        pack();
        setLocationRelativeTo(owner);
    }

    private void initComponents() {
        medicoComboBox = new JComboBox<>();
        dataField = new JTextField(LocalDate.now().toString(), 10); // Data atual por padrão
        buscarButton = new JButton("Buscar Agenda");
        agendaTableModel = new AgendaMedicoTableModel();
        agendaTable = new JTable(agendaTableModel);
        agendaTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    private void setupLayout() {
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        filterPanel.add(new JLabel("Médico:"));
        filterPanel.add(medicoComboBox);
        filterPanel.add(new JLabel("Data (AAAA-MM-DD):"));
        filterPanel.add(dataField);
        filterPanel.add(buscarButton);

        setLayout(new BorderLayout(10, 10));
        add(filterPanel, BorderLayout.NORTH);
        add(new JScrollPane(agendaTable), BorderLayout.CENTER);
    }

    private void addListeners() {
        buscarButton.addActionListener(e -> buscarAgenda());
    }

    private void loadMedicos() {
        try {
            List<Medico> medicos = medicoService.listarTodosMedicos();
            medicoComboBox.removeAllItems();
            medicoComboBox.addItem(null);
            for (Medico medico : medicos) {
                medicoComboBox.addItem(medico);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar lista de médicos: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void buscarAgenda() {
        try {
            Medico selectedMedico = (Medico) medicoComboBox.getSelectedItem();
            if (selectedMedico == null) {
                JOptionPane.showMessageDialog(this, "Selecione um médico para buscar a agenda.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            LocalDate dataAgenda;
            try {
                dataAgenda = LocalDate.parse(dataField.getText());
            } catch (DateTimeParseException e) {
                JOptionPane.showMessageDialog(this, "Formato de data inválido. Use AAAA-MM-DD.", "Erro de Formato", JOptionPane.WARNING_MESSAGE);
                return;
            }
            List<Consulta> consultas = consultaService.buscarAgendaMedico(selectedMedico.getId(), dataAgenda.atStartOfDay());
            agendaTableModel.setConsultas(consultas);
            if (consultas.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nenhuma consulta encontrada para o médico e data selecionados.", "Informação", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (IllegalArgumentException | IllegalStateException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao buscar agenda do médico: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private class AgendaMedicoTableModel extends AbstractTableModel {
        private final String[] COLUMNS = {"Paciente", "Horário", "Status"};
        private List<Consulta> consultas;

        public AgendaMedicoTableModel() {
            this.consultas = new ArrayList<>();
        }

        public void setConsultas(List<Consulta> consultas) {
            this.consultas = consultas;
            fireTableDataChanged();
        }

        @Override
        public int getRowCount() {
            return consultas.size();
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
            Consulta consulta = consultas.get(rowIndex);
            switch (columnIndex) {
                case 0: return consulta.getPaciente() != null ? consulta.getPaciente().getNome() : "N/A";
                case 1: return consulta.getDataHora();
                case 2: return consulta.getStatus();
                default: return null;
            }
        }
    }
}