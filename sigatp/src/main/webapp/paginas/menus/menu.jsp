<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

${_cabecalho_pre_head.raw()}
${moreHead.raw()}
<link rel="stylesheet" href="public/stylesheets/main.css" type="text/css" media="screen"/>
<link rel="stylesheet" href="public/javascripts/simplepagination/css/simplePagination.css" type="text/css"/>	
<script src="public/javascripts/jquery-1.6.4.min.js"></script>
<script src="public/javascripts/jquery/jquery-ui-1.8.16.custom.min.js"></script>
<script src="public/javascripts/simplepagination/js/jquery.simplePagination.js"></script>
<script src="public/javascripts/main.js"></script>

<c:if test="${_cabecalho_pre_menu != null}">
	${_cabecalho_pre_menu.raw()}
	<li id="limenuRequisicoes"><a id="menuRequisicoes" class="" href="#">Requisi&ccedil;&otilde;es</a>
		<ul>
			<li id="limenuRequisicoesIncluir"><a id="menuRequisicoesIncluir" class="" href="@{Requisicoes.incluir}">Incluir</a></li>
			
			<c:choose>
      			<c:when test="${exibirMenuAdministrar || exibirMenuAdministrarMissao || exibirMenuAdministrarMissaoComplexo || exibirMenuAdministrarFrota || exibirMenuAprovador }">
					<c:if test="${exibirMenuAdministrar || exibirMenuAdministrarMissao || exibirMenuAprovador}">
						<li id="limenuRequisicoesAprovar"><a id="menuRequisicoesAprovar" class="" href="@{Requisicoes.listarPAprovar}">Aprovar/Rejeitar</a></li>
					</c:if>
					<li id="limenuRequisicoesListar"><a id="menuRequisicoesListar" class="" href="@{Requisicoes.listarFiltrado(models.EstadoRequisicao.AUTORIZADA,models.EstadoRequisicao.NAOATENDIDA)}">Listar</a></li>
				</c:when>
				<c:otherwise>
					<li><a id="menuRequisicoesListar" class="" href="@{Requisicoes.listar}">Listar</a></li>
				</c:otherwise>
			</c:choose>
		</ul>
	</li>
	<c:if test="${exibirMenuGabinete || exibirMenuAdminGabinete}">
		<li>
			<a id="menuGabinetes" class="" href="#">Gabinetes</a>
		  	<ul>
				<li><a id="menuGabAbastecimentos" class="" href="@{Abastecimentos.listar}">Abastecimentos</a></li>  	
		 		<li><a id="menuGabinetesListar" class="" href="@{ControlesGabinete.listar()}">Controles de entrada / sa&iacute;da</a></li>
		 		<li><a id="menuAdmFornecedores" class="" href="@{Fornecedores.listar}">Fornecedores</a></li>	
			</ul>
		</li>
	</c:if>
	<c:if test="${exibirMenuAdministrar || exibirMenuAdministrarMissao || exibirMenuAdministrarMissaoComplexo  || exibirMenuAgente}">
	  <li><a id="menuMissoes" class="" href="#">Miss&otilde;es</a>
	  	<ul>
	  	<c:choose>
		 	 <c:when test="${exibirMenuAdministrar || exibirMenuAdministrarMissao || exibirMenuAdministrarMissaoComplexo}">
				<li><a id="menuMissoesIncluir" class="" href="@{Missoes.incluir}">Incluir</a></li>
				<li><a id="menuMissoesListar" class="" href="@{Missoes.listarFiltrado()}">Listar</a></li>
		 	 </c:when>
			 <c:otherwise>
			 	<li><a id="menuMissoesListar" class="" href="@{Missoes.listarPorCondutorLogado()}">Listar</a></li>
			 </c:otherwise>
	  	</c:choose>
		</ul>
	</li>
	</c:if>
	
	<c:choose>
		<c:when test="${exibirMenuAdministrar}">
			<li><a id="menuAdm" href="#">Administrar</a>  	
				<ul>
					<li><a id="menuAdmAbastecimentos" class="" href="@{Abastecimentos.listar}">Abastecimentos</a></li>
					<li><a id="menuAdmInfracoes" class="" href="@{AutosDeInfracao.listar}">Autos de Infra&ccedil;&atilde;o</a></li>
					<!-- <li><a id="menuAdmCompras" class="" href="@{Application.emdesenvolvimento}">Compras de Materiais</a></li> -->
					<li><a id="menuAdmCondutores" class="" href="@{Condutores.listar}">Condutores</a></li>
					<li><a id="menuAdmFinalidades" class="" href="@{Finalidades.listar}">Finalidades</a></li>
					<li><a id="menuAdmFornecedores" class="" href="@{Fornecedores.listar}">Fornecedores</a></li>
					<li><a id="menuAdmPenalidades" class="" href="@{penalidades.listar}">Penalidades</a></li>		
					<li><a id="menuAdmPlantoes" class="" href="@{PlantoesMensais.listar}">Plant&otilde;es mensais</a></li>
					<!-- <li><a id="menuAdmMateriais" class="" href="@{Application.emdesenvolvimento}">Materiais em Estoque</a></li> -->
					<li><a id="menuAdmRelat" class="" href="#">Relat&oacute;rios</a>
						<ul>
					  		<li><a id="menuAdmRelatAgendaCondutores" class="" href="#" onclick="javascript:window.open('@{Relatorios.listarAgendaTodosCondutores}');">Agenda dos Condutores</a></li>
					  		<li><a id="menuAdmRelatAgendaVeiculos" class="" href="#" onclick=  "javascript:window.open('@{Relatorios.listarAgendaTodosVeiculos}');">Agenda dos Ve&iacute;culos</a></li>
							<li><a id="menuAdmRelatMissoesEmAndamento" class="" href="#" onclick="javascript:window.open('@{Relatorios.listarMissoesEmAndamento}');">Miss&otilde;es em Andamento</a></li>
							<li><a id="menuAdmRelatRanking" class="" href="#" onclick="javascript:window.open('@{RelatoriosRanking.consultar}');">Ranking por Requisi&ccedil;&otilde;es</a></li>
							<li><a id="menuAdmRelatConsumoMedio" class="" href="#" onclick="javascript:window.open('@{RelatoriosConsumoMedio.consultar}');">Consumo M&eacute;dio de Combust&iacute;vel</a></li>
						</ul>
					</li>
					<li><a id="menuAdmServicos" class="" href="@{ServicosVeiculo.listar}">Servi&ccedil;os</a></li>
					<li><a id="menuAdmVeiculos" class="" href="@{Veiculos.listar}">Ve&iacute;culos</a></li>
				</ul>
			</li> 
		</c:when>
		<c:when test="${exibirMenuAdministrarFrota}">
			<li><a id="menuAdm" href="#">Administrar</a>  	
				<ul>
					<li><a id="menuAdmAbastecimentos" class="" href="@{Abastecimentos.listar}">Abastecimentos</a></li>
					<!-- <li><a id="menuAdmCompras" class="" href="@{Application.emdesenvolvimento}">Compras de Materiais</a></li> -->
					<li><a id="menuAdmFornecedores" class="" href="@{Fornecedores.listar}">Fornecedores</a></li>
					<li><a id="menuAdmPenalidades" class="" href="@{penalidades.listar}">Penalidades</a></li>			
					<!-- <li><a id="menuAdmMateriais" class="" href="@{Application.emdesenvolvimento}">Materiais em Estoque</a></li> -->
					<li><a id="menuAdmRelat" class="" href="#">Relat&oacute;rios</a>
						<ul>
					  		<li><a id="menuAdmRelatAgendaVeiculos" class="" href="#" onclick=  "javascript:window.open('@{Relatorios.listarAgendaTodosVeiculos}');">Agenda dos Ve&iacute;culos</a></li>
						</ul>
					</li>
					<li><a id="menuAdmServicos" class="" href="@{ServicosVeiculo.listar}">Servi&ccedil;os</a></li>
					<li><a id="menuAdmVeiculos" class="" href="@{Veiculos.listar}">Ve&iacute;culos</a></li>
				</ul>
			</li> 
		</c:when>
		<c:when test="${exibirMenuAdministrarMissao || exibirMenuAdministrarMissaoComplexo}">
			<li><a id="menuAdm" href="#">Administrar</a>  	
				<ul>
					<li><a id="menuAdmAbastecimentos" class="" href="@{Abastecimentos.listar}">Abastecimentos</a></li>
					<li><a id="menuAdmInfracoes" class="" href="@{AutosDeInfracao.listar}">Autos de Infra&ccedil;&atilde;o</a></li>
					<li><a id="menuAdmCondutores" class="" href="@{Condutores.listar}">Condutores</a></li>
					<li><a id="menuAdmFinalidades" class="" href="@{Finalidades.listar}">Finalidades</a></li>
					<li><a id="menuAdmFornecedores" class="" href="@{Fornecedores.listar}">Fornecedores</a></li>
					<li><a id="menuAdmPlantoes" class="" href="@{PlantoesMensais.listar}">Plant&otilde;es mensais</a></li>
					<!-- <li><a id="menuAdmMateriais" class="" href="@{Application.emdesenvolvimento}">Materiais em Estoque</a></li> -->
					<li><a id="menuAdmRelat" class="" href="#">Relat&oacute;rios</a>
						<ul>
					  		<li><a id="menuAdmRelatAgendaCondutores" class="" href="#" onclick="javascript:window.open('@{Relatorios.listarAgendaTodosCondutores}');">Agenda dos Condutores</a></li>
					  		<li><a id="menuAdmRelatAgendaVeiculos" class="" href="#" onclick=  "javascript:window.open('@{Relatorios.listarAgendaTodosVeiculos}');">Agenda dos Ve&iacute;culos</a></li>
							<li><a id="menuAdmRelatMissoesEmAndamento" class="" href="#" onclick="javascript:window.open('@{Relatorios.listarMissoesEmAndamento}');">Miss&otilde;es em Andamento</a></li>
							<li><a id="menuAdmRelatRanking" class="" href="#" onclick="javascript:window.open('@{RelatoriosRanking.consultar}');">Ranking por Requisi&ccedil;&otilde;es</a></li>
							<li><a id="menuAdmRelatConsumoMedio" class="" href="#" onclick="javascript:window.open('@{RelatoriosConsumoMedio.consultar}');">Consumo M&eacute;dio de Combust&iacute;vel</a></li>
						</ul>
					</li>
				</ul>
			</li>
		</c:when>
	</c:choose>
		
	<li><a class="" href="#">Ajuda</a>
		<ul>
			<li><a class="" href="#" onclick="javascript:window.open('@{Application.exibirManualUsuario}');">Manual do Usu&aacute;rio</a></li>
	    	<c:if test="${exibirMenuGabinete || exibirMenuAdminGabinete}">
				<li><a class="" href="#" onclick="javascript:window.open('@{Application.exibirManualUsuarioDeGabinete}');">Manual do Usu&aacute;rio de Gabinete</a></li>
	    	</c:if>
		</ul>	
	</li>
</c:if>
${_cabecalho_pos.raw()}
<%-- #{doLayout /} --%>
${_rodape.raw()}