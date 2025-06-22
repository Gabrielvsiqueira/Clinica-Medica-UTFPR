package gui;

import entities.Consulta;
import entities.Medico;
import entities.Paciente;
import services.ConsultaService;
import services.MedicoService;
import services.PacienteService;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

// Importar JDateChooser e JSpinner com DateEditor (jcalendar e/ou SwingX)
// import com.toedter.calendar.JDateChooser;
// import javax.swing.JSpinner.DateEditor;

public class AgendarConsultaDialog extends JDialog {

    private ConsultaService consultaService;
    private PacienteService pacienteService;
    private MedicoService medicoService;

    private JComboBox<Paciente> pacienteComboBox;
    private JComboBox<Medico> medicoComboBox;
    // private JDateChooser dataChooser; // Se usar JDateChooser
    private JTextField dataField; // Alternativa para data
    // private JSpinner horaSpinner; // Se usar JSpinner para hora
    private JTextField horaField; // Alternativa para hora
    private JTextField statusField; // Para exibir status (Agendada)

    private JButton agendarButton;
    private JButton cancelarButton; // Para fechar o diálogo

    public AgendarConsultaDialog(Frame owner) {
        super(owner, "Agendar Consulta", true); // true para modal
        this.consultaService = new ConsultaService();
        this.pacienteService = new PacienteService();
        this.medicoService = new MedicoService();
        initComponents();
        setupLayout();
        addListeners();
        loadComboboxes();
        pack(); // Ajusta o tamanho da janela aos componentes
        setLocationRelativeTo(owner); // Centraliza em relação à janela principal
    }

    private void initComponents() {
        pacienteComboBox = new JComboBox<>();
        medicoComboBox = new JComboBox<>();

        // Opção 1: JDateChooser e JSpinner (requer jcalendar)
        // dataChooser = new JDateChooser(new Date());
        // dataChooser.setDateFormatString("dd/MM/yyyy");
        // horaSpinner = new JSpinner(new SpinnerDateModel());
        // DateEditor timeEditor = new DateEditor(horaSpinner, "HH:mm");
        // horaSpinner.setEditor(timeEditor);
        // horaSpinner.setValue(new Date()); // Valor inicial como hora atual

        // Opção 2: JTextFields para data e hora (simples)
        dataField = new JTextField("AAAA-MM-DD", 10);
        horaField = new JTextField("HH:MM", 5);

        statusField = new JTextField("Agendada", 10);
        statusField.setEditable(false); // Status inicial é sempre "Agendada"

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

        // Data e Hora
        gbc.gridx = 0; gbc.gridy = row; gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Data (AAAA-MM-DD):"), gbc);
        gbc.gridx = 1; gbc.gridy = row; gbc.anchor = GridBagConstraints.WEST; gbc.weightx = 1.0;
        // if (dataChooser != null) formPanel.add(dataChooser, gbc); else 
        formPanel.add(dataField, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row; gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Hora (HH:MM):"), gbc);
        gbc.gridx = 1; gbc.gridy = row; gbc.anchor = GridBagConstraints.WEST; gbc.weightx = 1.0;
        // if (horaSpinner != null) formPanel.add(horaSpinner, gbc); else 
        formPanel.add(horaField, gbc);
        row++;
        gbc.weightx = 0; // Reset weightx

        addFormField(formPanel, gbc, "Status:", statusField, row++, 0);

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
        cancelarButton.addActionListener(e -> dispose()); // Fecha o diálogo
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

            // Coleta e parse da data e hora
            LocalDate data;
            LocalTime hora;
            try {
                data = LocalDate.parse(dataField.getText());
                hora = LocalTime.parse(horaField.getText());
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException("Formato de data ou hora inválido. Use AAAA-MM-DD e HH:MM.");
            }
            // Se usar JDateChooser e JSpinner
            // Date selectedDate = dataChooser.getDate();
            // Date selectedTime = (Date) horaSpinner.getValue();
            // if (selectedDate == null || selectedTime == null) throw new IllegalArgumentException("Data e hora são obrigatórias.");
            // Calendar cal = Calendar.getInstance();
            // cal.setTime(selectedDate);
            // Calendar timeCal = Calendar.getInstance();
            // timeCal.setTime(selectedTime);
            // LocalDateTime dataHora = LocalDateTime.of(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH),
            //                                         timeCal.get(Calendar.HOUR_OF_DAY), timeCal.get(Calendar.MINUTE));
            // consulta.setDataHora(dataHora);

            consulta.setDataHora(LocalDateTime.of(data, hora));
            consulta.setStatus(statusField.getText()); // 'Agendada'

            consultaService.agendarConsulta(consulta);
            JOptionPane.showMessageDialog(this, "Consulta agendada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            dispose(); // Fecha o diálogo após agendamento
        } catch (IllegalArgumentException | IllegalStateException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro de Validação/Negócio", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro inesperado ao agendar consulta: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
}