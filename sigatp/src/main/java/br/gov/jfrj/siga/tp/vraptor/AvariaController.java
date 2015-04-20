package br.gov.jfrj.siga.tp.vraptor;

import java.util.List;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.Validator;
import br.com.caelum.vraptor.view.Results;
import br.gov.jfrj.siga.tp.auth.annotation.RoleAdmin;
import br.gov.jfrj.siga.tp.auth.annotation.RoleAdminFrota;
import br.gov.jfrj.siga.tp.model.Avaria;
import br.gov.jfrj.siga.tp.model.ItemMenu;
import br.gov.jfrj.siga.tp.model.Missao;
import br.gov.jfrj.siga.tp.model.TpDao;
import br.gov.jfrj.siga.tp.model.Veiculo;
import br.gov.jfrj.siga.tp.util.PerguntaSimNao;
import br.gov.jfrj.siga.vraptor.SigaObjects;

@Resource
@Path("/app/avaria")
public class AvariaController extends TpController {
	
	private static final String MODO = "modo";
	private static final String BOTAO_EDITAR = "views.botoes.editar";
	private static final String BOTAO_INCLUIR = "views.botoes.incluir";

	public AvariaController(HttpServletRequest request, Result result, Validator validator, SigaObjects so, EntityManager em){
		super(request, result, TpDao.getInstance(), validator, so, em);
	}

	@Path("/listar")
	public void listar() {
		List<Avaria> avarias = Avaria.listarTodos();
		result.include("avarias", avarias);
	}
	
	@Path("/listarPorVeiculo/{idVeiculo}")
	public void listarPorVeiculo(Long idVeiculo) throws Exception {
		montarListaDeAvariasPorVeiculo(idVeiculo);
	}

	protected void montarListaDeAvariasPorVeiculo(Long idVeiculo) throws Exception {
		Veiculo veiculo = Veiculo.AR.findById(idVeiculo);
		result.include("veiculo", veiculo);
		result.include("avarias", Avaria.buscarTodasPorVeiculo(veiculo));
		MenuMontador.instance(result).recuperarMenuVeiculos(idVeiculo, ItemMenu.AVARIAS);
	}

	@RoleAdmin
	@RoleAdminFrota
	@Path("/incluir/{idVeiculo}")
	public void editar(Long idVeiculo) throws Exception {
		Avaria avaria = new Avaria();
		boolean fixarVeiculo = false;
		List<Veiculo> veiculos = Veiculo.listarTodos(getTitular().getOrgaoUsuario());
		if (idVeiculo != null) {
			avaria.setVeiculo(Veiculo.AR.findById(idVeiculo));
			fixarVeiculo = true;
			MenuMontador.instance(result).recuperarMenuVeiculos(avaria.getVeiculo().getId(), ItemMenu.AVARIAS);
		}
		
		result.include(MODO, BOTAO_INCLUIR);
		result.include("avaria", avaria);
		result.include("veiculos", veiculos);
		result.include("fixarVeiculo", fixarVeiculo);
	}

	@RoleAdmin
	@RoleAdminFrota
	@Path("/editar/{id}/{fixarVeiculo}")
	public void editar(Long id, Boolean fixarVeiculo) throws Exception {
		Avaria avaria = Avaria.AR.findById(id);
		Veiculo veiculo = new Veiculo();
		
		if (fixarVeiculo)
			veiculo = avaria.getVeiculo();
		
		List<Veiculo> veiculos = Veiculo.listarTodos(getTitular().getOrgaoUsuario());
		MenuMontador.instance(result).recuperarMenuVeiculos(avaria.getVeiculo().getId(), ItemMenu.AVARIAS);
		
		result.include(MODO, BOTAO_EDITAR);
		result.include("avaria", avaria);
		result.include("veiculos", veiculos);
		result.include("veiculo", veiculo);
		result.include("fixarVeiculo", fixarVeiculo);
	}

	@RoleAdmin
	@RoleAdminFrota
	@Path("/salvar")
	public void salvar(@Valid Avaria avaria, boolean fixarVeiculo) throws Exception {
		if (validator.hasErrors()) {
			MenuMontador.instance(result).recuperarMenuVeiculos(avaria.getVeiculo().getId(), ItemMenu.AVARIAS);
			List<Veiculo> veiculos = Veiculo.listarTodos(getTitular().getOrgaoUsuario());
			Veiculo veiculo = Veiculo.AR.findById(avaria.getVeiculo().getId());

			if(null == avaria.getId())
				result.include(MODO, BOTAO_INCLUIR);
			else
				result.include(MODO, BOTAO_EDITAR);
			
			result.include("avaria", avaria);
			result.include("veiculos", veiculos);
			result.include("veiculo", veiculo);
			result.include("fixarVeiculo", fixarVeiculo);
			
			validator.onErrorUse(Results.page()).of(AvariaController.class).editar(null);
		}

		if (avaria.podeCircular.equals(PerguntaSimNao.NAO)) {
			avaria.setVeiculo(Veiculo.AR.findById(avaria.getVeiculo().getId()));
			List<Missao> missoes = Missao.retornarMissoes("veiculo.id", avaria.getVeiculo().getId(), avaria.getVeiculo().getCpOrgaoUsuario().getId(), avaria.getDataDeRegistro(), avaria.getDataDeSolucao());
			StringBuilder listaMissoes = new StringBuilder();
			String delimitador = "";

			for (Missao item : missoes) {
				listaMissoes.append(delimitador).append(item.getSequence());
				delimitador = ",";
			}
			
			error(missoes.isEmpty(), "LinkErroVeiculo", listaMissoes.toString());
		}

		if (validator.hasErrors()) {
			result.include("avaria", avaria);
			if(avaria.getId() > 0)
				validator.onErrorUse(Results.page()).of(AvariaController.class).editar(avaria.getId(), true);
			else
				validator.onErrorUse(Results.page()).of(AvariaController.class).editar(null);
		} else {
			avaria.save();
			if (fixarVeiculo)
				result.redirectTo(this).listarPorVeiculo(avaria.getVeiculo().getId());
			else
				result.redirectTo(this).listar();
		}
	}

	@RoleAdmin
	@RoleAdminFrota
	@Path("/excluir/{id}/{fixarVeiculo}")
	public void excluir(Long id, boolean fixarVeiculo) throws Exception {
		Avaria avaria;
		avaria = Avaria.AR.findById(id);
		Veiculo veiculo = avaria.getVeiculo();
		avaria.delete();
		if (fixarVeiculo) {
			result.redirectTo(this).listarPorVeiculo(veiculo.getId());
		} else {
			result.redirectTo(this).listar();
		}
	}
}