package gui.cadastro;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import entities.Exame;
import services.ExameService;

public class CadastroExame extends JFrame {

    private final ExameService exameService = new ExameService();

    private final JTextField idField = new JTextField(5);
    private final JTextField nomeField = new JTextField(20);
    private final JFormattedTextField valorField;
    private final JTextArea orientacoesArea = new JTextArea(5, 30);
    private final JScrollPane orientacoesScrollPane;

    private final JButton saveButton = new JButton("Salvar");
    private final JButton newButton = new JButton("Novo");
    private final JButton deleteButton = new JButton("Excluir");
    private final JButton listButton = new JButton("Listar Tudo");

    private final ExameTableModel exameTableModel = new ExameTableModel();
    private final JTable exameTable = new JTable(exameTableModel);

    public CadastroExame() {
        super("Cadastro de Exames");

        idField.setEditable(false);

        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();
        valorField = new JFormattedTextField(currencyFormat);
        valorField.setColumns(15);
        valorField.setValue(BigDecimal.ZERO);

        orientacoesArea.setLineWrap(true);
        orientacoesArea.setWrapStyleWord(true);
        orientacoesScrollPane = new JScrollPane(orientacoesArea);

        exameTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        initLayout();
        addListeners();
        loadExamesIntoTable();

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 500);
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
        addFormField(formPanel, gbc, "Nome do Exame:", nomeField, row++);
        addFormField(formPanel, gbc, "Valor:", valorField, row++);

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Orientações:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 0.5;
        gbc.fill = GridBagConstraints.BOTH;
        formPanel.add(orientacoesScrollPane, gbc);

        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.add(newButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(listButton);

        add(formPanel, BorderLayout.NORTH);
        add(new JScrollPane(exameTable), BorderLayout.CENTER);
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
        saveButton.addActionListener(e -> saveExame());
        deleteButton.addActionListener(e -> deleteExame());
        listButton.addActionListener(e -> loadExamesIntoTable());

        exameTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && exameTable.getSelectedRow() != -1) {
                populateForm(exameTableModel.getExameAt(exameTable.getSelectedRow()));
            }
        });
    }

    private void clearForm() {
        idField.setText("");
        nomeField.setText("");
        valorField.setValue(BigDecimal.ZERO);
        orientacoesArea.setText("");
        saveButton.setText("Salvar");
        exameTable.clearSelection();
    }

    private void populateForm(Exame exame) {
        if (exame == null) return;
        idField.setText(String.valueOf(exame.getId()));
        nomeField.setText(exame.getNome());
        valorField.setValue(exame.getValor());
        orientacoesArea.setText(exame.getOrientacoes());
        saveButton.setText("Atualizar");
    }

    private void saveExame() {
        try {
            Exame exame = new Exame();
            if (!idField.getText().isEmpty()) exame.setId(Integer.parseInt(idField.getText()));
            exame.setNome(nomeField.getText());

            Number parsedValue = (Number) valorField.getValue();
            if (parsedValue == null) throw new IllegalArgumentException("O valor do exame é obrigatório.");
            exame.setValor(new BigDecimal(parsedValue.toString()));

            exame.setOrientacoes(orientacoesArea.getText());

            exameService.salvarExame(exame);

            JOptionPane.showMessageDialog(this, "Exame salvo com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            loadExamesIntoTable();
            clearForm();
        } catch (IllegalArgumentException | IllegalStateException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro de Validação/Negócio", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro inesperado ao salvar exame: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void deleteExame() {
        String idText = idField.getText();
        if (idText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Selecione um exame para excluir.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja excluir este exame?", "Confirmar Exclusão", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                exameService.deletarExame(Integer.parseInt(idText));
                JOptionPane.showMessageDialog(this, "Exame excluído com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                loadExamesIntoTable();
                clearForm();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "ID inválido.", "Erro", JOptionPane.ERROR_MESSAGE);
            } catch (IllegalArgumentException | IllegalStateException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro de Validação/Negócio", JOptionPane.WARNING_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro inesperado ao excluir exame: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    private void loadExamesIntoTable() {
        try {
            exameTableModel.setExames(exameService.listarTodosExames());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar exames: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private static class ExameTableModel extends AbstractTableModel {

        private final String[] columns = {"ID", "Nome do Exame", "Valor"};
        private List<Exame> exames = new ArrayList<>();

        public void setExames(List<Exame> exames) {
            this.exames = exames;
            fireTableDataChanged();
        }

        public Exame getExameAt(int rowIndex) {
            return exames.get(rowIndex);
        }

        @Override
        public int getRowCount() {
            return exames.size();
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
            Exame exame = exames.get(rowIndex);
            return switch (columnIndex) {
                case 0 -> exame.getId();
                case 1 -> exame.getNome();
                case 2 -> exame.getValor();
                default -> null;
            };
        }
    }
}
