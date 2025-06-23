package gui.cadastro;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

import entities.Paciente;
import services.PacienteService;

public class CadastroPaciente extends JFrame {
    private PacienteService service = new PacienteService();
    private JTextField idField = createField(false);
    private JTextField nomeField = new JTextField(20);
    private JTextField fotoField = new JTextField(20);
    private JTextField dataNascimentoField = new JTextField("AAAA-MM-DD", 10);
    private JTextField sexoField = new JTextField(10);
    private JTextField enderecoField = new JTextField(20);
    private JTextField telefoneField = new JTextField(15);
    private JComboBox<String> pagamentoCombo = new JComboBox<>(new String[]{"Particular", "Convênio", "Outros"});
    private JButton saveButton = new JButton("Salvar");
    private JButton newButton = new JButton("Novo");
    private JButton deleteButton = new JButton("Excluir");
    private JButton listButton = new JButton("Listar Tudo");
    private JTable table = new JTable(new PacienteTableModel());

    public CadastroPaciente() {
        super("Cadastro de Pacientes");
        initUI();
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(700, 500);
        setLocationRelativeTo(null);
    }

    private void initUI() {
        JPanel form = new JPanel(new GridLayout(0, 2));
        addRow(form, "ID:", idField);
        addRow(form, "Nome:", nomeField);
        addRow(form, "Foto:", fotoField);
        addRow(form, "Nascimento:", dataNascimentoField);
        addRow(form, "Sexo:", sexoField);
        addRow(form, "Endereço:", enderecoField);
        addRow(form, "Telefone:", telefoneField);
        addRow(form, "Pagamento:", pagamentoCombo);

        JPanel buttons = new JPanel();
        buttons.add(newButton); buttons.add(saveButton);
        buttons.add(deleteButton); buttons.add(listButton);

        add(form, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(buttons, BorderLayout.SOUTH);

        newButton.addActionListener(e -> clearForm());
        saveButton.addActionListener(e -> save());
        deleteButton.addActionListener(e -> delete());
        listButton.addActionListener(e -> load());
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1)
                populate((Paciente) ((PacienteTableModel) table.getModel()).getPacienteAt(table.getSelectedRow()));
        });

        load();
    }

    private void addRow(JPanel panel, String label, JComponent comp) {
        panel.add(new JLabel(label));
        panel.add(comp);
    }

    private JTextField createField(boolean editable) {
        JTextField field = new JTextField(5);
        field.setEditable(editable);
        return field;
    }

    private void clearForm() {
        idField.setText(""); nomeField.setText(""); fotoField.setText("");
        dataNascimentoField.setText("AAAA-MM-DD"); sexoField.setText("");
        enderecoField.setText(""); telefoneField.setText("");
        pagamentoCombo.setSelectedIndex(0); saveButton.setText("Salvar");
        table.clearSelection();
    }

    private void populate(Paciente p) {
        idField.setText(p.getId().toString());
        nomeField.setText(p.getNome()); fotoField.setText(p.getFoto());
        dataNascimentoField.setText(p.getDataNascimento().toString());
        sexoField.setText(p.getSexo()); enderecoField.setText(p.getEndereco());
        telefoneField.setText(p.getTelefone()); pagamentoCombo.setSelectedItem(p.getFormaPagamento());
        saveButton.setText("Atualizar");
    }

    private void save() {
        try {
            Paciente p = new Paciente();
            if (!idField.getText().isEmpty()) p.setId(Integer.parseInt(idField.getText()));
            p.setNome(nomeField.getText()); p.setFoto(fotoField.getText());
            p.setDataNascimento(LocalDate.parse(dataNascimentoField.getText()));
            p.setSexo(sexoField.getText()); p.setEndereco(enderecoField.getText());
            p.setTelefone(telefoneField.getText());
            p.setFormaPagamento((String) pagamentoCombo.getSelectedItem());

            service.salvarPaciente(p);
            load(); clearForm();
            JOptionPane.showMessageDialog(this, "Salvo com sucesso!");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage());
        }
    }

    private void delete() {
        if (idField.getText().isEmpty()) return;
        int confirm = JOptionPane.showConfirmDialog(this, "Confirmar exclusão?", "Aviso", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                service.deletarPaciente(Integer.parseInt(idField.getText()));
                load(); clearForm();
                JOptionPane.showMessageDialog(this, "Excluído com sucesso!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage());
            }
        }
    }

    private void load() {
        try {
            ((PacienteTableModel) table.getModel()).setPacientes(service.listarTodosPacientes());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage());
        }
    }

    private static class PacienteTableModel extends AbstractTableModel {
        private String[] colunas = {"ID", "Nome", "Nascimento", "Sexo", "Telefone", "Pagamento"};
        private List<Paciente> pacientes = List.of();

        public void setPacientes(List<Paciente> lista) {
            this.pacientes = lista;
            fireTableDataChanged();
        }

        public Paciente getPacienteAt(int row) {
            return pacientes.get(row);
        }

        public int getRowCount() { return pacientes.size(); }
        public int getColumnCount() { return colunas.length; }
        public String getColumnName(int i) { return colunas[i]; }

        public Object getValueAt(int row, int col) {
            Paciente p = pacientes.get(row);
            return switch (col) {
                case 0 -> p.getId();
                case 1 -> p.getNome();
                case 2 -> p.getDataNascimento();
                case 3 -> p.getSexo();
                case 4 -> p.getTelefone();
                case 5 -> p.getFormaPagamento();
                default -> null;
            };
        }
    }
}
