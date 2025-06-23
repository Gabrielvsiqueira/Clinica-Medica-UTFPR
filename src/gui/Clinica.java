package gui;

import gui.agendamento.AgendaExameView;
import gui.agendamento.AgendaMedicoView;
import gui.agendamento.AgendarConsultaDialog;
import gui.agendamento.AgendarExameDialog;
import gui.cadastro.CadastroEspecialidade;
import gui.cadastro.CadastroExame;
import gui.cadastro.CadastroMedico;
import gui.cadastro.CadastroPaciente;

import javax.swing.*;
import java.awt.*;

public class Clinica extends JFrame {

    public Clinica() {
        super("Sistema de Gestão de Clínica");
        initComponents();
        setupLayout();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1024, 768);
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        JMenuBar menuBar = new JMenuBar();

        JMenu cadastroMenu = new JMenu("Cadastros");
        JMenuItem pacientesMenuItem = new JMenuItem("Pacientes");
        JMenuItem medicosMenuItem = new JMenuItem("Médicos");
        JMenuItem especialidadesMenuItem = new JMenuItem("Especialidades");
        JMenuItem examesMenuItem = new JMenuItem("Exames");

        cadastroMenu.add(pacientesMenuItem);
        cadastroMenu.add(medicosMenuItem);
        cadastroMenu.add(especialidadesMenuItem);
        cadastroMenu.add(examesMenuItem);

        JMenu agendamentosMenu = new JMenu("Agendamentos");
        JMenuItem agendarConsultaMenuItem = new JMenuItem("Agendar Consulta");
        JMenuItem agendarExameMenuItem = new JMenuItem("Agendar Exame");
        JMenuItem agendaMedicoMenuItem = new JMenuItem("Agenda do Médico");
        JMenuItem agendaExameMenuItem = new JMenuItem("Agenda do Exame");

        agendamentosMenu.add(agendarConsultaMenuItem);
        agendamentosMenu.add(agendarExameMenuItem);
        agendamentosMenu.addSeparator();
        agendamentosMenu.add(agendaMedicoMenuItem);
        agendamentosMenu.add(agendaExameMenuItem);

        JMenu relatoriosMenu = new JMenu("Relatórios");
        JMenuItem historicoPacienteMenuItem = new JMenuItem("Histórico do Paciente");

        relatoriosMenu.add(historicoPacienteMenuItem);

        menuBar.add(cadastroMenu);
        menuBar.add(agendamentosMenu);
        menuBar.add(relatoriosMenu);

        setJMenuBar(menuBar);

        pacientesMenuItem.addActionListener(e -> new CadastroPaciente().setVisible(true));
        medicosMenuItem.addActionListener(e -> new CadastroMedico().setVisible(true));
        especialidadesMenuItem.addActionListener(e -> new CadastroEspecialidade().setVisible(true));
        examesMenuItem.addActionListener(e -> new CadastroExame().setVisible(true));
        agendarConsultaMenuItem.addActionListener(e -> new AgendarConsultaDialog(this).setVisible(true));
        agendarExameMenuItem.addActionListener(e -> new AgendarExameDialog(this).setVisible(true));
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
            new Clinica().setVisible(true);
        });
    }
}