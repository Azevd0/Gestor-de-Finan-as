# 💰 Sistema de Gestão Financeira - Tutorial de Uso

Este é um sistema de linha de comando (CLI) desenvolvido em Java para o controle completo de finanças pessoais. Abaixo você encontra o passo a passo de como compilar, executar e utilizar cada funcionalidade do sistema.
# Tecnologias
Java (JDK 21), JPA(3.1), Hibernate(6.5), PostgreSQL(42.5), Maven
---

## 🛠️ 1. Como gerar o Executável (Fat JAR)

Para que o sistema rode em qualquer computador sem precisar de uma IDE, precisamos gerar um **Fat JAR** 
### Opção A: Pelo Eclipse (Recomendado)
1. No painel *Package Explorer*, clique com o botão direito sobre o nome do projeto (ou no arquivo `pom.xml`).
2. Acesse **Run As** > **Maven build...** (a opção com as reticências `...`).
3. Na janela que abrir, no campo **Goals**, digite: `clean package`
4. Clique em **Run**.
5. Aguarde a mensagem `BUILD SUCCESS` no console, significa que o arquivo foi gerado na pasta target.

### Opção B: Pelo Terminal (CMD)
1. Abra o terminal na pasta raiz do projeto (onde está o `pom.xml`).
2. Digite o comando:
   ```bash
   mvn clean package
   ```
### Execute a aplicação
Use o comando abaixo e comece a gerenciar suas finanças
```bash
java -jar app.jar
```
