package gui;

import entities.Paciente;
import services.PacienteService;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException; // Para lidar com erros de parsing de data
import java.util.ArrayList;
import java.util.List;

// Importar JDateChooser (biblioteca jcalendar) se for usar
// import com.toedter.calendar.JDateChooser;
// import java.time.ZoneId; // Necessário para converter Date para LocalDate

public class PacienteFormFrame extends JFrame {

    private PacienteService pacienteService; // Usaremos o Service

    private JTextField idField;
    private JTextField nomeField;
    private JTextField fotoField; // Para o caminho da foto (VARCHAR no DB)
    // private JDateChooser dataNascimentoChooser; // Descomente se usar JDateChooser
    private JTextField dataNascimentoField; // Alternativa simples se não usar JDateChooser
    private JComboBox<String> sexoComboBox;
    private JTextField enderecoField;
    private JTextField telefoneField; // Mapeia para VARCHAR no DB (se você mudou o DDL)
    private JComboBox<String> formaPagamentoComboBox; // Mapeia para 'pagamento' no DB

    private JButton saveButton;
    private JButton newButton;
    private JButton deleteButton;
    private JButton listButton;

    private JTable pacienteTable;
    private PacienteTableModel pacienteTableModel;

    public PacienteFormFrame() {
        super("Cadastro de Pacientes");
        this.pacienteService = new PacienteService(); // Instancia o serviço
        initComponents();
        setupLayout();
        addListeners();
        loadPatientsIntoTable(); // Carrega os pacientes ao iniciar o formulário
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Fecha apenas esta janela, não a aplicação
        setSize(800, 600);
        setLocationRelativeTo(null); // Centraliza a janela
    }

    private void initComponents() {
        // Campos de entrada
        idField = new JTextField(5);
        idField.setEditable(false); // ID é gerado pelo banco e não deve ser editado
        nomeField = new JTextField(20);
        fotoField = new JTextField(20);

        // Opção 1: Usando JTextField para data (requer que o usuário digite no formato YYYY-MM-DD)
        dataNascimentoField = new JTextField("AAAA-MM-DD", 10);
        // Opção 2: Se usar JDateChooser (descomente e adicione a dependência jcalendar)
        // dataNascimentoChooser = new JDateChooser();
        // dataNascimentoChooser.setDateFormatString("yyyy-MM-dd"); // Define o formato visual

        sexoComboBox = new JComboBox<>(new String[]{"M", "F", "Outro"});
        enderecoField = new JTextField(30);
        telefoneField = new JTextField(15);
        formaPagamentoComboBox = new JComboBox<>(new String[]{"Particular", "Convênio", "Outros"}); // Adicione mais opções se necessário

        // Botões
        saveButton = new JButton("Salvar");
        newButton = new JButton("Novo");
        deleteButton = new JButton("Excluir");
        listButton = new JButton("Listar Tudo");

        // Tabela
        pacienteTableModel = new PacienteTableModel();
        pacienteTable = new JTable(pacienteTableModel);
        pacienteTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Permite selecionar apenas uma linha
    }

    private void setupLayout() {
        setLayout(new BorderLayout(10, 10)); // Margem interna do JFrame

        // Painel de formulário (usando GridBagLayout para alinhamento profissional)
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Espaçamento entre os componentes
        gbc.fill = GridBagConstraints.HORIZONTAL; // Faz os componentes preencherem o espaço horizontal

        int row = 0;
        addFormField(formPanel, gbc, "ID:", idField, row++, 0);
        addFormField(formPanel, gbc, "Nome:", nomeField, row++, 0);
        addFormField(formPanel, gbc, "Foto (Caminho):", fotoField, row++, 0);
        // Se usar JDateChooser, comente a linha abaixo e descomente a de baixo
        addFormField(formPanel, gbc, "Data Nasc. (AAAA-MM-DD):", dataNascimentoField, row++, 0);
        // addFormField(formPanel, gbc, "Data Nasc.:", dataNascimentoChooser, row++, 0);
        addFormField(formPanel, gbc, "Sexo:", sexoComboBox, row++, 0);
        addFormField(formPanel, gbc, "Endereço:", enderecoField, row++, 0);
        addFormField(formPanel, gbc, "Telefone:", telefoneField, row++, 0);
        addFormField(formPanel, gbc, "Forma Pagamento:", formaPagamentoComboBox, row++, 0);

        // Painel de botões
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10)); // Centraliza os botões com espaço
        buttonPanel.add(newButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(listButton);

        // Adiciona os painéis ao JFrame
        add(formPanel, BorderLayout.NORTH);
        add(new JScrollPane(pacienteTable), BorderLayout.CENTER); // Tabela em um JScrollPane para rolagem
        add(buttonPanel, BorderLayout.SOUTH);
    }

    // Método auxiliar para adicionar rótulo e campo ao GridBagLayout
    private void addFormField(JPanel panel, GridBagConstraints gbc, String labelText, JComponent field, int row, int col) {
        gbc.gridx = col;
        gbc.gridy = row;
        gbc.anchor = GridBagConstraints.EAST; // Alinha o rótulo à direita
        panel.add(new JLabel(labelText), gbc);

        gbc.gridx = col + 1;
        gbc.gridy = row;
        gbc.anchor = GridBagConstraints.WEST; // Alinha o campo à esquerda
        gbc.weightx = 1.0; // Dá peso horizontal para o campo se expandir
        panel.add(field, gbc);
        gbc.weightx = 0; // Reseta o peso para o próximo componente
    }

    private void addListeners() {
        newButton.addActionListener(e -> clearForm());
        saveButton.addActionListener(e -> savePaciente());
        deleteButton.addActionListener(e -> deletePaciente());
        listButton.addActionListener(e -> loadPatientsIntoTable());

        // Listener para a seleção da tabela: preenche o formulário com o paciente selecionado
        pacienteTable.getSelectionModel().addListSelectionListener(e -> {
            // Verifica se a seleção está finalizada e se há uma linha selecionada
            if (!e.getValueIsAdjusting() && pacienteTable.getSelectedRow() != -1) {
                int selectedRow = pacienteTable.getSelectedRow();
                Paciente selectedPaciente = pacienteTableModel.getPacienteAt(selectedRow);
                populateForm(selectedPaciente);
            }
        });
    }

    // Limpa todos os campos do formulário
    private void clearForm() {
        idField.setText("");
        nomeField.setText("");
        fotoField.setText("");
        dataNascimentoField.setText("AAAA-MM-DD");
        // if (dataNascimentoChooser != null) dataNascimentoChooser.setDate(null); // Se usar JDateChooser
        sexoComboBox.setSelectedIndex(0);
        enderecoField.setText("");
        telefoneField.setText("");
        formaPagamentoComboBox.setSelectedIndex(0);
        saveButton.setText("Salvar"); // Volta o texto do botão para "Salvar" para nova inserção
        pacienteTable.clearSelection(); // Limpa a seleção da tabela
    }

    // Preenche o formulário com os dados de um objeto Paciente
    private void populateForm(Paciente paciente) {
        if (paciente != null) {
            idField.setText(String.valueOf(paciente.getId()));
            nomeField.setText(paciente.getNome());
            fotoField.setText(paciente.getFoto());
            dataNascimentoField.setText(paciente.getDataNascimento().toString());
            // if (dataNascimentoChooser != null) dataNascimentoChooser.setDate(java.sql.Date.valueOf(paciente.getDataNascimento())); // Se usar JDateChooser
            sexoComboBox.setSelectedItem(paciente.getSexo());
            enderecoField.setText(paciente.getEndereco());
            telefoneField.setText(paciente.getTelefone());
            formaPagamentoComboBox.setSelectedItem(paciente.getFormaPagamento());
            saveButton.setText("Atualizar"); // Muda o texto do botão para "Atualizar" para edição
        }
    }

    // Salva ou atualiza um paciente usando o PacienteService
    private void savePaciente() {
        try {
            Paciente paciente = new Paciente();
            if (!idField.getText().isEmpty()) {
                paciente.setId(Integer.parseInt(idField.getText())); // Para atualização
            }
            paciente.setNome(nomeField.getText());
            paciente.setFoto(fotoField.getText());

            // Tratamento da data de nascimento
            try {
                paciente.setDataNascimento(LocalDate.parse(dataNascimentoField.getText())); // Converte String para LocalDate
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException("Formato de data de nascimento inválido. Use AAAA-MM-DD.");
            }
            // if (dataNascimentoChooser != null && dataNascimentoChooser.getDate() != null) {
            //     paciente.setDataNascimento(dataNascimentoChooser.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
            // } else {
            //     throw new IllegalArgumentException("A data de nascimento é obrigatória.");
            // }

            paciente.setSexo((String) sexoComboBox.getSelectedItem());
            paciente.setEndereco(enderecoField.getText());
            paciente.setTelefone(telefoneField.getText());
            paciente.setFormaPagamento((String) formaPagamentoComboBox.getSelectedItem());

            // Chama o método do SERVIÇO, que contém as validações e a lógica de persistência
            pacienteService.salvarPaciente(paciente);

            JOptionPane.showMessageDialog(this, "Paciente salvo com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            loadPatientsIntoTable(); // Recarrega a tabela após salvar
            clearForm(); // Limpa o formulário
        } catch (IllegalArgumentException | IllegalStateException ex) {
            // Captura exceções de validação ou de regra de negócio lançadas pelo Service
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro de Validação/Negócio", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            // Captura outras exceções inesperadas (ex: erro de conversão de número, I/O do DB)
            JOptionPane.showMessageDialog(this, "Erro inesperado ao salvar paciente: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace(); // Imprime o stack trace no console para depuração
        }
    }

    // Exclui um paciente usando o PacienteService
    private void deletePaciente() {
        String idText = idField.getText();
        if (idText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Selecione um paciente para excluir.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja excluir este paciente?", "Confirmar Exclusão", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                int id = Integer.parseInt(idText);
                // Chama o método do SERVIÇO
                pacienteService.deletarPaciente(id);
                JOptionPane.showMessageDialog(this, "Paciente excluído com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                loadPatientsIntoTable(); // Recarrega a tabela
                clearForm(); // Limpa o formulário
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "ID inválido.", "Erro", JOptionPane.ERROR_MESSAGE);
            } catch (IllegalArgumentException | IllegalStateException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro de Validação/Negócio", JOptionPane.WARNING_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro inesperado ao excluir paciente: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    // Carrega todos os pacientes para a JTable usando o PacienteService
    private void loadPatientsIntoTable() {
        try {
            // Chama o método do SERVIÇO
            List<Paciente> pacientes = pacienteService.listarTodosPacientes();
            pacienteTableModel.setPacientes(pacientes);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar pacientes: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    // Classe interna para o TableModel (facilita a exibição de objetos em JTable)
    private class PacienteTableModel extends AbstractTableModel {
        // Nomes das colunas da tabela que serão exibidas na GUI
        private final String[] COLUMNS = {"ID", "Nome", "Data Nasc.", "Sexo", "Telefone", "Pagamento"};
        private List<Paciente> pacientes;

        public PacienteTableModel() {
            this.pacientes = new ArrayList<>();
        }

        public void setPacientes(List<Paciente> pacientes) {
            this.pacientes = pacientes;
            fireTableDataChanged(); // Notifica a tabela que os dados mudaram
        }

        public Paciente getPacienteAt(int rowIndex) {
            return pacientes.get(rowIndex);
        }

        @Override
        public int getRowCount() {
            return pacientes.size();
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
            Paciente paciente = pacientes.get(rowIndex);
            switch (columnIndex) {
                case 0: return paciente.getId();
                case 1: return paciente.getNome();
                case 2: return paciente.getDataNascimento();
                case 3: return paciente.getSexo();
                case 4: return paciente.getTelefone(); // O telefone é String na Entity (se o DB for VARCHAR)
                case 5: return paciente.getFormaPagamento(); // Mapeia para 'pagamento' no DB
                default: return null;
            }
        }
    }

    // Método main para testar esta tela individualmente
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new PacienteFormFrame().setVisible(true);
        });
    }
}