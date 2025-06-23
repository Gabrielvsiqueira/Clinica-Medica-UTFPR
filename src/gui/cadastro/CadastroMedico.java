package gui.cadastro;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.ArrayList;
import java.util.List;

import entities.Especialidade;
import entities.Medico;
import services.EspecialidadeService;
import services.MedicoService;

public class CadastroMedico extends JFrame {

    private final MedicoService medicoService = new MedicoService();
    private final EspecialidadeService especialidadeService = new EspecialidadeService();

    private final JTextField idField = new JTextField(5);
    private final JTextField crmField = new JTextField(10);
    private final JTextField nomeCompletoField = new JTextField(25);
    private final JTextField enderecoField = new JTextField(30);
    private final JTextField telefoneField = new JTextField(15);
    private final JComboBox<Especialidade> especialidadeComboBox = new JComboBox<>();

    private final JButton saveButton = new JButton("Salvar");
    private final JButton newButton = new JButton("Novo");
    private final JButton deleteButton = new JButton("Excluir");
    private final JButton listButton = new JButton("Listar Tudo");

    private final MedicoTableModel medicoTableModel = new MedicoTableModel();
    private final JTable medicoTable = new JTable(medicoTableModel);

    public CadastroMedico() {
        super("Cadastro de Médicos");
        idField.setEditable(false);
        medicoTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        initLayout();
        addListeners();
        loadEspecialidades();
        loadMedicosIntoTable();
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);
    }

    private void initLayout() {
        setLayout(new BorderLayout(10, 10));

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;
        addFormField(formPanel, gbc, "ID:", idField, row++);
        addFormField(formPanel, gbc, "CRM:", crmField, row++);
        addFormField(formPanel, gbc, "Nome Completo:", nomeCompletoField, row++);
        addFormField(formPanel, gbc, "Endereço:", enderecoField, row++);
        addFormField(formPanel, gbc, "Telefone:", telefoneField, row++);
        addFormField(formPanel, gbc, "Especialidade:", especialidadeComboBox, row++);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.add(newButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(listButton);

        add(formPanel, BorderLayout.NORTH);
        add(new JScrollPane(medicoTable), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void addFormField(JPanel panel, GridBagConstraints gbc, String labelText, JComponent field, int row) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(new JLabel(labelText), gbc);
        gbc.gridx = 1;
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
                populateForm(medicoTableModel.getMedicoAt(medicoTable.getSelectedRow()));
            }
        });

        crmField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                if (!crmField.getText().isEmpty()) {
                    try {
                        Integer.parseInt(crmField.getText());
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(CadastroMedico.this, "O CRM deve ser um número inteiro válido.", "Erro de Formato", JOptionPane.WARNING_MESSAGE);
                        crmField.requestFocusInWindow();
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
        especialidadeComboBox.setSelectedIndex(-1);
        saveButton.setText("Salvar");
        medicoTable.clearSelection();
    }

    private void populateForm(Medico medico) {
        if (medico == null) return;

        idField.setText(String.valueOf(medico.getId()));
        crmField.setText(String.valueOf(medico.getCrm()));
        nomeCompletoField.setText(medico.getNomeCompleto());
        enderecoField.setText(medico.getEndereco());
        telefoneField.setText(medico.getTelefone());

        Especialidade especialidade = medico.getEspecialidade();
        if (especialidade != null) {
            for (int i = 0; i < especialidadeComboBox.getItemCount(); i++) {
                if (especialidadeComboBox.getItemAt(i).equals(especialidade)) {
                    especialidadeComboBox.setSelectedIndex(i);
                    break;
                }
            }
        } else {
            especialidadeComboBox.setSelectedIndex(-1);
        }
        saveButton.setText("Atualizar");
    }

    private void loadEspecialidades() {
        try {
            especialidadeComboBox.removeAllItems();
            for (Especialidade esp : especialidadeService.listarTodasEspecialidades()) {
                especialidadeComboBox.addItem(esp);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar especialidades: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void saveMedico() {
        try {
            Medico medico = new Medico();
            if (!idField.getText().isEmpty()) medico.setId(Integer.parseInt(idField.getText()));
            try {
                medico.setCrm(Integer.parseInt(crmField.getText()));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("O CRM deve ser um número inteiro válido.");
            }
            medico.setNomeCompleto(nomeCompletoField.getText());
            medico.setEndereco(enderecoField.getText());
            medico.setTelefone(telefoneField.getText());

            Especialidade selectedEspecialidade = (Especialidade) especialidadeComboBox.getSelectedItem();
            if (selectedEspecialidade == null) throw new IllegalArgumentException("A especialidade é obrigatória.");
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
                medicoService.deletarMedico(Integer.parseInt(idText));
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
            medicoTableModel.setMedicos(medicoService.listarTodosMedicos());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar médicos: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private static class MedicoTableModel extends AbstractTableModel {

        private final String[] columns = {"ID", "CRM", "Nome Completo", "Telefone", "Especialidade"};
        private List<Medico> medicos = new ArrayList<>();

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
            return columns.length;
        }

        @Override
        public String getColumnName(int column) {
            return columns[column];
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            Medico medico = medicos.get(rowIndex);
            return switch (columnIndex) {
                case 0 -> medico.getId();
                case 1 -> medico.getCrm();
                case 2 -> medico.getNomeCompleto();
                case 3 -> medico.getTelefone();
                case 4 -> medico.getEspecialidade() != null ? medico.getEspecialidade().getNome() : "N/A";
                default -> null;
            };
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CadastroMedico().setVisible(true));
    }
}
