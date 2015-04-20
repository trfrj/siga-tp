package br.gov.jfrj.siga.tp.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;

import br.gov.jfrj.siga.model.ActiveRecord;
import br.gov.jfrj.siga.tp.vraptor.ConvertableEntity;

@SuppressWarnings("serial")
@Entity
@Audited
@Table(schema = "SIGATP")
public class Plantao extends TpModel implements ConvertableEntity, Comparable<Plantao> {

	public static final ActiveRecord<Plantao> AR = new ActiveRecord<>(Plantao.class);

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence_generator")
	@SequenceGenerator(name = "hibernate_sequence_generator", sequenceName = "SIGATP.hibernate_sequence")
	public Long id;

	@ManyToOne(cascade = CascadeType.ALL)
	@NotNull
	public Condutor condutor;

	@NotNull
	public Calendar dataHoraInicio;

	@NotNull
	public Calendar dataHoraFim;

	public Plantao() {
		this.id = new Long(0);
	}

	public String referencia;

	public Plantao(long id, Condutor condutor, Calendar dataHoraInicio, Calendar dataHoraFim) {
		super();
		this.id = id;
		this.condutor = condutor;
		this.dataHoraInicio = dataHoraInicio;
		this.dataHoraFim = dataHoraFim;
	}

	public Plantao(Condutor condutor, Calendar dataHoraInicio, Calendar dataHoraFim) {
		this.id = new Long(0);
		this.condutor = condutor;
		this.dataHoraInicio = dataHoraInicio;
		this.dataHoraFim = dataHoraFim;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Calendar getDataHoraInicio() {
		return dataHoraInicio;
	}

	public void setDataHoraInicio(Calendar dataHoraInicio) {
		this.dataHoraInicio = dataHoraInicio;
	}

	public Calendar getDataHoraFim() {
		return dataHoraFim;
	}

	public void setDataHoraFim(Calendar dataHoraFim) {
		this.dataHoraFim = dataHoraFim;
	}

	public Condutor getCondutor() {
		return condutor;
	}

	public void setCondutor(Condutor condutor) {
		this.condutor = condutor;
	}

	public String getReferencia() {
		return referencia;
	}

	public void setReferencia(String referencia) {
		this.referencia = referencia;
	}

	public static List<Plantao> buscarTodosPorCondutor(Condutor condutor) {
		return Plantao.AR.find("CONDUTOR_ID = ? ORDER BY DATAHORAINICIO DESC", condutor.getId()).fetch();
	}

	public static List<Plantao> buscarPorCondutor(Long IdCondutor, Calendar dataHoraInicio) {
		return Plantao.AR.find("condutor.id = ? AND dataHoraInicio <= ? AND (dataHoraFim is null OR dataHoraFim >= ?) order by dataHoraInicio", IdCondutor, dataHoraInicio, dataHoraInicio).fetch();
	}

	public boolean ordemDeDatasCorreta() {
		return this.dataHoraInicio.before(this.dataHoraFim);
	}

	@SuppressWarnings("unchecked")
	private static List<Plantao> retornarLista(String qrl) throws NoResultException {
		List<Plantao> plantoes;
		Query qry = AR.em().createQuery(qrl);
		try {
			plantoes = (List<Plantao>) qry.getResultList();
		} catch (NoResultException ex) {
			plantoes = null;
		}
		return plantoes;
	}

	public static List<Plantao> buscarPorCondutores(Long IdCondutor, String dataHoraInicio) {
		String filtroCondutor = "";
		String dataFormatadaOracle = "to_date('" + dataHoraInicio + "', 'DD/MM/YYYY')";

		if (IdCondutor != null) {
			filtroCondutor = "condutor.id = " + IdCondutor + " AND ";
		}

		String qrl = "SELECT p FROM Plantao p WHERE " + filtroCondutor + "  trunc(dataHoraInicio) <= trunc(" + dataFormatadaOracle + ")" + " AND (dataHoraFim IS NULL OR trunc(dataHoraFim) >= trunc("
				+ dataFormatadaOracle + "))";

		return retornarLista(qrl);
	}

	public static List<Plantao> buscarPorCondutores(Long IdCondutor, String dataHoraInicio, String dataHoraFim) {
		String dataFormatadaOracleInicio = "to_date('" + dataHoraInicio + "', 'DD/MM/YYYY')";
		String dataFormatadaOracleFim = "to_date('" + dataHoraFim + "', 'DD/MM/YYYY')";
		String filtroCondutor = "";

		if (IdCondutor != null) {
			filtroCondutor = "condutor.id = " + IdCondutor + " AND ";
		}

		String qrl = "SELECT p FROM Plantao p " + " WHERE " + filtroCondutor + " ((trunc(dataHoraInicio) <= trunc(" + dataFormatadaOracleInicio + ")"
				+ " AND (dataHoraFim IS NULL OR trunc(dataHoraFim) >= trunc(" + dataFormatadaOracleInicio + ")))" + " OR (trunc(dataHoraInicio) <= trunc(" + dataFormatadaOracleFim + ")"
				+ " AND (dataHoraFim IS NULL OR trunc(dataHoraFim) >= trunc(" + dataFormatadaOracleFim + "))))";

		return retornarLista(qrl);
	}

	@Override
	public int compareTo(Plantao o) {
		// por enquanto compara por data de inicio
		// sera usado para ordenar os plantoes 24h

		return this.dataHoraInicio.compareTo(o.dataHoraInicio);
	}

	public static List<String> getReferencias(Long orgaoUsuario) {
		List<Plantao> objetos;
		List<String> retorno;
		// TODO ordenar
		/*
		 * SELECT p.referencia FROM Plantao p WHERE p.timestamp = (SELECT MAX(ee.timestamp) FROM Entity ee WHERE ee.entityId = e.entityId)
		 */
		Query qry = AR.em().createQuery(
				"select p from Plantao p " + "where p.referencia is not null " + "and p.dataHoraInicio = (select max(pp.dataHoraInicio) from Plantao pp where pp.referencia = p.referencia) "
						+ "and p.condutor.cpOrgaoUsuario = " + orgaoUsuario + " " + "order by p.dataHoraInicio desc ");
		try {
			objetos = (List<Plantao>) qry.getResultList();
		} catch (NoResultException ex) {
			return null;
		}

		retorno = new ArrayList<String>();
		for (Iterator<Plantao> iterator = objetos.iterator(); iterator.hasNext();) {
			Plantao plantao = (Plantao) iterator.next();
			retorno.add(plantao.referencia);
		}
		return retorno;
	}

	public static List<Plantao> getPlantoesPorReferencia(String referencia) {
		List<Plantao> retorno;
		Query qry = AR.em().createQuery("select p " + "from Plantao p " + "where p.referencia = '" + referencia + "' " + "order by p.dataHoraInicio");
		try {
			retorno = (List<Plantao>) qry.getResultList();
		} catch (NoResultException ex) {
			retorno = null;
		}
		return retorno;
	}

	public static boolean plantaoMensalJaExiste(String referencia) {
		List<Plantao> plantoes;
		Query qry = AR.em().createQuery("select p " + "from Plantao p " + "where p.referencia = '" + referencia + "'");
		try {
			plantoes = (List<Plantao>) qry.getResultList();
		} catch (NoResultException ex) {
			return false;
		}
		if (plantoes.isEmpty()) {
			return false;
		}
		return true;
	}

	public static List<Plantao> buscarTodosPorReferencia(String referencia) {
		List<Plantao> retorno;

		retorno = Plantao.AR.find("referencia", referencia).fetch();

		return retorno;
	}

}
