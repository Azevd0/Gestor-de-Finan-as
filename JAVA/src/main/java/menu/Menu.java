package menu;

import service.FinanceiroService;
import java.math.BigDecimal;
import java.util.Scanner;

import dao.DespesaDao;
import dao.ReceitaDao;

public class Menu {
	private final Scanner sc = new Scanner(System.in);
	private final FinanceiroService service = new FinanceiroService();
	private final DespesaDao despesaDAO = new DespesaDao();
	private final ReceitaDao receitaDAO = new ReceitaDao();

	public void exibirMenu() {
		while (true) {
			System.out.println("\n============== GESTAO FINANCEIRA ==============\n");
			System.out.println("1 - Cadastrar (Receita/Despesa)");
			System.out.println("2 - Editar (Receita/Despesa)");
			System.out.println("3 - Excluir Unitario (Receita/Despesa)");
			System.out.println("4 - Listar Todas as Receitas/Despesas");
			System.out.println("5 - Listar Receitas/Despesas por mes/ano");
			System.out.println("6 - Listar Receitas/Despesas por tipo");
			System.out.println("7 - Relatorio: Gastos por tipo de despesa (%)");
			System.out.println("8 - Relatorio: Ganhos por tipo de receita (%)");
			System.out.println("9 - Relatorio: Economia do mes");
			System.out.println("10 - Limpar Mes/Ano Especifico");
			System.out.println("0 - Sair");
			System.out.print("Escolha: ");

			String op = sc.nextLine();
			try {
				switch (op) {
				case "1" -> cadastrar();
				case "2" -> editar();
				case "3" -> excluirUnitario();
				case "4" -> listarTudo();
				case "5" -> listarPorMes();
				case "6" -> listarPorCategoria();
				case "7" -> relatorioDespesas();
				case "8" -> relatorioReceitas();
				case "9" -> calcularEconomia();
				case "10" -> excluirPorMes();
				case "0" -> {
					System.out.println("Encerrando...");
					return;
				}
				default -> System.out.println("Opcao invalida!");
				}
			} catch (Exception e) {
				System.out.println("Erro!! " + e.getMessage());
			}
		}
	}

	// REGRAS ---------------------------------------------------

	private BigDecimal lerValor(String msg) {
		while (true) {
			try {
				System.out.print(msg);
				return new BigDecimal(sc.nextLine().replace(",", "."));
			} catch (Exception e) {
				System.out.println("Este campo deve ser preenchido com numeros.");
			}
		}
	}

	private int lerInteiro(String msg) {
		while (true) {
			try {
				System.out.print(msg);
				return Integer.parseInt(sc.nextLine());
			} catch (Exception e) {
				System.out.println("Use apenas numeros inteiros.");
			}
		}
	}

	private Long lerId(String msg) {
		while (true) {
			try {
				System.out.print(msg);
				return Long.parseLong(sc.nextLine());
			} catch (Exception e) {
				System.out.println("ID invalido. Use apenas numeros.");
			}
		}
	}

	// Opções de operação ------------------------------------------

	private void cadastrar() {
		try {
			System.out.print("Tipo (R = Receita / D = Despesa): ");
			String tipo = sc.nextLine().toUpperCase();

			if (!tipo.equalsIgnoreCase("R") && !tipo.equalsIgnoreCase("D")) {
				System.out.println("Invalido! Digite apenas R ou D.");
				return;
			}

			System.out.print("Titulo: ");
			String t = sc.nextLine();
			BigDecimal v = lerValor("Valor: ");
			System.out.print("Categoria (ex: Salario, Aluguel): ");
			String cat = sc.nextLine().toLowerCase();

			if (tipo.equals("R")) {
				service.cadastrarReceita(t, v, cat);
				System.out.println("Receita cadastrada com sucesso!");
			} else {
				service.cadastrarDespesa(t, v, cat);
				System.out.println("Despesa cadastrada com sucesso!");
			}

		} catch (Exception e) {
			System.out.println("Falha ao cadastrar financa!");
		}
	}

	private void editar() {
		System.out.print("Editar (R = Receita / D = Despesa): ");
		String tipo = sc.nextLine().toUpperCase();

		if (!tipo.equals("R") && !tipo.equals("D")) {
			System.out.println("Tipo invalido. Use R ou D.");
			return;
		}

		Long id = lerId("Informe o ID para editar: ");

		Object encontrado = (tipo.equals("R")) ? service.buscarReceitaPorId(id) : service.buscarDespesaPorId(id);

		if (encontrado == null) {
			System.out.println("Registro com ID " + id + " nao encontrado.");
			return;
		}

		System.out.print("Novo Titulo: ");
		String t = sc.nextLine();
		BigDecimal v = lerValor("Novo Valor: ");
		System.out.print("Nova Categoria: ");
		String cat = sc.nextLine();

		if (tipo.equals("R")) {
			service.editarReceita(id, t, v, cat);
		} else {
			service.editarDespesa(id, t, v, cat);
		}

		System.out.println("Registro " + id + " atualizado com sucesso!");
	}

	private void listarPorMes() {
		int m = lerInteiro("Digite o mes (1-12): ");
		int a = lerInteiro("Digite o ano (Ex: 2026): ");

		System.out.println("\n============== FINANCAS DE " + m + "/" + a + " ==============\n");

		service.listarFinancasMes(m, a);
	}

	private void excluirUnitario() {
		System.out.print("Excluir (R = Receita / D = Despesa): ");
		String tipo = sc.nextLine().toUpperCase();

		if (!tipo.equalsIgnoreCase("R") && !tipo.equalsIgnoreCase("D")) {
			System.out.println("Inválido! Digite R ou D.");
			return;
		}
		Long id = lerId("ID para excluir: ");

		if (tipo.equalsIgnoreCase("R"))
			service.excluirReceita(id);
		else
			service.excluirDespesa(id);
		System.out.println("Excluido com sucesso.");
	}

	private void listarTudo() {
	    String formatoCabecalho = "%-4s | %-25s | %-12s | %-15s | %-10s%n";
	    String formatoDados = "%-4s | %-25s | R$ %10.2f | %-15s | %-10s%n";

	    try {
	        var receitas = service.listarTodasReceitas(); 
	        
	        System.out.println("\n================================ LISTAGEM DE RECEITAS =================================");
	        System.out.printf(formatoCabecalho, "ID", "TITULO", "VALOR", "TIPO", "DATA");
	        System.out.println("---------------------------------------------------------------------------------");
	        
	        receitas.forEach(r -> {
	            System.out.printf(formatoDados, 
	                r.getId(), 
	                r.getTitulo(), 
	                r.getValor(), 
	                r.getTipo(), 
	                r.getDataCadastro());
	            System.out.println("---------------------------------------------------------------------------------");
	        });
	    } catch (Exception e) {
	        System.out.println("Erro ao listar receitas: " + e.getMessage());
	    }

	    try {
	        var despesas = service.listarTodasDespesas(); 

	        System.out.println("\n============================= LISTAGEM DE DESPESAS ==============================");
	        System.out.printf(formatoCabecalho, "ID", "TITULO", "VALOR", "TIPO", "DATA");
	        System.out.println("---------------------------------------------------------------------------------");
	        
	        despesas.forEach(d -> {
	            System.out.printf(formatoDados, 
	                d.getId(), 
	                d.getTitulo(), 
	                d.getValor(), 
	                d.getTipo(), 
	                d.getDataCadastro());
	            System.out.println("---------------------------------------------------------------------------------");

	        });
	    } catch (Exception e) {
	        System.out.println("Erro ao listar despesas: " + e.getMessage());
	    }
	}

	private void listarPorCategoria() {
		System.out.print("Deseja filtrar (R)eceita ou (D)espesa? ");
		String opcao = sc.nextLine().toUpperCase();

		System.out.print("Digite o nome da categoria (ex: viagem, lazer, mercado): ");
		String categoria = sc.nextLine();

		if (opcao.equals("D")) {
			service.listarDespesasPorTipo(categoria);
		} else if (opcao.equals("R")) {
			service.listarReceitasPorTipo(categoria);
		} else {
			System.out.println("Opção inválida!");
		}
	}

	private void relatorioDespesas() {
		int m = lerInteiro("Digite o mes para o relatorio (1-12): ");
		int a = lerInteiro("Digite o ano (Ex: 2026): ");

		service.gerarRelatorioDespesas(m, a);
	}

	private void relatorioReceitas() {
		int m = lerInteiro("Digite o mes para o relatorio (1-12): ");
		int a = lerInteiro("Digite o ano (Ex: 2026): ");

		service.gerarRelatorioReceita(m, a);
	}

	private void calcularEconomia() {
		int m = lerInteiro("Mes (1-12): ");
		int a = lerInteiro("Ano (Ex: 2026): ");
		service.calcularEconomiaMes(m, a);
	}

	private void excluirPorMes() {
		int m = lerInteiro("Mes para limpar: ");
		int a = lerInteiro("Ano para limpar: ");
		System.out.print("Confirmar a exclusao de TUDO de " + m + "/" + a + "? (S/N): ");

		if (sc.nextLine().equalsIgnoreCase("S")) {
			service.excluirTudoPorMesAno(m, a);
			System.out.println("Dados do periodo removidos.");
		}
	}
}
