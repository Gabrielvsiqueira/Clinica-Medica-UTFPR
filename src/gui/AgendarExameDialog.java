package gui;

import entities.AgendamentoExame;
import entities.Exame;
import entities.Medico;
import entities.Paciente;
import services.AgendamentoExameService;
import services.ExameService;
import services.MedicoService;
import services.PacienteService;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;

// Importar JDateChooser e JSpinner com DateEditor (jcalendar e/ou SwingX)
// import com.toedter.calendar.JDateChooser;
// import javax.swing.JSpinner.DateEditor;

public class AgendarExameDialog extends JDialog {

    private AgendamentoExameService agendamentoExameService;
    private ExameService exameService;
    private PacienteService pacienteService;
    private MedicoService medicoService;

    private JComboBox<Exame> exameComboBox;
    private JComboBox<Paciente> pacienteComboBox;
    private JComboBox<Medico> medicoRequisitanteComboBox; // Pode ser nulo
    // private JDateChooser dataChooser; // Se usar JDateChooser
    private JTextField dataField; // Alternativa para data
    // private JSpinner horaSpinner; // Se usar JSpinner para hora
    private JTextField horaField; // Alternativa para hora
    private JTextField valorPagoField; // Para o valor a ser pago (BigDecimal)
    private JTextField statusField; // Para exibir status (Agendado)

    private JButton agendarButton;
    private JButton cancelarButton;

    public AgendarExameDialog(Frame owner) {
        super(owner, "Agendar Exame", true); // true para modal
        this.agendamentoExameService = new AgendamentoExameService();
        this.exameService = new ExameService();
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
        exameComboBox = new JComboBox<>();
        pacienteComboBox = new JComboBox<>();
        medicoRequisitanteComboBox = new JComboBox<>();

        // Adiciona uma opção para "Nenhum" médico requisitante
        medicoRequisitanteComboBox.addItem(null);

        // Opção 1: JDateChooser e JSpinner
        // dataChooser = new JDateChooser(new Date());
        // dataChooser.setDateFormatString("dd/MM/yyyy");
        // horaSpinner = new JSpinner(new SpinnerDateModel());
        // JSpinner.DateEditor timeEditor = new JSpinner.DateEditor(horaSpinner, "HH:mm");
        // horaSpinner.setEditor(timeEditor);
        // horaSpinner.setValue(new Date());

        // Opção 2: JTextFields para data e hora
        dataField = new JTextField("AAAA-MM-DD", 10);
        horaField = new JTextField("HH:MM", 5);

        valorPagoField = new JTextField(15);
        statusField = new JTextField("Agendado", 10);
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
        addFormField(formPanel, gbc, "Exame:", exameComboBox, row++, 0);
        addFormField(formPanel, gbc, "Paciente:", pacienteComboBox, row++, 0);
        addFormField(formPanel, gbc, "Médico Requisitante:", medicoRequisitanteComboBox, row++, 0);

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
        gbc.weightx = 0;

        addFormField(formPanel, gbc, "Valor a Pagar:", valorPagoField, row++, 0);
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
        agendarButton.addActionListener(e -> agendarExame());
        cancelarButton.addActionListener(e -> dispose());
    }

    private void loadComboboxes() {
        try {
            List<Exame> exames = exameService.listarTodosExames();
            exameComboBox.removeAllItems();
            for (Exame ex : exames) {
                exameComboBox.addItem(ex);
            }

            List<Paciente> pacientes = pacienteService.listarTodosPacientes();
            pacienteComboBox.removeAllItems();
            for (Paciente p : pacientes) {
                pacienteComboBox.addItem(p);
            }

            // Adicione os médicos (incluindo o 'null' inicial)
            List<Medico> medicos = medicoService.listarTodosMedicos();
            // A opção null já foi adicionada em initComponents(), garanta que ela permaneça a primeira.
            // Para garantir que o null seja a primeira opção e os médicos venham depois:
            medicoRequisitanteComboBox.removeAllItems(); // Limpa, para garantir que o null seja adicionado corretamente
            medicoRequisitanteComboBox.addItem(null); // Adiciona a opção "Nenhum" novamente
            for (Medico m : medicos) {
                medicoRequisitanteComboBox.addItem(m);
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar dados para agendamento de exames: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void agendarExame() {
        try {
            AgendamentoExame agendamento = new AgendamentoExame();

            Exame selectedExame = (Exame) exameComboBox.getSelectedItem();
            if (selectedExame == null) throw new IllegalArgumentException("Selecione um exame.");
            agendamento.setExame(selectedExame);

            Paciente selectedPaciente = (Paciente) pacienteComboBox.getSelectedItem();
            if (selectedPaciente == null) throw new IllegalArgumentException("Selecione um paciente.");
            agendamento.setPaciente(selectedPaciente);

            Medico selectedMedicoRequisitante = (Medico) medicoRequisitanteComboBox.getSelectedItem();
            // medicoRequisitante pode ser null, o service lida com isso.
            agendamento.setMedicoRequisitante(selectedMedicoRequisitante);

            // Coleta e parse da data e hora
            LocalDate data;
            LocalTime hora;
            try {
                data = LocalDate.parse(dataField.getText());
                hora = LocalTime.parse(horaField.getText());
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException("Formato de data ou hora inválido. Use AAAA-MM-DD e HH:MM.");
            }
            agendamento.setDataRealizacao(LocalDateTime.of(data, hora));

            // Parse do valor a pagar
            try {
                agendamento.setValorPago(new BigDecimal(valorPagoField.getText()));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Formato de valor a pagar inválido. Digite um número.");
            }

            agendamento.setStatus(statusField.getText()); // 'Agendado'

            agendamentoExameService.agendarExame(agendamento);
            JOptionPane.showMessageDialog(this, "Agendamento de exame realizado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } catch (IllegalArgumentException | IllegalStateException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro de Validação/Negócio", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro inesperado ao agendar exame: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
}