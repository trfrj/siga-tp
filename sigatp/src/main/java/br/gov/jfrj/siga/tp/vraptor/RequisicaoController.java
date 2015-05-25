package br.gov.jfrj.siga.tp.vraptor;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.Validator;
import br.com.caelum.vraptor.validator.I18nMessage;
import br.gov.jfrj.siga.base.AplicacaoException;
import br.gov.jfrj.siga.cp.CpComplexo;
import br.gov.jfrj.siga.dp.DpPessoa;
import br.gov.jfrj.siga.tp.auth.AutorizacaoGI;
import br.gov.jfrj.siga.tp.auth.annotation.RoleAdmin;
import br.gov.jfrj.siga.tp.auth.annotation.RoleAdminMissao;
import br.gov.jfrj.siga.tp.auth.annotation.RoleAdminMissaoComplexo;
import br.gov.jfrj.siga.tp.auth.annotation.RoleAprovador;
import br.gov.jfrj.siga.tp.model.Andamento;
import br.gov.jfrj.siga.tp.model.EstadoRequisicao;
import br.gov.jfrj.siga.tp.model.FinalidadeRequisicao;
import br.gov.jfrj.siga.tp.model.RequisicaoTransporte;
import br.gov.jfrj.siga.tp.model.TipoDePassageiro;
import br.gov.jfrj.siga.tp.model.TpDao;
import br.gov.jfrj.siga.tp.util.SigaTpException;
import br.gov.jfrj.siga.vraptor.SigaObjects;

@Resource
@Path("/app/requisicao")
public class RequisicaoController extends TpController {

	private AutorizacaoGI autorizacaoGI;

	public RequisicaoController(HttpServletRequest request, Result result, Validator validator, SigaObjects so, EntityManager em, AutorizacaoGI autorizacaoGI) {
		super(request, result, TpDao.getInstance(), validator, so, em);
		this.autorizacaoGI = autorizacaoGI;
	}

	@Path("/listar")
	public void listar() throws Exception {
		carregarRequisicoesUltimosSeteDiasPorEstados(null);
		result.include("estadoRequisicao",EstadoRequisicao.PROGRAMADA);
		MenuMontador.instance(result).recuperarMenuListarRequisicoes(null);
	}

//	@RoleAprovador
//	@RoleAdmin
//	@RoleAdminMissao
//	@RoleAdminMissaoComplexo
	@Path("/listarPAprovar")
	public void listarPAprovar() throws Exception {
		EstadoRequisicao estadosRequisicao[] = {EstadoRequisicao.ABERTA,EstadoRequisicao.AUTORIZADA,EstadoRequisicao.REJEITADA};
		carregarRequisicoesUltimosSeteDiasPorEstados(estadosRequisicao);
		result.include("estadoRequisicao",EstadoRequisicao.ABERTA);

		MenuMontador.instance(result).recuperarMenuListarPAprovarRequisicoes(null);
		List<CpComplexo> complexos = TpDao.find(CpComplexo.class, "orgaoUsuario", getTitular().getOrgaoUsuario()).fetch();

		result.include("complexos", complexos);
	}

//	@RoleAdmin
//	@RoleAdminMissao
	@Path("/salvarNovoComplexo")
	public void salvarNovoComplexo(Long[] req, CpComplexo novoComplexo) throws Exception {
		if (req == null) {
			throw new Exception(new I18nMessage("requisicoes", "requisicoes.salvarNovoComplexo.exception").getMessage());
		}

		for (int cont = 0; cont < req.length; cont++) {
			RequisicaoTransporte requisicao = RequisicaoTransporte.AR.findById(req[cont]);
			requisicao.setCpComplexo(novoComplexo);
			requisicao.save();
		}

		result.redirectTo(this).listarPAprovar();
	}


	public void listarFiltrado(EstadoRequisicao estadoRequisicao, EstadoRequisicao estadoRequisicaoP) throws Exception {
		if (estadoRequisicaoP == null) { estadoRequisicaoP = estadoRequisicao; }
		EstadoRequisicao estadosRequisicao[] = {estadoRequisicao,estadoRequisicaoP};
		carregarRequisicoesUltimosSeteDiasPorEstados(estadosRequisicao);
		MenuMontador.instance(result).recuperarMenuListarRequisicoes(estadoRequisicao, estadoRequisicaoP);

		result.include("estadoRequisicao", estadoRequisicao);

		result.redirectTo(this).listar();
	}

	@RoleAdmin
	@RoleAdminMissao
	@RoleAdminMissaoComplexo
	@RoleAprovador
	public void listarPAprovarFiltrado(EstadoRequisicao estadoRequisicao) throws Exception {
		EstadoRequisicao estadosRequisicao[] = {estadoRequisicao};
		carregarRequisicoesUltimosSeteDiasPorEstados(estadosRequisicao);
		MenuMontador.instance(result).recuperarMenuListarPAprovarRequisicoes(estadoRequisicao);

		result.include("estadoRequisicao", estadoRequisicao);

		result.redirectTo(this).listarPAprovar();
	}

	public void salvar(RequisicaoTransporte requisicaoTransporte, TipoDePassageiro[] tiposDePassageiros, boolean checkRetorno, boolean checkSemPassageiros) throws Exception {
		if ((requisicaoTransporte.getDataHoraSaidaPrevista() != null) && (requisicaoTransporte.getDataHoraRetornoPrevisto() != null) && (!requisicaoTransporte.ordemDeDatasCorreta()))
			validator.add(new I18nMessage("dataHoraRetornoPrevisto", "requisicaoTransporte.dataHoraRetornoPrevisto.validation"));

		if(!checkSemPassageiros)
			if((tiposDePassageiros == null) || (tiposDePassageiros.length == 0))
				validator.add(new I18nMessage("tiposDePassageiros", "requisicaoTransporte.tiposDePassageiros.validation"));

		requisicaoTransporte.setTiposDePassageiro(converterTiposDePassageiros(tiposDePassageiros));

		if(checkRetorno && requisicaoTransporte.getDataHoraRetornoPrevisto() == null)
			validator.add(new I18nMessage("dataHoraRetornoPrevisto", "requisicaoTransporte.dataHoraRetornoPrevisto.validation"));

		if(!checkSemPassageiros && (requisicaoTransporte.getPassageiros() == null || requisicaoTransporte.getPassageiros().isEmpty()))
			validator.add(new I18nMessage("passageiros", "requisicaoTransporte.passageiros.validation"));

		if(requisicaoTransporte.getTipoFinalidade().ehOutra() && requisicaoTransporte.getFinalidade().isEmpty())
			validator.add(new I18nMessage("finalidade", "requisicaoTransporte.finalidade.validation"));

		validator.validate(requisicaoTransporte);
		redirecionarSeErroAoSalvar(requisicaoTransporte, checkRetorno, checkSemPassageiros);

		DpPessoa dpPessoa = recuperaPessoa(requisicaoTransporte.getIdSolicitante());
		checarSolicitante(dpPessoa.getIdInicial(), recuperarComplexoPadrao().getIdComplexo(), true);

		requisicaoTransporte.setCpOrgaoUsuario(getTitular().getOrgaoUsuario());

		requisicaoTransporte.setCpComplexo(recuperarComplexoPadrao());

		requisicaoTransporte.setSequence(requisicaoTransporte.getCpOrgaoUsuario());
		boolean novaRequisicao = false;

		if(requisicaoTransporte.getId() == 0) {
			novaRequisicao  = true;
			requisicaoTransporte.setDataHora(Calendar.getInstance());
		}

		requisicaoTransporte.setSolicitante(recuperaPessoa(requisicaoTransporte.getIdSolicitante()));

		requisicaoTransporte.save();
		requisicaoTransporte.refresh();
		if (novaRequisicao) {
			Andamento andamento = new Andamento();
			andamento.setDescricao("NOVA REQUISICAO");
			andamento.setDataAndamento(Calendar.getInstance());
			andamento.setEstadoRequisicao(EstadoRequisicao.ABERTA);
			andamento.setRequisicaoTransporte(requisicaoTransporte);
			andamento.setResponsavel(getCadastrante());
			andamento.save();
		}

		result.redirectTo(this).listar();
	}

	private void carregarRequisicoesUltimosSeteDiasPorEstados(EstadoRequisicao[] estadosRequisicao) throws Exception {
		StringBuilder criterioBusca = new StringBuilder();
		criterioBusca.append("((dataHoraRetornoPrevisto is null and dataHoraSaidaPrevista >= ?) or (dataHoraRetornoPrevisto >= ?)) and cpOrgaoUsuario = ? ");
		Calendar ultimos7dias = Calendar.getInstance();
		ultimos7dias.add(Calendar.DATE, -7);
		Object[] parametros = {ultimos7dias, ultimos7dias, getTitular().getOrgaoUsuario()};
		recuperarRequisicoes(criterioBusca, parametros, estadosRequisicao);
	}

	protected void recuperarRequisicoes(StringBuilder criterioBusca, Object[] parametros, EstadoRequisicao[] estadosRequisicao) throws Exception {
		if (!autorizacaoGI.ehAdministrador() && !autorizacaoGI.ehAdministradorMissao()  && !autorizacaoGI.ehAdministradorMissaoPorComplexo() && !autorizacaoGI.ehAprovador()) {
			criterioBusca.append(" and solicitante.idPessoaIni = ?");

			Object [] parametrosFiltrado = new Object[parametros.length + 1];

			for (int i = 0; i < parametros.length; i++)
				parametrosFiltrado[i] = parametros[i];

			parametrosFiltrado[parametros.length] = getTitular().getIdInicial();
			parametros = parametrosFiltrado;
		} else {

			if (autorizacaoGI.ehAdministradorMissaoPorComplexo() || autorizacaoGI.ehAprovador()) {
				criterioBusca.append(" and cpComplexo = ?");

				Object [] parametrosFiltrado = new Object[parametros.length + 1];

				for (int i = 0; i < parametros.length; i++)
					parametrosFiltrado[i] = parametros[i];

				if (autorizacaoGI.ehAdministradorMissaoPorComplexo())
					parametrosFiltrado[parametros.length] = getComplexoAdministrado();
				else
					parametrosFiltrado[parametros.length] = recuperarComplexoPadrao();

				parametros = parametrosFiltrado;
			}
		}
		criterioBusca.append(" order by dataHoraSaidaPrevista desc");

		List<RequisicaoTransporte> requisicoesTransporte = RequisicaoTransporte.AR.find(criterioBusca.toString(), parametros).fetch();

		if (estadosRequisicao != null)
			filtrarRequisicoes(requisicoesTransporte,estadosRequisicao);

		result.include("requisicoesTransporte", requisicoesTransporte);
	}

	private void filtrarRequisicoes(List<RequisicaoTransporte> requisicoesTransporte, EstadoRequisicao[] estadosRequisicao ) {
		Boolean filtrarRequisicao;
		for (Iterator<RequisicaoTransporte> iterator = requisicoesTransporte.iterator(); iterator.hasNext();) {
			filtrarRequisicao = true;
			RequisicaoTransporte requisicaoTransporte = (RequisicaoTransporte) iterator.next();

			for (EstadoRequisicao estadoRequisicao : estadosRequisicao)
				if (requisicaoTransporte.getUltimoAndamento().getEstadoRequisicao().equals(estadoRequisicao)) {
					filtrarRequisicao = false;
					break;
				}

			if (filtrarRequisicao)
				iterator.remove();
		}
	}

	private List<TipoDePassageiro> converterTiposDePassageiros(TipoDePassageiro[] tiposDePassageiros) {
		List<TipoDePassageiro> tiposParaSalvar = new ArrayList<TipoDePassageiro>();

		if((tiposDePassageiros == null) || (tiposDePassageiros.length == 0))
			tiposParaSalvar.add(TipoDePassageiro.NENHUM);
		else
			for (int i = 0; i < tiposDePassageiros.length; i++)
				tiposParaSalvar.add(tiposDePassageiros[i]);

		return tiposParaSalvar;
	}

	public void salvarAndamentos(@Valid RequisicaoTransporte requisicaoTransporte, boolean checkRetorno, boolean checkSemPassageiros) throws Exception {
		redirecionarSeErroAoSalvar(requisicaoTransporte, checkRetorno, checkSemPassageiros);
		checarSolicitante(requisicaoTransporte.getSolicitante().getIdInicial(), requisicaoTransporte.getCpComplexo().getIdComplexo(), true);

		requisicaoTransporte.setCpOrgaoUsuario(getTitular().getOrgaoUsuario());
		requisicaoTransporte.save();
		requisicaoTransporte.refresh();

		if (requisicaoTransporte.getId() == 0) {
			Andamento andamento = new Andamento();
			andamento.setDescricao("NOVA REQUISICAO");
			andamento.setDataAndamento(Calendar.getInstance());
			andamento.setEstadoRequisicao(EstadoRequisicao.ABERTA);
			andamento.setRequisicaoTransporte(requisicaoTransporte);
			andamento.setResponsavel(getCadastrante());
			andamento.save();
		}

		result.redirectTo(this).listar();
	}

	private void redirecionarSeErroAoSalvar(RequisicaoTransporte requisicaoTransporte, boolean checkRetorno, boolean checkSemPassageiros) throws Exception {
		if(validator.hasErrors()) {
			MenuMontador.instance(result).recuperarMenuRequisicoes(requisicaoTransporte.getId(), false, false);
			carregarTiposDeCarga(requisicaoTransporte);
			carregarFinalidades();

			result.include("requisicaoTransporte", requisicaoTransporte);
			result.include("checkRetorno", checkRetorno);
			result.include("checkSemPassageiros", checkSemPassageiros);

			if(requisicaoTransporte.getId() > 0)
				result.forwardTo(RequisicaoController.class).editar(requisicaoTransporte.getId());
			else
				result.forwardTo(RequisicaoController.class).incluir();

		}
	}

	protected void carregarFinalidades() {
		result.include("finalidades", FinalidadeRequisicao.listarTodos());
	}

	public void incluir(){
		RequisicaoTransporte requisicaoTransporte = new RequisicaoTransporte();
		DpPessoa dpPessoa = getTitular();
		requisicaoTransporte.setSolicitante(dpPessoa);
		requisicaoTransporte.setIdSolicitante(dpPessoa.getId());

		carregarTiposDeCarga(requisicaoTransporte);

		carregarFinalidades();

		result.include("requisicaoTransporte", requisicaoTransporte);
	}

	public void editar(Long id) throws Exception {
		RequisicaoTransporte requisicaoTransporte = RequisicaoTransporte.AR.findById(id);
		checarSolicitante(requisicaoTransporte.getSolicitante().getIdInicial(), requisicaoTransporte.getCpComplexo().getIdComplexo(), true);
		requisicaoTransporte.setIdSolicitante(requisicaoTransporte.getSolicitante().getId());

		carregarTiposDeCarga(requisicaoTransporte);
		carregarFinalidades();
		boolean checkRetorno = (requisicaoTransporte.getDataHoraRetornoPrevisto() == null ? false : true);

		result.include("requisicaoTransporte", requisicaoTransporte);
		result.include("checkRetorno", checkRetorno);
	}

	private void checarSolicitante(
			Long idSolicitante, Long idComplexo, Boolean escrita) throws Exception {
		if (! autorizacaoGI.ehAdministrador() && ! autorizacaoGI.ehAprovador() && ! autorizacaoGI.ehAgente() && ! autorizacaoGI.ehAdministradorMissao() && ! autorizacaoGI.ehAdministradorMissaoPorComplexo()) {
			if (! getTitular().getIdInicial().equals(idSolicitante)) {
				try {
					throw new Exception(new I18nMessage("requisicoes", "requisicoes.checarSolicitante.exception").getMessage());
				} catch (Exception e) {
					tratarExcecoes(e);
				}
			}
		} else if ( autorizacaoGI.ehAgente() ) {
			if ( !getTitular().getIdInicial().equals(idSolicitante) && escrita ) {
				try {
					throw new Exception(new I18nMessage("requisicoes", "requisicoes.checarSolicitante.exception").getMessage());
				} catch (Exception e) {
					tratarExcecoes(e);
				}
			}
		} else if (autorizacaoGI.ehAdministradorMissaoPorComplexo()) {
			if (! getComplexoAdministrado().getIdComplexo().equals(idComplexo) && escrita) {
				try {
					throw new Exception(new I18nMessage("requisicoes", "requisicoes.checarSolicitante.exception").getMessage());
				} catch (Exception e) {
					tratarExcecoes(e);
				}
			} else if (autorizacaoGI.ehAprovador()) {
				if ( !recuperarComplexoPadrao().getIdComplexo().equals(idComplexo) && escrita )
					try {
						throw new Exception(new I18nMessage("requisicoes", "requisicoes.checarSolicitante.exception").getMessage());
					} catch (Exception e) {
						tratarExcecoes(e);
					}
			}
		}
	}

	public void ler(Long id) throws Exception {
		RequisicaoTransporte requisicaoTransporte = RequisicaoTransporte.AR.findById(id);
		checarSolicitante(requisicaoTransporte.getSolicitante().getIdInicial(), requisicaoTransporte.getCpComplexo().getIdComplexo(), false);
		requisicaoTransporte.setIdSolicitante(requisicaoTransporte.getSolicitante().getId());
		MenuMontador.instance(result).recuperarMenuRequisicoes(id, false, false);
		carregarTiposDeCarga(requisicaoTransporte);
		carregarFinalidades();

		result.include("requisicaoTransporte", requisicaoTransporte);
	}

	protected void carregarTiposDeCarga(RequisicaoTransporte req) {
		TipoDePassageiro tipoDePassageiro = TipoDePassageiro.CARGA;
		boolean checkSemPassageiros = false;

		if( (req != null) && (req.getTiposDePassageiro() != null) )
			checkSemPassageiros = req.getTiposDePassageiro().contains(TipoDePassageiro.NENHUM);

		result.include("tipoDePassageiro", tipoDePassageiro);
		result.include("checkSemPassageiros", checkSemPassageiros);
	}

	public void ler() {
		// Redireciona para a view ler.jsp
	}

	public void buscarPelaSequence(String sequence, boolean popUp) throws Exception {
		RequisicaoTransporte req = recuperarPelaSigla(sequence, popUp);
		carregarTiposDeCarga(req);
		carregarFinalidades();

		result.redirectTo(this).ler();
	}

	protected RequisicaoTransporte recuperarPelaSigla(String sequence, boolean popUp) throws Exception {
		RequisicaoTransporte requisicaoTransporte = RequisicaoTransporte.buscar(sequence);
		checarSolicitante(requisicaoTransporte.getSolicitante().getIdInicial(), requisicaoTransporte.getCpComplexo().getIdComplexo(), false);
		MenuMontador.instance(result).recuperarMenuRequisicoes(requisicaoTransporte.getId(), popUp, false);
		requisicaoTransporte.setIdSolicitante(requisicaoTransporte.getSolicitante().getId());

		if(!popUp) {
			MenuMontador.instance(result).recuperarMenuRequisicoes(requisicaoTransporte.getId(), popUp, false);
		}

		result.include("requisicaoTransporte", requisicaoTransporte);

		if(null != requisicaoTransporte.getDataHoraRetornoPrevisto())
			result.include("checkRetorno", true);

		return requisicaoTransporte;
	}

	public void excluir(Long id) throws Exception {
		RequisicaoTransporte requisicaoTransporte = RequisicaoTransporte.AR.findById(id);
		checarSolicitante(requisicaoTransporte.getSolicitante().getIdInicial(), requisicaoTransporte.getCpComplexo().getIdComplexo(), true);
		requisicaoTransporte.setIdSolicitante(requisicaoTransporte.getSolicitante().getId());

		try {
			requisicaoTransporte.excluir(false);
		} catch (SigaTpException ex) {
			error(true, "requisicaoTransporte", ex.getMessage().toString());
			if(validator.hasErrors()) {
				MenuMontador.instance(result).recuperarMenuRequisicoes(requisicaoTransporte.getId(), true, false);
				carregarTiposDeCarga(requisicaoTransporte);
				carregarFinalidades();
				result.include("requisicaoTransporte", requisicaoTransporte);

				result.redirectTo(this).ler();
			}
		}
		catch (Exception ex) {
			throw ex;
		}

		result.redirectTo(this).listar();
	}

	private DpPessoa recuperaPessoa(Long idSolicitante) throws Exception {
		DpPessoa dpPessoa = DpPessoa.AR.findById(idSolicitante);
		return 	DpPessoa.AR.find("idPessoaIni = ? and dataFimPessoa = null", dpPessoa.getIdInicial()).first();
	}

	private void tratarExcecoes(Exception e) {
		throw new AplicacaoException(e.getMessage());
	}
}