package gui;

import entities.Exame;
import services.ExameService;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.text.NumberFormat; // Para formatação de moeda
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class ExameFormFrame extends JFrame {

    private ExameService exameService;

    private JTextField idField;
    private JTextField nomeField;
    private JFormattedTextField valorField; // Usar JFormattedTextField para valor monetário
    private JTextArea orientacoesArea;
    private JScrollPane orientacoesScrollPane;

    private JButton saveButton;
    private JButton newButton;
    private JButton deleteButton;
    private JButton listButton;

    private JTable exameTable;
    private ExameTableModel exameTableModel;

    public ExameFormFrame() {
        super("Cadastro de Exames");
        this.exameService = new ExameService();
        initComponents();
        setupLayout();
        addListeners();
        loadExamesIntoTable();
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 500);
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        idField = new JTextField(5);
        idField.setEditable(false);
        nomeField = new JTextField(20);

        // JFormattedTextField para valor monetário
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(); // Formato de moeda local
        valorField = new JFormattedTextField(currencyFormat);
        valorField.setColumns(15);
        valorField.setValue(BigDecimal.ZERO); // Valor inicial

        orientacoesArea = new JTextArea(5, 30); // 5 linhas, 30 colunas
        orientacoesArea.setLineWrap(true); // Quebra de linha automática
        orientacoesArea.setWrapStyleWord(true); // Quebra por palavra
        orientacoesScrollPane = new JScrollPane(orientacoesArea); // Adiciona rolagem para área de texto

        saveButton = new JButton("Salvar");
        newButton = new JButton("Novo");
        deleteButton = new JButton("Excluir");
        listButton = new JButton("Listar Tudo");

        exameTableModel = new ExameTableModel();
        exameTable = new JTable(exameTableModel);
        exameTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    private void setupLayout() {
        setLayout(new BorderLayout(10, 10));

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;
        addFormField(formPanel, gbc, "ID:", idField, row++, 0);
        addFormField(formPanel, gbc, "Nome do Exame:", nomeField, row++, 0);
        addFormField(formPanel, gbc, "Valor:", valorField, row++, 0);

        // Orientacoes (JTextArea com JScrollPane)
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Orientações:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = row;
        gbc.gridwidth = 1; // Ocupa uma coluna
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH; // Preenche em ambas as direções
        gbc.weighty = 0.5; // Permite que a área de texto se expanda verticalmente
        formPanel.add(orientacoesScrollPane, gbc);
        gbc.weighty = 0; // Reseta weighty
        gbc.fill = GridBagConstraints.HORIZONTAL; // Volta ao preenchimento horizontal padrão
        row++; // Incrementa a linha

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.add(newButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(listButton);

        add(formPanel, BorderLayout.NORTH);
        add(new JScrollPane(exameTable), BorderLayout.CENTER);
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
        saveButton.addActionListener(e -> saveExame());
        deleteButton.addActionListener(e -> deleteExame());
        listButton.addActionListener(e -> loadExamesIntoTable());

        exameTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && exameTable.getSelectedRow() != -1) {
                int selectedRow = exameTable.getSelectedRow();
                Exame selectedExame = exameTableModel.getExameAt(selectedRow);
                populateForm(selectedExame);
            }
        });
    }

    private void clearForm() {
        idField.setText("");
        nomeField.setText("");
        valorField.setValue(BigDecimal.ZERO); // Volta para zero
        orientacoesArea.setText("");
        saveButton.setText("Salvar");
        exameTable.clearSelection();
    }

    private void populateForm(Exame exame) {
        if (exame != null) {
            idField.setText(String.valueOf(exame.getId()));
            nomeField.setText(exame.getNome());
            valorField.setValue(exame.getValor()); // Define o BigDecimal no JFormattedTextField
            orientacoesArea.setText(exame.getOrientacoes());
            saveButton.setText("Atualizar");
        }
    }

    private void saveExame() {
        try {
            Exame exame = new Exame();
            if (!idField.getText().isEmpty()) {
                exame.setId(Integer.parseInt(idField.getText()));
            }
            exame.setNome(nomeField.getText());

            // Lidar com o valor formatado
            try {
                // Parseia o valor do campo formatado para BigDecimal
                Number parsedValue = (Number) valorField.getValue();
                if (parsedValue == null) {
                    throw new IllegalArgumentException("O valor do exame é obrigatório.");
                }
                exame.setValor(new BigDecimal(parsedValue.toString()));
            } catch (ClassCastException ex) { // Se o valor não for um Number (ex: texto vazio)
                throw new IllegalArgumentException("Formato de valor inválido. Digite um número.");
            }

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
                int id = Integer.parseInt(idText);
                exameService.deletarExame(id);
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
            List<Exame> exames = exameService.listarTodosExames();
            exameTableModel.setExames(exames);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar exames: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private class ExameTableModel extends AbstractTableModel {
        private final String[] COLUMNS = {"ID", "Nome do Exame", "Valor"};
        private List<Exame> exames;

        public ExameTableModel() {
            this.exames = new ArrayList<>();
        }

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
            return COLUMNS.length;
        }

        @Override
        public String getColumnName(int column) {
            return COLUMNS[column];
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            Exame exame = exames.get(rowIndex);
            switch (columnIndex) {
                case 0: return exame.getId();
                case 1: return exame.getNome();
                case 2: return exame.getValor(); // BigDecimal será exibido diretamente
                default: return null;
            }
        }
    }
}