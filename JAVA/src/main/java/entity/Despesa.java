package entity;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "despesas")
public class Despesa {
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String titulo;
    private BigDecimal valor;
    private String tipo;
    private LocalDate dataCadastro = LocalDate.now();

    public Despesa() {}
    
    public Despesa(Long id, String titulo, BigDecimal valor, String tipo) {
		this.id = id;
		this.titulo = titulo;
		this.valor = valor;
		this.tipo = tipo;
	}

	public Despesa(String titulo, BigDecimal valor, String tipo) {
        this.titulo = titulo;
        this.valor = valor;
        this.tipo = tipo;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public LocalDate getDataCadastro() { return dataCadastro; }
    public void setDataCadastro(LocalDate dataCadastro) { this.dataCadastro = dataCadastro; }

	@Override
	public int hashCode() {
		return Objects.hash(dataCadastro, id, tipo, titulo, valor);
	}
	@Override
	public String toString() {
		return "Id: " + id + " | Titulo: " + titulo + " | Valor: " + valor + " | Tipo: " + tipo + "| Data: "
				+ dataCadastro + "\n--------------------------------------------------------------------------------------------";
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Despesa other = (Despesa) obj;
		return Objects.equals(dataCadastro, other.dataCadastro) && Objects.equals(id, other.id)
				&& Objects.equals(tipo, other.tipo) && Objects.equals(titulo, other.titulo)
				&& Objects.equals(valor, other.valor);
	}

	

	
    
}
    
    

