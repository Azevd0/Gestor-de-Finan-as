package dao;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import entity.Despesa;
import entity.Receita;
import jakarta.persistence.EntityManager;
import util.JpaUtil;

public class DespesaDao {

	public Despesa salvar(Despesa despesa) {
	    EntityManager emf = JpaUtil.getEntityManagerFactory().createEntityManager();
	    try {
	        emf.getTransaction().begin();
	        if (despesa.getId() == null) {
	            emf.persist(despesa);
	        } else {
	            emf.merge(despesa);
	        }
	        emf.getTransaction().commit();
	        return despesa;
	    } finally { 
	        emf.close(); 
	    }
	}

    public Despesa buscarPorId(Long id) {
    	EntityManager emf = JpaUtil.getEntityManagerFactory().createEntityManager();

        try { return emf.find(Despesa.class, id); } 
        finally { emf.close(); }
    }

    public List<Despesa> buscarTodas() {
    	EntityManager emf = JpaUtil.getEntityManagerFactory().createEntityManager();

        try { return emf.createQuery("SELECT d FROM Despesa d", Despesa.class).getResultList(); } 
        finally { emf.close(); }
    }

    public List<Despesa> buscarPorMesAno(int mes, int ano) {
    	EntityManager emf = JpaUtil.getEntityManagerFactory().createEntityManager();
        try {
            return emf.createQuery("SELECT d FROM Despesa d WHERE EXTRACT(MONTH FROM d.dataCadastro) = :mes AND EXTRACT(YEAR FROM d.dataCadastro) = :ano", Despesa.class)
                     .setParameter("mes", mes).setParameter("ano", ano).getResultList();
        } finally { emf.close(); }
    }
    
    public void relatorioDespesasPorTipo() {
    	EntityManager emf = JpaUtil.getEntityManagerFactory().createEntityManager();
		try {
			BigDecimal total = emf.createQuery("SELECT SUM(d.valor) FROM Despesa d", BigDecimal.class).getSingleResult();
			if (total == null || total.compareTo(BigDecimal.ZERO) == 0) {
			    throw new RuntimeException("Nao ha despesas para calcular o percentual.");
			}

			List<Object[]> resultados = emf
					.createQuery("SELECT d.tipo, SUM(d.valor) FROM Despesa d GROUP BY d.tipo", Object[].class)
					.getResultList();

			System.out.println("\n============== RELATORIO DE DESPESAS ==============\n");
			for (Object[] res : resultados) {
			    BigDecimal soma = (BigDecimal) res[1];
			    BigDecimal perc = soma.divide(total, 4, RoundingMode.HALF_UP).multiply(new BigDecimal("100"));
			    
			    System.out.printf("Tipo: %-15s | Valor: R$ %10.2f | %6.2f%% do total gasto%n", 
			        res[0], 
			        soma, 
			        perc.setScale(2, RoundingMode.HALF_UP));
			    
			    System.out.println("---------------------------------------------------------------------------");
			}
		} finally {
			emf.close();
		}

	}

    public void excluir(Long idDespesa) {
    	EntityManager emf = JpaUtil.getEntityManagerFactory().createEntityManager();

        try {
        	emf.getTransaction().begin();
            Despesa despesa = emf.find(Despesa.class, idDespesa);
            
            if (despesa != null) {
                emf.remove(despesa);
            }
            emf.getTransaction().commit();
        } catch (Exception e) {
            if (emf.getTransaction().isActive()) {
                emf.getTransaction().rollback();
            }
            throw e;
        } finally { emf.close(); }
    }

    public int excluirMesAno(int mes, int ano) {
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            int totalDeletado = em.createQuery("DELETE FROM Despesa d WHERE EXTRACT(MONTH FROM d.dataCadastro) = :mes AND EXTRACT(YEAR FROM d.dataCadastro) = :ano")
              .setParameter("mes", mes)
              .setParameter("ano", ano)
              .executeUpdate();
            
            em.getTransaction().commit();
            return totalDeletado;
        } finally { 
            em.close(); 
        }
    }
}
