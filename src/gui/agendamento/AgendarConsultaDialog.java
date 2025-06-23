package gui.agendamento;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;

import entities.Consulta;
import entities.Medico;
import entities.Paciente;
import services.ConsultaService;
import services.MedicoService;
import services.PacienteService;

public class AgendarConsultaDialog extends JDialog {

    private ConsultaService consultaService;
    private PacienteService pacienteService;
    private MedicoService medicoService;
    private JComboBox<Paciente> pacienteComboBox;
    private JComboBox<Medico> medicoComboBox;
    private JTextField dataField;
    private JTextField horaField;
    private JTextField statusField;
    private JButton agendarButton;
    private JButton cancelarButton;

    public AgendarConsultaDialog(Frame owner) {
        super(owner, "Agendar Consulta", true);
        this.consultaService = new ConsultaService();
        this.pacienteService = new PacienteService();
        this.medicoService = new MedicoService();
        initComponents();
        setupLayout();
        addListeners();
        loadComboboxes();
        pack();
        setLocationRelativeTo(owner);
    }

    private void initComponents() {
        pacienteComboBox = new JComboBox<>();
        medicoComboBox = new JComboBox<>();
        dataField = new JTextField("AAAA-MM-DD", 10);
        horaField = new JTextField("HH:MM", 10);
        statusField = new JTextField("Agendada", 10);
        statusField.setEditable(false);
        agendarButton = new JButton("Agendar");
        cancelarButton = new JButton("Cancelar");
    }

    private void setupLayout() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        int row = 0;
        addFormField(formPanel, gbc, "Paciente:", pacienteComboBox, row++, 0);
        addFormField(formPanel, gbc, "Médico:", medicoComboBox, row++, 0);
        gbc.gridx = 0; gbc.gridy = row; gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Data (AAAA-MM-DD):"), gbc);
        gbc.gridx = 1; gbc.gridy = row; gbc.anchor = GridBagConstraints.WEST; gbc.weightx = 1.0;
        formPanel.add(dataField, gbc);
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Hora (HH:MM):"), gbc);

        gbc.gridx = 1; gbc.gridy = row; gbc.anchor = GridBagConstraints.WEST; gbc.weightx = 1.0;
        formPanel.add(horaField, gbc);
        row++;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.add(agendarButton);
        buttonPanel.add(cancelarButton);
        setLayout(new BorderLayout());
        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void addFormField(JPanel panel, GridBagConstraints gbc, String labelText, JComponent field, int row, int col) {
        gbc.gridx = col;
        gbc.gridy = row;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(new JLabel(labelText), gbc);
        gbc.gridx = col + 1;
        gbc.gridy = row;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 1.0;
        panel.add(field, gbc);
        gbc.weightx = 0;
    }

    private void addListeners() {
        agendarButton.addActionListener(e -> agendarConsulta());
        cancelarButton.addActionListener(e -> dispose());
    }

    private void loadComboboxes() {
        try {
            List<Paciente> pacientes = pacienteService.listarTodosPacientes();
            pacienteComboBox.removeAllItems();
            for (Paciente p : pacientes) {
                pacienteComboBox.addItem(p);
            }
            List<Medico> medicos = medicoService.listarTodosMedicos();
            medicoComboBox.removeAllItems();
            for (Medico m : medicos) {
                medicoComboBox.addItem(m);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar dados de Pacientes/Médicos: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void agendarConsulta() {
        try {
            Consulta consulta = new Consulta();
            Paciente selectedPaciente = (Paciente) pacienteComboBox.getSelectedItem();
            if (selectedPaciente == null) throw new IllegalArgumentException("Selecione um paciente.");
            consulta.setPaciente(selectedPaciente);
            Medico selectedMedico = (Medico) medicoComboBox.getSelectedItem();
            if (selectedMedico == null) throw new IllegalArgumentException("Selecione um médico.");
            consulta.setMedico(selectedMedico);
            LocalDate data;
            LocalTime hora;
            try {
                data = LocalDate.parse(dataField.getText());
                hora = LocalTime.parse(horaField.getText());
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException("Formato de data ou hora inválido. Use AAAA-MM-DD e HH:MM.");
            }
            consulta.setDataHora(LocalDateTime.of(data, hora));
            consulta.setStatus(statusField.getText()); // 'Agendada'
            consultaService.agendarConsulta(consulta);
            JOptionPane.showMessageDialog(this, "Consulta agendada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } catch (IllegalArgumentException | IllegalStateException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro de Validação/Negócio", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro inesperado ao agendar consulta: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
}