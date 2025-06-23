# Clínica Médica UTFPR - Projeto Disciplina 002

Projeto criado para a disciplina de Orientação a Objetos 2, onde foram aplicados conhecimentos adquiridos ao longo das aulas do semestre.

## Sobre o Projeto

Este projeto consiste no desenvolvimento de um sistema de gestão médica com interface gráfica (usando JFrame), que permite o gerenciamento integrado de pacientes, médicos, especialidades, consultas e exames. O sistema utiliza uma base de dados para armazenamento persistente das informações e segue a arquitetura em camadas, garantindo organização e manutenção do código.

As principais funcionalidades incluem:

- Cadastro e manutenção (CRUD) de pacientes, médicos, especialidades e exames.
- Agendamento de consultas médicas com controle rigoroso para evitar sobreposição de horários na agenda dos médicos.
- Agendamento e controle de exames realizados na clínica, também sem sobreposição de horários.
- Permite que a recepcionista atualize o status de consultas e exames para refletir sua realização.
- Geração de relatórios em tela e exportação para arquivos, apresentando:
  - Agenda de um médico específico,
  - Agenda de um exame específico,
  - Histórico completo de consultas e exames (agendados ou realizados) de um paciente.

O sistema também implementa programação concorrente para garantir maior eficiência e robustez no processamento das operações.

---

## Configuração da Base de Dados

1. Crie uma base de dados com o nome `clinica` no MySQL.
2. Na raiz do projeto Java, há uma pasta chamada `sql` contendo o arquivo `database.sql`. Importe este arquivo para a base de dados `clinica` criada no passo anterior.

---

## Configuração do Projeto

- O projeto utiliza a biblioteca **jcalendar 1.4**, que está dentro da pasta `lib`. Basta adicioná-la como dependência nas propriedades do projeto (Project Properties > Libraries > Add JARs).

1. Se estiver utilizando o Eclipse IDE:
   - Importe o projeto para o Eclipse:  
     `File > Import > General > Existing Projects into Workspace`

2. Adicione o conector MySQL:
   - Faça o download do conector MySQL neste link:  
     [https://moodle.utfpr.edu.br/mod/resource/view.php?id=1770834](https://moodle.utfpr.edu.br/mod/resource/view.php?id=1770834)
   - Clique com o botão direito no projeto e selecione `Properties`.
   - Vá em `Java Build Path > Libraries > Modulepath`.
   - Se o conector não for reconhecido, exclua-o e clique em `Add External JARs...`.
   - Selecione o conector baixado e adicione-o.

3. O arquivo do conector já está incluso na pasta `lib`, mas caso queira garantir, pode seguir o passo 2 para adicionar novamente.

---
