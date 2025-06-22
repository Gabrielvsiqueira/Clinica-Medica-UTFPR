package gui;

import entities.Especialidade;
import services.EspecialidadeService;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class EspecialidadeFormFrame extends JFrame {

    private EspecialidadeService especialidadeService;

    private JTextField idField;
    private JTextField nomeField; // Mapeia para 'especialidade' no DB

    private JButton saveButton;
    private JButton newButton;
    private JButton deleteButton;
    private JButton listButton;

    private JTable especialidadeTable;
    private EspecialidadeTableModel especialidadeTableModel;

    public EspecialidadeFormFrame() {
        super("Cadastro de Especialidades");
        this.especialidadeService = new EspecialidadeService();
        initComponents();
        setupLayout();
        addListeners();
        loadEspecialidadesIntoTable();
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        idField = new JTextField(5);
        idField.setEditable(false);
        nomeField = new JTextField(20);

        saveButton = new JButton("Salvar");
        newButton = new JButton("Novo");
        deleteButton = new JButton("Excluir");
        listButton = new JButton("Listar Tudo");

        especialidadeTableModel = new EspecialidadeTableModel();
        especialidadeTable = new JTable(especialidadeTableModel);
        especialidadeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    private void setupLayout() {
        setLayout(new BorderLayout(10, 10));

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;
        addFormField(formPanel, gbc, "ID:", idField, row++, 0);
        addFormField(formPanel, gbc, "Nome da Especialidade:", nomeField, row++, 0);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.add(newButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(listButton);

        add(formPanel, BorderLayout.NORTH);
        add(new JScrollPane(especialidadeTable), BorderLayout.CENTER);
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
        saveButton.addActionListener(e -> saveEspecialidade());
        deleteButton.addActionListener(e -> deleteEspecialidade());
        listButton.addActionListener(e -> loadEspecialidadesIntoTable());

        especialidadeTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && especialidadeTable.getSelectedRow() != -1) {
                int selectedRow = especialidadeTable.getSelectedRow();
                Especialidade selectedEspecialidade = especialidadeTableModel.getEspecialidadeAt(selectedRow);
                populateForm(selectedEspecialidade);
            }
        });
    }

    private void clearForm() {
        idField.setText("");
        nomeField.setText("");
        saveButton.setText("Salvar");
        especialidadeTable.clearSelection();
    }

    private void populateForm(Especialidade especialidade) {
        if (especialidade != null) {
            idField.setText(String.valueOf(especialidade.getId()));
            nomeField.setText(especialidade.getNome());
            saveButton.setText("Atualizar");
        }
    }

    private void saveEspecialidade() {
        try {
            Especialidade especialidade = new Especialidade();
            if (!idField.getText().isEmpty()) {
                especialidade.setId(Integer.parseInt(idField.getText()));
            }
            especialidade.setNome(nomeField.getText());

            especialidadeService.salvarEspecialidade(especialidade);
            JOptionPane.showMessageDialog(this, "Especialidade salva com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            loadEspecialidadesIntoTable();
            clearForm();
        } catch (IllegalArgumentException | IllegalStateException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro de Validação/Negócio", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro inesperado ao salvar especialidade: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void deleteEspecialidade() {
        String idText = idField.getText();
        if (idText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Selecione uma especialidade para excluir.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja excluir esta especialidade?", "Confirmar Exclusão", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                int id = Integer.parseInt(idText);
                especialidadeService.deletarEspecialidade(id);
                JOptionPane.showMessageDialog(this, "Especialidade excluída com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                loadEspecialidadesIntoTable();
                clearForm();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "ID inválido.", "Erro", JOptionPane.ERROR_MESSAGE);
            } catch (IllegalArgumentException | IllegalStateException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro de Validação/Negócio", JOptionPane.WARNING_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro inesperado ao excluir especialidade: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    private void loadEspecialidadesIntoTable() {
        try {
            List<Especialidade> especialidades = especialidadeService.listarTodasEspecialidades();
            especialidadeTableModel.setEspecialidades(especialidades);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar especialidades: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private class EspecialidadeTableModel extends AbstractTableModel {
        private final String[] COLUMNS = {"ID", "Nome da Especialidade"};
        private List<Especialidade> especialidades;

        public EspecialidadeTableModel() {
            this.especialidades = new ArrayList<>();
        }

        public void setEspecialidades(List<Especialidade> especialidades) {
            this.especialidades = especialidades;
            fireTableDataChanged();
        }

        public Especialidade getEspecialidadeAt(int rowIndex) {
            return especialidades.get(rowIndex);
        }

        @Override
        public int getRowCount() {
            return especialidades.size();
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
            Especialidade especialidade = especialidades.get(rowIndex);
            switch (columnIndex) {
                case 0: return especialidade.getId();
                case 1: return especialidade.getNome();
                default: return null;
            }
        }
    }
}