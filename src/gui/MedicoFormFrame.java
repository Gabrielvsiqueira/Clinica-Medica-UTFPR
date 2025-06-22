package gui;

import entities.Especialidade;
import entities.Medico;
import services.EspecialidadeService;
import services.MedicoService;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.FocusAdapter; // Para validação de foco
import java.awt.event.FocusEvent;
import java.util.ArrayList;
import java.util.List;

public class MedicoFormFrame extends JFrame {

    private MedicoService medicoService;
    private EspecialidadeService especialidadeService; // Para carregar as especialidades

    private JTextField idField;
    private JTextField crmField; // CRM agora é Integer
    private JTextField nomeCompletoField; // Mapeia para 'nome' no DB
    private JTextField enderecoField;
    private JTextField telefoneField; // Mapeia para VARCHAR no DB (se você mudou o DDL)
    private JComboBox<Especialidade> especialidadeComboBox; // JComboBox de objetos Especialidade

    private JButton saveButton;
    private JButton newButton;
    private JButton deleteButton;
    private JButton listButton;

    private JTable medicoTable;
    private MedicoTableModel medicoTableModel;

    public MedicoFormFrame() {
        super("Cadastro de Médicos");
        this.medicoService = new MedicoService();
        this.especialidadeService = new EspecialidadeService(); // Instancia o serviço de especialidade
        initComponents();
        setupLayout();
        addListeners();
        loadEspecialidades(); // Carrega as especialidades ao iniciar o formulário
        loadMedicosIntoTable(); // Carrega os médicos ao iniciar
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        idField = new JTextField(5);
        idField.setEditable(false);
        crmField = new JTextField(10);
        nomeCompletoField = new JTextField(25);
        enderecoField = new JTextField(30);
        telefoneField = new JTextField(15);

        // JComboBox para Especialidade
        especialidadeComboBox = new JComboBox<>();

        saveButton = new JButton("Salvar");
        newButton = new JButton("Novo");
        deleteButton = new JButton("Excluir");
        listButton = new JButton("Listar Tudo");

        medicoTableModel = new MedicoTableModel();
        medicoTable = new JTable(medicoTableModel);
        medicoTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    private void setupLayout() {
        setLayout(new BorderLayout(10, 10));

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;
        addFormField(formPanel, gbc, "ID:", idField, row++, 0);
        addFormField(formPanel, gbc, "CRM:", crmField, row++, 0);
        addFormField(formPanel, gbc, "Nome Completo:", nomeCompletoField, row++, 0);
        addFormField(formPanel, gbc, "Endereço:", enderecoField, row++, 0);
        addFormField(formPanel, gbc, "Telefone:", telefoneField, row++, 0);
        addFormField(formPanel, gbc, "Especialidade:", especialidadeComboBox, row++, 0);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.add(newButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(listButton);

        add(formPanel, BorderLayout.NORTH);
        add(new JScrollPane(medicoTable), BorderLayout.CENTER);
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
        newButton.addActionListener(e -> clearForm());
        saveButton.addActionListener(e -> saveMedico());
        deleteButton.addActionListener(e -> deleteMedico());
        listButton.addActionListener(e -> loadMedicosIntoTable());

        medicoTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && medicoTable.getSelectedRow() != -1) {
                int selectedRow = medicoTable.getSelectedRow();
                Medico selectedMedico = medicoTableModel.getMedicoAt(selectedRow);
                populateForm(selectedMedico);
            }
        });

        // Listener para validação de CRM: garantir que seja numérico
        crmField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                if (!crmField.getText().isEmpty()) {
                    try {
                        Integer.parseInt(crmField.getText());
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(MedicoFormFrame.this, "O CRM deve ser um número inteiro válido.", "Erro de Formato", JOptionPane.WARNING_MESSAGE);
                        crmField.requestFocusInWindow(); // Volta o foco para o campo CRM
                    }
                }
            }
        });
    }

    private void clearForm() {
        idField.setText("");
        crmField.setText("");
        nomeCompletoField.setText("");
        enderecoField.setText("");
        telefoneField.setText("");
        especialidadeComboBox.setSelectedIndex(-1); // Limpa a seleção do ComboBox
        saveButton.setText("Salvar");
        medicoTable.clearSelection();
    }

    private void populateForm(Medico medico) {
        if (medico != null) {
            idField.setText(String.valueOf(medico.getId()));
            crmField.setText(String.valueOf(medico.getCrm()));
            nomeCompletoField.setText(medico.getNomeCompleto());
            enderecoField.setText(medico.getEndereco());
            telefoneField.setText(medico.getTelefone());

            // Seleciona a especialidade correta no ComboBox
            if (medico.getEspecialidade() != null) {
                Especialidade selectedEspecialidade = medico.getEspecialidade();
                for (int i = 0; i < especialidadeComboBox.getItemCount(); i++) {
                    if (especialidadeComboBox.getItemAt(i).equals(selectedEspecialidade)) {
                        especialidadeComboBox.setSelectedIndex(i);
                        break;
                    }
                }
            } else {
                especialidadeComboBox.setSelectedIndex(-1);
            }
            saveButton.setText("Atualizar");
        }
    }

    // Carrega as especialidades no JComboBox
    private void loadEspecialidades() {
        try {
            List<Especialidade> especialidades = especialidadeService.listarTodasEspecialidades();
            especialidadeComboBox.removeAllItems(); // Limpa itens existentes
            for (Especialidade esp : especialidades) {
                especialidadeComboBox.addItem(esp); // Adiciona cada objeto Especialidade
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar especialidades: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void saveMedico() {
        try {
            Medico medico = new Medico();
            if (!idField.getText().isEmpty()) {
                medico.setId(Integer.parseInt(idField.getText()));
            }

            // Converte CRM para Integer
            try {
                medico.setCrm(Integer.parseInt(crmField.getText()));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("O CRM deve ser um número inteiro válido.");
            }

            medico.setNomeCompleto(nomeCompletoField.getText());
            medico.setEndereco(enderecoField.getText());
            medico.setTelefone(telefoneField.getText());

            // Obtém a Especialidade selecionada no ComboBox
            Especialidade selectedEspecialidade = (Especialidade) especialidadeComboBox.getSelectedItem();
            if (selectedEspecialidade == null) {
                throw new IllegalArgumentException("A especialidade é obrigatória.");
            }
            medico.setEspecialidade(selectedEspecialidade);

            medicoService.salvarMedico(medico);
            JOptionPane.showMessageDialog(this, "Médico salvo com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            loadMedicosIntoTable();
            clearForm();
        } catch (IllegalArgumentException | IllegalStateException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro de Validação/Negócio", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro inesperado ao salvar médico: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void deleteMedico() {
        String idText = idField.getText();
        if (idText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Selecione um médico para excluir.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja excluir este médico?", "Confirmar Exclusão", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                int id = Integer.parseInt(idText);
                medicoService.deletarMedico(id);
                JOptionPane.showMessageDialog(this, "Médico excluído com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                loadMedicosIntoTable();
                clearForm();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "ID inválido.", "Erro", JOptionPane.ERROR_MESSAGE);
            } catch (IllegalArgumentException | IllegalStateException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro de Validação/Negócio", JOptionPane.WARNING_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro inesperado ao excluir médico: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    private void loadMedicosIntoTable() {
        try {
            List<Medico> medicos = medicoService.listarTodosMedicos();
            medicoTableModel.setMedicos(medicos);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar médicos: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private class MedicoTableModel extends AbstractTableModel {
        private final String[] COLUMNS = {"ID", "CRM", "Nome Completo", "Telefone", "Especialidade"};
        private List<Medico> medicos;

        public MedicoTableModel() {
            this.medicos = new ArrayList<>();
        }

        public void setMedicos(List<Medico> medicos) {
            this.medicos = medicos;
            fireTableDataChanged();
        }

        public Medico getMedicoAt(int rowIndex) {
            return medicos.get(rowIndex);
        }

        @Override
        public int getRowCount() {
            return medicos.size();
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
            Medico medico = medicos.get(rowIndex);
            switch (columnIndex) {
                case 0: return medico.getId();
                case 1: return medico.getCrm(); // CRM é Integer
                case 2: return medico.getNomeCompleto();
                case 3: return medico.getTelefone();
                case 4: return medico.getEspecialidade() != null ? medico.getEspecialidade().getNome() : "N/A"; // Exibe o nome da especialidade
                default: return null;
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MedicoFormFrame().setVisible(true);
        });
    }
}