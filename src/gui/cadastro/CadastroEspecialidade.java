package gui.cadastro;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import entities.Especialidade;
import services.EspecialidadeService;


public class CadastroEspecialidade extends JFrame {

    private final EspecialidadeService especialidadeService = new EspecialidadeService();

    private final JTextField idField = new JTextField(5);
    private final JTextField nomeField = new JTextField(20);

    private final JButton saveButton = new JButton("Salvar");
    private final JButton newButton = new JButton("Novo");
    private final JButton deleteButton = new JButton("Excluir");
    private final JButton listButton = new JButton("Listar Tudo");

    private final EspecialidadeTableModel especialidadeTableModel = new EspecialidadeTableModel();
    private final JTable especialidadeTable = new JTable(especialidadeTableModel);

    public CadastroEspecialidade() {
        super("Cadastro de Especialidades");

        idField.setEditable(false);
        especialidadeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        initLayout();
        addListeners();
        loadEspecialidadesIntoTable();

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(600, 400);
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
        addFormField(formPanel, gbc, "Nome da Especialidade:", nomeField, row++);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.add(newButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(listButton);

        add(formPanel, BorderLayout.NORTH);
        add(new JScrollPane(especialidadeTable), BorderLayout.CENTER);
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
        saveButton.addActionListener(e -> saveEspecialidade());
        deleteButton.addActionListener(e -> deleteEspecialidade());
        listButton.addActionListener(e -> loadEspecialidadesIntoTable());

        especialidadeTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && especialidadeTable.getSelectedRow() != -1) {
                populateForm(especialidadeTableModel.getEspecialidadeAt(especialidadeTable.getSelectedRow()));
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
        if (especialidade == null) return;
        idField.setText(String.valueOf(especialidade.getId()));
        nomeField.setText(especialidade.getNome());
        saveButton.setText("Atualizar");
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
                especialidadeService.deletarEspecialidade(Integer.parseInt(idText));
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
            especialidadeTableModel.setEspecialidades(especialidadeService.listarTodasEspecialidades());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar especialidades: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private static class EspecialidadeTableModel extends AbstractTableModel {
        private final String[] columns = {"ID", "Nome da Especialidade"};
        private List<Especialidade> especialidades = new ArrayList<>();

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
            return columns.length;
        }

        @Override
        public String getColumnName(int column) {
            return columns[column];
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            Especialidade especialidade = especialidades.get(rowIndex);
            return switch (columnIndex) {
                case 0 -> especialidade.getId();
                case 1 -> especialidade.getNome();
                default -> null;
            };
        }
    }
}
