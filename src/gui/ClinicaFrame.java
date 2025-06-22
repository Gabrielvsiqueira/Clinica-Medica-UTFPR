package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ClinicaFrame extends JFrame {

    public ClinicaFrame() {
        super("Sistema de Gestão de Clínica");
        initComponents();
        setupLayout();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1024, 768);
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        JMenuBar menuBar = new JMenuBar();

        // --- Menu Cadastros ---
        JMenu cadastroMenu = new JMenu("Cadastros");
        JMenuItem pacientesMenuItem = new JMenuItem("Pacientes");
        JMenuItem medicosMenuItem = new JMenuItem("Médicos");
        JMenuItem especialidadesMenuItem = new JMenuItem("Especialidades");
        JMenuItem examesMenuItem = new JMenuItem("Exames");

        cadastroMenu.add(pacientesMenuItem);
        cadastroMenu.add(medicosMenuItem);
        cadastroMenu.add(especialidadesMenuItem);
        cadastroMenu.add(examesMenuItem);

        // --- Menu Agendamentos ---
        JMenu agendamentosMenu = new JMenu("Agendamentos");
        JMenuItem agendarConsultaMenuItem = new JMenuItem("Agendar Consulta");
        JMenuItem agendarExameMenuItem = new JMenuItem("Agendar Exame");
        JMenuItem agendaMedicoMenuItem = new JMenuItem("Agenda do Médico"); // Adicionado
        JMenuItem agendaExameMenuItem = new JMenuItem("Agenda do Exame");   // Adicionado

        agendamentosMenu.add(agendarConsultaMenuItem);
        agendamentosMenu.add(agendarExameMenuItem);
        agendamentosMenu.addSeparator();
        agendamentosMenu.add(agendaMedicoMenuItem); // Adicionado
        agendamentosMenu.add(agendaExameMenuItem);   // Adicionado

        // --- Menu Relatórios ---
        JMenu relatoriosMenu = new JMenu("Relatórios");
        JMenuItem historicoPacienteMenuItem = new JMenuItem("Histórico do Paciente"); // Adicionado

        relatoriosMenu.add(historicoPacienteMenuItem); // Adicionado

        menuBar.add(cadastroMenu);
        menuBar.add(agendamentosMenu);
        menuBar.add(relatoriosMenu);

        setJMenuBar(menuBar);

        // --- Listeners para os itens de menu ---
        pacientesMenuItem.addActionListener(e -> new PacienteFormFrame().setVisible(true));
        medicosMenuItem.addActionListener(e -> new MedicoFormFrame().setVisible(true));
        especialidadesMenuItem.addActionListener(e -> new EspecialidadeFormFrame().setVisible(true));
        examesMenuItem.addActionListener(e -> new ExameFormFrame().setVisible(true));

        agendarConsultaMenuItem.addActionListener(e -> new AgendarConsultaDialog(this).setVisible(true));
        agendarExameMenuItem.addActionListener(e -> new AgendarExameDialog(this).setVisible(true));

        // Adicionados os listeners para as novas telas de visualização/relatório
        agendaMedicoMenuItem.addActionListener(e -> new AgendaMedicoView(this).setVisible(true));
        agendaExameMenuItem.addActionListener(e -> new AgendaExameView(this).setVisible(true));
        historicoPacienteMenuItem.addActionListener(e -> new HistoricoPacienteView(this).setVisible(true));
    }

    private void setupLayout() {
        JPanel contentPanel = new JPanel(new BorderLayout());
        JLabel welcomeLabel = new JLabel("Bem-vindo ao Sistema de Gestão de Clínica da UTFPR!", SwingConstants.CENTER);
        contentPanel.add(welcomeLabel, BorderLayout.CENTER);
        add(contentPanel);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ClinicaFrame().setVisible(true);
        });
    }
}