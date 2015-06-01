package br.gov.jfrj.siga.tp.model;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

import br.com.caelum.vraptor.validator.I18nMessage;
import br.gov.jfrj.siga.cp.CpComplexo;
import br.gov.jfrj.siga.dp.CpOrgaoUsuario;
import br.gov.jfrj.siga.dp.DpPessoa;
import br.gov.jfrj.siga.model.ActiveRecord;
import br.gov.jfrj.siga.model.ContextoPersistencia;
import br.gov.jfrj.siga.tp.util.PerguntaSimNao;
import br.gov.jfrj.siga.tp.util.Reflexao;
import br.gov.jfrj.siga.tp.validation.annotation.Data;
import br.gov.jfrj.siga.tp.validation.annotation.UpperCase;
import br.gov.jfrj.siga.vraptor.converter.ConvertableEntity;
import br.jus.jfrj.siga.uteis.Sequence;
import br.jus.jfrj.siga.uteis.SequenceMethods;
import br.jus.jfrj.siga.uteis.SiglaDocumentoType;

@SuppressWarnings("serial")
@Entity
//@Table(name = "VEICULO_2", schema="SIGAOR")
// @Table(name = "VEICULO_2", schema="SIGAOR")
@Audited
@Table(schema = "SIGATP")
public class Missao extends TpModel implements ConvertableEntity, Comparable<Missao>, SequenceMethods {

	public static final ActiveRecord<Missao> AR = new ActiveRecord<>(Missao.class);

	@Id
	@Sequence(propertieOrgao="cpOrgaoUsuario",siglaDocumento=SiglaDocumentoType.MTP)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence_generator")
	@SequenceGenerator(name = "hibernate_sequence_generator", sequenceName = "SIGATP.hibernate_sequence")
	private Long id;

    @Column(updatable = false)
 	@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
 	@Sequence(propertieOrgao = "cpOrgaoUsuario", siglaDocumento = SiglaDocumentoType.MTP)
	private Long numero;

	@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
	@ManyToOne
	@JoinColumn(name = "ID_PESSOA")
	private DpPessoa responsavel;

	@Data(descricaoCampo = "dataHora")
	private Calendar dataHora;

	@Transient
	private double distanciaPercorridaEmKm;


	@Transient
	@Data(descricaoCampo = "tempoBruto")
	private Calendar tempoBruto;

	private double consumoEmLitros;

	@NotNull
	@Data(descricaoCampo = "dataHoraSaida")
	private Calendar dataHoraSaida;

	private double odometroSaidaEmKm;

	@Enumerated(EnumType.STRING)
	private PerguntaSimNao estepe;

	@Enumerated(EnumType.STRING)
	private PerguntaSimNao avariasAparentesSaida;

	@Enumerated(EnumType.STRING)
	private PerguntaSimNao limpeza;

	@Enumerated(EnumType.STRING)
	private NivelDeCombustivel nivelCombustivelSaida;

	@Enumerated(EnumType.STRING)
	private PerguntaSimNao triangulos;

	@Enumerated(EnumType.STRING)
	private PerguntaSimNao extintor;

	@Enumerated(EnumType.STRING)
	private PerguntaSimNao ferramentas;

	@Enumerated(EnumType.STRING)
	private PerguntaSimNao licenca;

	@Enumerated(EnumType.STRING)
	private PerguntaSimNao cartaoSeguro;

	@Enumerated(EnumType.STRING)
	private PerguntaSimNao cartaoAbastecimento;

	@Enumerated(EnumType.STRING)
	private PerguntaSimNao cartaoSaida;

	@Data(descricaoCampo = "dataHoraRetorno")
	private Calendar dataHoraRetorno;

	private double odometroRetornoEmKm;

	@Enumerated(EnumType.STRING)
	private PerguntaSimNao avariasAparentesRetorno;

	@Enumerated(EnumType.STRING)
	private NivelDeCombustivel nivelCombustivelRetorno;

	@UpperCase
	private String ocorrencias;

	@UpperCase
	private String itinerarioCompleto;

	@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
	@ManyToOne
	@JoinColumn(name = "ID_ORGAO_USU")
	private CpOrgaoUsuario cpOrgaoUsuario;

	@NotNull(message = "missao.requisicoesTransporte.required")
	@ManyToMany
	@JoinTable(name = "missao_requisTransporte", joinColumns = @JoinColumn(name = "missao_Id"), inverseJoinColumns = @JoinColumn(name = "requisicaoTransporte_Id"))
	private List<RequisicaoTransporte> requisicoesTransporte;

	@NotNull
	@ManyToOne
	private Veiculo veiculo;

	@NotNull
	@ManyToOne
	private Condutor condutor;

	@Enumerated(EnumType.STRING)
	private EstadoMissao estadoMissao;

	@UpperCase
	private String justificativa;

	@Enumerated(EnumType.STRING)
	private PerguntaSimNao inicioRapido;

	@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
	@ManyToOne
	@JoinColumn(name = "ID_COMPLEXO")
	private CpComplexo cpComplexo;

	public Missao() {
		this.id = new Long(0);
		this.numero = new Long(0);
		this.dataHora = Calendar.getInstance();
		this.avariasAparentesRetorno = PerguntaSimNao.NAO;
		this.avariasAparentesSaida = PerguntaSimNao.NAO;
		this.cartaoAbastecimento = PerguntaSimNao.SIM;
		this.cartaoSaida = PerguntaSimNao.SIM;
		this.cartaoSeguro = PerguntaSimNao.SIM;
		this.estepe = PerguntaSimNao.SIM;
		this.extintor = PerguntaSimNao.SIM;
		this.ferramentas = PerguntaSimNao.SIM;
		this.licenca = PerguntaSimNao.SIM;
		this.limpeza = PerguntaSimNao.SIM;
		this.triangulos = PerguntaSimNao.SIM;
		this.nivelCombustivelRetorno = NivelDeCombustivel.A;
		this.nivelCombustivelSaida = NivelDeCombustivel.A;
		this.estadoMissao = EstadoMissao.PROGRAMADA;
		this.inicioRapido = PerguntaSimNao.NAO;
	}


	public String getDadosParaExibicao() {
		return this.numero.toString();
	}

	@Override
	public int compareTo(Missao o) {
		return (this.numero).compareTo(o.numero);
	}

	@Override
	public String getSequence() {
		if (this.numero != 0) {
			return cpOrgaoUsuario.getAcronimoOrgaoUsu().replace("-","").toString() +  "-" +
				   Reflexao.recuperaAnotacaoField(this).substring(1) + "-" +
				   String.format("%04d",this.dataHora.get(Calendar.YEAR)) + "/" +
				   String.format("%05d", numero) + "-" +
				   Reflexao.recuperaAnotacaoField(this).substring(0,1);
		} else {
			return "";
		}
	}

	public void setSequence(Object cpOrgaoUsuarioObject) {
		CpOrgaoUsuario cpOrgaoUsuario = (CpOrgaoUsuario) cpOrgaoUsuarioObject;
		int year = Calendar.getInstance().get(Calendar.YEAR);
		String qrl = "SELECT m FROM Missao m where m.numero = ";
		qrl = qrl + "(SELECT MAX(t.numero) FROM Missao t";
		qrl = qrl + " where cpOrgaoUsuario.id = " + cpOrgaoUsuario.getId();
		qrl = qrl + " and YEAR(dataHora) = " + year;
		qrl = qrl + " and m.cpOrgaoUsuario.id = t.cpOrgaoUsuario.id";
		qrl = qrl + " and YEAR(m.dataHora) = YEAR(t.dataHora)";
		qrl = qrl + ") order by m.numero desc";
		Query qry = AR.em().createQuery(qrl);
		try {
			Object obj = qry.getResultList().get(0);
			this.numero = ((Missao) obj).numero + 1;
		} catch (IndexOutOfBoundsException ex) {
			this.numero = new Long(1);
		}

	}

	public static List<Missao> buscarEmAndamento() {
		return Missao.AR.find("trunc(dataHoraSaida) = trunc(sysdate)").fetch();
	}

	public static Missao buscar(String sequence) throws Exception {
		String[] partesDoCodigo = null;
		Missao missao = new Missao();
		try {
			 partesDoCodigo = sequence.split("[-/]");

			partesDoCodigo = sequence.split("[-/]");

		} catch (Exception e) {
			throw new Exception(new I18nMessage("missao", "missao.buscar.sequence.exception", sequence).getMessage());
		}

		CpOrgaoUsuario cpOrgaoUsuario = CpOrgaoUsuario.AR.find("acronimoOrgaoUsu",partesDoCodigo[0]).first();
		Integer ano = new Integer(Integer.parseInt(partesDoCodigo[2]));
		Long numero = new Long(Integer.parseInt(partesDoCodigo[3]));
		String siglaDocumento = partesDoCodigo[4] + partesDoCodigo[1];
		if (! Reflexao.recuperaAnotacaoField(missao).equals(siglaDocumento)) {
			throw new Exception(new I18nMessage("missao", "missao.buscar.siglaDocumento.exception", sequence).getMessage());
		}
		List<Missao> missoes = Missao.AR.find("cpOrgaoUsuario = ? and numero = ? and YEAR(dataHora) = ?", cpOrgaoUsuario, numero, ano).fetch();
		if (missoes.size() > 1)
			throw new Exception(new I18nMessage("missao", "missao.buscar.codigoDuplicado.exception").getMessage());
		if (missoes.size() == 0)
			throw new Exception(new I18nMessage("missao", "missao.buscar.codigoInvalido.exception").getMessage());
		return missoes.get(0);
	}

	public static List<Missao> buscarPorCondutor(Long IdCondutor, Calendar dataHoraInicio) {
		return Missao.AR.find("condutor.id = ? AND dataHoraSaida <= ? AND (dataHoraRetorno is null OR dataHoraRetorno >= ?) AND estadoMissao NOT IN (?,?) order by dataHoraSaida", IdCondutor,
				dataHoraInicio, dataHoraInicio, EstadoMissao.CANCELADA, EstadoMissao.FINALIZADA).fetch();
	}

	public static List<Missao> buscarMissoesEmAbertoPorCondutor(Condutor condutor) {
		return Missao.AR.find("condutor.id = ? AND estadoMissao NOT IN (?,?) order by dataHoraSaida", condutor.getId(), EstadoMissao.CANCELADA, EstadoMissao.FINALIZADA).fetch();
	}

	public static List<Missao> buscarTodasAsMissoesPorCondutor(Condutor condutor) {
		if (condutor == null)
			return new ArrayList<Missao>();
		return Missao.AR.find("condutor.id = ? order by dataHoraSaida desc", condutor.getId()).fetch();
	}

	public static List<Missao> buscarPorVeiculos(Long IdVeiculo, String dataHoraInicio) {
		return filtrarMissoes("veiculo", IdVeiculo, dataHoraInicio);
	}

	public static List<Missao> buscarPorCondutores(Long IdCondutor, String dataHoraInicio) {
		return filtrarMissoes("condutor", IdCondutor, dataHoraInicio);
	}

	@SuppressWarnings("unchecked")
	private static List<Missao> filtrarMissoes(String entidade, Long idEntidade, String dataHoraInicio) {
		String filtroEntidade = "";
		if (idEntidade != null) {
			filtroEntidade = entidade + ".id = " + idEntidade + " AND ";
			filtroEntidade = entidade + ".id = " + idEntidade + " AND ";
		}

		String dataFormatadaOracle = "to_date('" + dataHoraInicio + "', 'DD/MM/YYYY')";
		List<Missao> missoes;

		String qrl = "SELECT m FROM Missao m WHERE " + filtroEntidade
				+
				// "  estadoMissao NOT IN ('" + EstadoMissao.CANCELADA + "','" + EstadoMissao.FINALIZADA + "')" +
				"  estadoMissao NOT IN ('" + EstadoMissao.CANCELADA + "')" + " AND trunc(dataHoraSaida) <= trunc(" + dataFormatadaOracle + ")"
				+ " AND (dataHoraRetorno IS NULL OR trunc(dataHoraRetorno) >= trunc(" + dataFormatadaOracle + "))";

		Query qry = AR.em().createQuery(qrl);
		try {
			missoes = (List<Missao>) qry.getResultList();
		} catch (NoResultException ex) {
			missoes = null;
		}
		return missoes;
	}


	@SuppressWarnings("unchecked")
	public static List<Missao> retornarMissoes(String entidade, Long idEntidade, Long usuarioId, Calendar dataHoraInicio, Calendar dataHoraFim) {
		String filtroEntidade = "";
		if (idEntidade != null) {
			filtroEntidade = entidade + " = " + idEntidade;
			filtroEntidade = entidade + " = " + idEntidade;
		}


		String qrl = "SELECT m FROM Missao m";
		qrl += " WHERE " + filtroEntidade;
		SimpleDateFormat formatar = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		String dataInicioFormatadaOracle = "to_date('" + formatar.format(dataHoraInicio.getTime()) + "', 'DD/MM/YYYY HH24:MI')";
		String dataFimFormatadaOracle = dataHoraFim == null ? "" : "to_date('" + formatar.format(dataHoraFim.getTime()) + "', 'DD/MM/YYYY HH24:MI')";


		if (!dataFimFormatadaOracle.isEmpty()) {
			qrl += " AND dataHoraSaida BETWEEN " + dataInicioFormatadaOracle + " AND " + dataFimFormatadaOracle;
		} else {
			qrl += " AND trunc(dataHoraSaida) <= trunc(" + dataInicioFormatadaOracle + ")";
		}


		qrl += " AND cpOrgaoUsuario.id = " + usuarioId;

		Query qry = AR.em().createQuery(qrl);
		return (List<Missao>) qry.getResultList();
	}

	@Override
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getNumero() {
		return numero;
	}

	public void setNumero(Long numero) {
		this.numero = numero;
	}

	public DpPessoa getResponsavel() {
		return responsavel;
	}

	public void setResponsavel(DpPessoa responsavel) {
		this.responsavel = responsavel;
	}

	public Calendar getDataHora() {
		return dataHora;
	}

	public void setDataHora(Calendar dataHora) {
		this.dataHora = dataHora;
	}

	public double getDistanciaPercorridaEmKm() {
		return distanciaPercorridaEmKm;
	}

	public void setDistanciaPercorridaEmKm(double distanciaPercorridaEmKm) {
		this.distanciaPercorridaEmKm = distanciaPercorridaEmKm;
	}

	public Calendar getTempoBruto() {
		return tempoBruto;
	}

	public void setTempoBruto(Calendar tempoBruto) {
		this.tempoBruto = tempoBruto;
	}

	public double getConsumoEmLitros() {
		return consumoEmLitros;
	}

	public void setConsumoEmLitros(double consumoEmLitros) {
		this.consumoEmLitros = consumoEmLitros;
	}

	public Calendar getDataHoraSaida() {
		return dataHoraSaida;
	}

	public void setDataHoraSaida(Calendar dataHoraSaida) {
		this.dataHoraSaida = dataHoraSaida;
	}

	public double getOdometroSaidaEmKm() {
		return odometroSaidaEmKm;
	}

	public void setOdometroSaidaEmKm(double odometroSaidaEmKm) {
		this.odometroSaidaEmKm = odometroSaidaEmKm;
	}

	public PerguntaSimNao getEstepe() {
		return estepe;
	}

	public void setEstepe(PerguntaSimNao estepe) {
		this.estepe = estepe;
	}

	public PerguntaSimNao getAvariasAparentesSaida() {
		return avariasAparentesSaida;
	}

	public void setAvariasAparentesSaida(PerguntaSimNao avariasAparentesSaida) {
		this.avariasAparentesSaida = avariasAparentesSaida;
	}

	public PerguntaSimNao getLimpeza() {
		return limpeza;
	}

	public void setLimpeza(PerguntaSimNao limpeza) {
		this.limpeza = limpeza;
	}

	public NivelDeCombustivel getNivelCombustivelSaida() {
		return nivelCombustivelSaida;
	}

	public void setNivelCombustivelSaida(NivelDeCombustivel nivelCombustivelSaida) {
		this.nivelCombustivelSaida = nivelCombustivelSaida;
	}

	public PerguntaSimNao getTriangulos() {
		return triangulos;
	}

	public void setTriangulos(PerguntaSimNao triangulos) {
		this.triangulos = triangulos;
	}

	public PerguntaSimNao getExtintor() {
		return extintor;
	}

	public void setExtintor(PerguntaSimNao extintor) {
		this.extintor = extintor;
	}

	public PerguntaSimNao getFerramentas() {
		return ferramentas;
	}

	public void setFerramentas(PerguntaSimNao ferramentas) {
		this.ferramentas = ferramentas;
	}

	public PerguntaSimNao getLicenca() {
		return licenca;
	}

	public void setLicenca(PerguntaSimNao licenca) {
		this.licenca = licenca;
	}

	public PerguntaSimNao getCartaoSeguro() {
		return cartaoSeguro;
	}

	public void setCartaoSeguro(PerguntaSimNao cartaoSeguro) {
		this.cartaoSeguro = cartaoSeguro;
	}

	public PerguntaSimNao getCartaoAbastecimento() {
		return cartaoAbastecimento;
	}

	public void setCartaoAbastecimento(PerguntaSimNao cartaoAbastecimento) {
		this.cartaoAbastecimento = cartaoAbastecimento;
	}

	public PerguntaSimNao getCartaoSaida() {
		return cartaoSaida;
	}

	public void setCartaoSaida(PerguntaSimNao cartaoSaida) {
		this.cartaoSaida = cartaoSaida;
	}

	public Calendar getDataHoraRetorno() {
		return dataHoraRetorno;
	}

	public void setDataHoraRetorno(Calendar dataHoraRetorno) {
		this.dataHoraRetorno = dataHoraRetorno;
	}

	public double getOdometroRetornoEmKm() {
		return odometroRetornoEmKm;
	}

	public void setOdometroRetornoEmKm(double odometroRetornoEmKm) {
		this.odometroRetornoEmKm = odometroRetornoEmKm;
	}

	public PerguntaSimNao getAvariasAparentesRetorno() {
		return avariasAparentesRetorno;
	}

	public void setAvariasAparentesRetorno(PerguntaSimNao avariasAparentesRetorno) {
		this.avariasAparentesRetorno = avariasAparentesRetorno;
	}

	public NivelDeCombustivel getNivelCombustivelRetorno() {
		return nivelCombustivelRetorno;
	}

	public void setNivelCombustivelRetorno(
			NivelDeCombustivel nivelCombustivelRetorno) {
		this.nivelCombustivelRetorno = nivelCombustivelRetorno;
	}

	public String getOcorrencias() {
		return ocorrencias;
	}

	public void setOcorrencias(String ocorrencias) {
		this.ocorrencias = ocorrencias;
	}

	public String getItinerarioCompleto() {
		return itinerarioCompleto;
	}

	public void setItinerarioCompleto(String itinerarioCompleto) {
		this.itinerarioCompleto = itinerarioCompleto;
	}

	public CpOrgaoUsuario getCpOrgaoUsuario() {
		return cpOrgaoUsuario;
	}

	public void setCpOrgaoUsuario(CpOrgaoUsuario cpOrgaoUsuario) {
		this.cpOrgaoUsuario = cpOrgaoUsuario;
	}

	public List<RequisicaoTransporte> getRequisicoesTransporte() {
		return requisicoesTransporte;
	}

	public void setRequisicoesTransporte(
			List<RequisicaoTransporte> requisicoesTransporte) {
		this.requisicoesTransporte = requisicoesTransporte;
	}

	public Veiculo getVeiculo() {
		return veiculo;
	}

	public void setVeiculo(Veiculo veiculo) {
		this.veiculo = veiculo;
	}

	public Condutor getCondutor() {
		return condutor;
	}

	public void setCondutor(Condutor condutor) {
		this.condutor = condutor;
	}

	public EstadoMissao getEstadoMissao() {
		return estadoMissao;
	}

	public void setEstadoMissao(EstadoMissao estadoMissao) {
		this.estadoMissao = estadoMissao;
	}

	public String getJustificativa() {
		return justificativa;
	}

	public void setJustificativa(String justificativa) {
		this.justificativa = justificativa;
	}

	public PerguntaSimNao getInicioRapido() {
		return inicioRapido;
	}

	public void setInicioRapido(PerguntaSimNao inicioRapido) {
		this.inicioRapido = inicioRapido;
	}

	public CpComplexo getCpComplexo() {
		return cpComplexo;
	}

	public void setCpComplexo(CpComplexo cpComplexo) {
		this.cpComplexo = cpComplexo;
	}
}