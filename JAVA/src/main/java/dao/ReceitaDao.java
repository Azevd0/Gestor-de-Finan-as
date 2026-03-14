package dao;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import entity.Despesa;
import entity.Receita;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import util.JpaUtil;

public class ReceitaDao {

	public Receita salvar(Receita receita) {
	    EntityManager emf = JpaUtil.getEntityManagerFactory().createEntityManager();
	    try {
	        emf.getTransaction().begin();
	        if (receita.getId() == null) {
	            emf.persist(receita);
	        } else {
	            emf.merge(receita);
	        }
	        emf.getTransaction().commit();
	        return receita;
	    } finally { 
	        emf.close(); 
	    }
	}

    public Receita buscarPorId(Long id) {
    	EntityManager emf = JpaUtil.getEntityManagerFactory().createEntityManager();

        try { return emf.find(Receita.class, id); } 
        finally { emf.close(); }
    }

    public List<Receita> buscarTodas() {
    	EntityManager emf = JpaUtil.getEntityManagerFactory().createEntityManager();

        try { return emf.createQuery("SELECT d FROM Receita d", Receita.class).getResultList(); } 
        finally { emf.close(); }
    }

    public List<Receita> buscarPorMesAno(int mes, int ano) {
    	EntityManager emf = JpaUtil.getEntityManagerFactory().createEntityManager();
        try {
            return emf.createQuery("SELECT r FROM Receita r WHERE EXTRACT(MONTH FROM r.dataCadastro) = :mes AND EXTRACT(YEAR FROM r.dataCadastro) = :ano", Receita.class)
                     .setParameter("mes", mes).setParameter("ano", ano).getResultList();
        } finally { emf.close(); }
    }
    
    public void relatorioReceitasPorTipo() {
    	EntityManager emf = JpaUtil.getEntityManagerFactory().createEntityManager();
		try {
			BigDecimal total = emf.createQuery("SELECT SUM(r.valor) FROM Receita r", BigDecimal.class).getSingleResult();
			if (total == null || total.compareTo(BigDecimal.ZERO) == 0) {
			    throw new RuntimeException("Nao ha receitas para calcular o percentual.");
			}

			List<Object[]> resultados = emf
					.createQuery("SELECT r.tipo, SUM(r.valor) FROM Receita r GROUP BY r.tipo", Object[].class)
					.getResultList();

			System.out.println("\n============== RELATORIO DAS RECEITAS ==============\n");
			for (Object[] res : resultados) {
			    BigDecimal soma = (BigDecimal) res[1];
			    BigDecimal perc = soma.divide(total, 4, RoundingMode.HALF_UP).multiply(new BigDecimal("100"));
			    
			    System.out.printf("Tipo: %-15s | Valor: R$ %10.2f | %6.2f%% do total recebido%n", 
			        res[0], 
			        soma, 
			        perc.setScale(2, RoundingMode.HALF_UP));
			    
			    System.out.println("---------------------------------------------------------------------------");
			}
		} finally {
			emf.close();
		}

	}

    public void excluir(Receita receitaExcluida) {
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();

        try {
            em.getTransaction().begin();
            Receita receita = em.contains(receitaExcluida) ? receitaExcluida : em.merge(receitaExcluida);
            
            if (receita != null) {
                em.remove(receita);
            }
            
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally { 
            em.close(); 
        }
    }

    public int excluirMesAno(int mes, int ano) {
        EntityManager em = JpaUtil.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            int totalDeletado = em.createQuery("DELETE FROM Receita r WHERE EXTRACT(MONTH FROM r.dataCadastro) = :mes AND EXTRACT(YEAR FROM r.dataCadastro) = :ano")
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
