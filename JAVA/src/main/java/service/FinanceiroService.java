package service;

import java.math.BigDecimal;
import java.util.List;

import dao.DespesaDao;
import dao.ReceitaDao;
import entity.Despesa;
import entity.Receita;
import jakarta.persistence.EntityManager;
import util.JpaUtil;

public class FinanceiroService {
	private final DespesaDao despesaDAO = new DespesaDao();
	private final ReceitaDao receitaDAO = new ReceitaDao();

	// DESPESAS --------------------------------------------------

	public void cadastrarDespesa(String titulo, BigDecimal valor, String tipo) {
		despesaDAO.salvar(new Despesa(titulo, valor, tipo));

	}

	public void editarDespesa(Long id, String titulo, BigDecimal valor, String tipo) {
		Despesa d = despesaDAO.buscarPorId(id);
		if (d == null)
			throw new IllegalArgumentException("Erro: Despesa com ID " + id + " nao encontrada.");

		d.setTitulo(titulo);
		d.setValor(valor);
		d.setTipo(tipo);
		despesaDAO.salvar(d);
	}

	public void gerarRelatorioDespesasPorTipo() {
		try {
			despesaDAO.relatorioDespesasPorTipo();
		} catch (Exception e) {
			throw new RuntimeException("Erro ao gerar relatorio: Verifique se existem despesas cadastradas.");
		}
	}

	public List<Despesa> listarTodasDespesas() {
		List<Despesa> lista = despesaDAO.buscarTodas();
		if (lista.isEmpty())
			throw new IllegalStateException("Nenhuma despesa cadastrada no sistema.");
		return lista;
	}

	public Despesa buscarDespesaPorId(Long id) {
		EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();
		try {
			return em.find(Despesa.class, id);
		} finally {
			em.close();
		}
	}

	public void excluirDespesa(Long id) {
		Despesa despesaExcluida = despesaDAO.buscarPorId(id);
		if (despesaExcluida == null)
			throw new IllegalArgumentException("Nao e possível excluir. Despesa nao encontrada.");
		despesaDAO.excluir(id);

	}

	// RECEITAS ----------------------------------------------------

	public void cadastrarReceita(String titulo, BigDecimal valor, String tipo) {
		receitaDAO.salvar(new Receita(titulo, valor, tipo));
	}

	public void editarReceita(Long id, String titulo, BigDecimal valor, String tipo) {
		Receita r = receitaDAO.buscarPorId(id);
		if (r == null)
			throw new IllegalArgumentException("Receita com ID " + id + " nao encontrada.");

		r.setTitulo(titulo);
		r.setValor(valor);
		r.setTipo(tipo);
		receitaDAO.salvar(r);
	}

	public List<Receita> listarTodasReceitas() {
		List<Receita> lista = receitaDAO.buscarTodas();
		if (lista.isEmpty())
			throw new IllegalStateException("Nenhuma receita cadastrada no sistema.");
		return lista;
	}

	public Receita buscarReceitaPorId(Long id) {
		EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();
		try {
			return em.find(Receita.class, id);
		} finally {
			em.close();
		}
	}

	public void gerarRelatorioReceitaPorTipo() {
		try {
			receitaDAO.relatorioReceitasPorTipo();
		} catch (Exception e) {
			throw new RuntimeException("Falha ao gerar relatorio: Verifique se existem receitas cadastradas.");
		}
	}

	public void excluirReceita(Long id) {
		Receita receitaExcluida = receitaDAO.buscarPorId(id);
		if (receitaExcluida == null)
			throw new IllegalArgumentException("Nao e possivel excluir. Receita nao encontrada.");
		receitaDAO.excluir(receitaExcluida);
	}

	// GERAL -----------------------------------------------------

	public void listarFinancasMes(int mes, int ano) {
		List<Receita> recs = receitaDAO.buscarPorMesAno(mes, ano);
		List<Despesa> dess = despesaDAO.buscarPorMesAno(mes, ano);

		if (recs.isEmpty() && dess.isEmpty()) {
			System.out.println("\nNão há financas no mes/ano selecionados.");
		} else {
			System.out.println("RECEITAS\n");
			recs.forEach(r -> System.out.printf(r.toString() + "\n"));

			System.out.println("DESPESAS\n");
			dess.forEach(d -> System.out.printf(d.toString() + "\n"));
		}

	}

	public void calcularEconomiaMes(int mes, int ano) {
		List<Receita> receitas = receitaDAO.buscarPorMesAno(mes, ano);
		List<Despesa> despesas = despesaDAO.buscarPorMesAno(mes, ano);

		BigDecimal totalRec = receitas.stream().map(Receita::getValor).reduce(BigDecimal.ZERO, BigDecimal::add);
		BigDecimal totalDes = despesas.stream().map(Despesa::getValor).reduce(BigDecimal.ZERO, BigDecimal::add);
		BigDecimal economia = totalRec.subtract(totalDes);

		if (receitas.isEmpty() && despesas.isEmpty()) {
			System.out.println("\nNao ha financas no mes/ano selecionados.");
		} else {
			System.out.printf("\n================ RESUMO DE ECONOMIA (%02d/%d) ================\n", mes, ano);
			System.out.println("\n(+) Receitas: R$ " + totalRec);
			System.out.println("(-) Despesas: R$ " + totalDes);
			System.out.println("(=) Economizou: R$ " + economia);
		}
	}

	public void excluirTudoPorMesAno(int mes, int ano) {
		int receitas = receitaDAO.excluirMesAno(mes, ano);
		int despesas = despesaDAO.excluirMesAno(mes, ano);

		if (receitas + despesas == 0) {
			throw new RuntimeException("\nNão há finanças para deletar no mes/ano selecionados");
		}

		System.out.println("Sucesso: " + (receitas + despesas) + " registros removidos.");
	}

}
