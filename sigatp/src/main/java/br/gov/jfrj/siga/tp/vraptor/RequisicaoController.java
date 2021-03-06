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
import br.com.caelum.vraptor.view.Results;
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
import br.gov.jfrj.siga.tp.model.TipoRequisicao;
import br.gov.jfrj.siga.tp.model.TpDao;
import br.gov.jfrj.siga.tp.util.SigaTpException;
import br.gov.jfrj.siga.tp.vraptor.i18n.MessagesBundle;
import br.gov.jfrj.siga.vraptor.SigaObjects;

@Resource
@Path("/app/requisicao")
public class RequisicaoController extends TpController {

    private static final String REQUISICOES_CHECAR_SOLICITANTE_EXCEPTION = "requisicoes.checarSolicitante.exception";
    private static final String TIPOS_REQUISICAO = "tiposRequisicao";
    private static final String CHECK_RETORNO = "checkRetorno";
    private static final String ESTADO_REQUISICAO = "estadoRequisicao";
    private static final String REQUISICAO_TRANSPORTE = "requisicaoTransporte";
    private AutorizacaoGI autorizacaoGI;

    public RequisicaoController(HttpServletRequest request, Result result, Validator validator, SigaObjects so, EntityManager em, AutorizacaoGI autorizacaoGI) {
        super(request, result, TpDao.getInstance(), validator, so, em);
        this.autorizacaoGI = autorizacaoGI;
    }

    @Path("/listar")
    public void listar() {
        carregarRequisicoesUltimosSeteDiasPorEstados(null);
        result.include(ESTADO_REQUISICAO, EstadoRequisicao.PROGRAMADA);
        MenuMontador.instance(result).recuperarMenuListarRequisicoes(null);
        result.include("estadosRequisicao", EstadoRequisicao.values());
    }

    @RoleAprovador
    @RoleAdmin
    @RoleAdminMissao
    @RoleAdminMissaoComplexo
    @Path("/listarPAprovar")
    public void listarPAprovar() {
        EstadoRequisicao[] estadosRequisicao = { EstadoRequisicao.ABERTA, EstadoRequisicao.AUTORIZADA, EstadoRequisicao.REJEITADA };
        carregarRequisicoesUltimosSeteDiasPorEstados(estadosRequisicao);
        result.include(ESTADO_REQUISICAO, EstadoRequisicao.ABERTA);
        MenuMontador.instance(result).recuperarMenuListarPAprovarRequisicoes(null);
        List<CpComplexo> complexos = TpDao.find(CpComplexo.class, "orgaoUsuario", getTitular().getOrgaoUsuario()).fetch();
        result.include("complexos", complexos);
    }

    @RoleAdmin
    @RoleAdminMissao
    @Path("/salvarNovoComplexo")
    public void salvarNovoComplexo(Long[] req, CpComplexo novoComplexo) {
        if (req == null) {
            throw new NullPointerException(MessagesBundle.getMessage("requisicoes.salvarNovoComplexo.exception"));
        }

        for (int cont = 0; cont < req.length; cont++) {
            RequisicaoTransporte requisicao = RequisicaoTransporte.AR.findById(req[cont]);
            requisicao.setCpComplexo(novoComplexo);
            requisicao.save();
        }

        result.redirectTo(this).listarPAprovar();
    }

    @Path("/listarFiltrado/{estadoRequisicao}")
    public void listarFiltrado(String estadoRequisicao) {
        result.forwardTo(this).listarFiltrado(estadoRequisicao, null);
    }

    @Path("/listarFiltrado/{estadoRequisicao}/{estadoRequisicaoP}")
    public void listarFiltrado(String estadoRequisicao, String estadoRequisicaoP) {
        EstadoRequisicao estadoReq = EstadoRequisicao.valueOf(estadoRequisicao);
        EstadoRequisicao estadoReqP = EstadoRequisicao.valueOf(null != estadoRequisicaoP ? estadoRequisicaoP : estadoRequisicao);
        EstadoRequisicao[] estadosRequisicao = { estadoReq, estadoReqP };
        carregarRequisicoesUltimosSeteDiasPorEstados(estadosRequisicao);
        MenuMontador.instance(result).recuperarMenuListarRequisicoes(estadoReq, estadoReqP);
        result.include(ESTADO_REQUISICAO, estadoRequisicao);
        result.use(Results.page()).of(RequisicaoController.class).listar();
    }

    @RoleAdmin
    @RoleAdminMissao
    @RoleAdminMissaoComplexo
    @RoleAprovador
    @Path("/listarPAprovarFiltrado/{estadoRequisicao}")
    public void listarPAprovarFiltrado(EstadoRequisicao estadoRequisicao) {
        EstadoRequisicao[] estadosRequisicao = { estadoRequisicao };
        carregarRequisicoesUltimosSeteDiasPorEstados(estadosRequisicao);
        MenuMontador.instance(result).recuperarMenuListarPAprovarRequisicoes(estadoRequisicao);
        result.include(ESTADO_REQUISICAO, estadoRequisicao);
        result.use(Results.page()).of(RequisicaoController.class).listarPAprovar();
    }

    @Path("/salvar")
    public void salvar(RequisicaoTransporte requisicaoTransporte, TipoDePassageiro[] tiposDePassageiros, boolean checkRetorno, boolean checkSemPassageiros) {
        validar(requisicaoTransporte, checkSemPassageiros, tiposDePassageiros, checkRetorno);
        requisicaoTransporte.setTiposDePassageiro(converterTiposDePassageiros(tiposDePassageiros));
        redirecionarSeErroAoSalvar(requisicaoTransporte, checkRetorno, checkSemPassageiros);

        DpPessoa dpPessoa = recuperaPessoa(requisicaoTransporte.getIdSolicitante());
        checarSolicitante(dpPessoa.getIdInicial(), recuperarComplexoPadrao().getIdComplexo(), true);

        requisicaoTransporte.setCpOrgaoUsuario(getTitular().getOrgaoUsuario());

        requisicaoTransporte.setCpComplexo(recuperarComplexoPadrao());

        requisicaoTransporte.setSequence(requisicaoTransporte.getCpOrgaoUsuario());
        boolean novaRequisicao = false;

        if (requisicaoTransporte.getId() == 0) {
            novaRequisicao = true;
            requisicaoTransporte.setDataHora(Calendar.getInstance());
        }

        requisicaoTransporte.setSolicitante(recuperaPessoa(requisicaoTransporte.getIdSolicitante()));

        requisicaoTransporte.save();

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

    private void validar(RequisicaoTransporte requisicaoTransporte, boolean checkSemPassageiros, TipoDePassageiro[] tiposDePassageiros, boolean checkRetorno) {

        if ((requisicaoTransporte.getDataHoraSaidaPrevista() != null) && (requisicaoTransporte.getDataHoraRetornoPrevisto() != null) && (!requisicaoTransporte.ordemDeDatasCorreta())
                || (checkRetorno && requisicaoTransporte.getDataHoraRetornoPrevisto() == null))
            validator.add(new I18nMessage("dataHoraRetornoPrevisto", "requisicaoTransporte.dataHoraRetornoPrevisto.validation"));

        if (!checkSemPassageiros && ((tiposDePassageiros == null) || (tiposDePassageiros.length == 0)))
            validator.add(new I18nMessage("tiposDePassageiros", "requisicaoTransporte.tiposDePassageiros.validation"));

        if (!checkSemPassageiros && (requisicaoTransporte.getPassageiros() == null || requisicaoTransporte.getPassageiros().isEmpty()))
            validator.add(new I18nMessage("passageiros", "requisicaoTransporte.passageiros.validation"));

        if (requisicaoTransporte.getTipoFinalidade().ehOutra() && requisicaoTransporte.getFinalidade() == null)
            validator.add(new I18nMessage("finalidade", "requisicaoTransporte.finalidade.validation"));

        validator.validate(requisicaoTransporte);
    }

    private void carregarRequisicoesUltimosSeteDiasPorEstados(EstadoRequisicao[] estadosRequisicao) {
        StringBuilder criterioBusca = new StringBuilder();
        criterioBusca.append("((dataHoraRetornoPrevisto is null and dataHoraSaidaPrevista >= ?) or (dataHoraRetornoPrevisto >= ?)) and cpOrgaoUsuario = ? ");
        Calendar ultimos7dias = Calendar.getInstance();
        ultimos7dias.add(Calendar.DATE, -7);
        Object[] parametros = { ultimos7dias, ultimos7dias, getTitular().getOrgaoUsuario() };
        recuperarRequisicoes(criterioBusca, parametros, estadosRequisicao);
    }

    protected void recuperarRequisicoes(StringBuilder criterioBusca, Object[] parametros, EstadoRequisicao[] estadosRequisicao) {
        if (!autorizacaoGI.ehAdministrador() && !autorizacaoGI.ehAdministradorMissao() && !autorizacaoGI.ehAdministradorMissaoPorComplexo() && !autorizacaoGI.ehAprovador()) {
            criterioBusca.append(" and solicitante.idPessoaIni = ?");

            Object[] parametrosFiltrado = new Object[parametros.length + 1];

            for (int i = 0; i < parametros.length; i++)
                parametrosFiltrado[i] = parametros[i];

            parametrosFiltrado[parametros.length] = getTitular().getIdInicial();
            parametros = parametrosFiltrado;
        } else {

            if (autorizacaoGI.ehAdministradorMissaoPorComplexo() || autorizacaoGI.ehAprovador()) {
                criterioBusca.append(" and cpComplexo = ?");

                Object[] parametrosFiltrado = new Object[parametros.length + 1];

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
            filtrarRequisicoes(requisicoesTransporte, estadosRequisicao);

        result.include("requisicoesTransporte", requisicoesTransporte);
    }

    private void filtrarRequisicoes(List<RequisicaoTransporte> requisicoesTransporte, EstadoRequisicao[] estadosRequisicao) {
        Boolean filtrarRequisicao;
        for (Iterator<RequisicaoTransporte> iterator = requisicoesTransporte.iterator(); iterator.hasNext();) {
            filtrarRequisicao = true;
            RequisicaoTransporte requisicaoTransporte = iterator.next();

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

        if ((tiposDePassageiros == null) || (tiposDePassageiros.length == 0))
            tiposParaSalvar.add(TipoDePassageiro.NENHUM);
        else
            for (int i = 0; i < tiposDePassageiros.length; i++)
                tiposParaSalvar.add(tiposDePassageiros[i]);

        return tiposParaSalvar;
    }

    @Path("/salvarAndamentos")
    public void salvarAndamentos(@Valid RequisicaoTransporte requisicaoTransporte, boolean checkRetorno, boolean checkSemPassageiros) {
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

    private void redirecionarSeErroAoSalvar(RequisicaoTransporte requisicaoTransporte, boolean checkRetorno, boolean checkSemPassageiros) {
        if (validator.hasErrors()) {
            MenuMontador.instance(result).recuperarMenuRequisicoes(requisicaoTransporte.getId(), false, false);
            carregarTiposDeCarga(requisicaoTransporte);
            carregarFinalidades();

            result.include(REQUISICAO_TRANSPORTE, requisicaoTransporte);
            result.include(CHECK_RETORNO, checkRetorno);
            result.include("checkSemPassageiros", checkSemPassageiros);
            result.include(TIPOS_REQUISICAO, TipoRequisicao.values());

            if (requisicaoTransporte.getId() > 0)
                validator.onErrorUse(Results.page()).of(RequisicaoController.class).editar(requisicaoTransporte.getId());
            else
                validator.onErrorUse(Results.page()).of(RequisicaoController.class).incluir();
        }
    }

    protected void carregarFinalidades() {
        result.include("finalidades", FinalidadeRequisicao.listarTodos());
    }

    @Path("/incluir")
    public void incluir() {
        RequisicaoTransporte requisicaoTransporte = new RequisicaoTransporte();
        DpPessoa dpPessoa = getTitular();
        requisicaoTransporte.setSolicitante(dpPessoa);
        requisicaoTransporte.setIdSolicitante(dpPessoa.getId());

        carregarTiposDeCarga(requisicaoTransporte);

        carregarFinalidades();

        result.include("opcoesDeTiposDePassageiro", TipoDePassageiro.values());
        result.include(TIPOS_REQUISICAO, TipoRequisicao.values());
        result.include(REQUISICAO_TRANSPORTE, requisicaoTransporte);
    }

    @Path("/editar/{id}")
    public void editar(Long id) {
        RequisicaoTransporte requisicaoTransporte = RequisicaoTransporte.AR.findById(id);
        checarSolicitante(requisicaoTransporte.getSolicitante().getIdInicial(), requisicaoTransporte.getCpComplexo().getIdComplexo(), true);
        requisicaoTransporte.setIdSolicitante(requisicaoTransporte.getSolicitante().getId());

        carregarTiposDeCarga(requisicaoTransporte);
        carregarFinalidades();
        boolean checkRetorno = requisicaoTransporte.getDataHoraRetornoPrevisto() == null ? false : true;

        result.include("esconderBotoes", false);
        result.include(REQUISICAO_TRANSPORTE, requisicaoTransporte);
        result.include(CHECK_RETORNO, checkRetorno);
        result.include(TIPOS_REQUISICAO, TipoRequisicao.values());
    }

    private void checarSolicitante(Long idSolicitante, Long idComplexo, Boolean escrita) {
        if (!autorizacaoGI.ehAdministrador() && !autorizacaoGI.ehAprovador() && !autorizacaoGI.ehAgente() && !autorizacaoGI.ehAdministradorMissao()
                && !autorizacaoGI.ehAdministradorMissaoPorComplexo()) {
            if (!getTitular().getIdInicial().equals(idSolicitante)) {
                try {
                    throw new Exception(MessagesBundle.getMessage(REQUISICOES_CHECAR_SOLICITANTE_EXCEPTION));
                } catch (Exception e) {
                    tratarExcecoes(e);
                }
            }
        } else if (autorizacaoGI.ehAgente()) {
            if (!getTitular().getIdInicial().equals(idSolicitante) && escrita) {
                try {
                    throw new Exception(MessagesBundle.getMessage(REQUISICOES_CHECAR_SOLICITANTE_EXCEPTION));
                } catch (Exception e) {
                    tratarExcecoes(e);
                }
            }
        } else if (autorizacaoGI.ehAdministradorMissaoPorComplexo()) {
            if (!getComplexoAdministrado().getIdComplexo().equals(idComplexo) && escrita) {
                try {
                    throw new Exception(MessagesBundle.getMessage(REQUISICOES_CHECAR_SOLICITANTE_EXCEPTION));
                } catch (Exception e) {
                    tratarExcecoes(e);
                }
            } else if (autorizacaoGI.ehAprovador() && (!recuperarComplexoPadrao().getIdComplexo().equals(idComplexo) && escrita)) {
                try {
                    throw new Exception(MessagesBundle.getMessage(REQUISICOES_CHECAR_SOLICITANTE_EXCEPTION));
                } catch (Exception e) {
                    tratarExcecoes(e);
                }
            }
        }
    }

    @Path("/ler/{id}")
    public void ler(Long id) {
        RequisicaoTransporte requisicaoTransporte = RequisicaoTransporte.AR.findById(id);
        checarSolicitante(requisicaoTransporte.getSolicitante().getIdInicial(), requisicaoTransporte.getCpComplexo().getIdComplexo(), false);
        requisicaoTransporte.setIdSolicitante(requisicaoTransporte.getSolicitante().getId());
        MenuMontador.instance(result).recuperarMenuRequisicoes(id, false, false);
        carregarTiposDeCarga(requisicaoTransporte);
        carregarFinalidades();

        result.include(REQUISICAO_TRANSPORTE, requisicaoTransporte);
    }

    protected void carregarTiposDeCarga(RequisicaoTransporte req) {
        boolean checkSemPassageiros = false;

        if ((req != null) && (req.getTiposDePassageiro() != null))
            checkSemPassageiros = req.getTiposDePassageiro().contains(TipoDePassageiro.NENHUM);

        result.include("opcoesDeTiposDePassageiro", TipoDePassageiro.values());
        result.include("checkSemPassageiros", checkSemPassageiros);
    }

    @Path("/ler")
    public void ler() {
        result.include("esconderBotoes", true);
    }

    @Path("/buscarPelaSequence/{popUp}/{sequence*}")
    public void buscarPelaSequence(boolean popUp, String sequence) {
        RequisicaoTransporte requisicaoTransporte = recuperarPelaSigla(sequence, popUp);
        carregarTiposDeCarga(requisicaoTransporte);
        carregarFinalidades();

        result.include(TIPOS_REQUISICAO, TipoRequisicao.values());
        result.include(REQUISICAO_TRANSPORTE, requisicaoTransporte);
        result.forwardTo(this).ler();
    }

    protected RequisicaoTransporte recuperarPelaSigla(String sequence, boolean popUp) {
        RequisicaoTransporte requisicaoTransporte = RequisicaoTransporte.buscar(sequence);
        checarSolicitante(requisicaoTransporte.getSolicitante().getIdInicial(), requisicaoTransporte.getCpComplexo().getIdComplexo(), false);
        MenuMontador.instance(result).recuperarMenuRequisicoes(requisicaoTransporte.getId(), popUp, false);
        requisicaoTransporte.setIdSolicitante(requisicaoTransporte.getSolicitante().getId());

        if (!popUp) {
            MenuMontador.instance(result).recuperarMenuRequisicoes(requisicaoTransporte.getId(), popUp, false);
        }

        result.include(REQUISICAO_TRANSPORTE, requisicaoTransporte);

        if (null != requisicaoTransporte.getDataHoraRetornoPrevisto())
            result.include(CHECK_RETORNO, true);

        return requisicaoTransporte;
    }

    @Path("/excluir/{id}")
    public void excluir(Long id) {
        RequisicaoTransporte requisicaoTransporte = RequisicaoTransporte.AR.findById(id);
        checarSolicitante(requisicaoTransporte.getSolicitante().getIdInicial(), requisicaoTransporte.getCpComplexo().getIdComplexo(), true);
        requisicaoTransporte.setIdSolicitante(requisicaoTransporte.getSolicitante().getId());

        try {
            requisicaoTransporte.excluir(false);
        } catch (SigaTpException ex) {
            error(true, REQUISICAO_TRANSPORTE, ex.getMessage().toString());
            if (validator.hasErrors()) {
                MenuMontador.instance(result).recuperarMenuRequisicoes(requisicaoTransporte.getId(), true, false);
                carregarTiposDeCarga(requisicaoTransporte);
                carregarFinalidades();
                result.include(REQUISICAO_TRANSPORTE, requisicaoTransporte);
                result.redirectTo(this).ler();
            }
        } catch (Exception ex) {
            throw ex;
        }

        result.redirectTo(this).listar();
    }

    private DpPessoa recuperaPessoa(Long idSolicitante) {
        DpPessoa dpPessoa = DpPessoa.AR.findById(idSolicitante);
        return DpPessoa.AR.find("idPessoaIni = ? and dataFimPessoa = null", dpPessoa.getIdInicial()).first();
    }

    private void tratarExcecoes(Exception e) {
        throw new AplicacaoException(e.getMessage());
    }
}