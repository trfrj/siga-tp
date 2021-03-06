<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://localhost/jeetags" prefix="siga" %>
<%@ taglib prefix="sigatp" tagdir="/WEB-INF/tags/" %>
<%@ taglib prefix="tptags" uri="/WEB-INF/tpTags.tld"%>

<script src="/sigatp/public/javascripts/jquery/jquery-ui-1.8.16.custom.min.js"></script>

<jsp:include page="../tags/calendario.jsp" />
<sigatp:decimal />

<style>
 	#errosAjax {display: none;}
</style>

<form id="formMissoes" method="post" enctype="multipart/form-data">
	<sigatp:erros />

	<div id="errosAjax" class="gt-error">
		<li></li>
	</div>

	<br><br>
	<input type="hidden" id="missaoId" name="missao" value="${missao.id}" />
	<input type="hidden" id="veiculosDisp" name="veiculosDisp" value="" />

	<c:if test="${mostrarDadosProgramada}">
		<h3>Informa&ccedil;&otilde;es B&aacute;sicas</h3>
		<div id ="infbasicas" class="gt-content-box gt-form clearfix">
	       	<label class="coluna">Estado:</label>
	       	<p class="clearfix">${missao.estadoMissao}</p>
	       	<c:if test="${mostrarDadosFinalizada}">
	        	<div class="coluna margemDireitaG">
		        	<label for="distanciaPercorridaEmKm" >Dist&acirc;ncia Percorrida</label>
	        		<input type="text" readonly="readonly" name="missao.distanciaPercorridaEmKm" value="${missao.distanciaPercorridaEmKm}" size="12" class="decimal"/>
				</div>
	        	<div class="coluna margemDireitaG">
		        	<label for="tempoBruto">Tempo</label>
	        		<input type="text" readonly="readonly" name="missao.tempoBruto" value="${missao.tempoBruto}" size="12" />
				</div>
	        	<div class="coluna margemDireitaG">
		        	<label for="consumoEmLitros">Consumo (l)</label>
	        		<input type="text" name="missao.consumoEmLitros" value="${missao.consumoEmLitros}" size="12" class="decimal" />
				</div>
	       	</c:if>
		</div>
	</c:if>

	<h3> Requisi&ccedil;&atilde;o(&otilde;es)</h3>
    <script>
		function inserirLinhaTabela() {
			var table = document.getElementById("tbody");
			table.innerHTML = table.innerHTML;
		}

		var okButton = function() {
		    	inserirRequisicoesSelecionadas();
			 	if (!${missao.id})
			 		verificarMenorDataRequisicao();
				$( this ).dialog( "close" );
			}

		var cancelButton = function() {
				$( this ).dialog( "close" );
			}

		$(function() {
			$( "#dialog-form" ).dialog({
				autoOpen: false,
				height: 600,
				width: 850,
				modal: true,
				buttons: {
					"Ok": okButton,
					Cancel: cancelButton
				},
				close: function() {
				}
			});

			$( "#btn-Incluir-Requisicoes" ).click(function() {
					$( "#dialog-form" ).dialog( "open" );
				});
		});
    </script>

	<div id ="gridRequisicoes" class="gt-content-box gt-for-table">
	 	<table id="htmlgridRequisicoes" class="gt-table" >
	    	<thead>
		    	<tr style="font-weight: bold;">
		    		<th>Sa&iacute;da prevista</th>
		    		<th>Retorno previsto</th>
		    		<th>Dados da Requisi&ccedil;&atilde;o</th>
		    		<th width="8%"></th>
				</tr>
			</thead>
			<tbody id="tbody">
				<c:if test="${null != missao.requisicoesTransporte && !missao.requisicoesTransporte.isEmpty()}">
					<c:forEach items="${missao.requisicoesTransporte}" var="requisicaoTransporte">
			   			<input type="hidden" name="requisicoesAntigas" readonly="readonly" value="${requisicaoTransporte.id}" class="requisicoes" />
			   			<input type="hidden" name="missao.inicioRapido" id="inicioRapido" value="${missao.inicioRapido}" />

                		<tr id="row_${requisicaoTransporte.id}">
			   	    		<input type="hidden" name='requisicoesSelecionadas' readonly="readonly" value="${requisicaoTransporte.id}" class="requisicoes" />
			   	    		<input type="hidden" name='requisicoesSelecionadas1' readonly="readonly" value="${requisicaoTransporte.id}" class="requisicoes" />

			   	   			<td name="saidaDataReqSelecionada"><fmt:formatDate pattern="dd/MM/yyyy HH:mm" value="${requisicaoTransporte.dataHoraSaidaPrevista.time}" /></td>
		    				<td>
		    					<c:choose>
		    						<c:when test="${requisicaoTransporte.dataHoraRetornoPrevisto != null}">
		    							<fmt:formatDate pattern="dd/MM/yyyy HH:mm" value="${requisicaoTransporte.dataHoraRetornoPrevisto.time}" />
		    						</c:when>
		    						<c:otherwise>
		    							<fmt:message key="no"/>
		    						</c:otherwise>
		    					</c:choose>
		    				</td>
			   	    		<td>
			   	    			<tptags:link texto="${requisicaoTransporte.descricaoCompleta}"
			   	    						 parteTextoLink="${requisicaoTransporte.buscarSequence()}"
			   	    						 comando="${linkTo[RequisicaoController].buscarPelaSequence[true][requisicaoTransporte.buscarSequence()]}">
			   	    			</tptags:link>
							</td>
							<c:if test="${requisicaoTransporte.servicoVeiculo != null}">
								<input type="hidden" name="requisicoesSelecionadasVeiculo"  readonly="readonly" value="${null != requisicaoTransporte.servicoVeiculo.veiculo ? requisicaoTransporte.servicoVeiculo.veiculo.id : ''}"/>
							</c:if>
					   	    <td width="8%" >
					   	    	<c:if test="${(mostrarDadosIniciada || mostrarDadosFinalizada) && !mostrarBotoesIniciar && !mostrarBotoesIniciarRapido}">
						   	    	<select name="requisicaoTransporte.ultimoEstado">
						   	    		<c:forEach items="${ultimosEstados}" var="ultimos">
						   	    			<option value="${ultimos}" ${ultimos == requisicaoTransporte.ultimoEstado ? 'selected' : ''}>${ultimos.descricao}</option>
						   	    		</c:forEach>
						   	    	</select>
					   	    	</c:if>
					   	    	<c:if test="${!mostrarDadosCancelada && !mostrarDadosIniciada && !mostrarDadosFinalizada}">
			   	    				<a class="linkExcluir" name="linkExcluirSelecionados" style="display:inline" onclick="javascript:apagaLinha(this);" href="#"><fmt:message key="views.label.excluir"/></a>
					   	    	</c:if>
					   	    </td>
						</tr>
					</c:forEach>
				</c:if>
			</tbody>
		</table>
	</div>
	<c:if test="${!mostrarDadosFinalizada && !mostrarDadosIniciada}">
		<div id ="btngridRequisicoes" class="gt-table-buttons">
	    	<input type="button" id="btn-Incluir-Requisicoes" value="<fmt:message key='views.botoes.incluir'/>"  class="gt-btn-medium gt-btn-left btnSelecao" />
		</div>
	</c:if>
	<script type="text/javascript">

		var selectFactory = function(array, id, name, disabled) {
			var $select = $('<select>');
			$select.attr('id', id);
			$select.attr('name', name);
			$select.attr('size', 1);
			if(disabled)
				$select.attr('disabled', 'disabled');

			$.each(array, function(idx, item){
				  var $option = $('<option>');
				  $option.attr('value', item.id);
				  if(item.selected)
					  $option.attr('selected', 'selected');
				  $option.html(item.descricao);
				  $select.append($option);
			  });

			return $select;
		}

		var getLink = function() {
			var idMissao = $('#missaoId').val();
			var dataSaida = $('#inputdataHoraSaida').val();
			var veiculosDisp = $('#veiculosDisp').val();
			var inicioRapido = $('#inicioRapido').val() !== undefined ? $('#inicioRapido').val() : "NAO" ;

			var listarVeiculosCondutoresDisp = "${linkTo[MissaoController].listarVeiculosECondutoresDisponiveis}";
			return listarVeiculosCondutoresDisp + "?idMissao=" + idMissao + "&veiculosDisp=" + veiculosDisp + "&inicioRapido=" + inicioRapido +"&dataSaida=" + dataSaida;
		}

		var escreverMensagemErro = function(msg) {
			var $errosAjax = $('#errosAjax');
			$errosAjax.css('display', 'block');
			$errosAjax.find('li').html(msg);
		}

		var success = function(data) {

			if(data.veiculos === undefined) {
				escreverMensagemErro(data);
			}

			var $veiculos = $('#veiculosDisponiveis');
			var $condutores = $('#condutoresDisponiveis');

			$veiculos.html(selectFactory(data.veiculos, 'selveiculosdisponiveis', 'missao.veiculo.id', data.disabled));
			$condutores.html(selectFactory(data.condutores, 'selcondutoresdisponiveis', 'missao.condutor.id', data.disabled));

			$('#selveiculosdisponiveis').focus();

			if($('#odometroSaidaEmKm').val() == "") {
			   $('#odometroSaidaEmKm').val('0,00');
			}
		}

		var error = function(error){
			console.error("Ocorreu um erro no servidor ao tentar preencher os dados de veiculos e condutores.");
		}

		$(function() {
			if (! ${missao.id})
				verificarMenorDataRequisicao();

			var $mudouDataHoraSaida =  $('#inputdataHoraSaida').change( function() {
				var dataHoraSaida = $('#inputdataHoraSaida').val();
				if ("${exibirMenuAdministrar || exibirMenuAdministrarMissao || exibirMenuAdministrarMissaoComplexo}") {
					if (dataHoraSaida != '' && dataHoraSaida != '__/__/____ __:__') {
						$.ajax({
							url: getLink(),
							dataType: 'JSON',
							success: success,
							error: error
						});
					}
				}
			});

			if ("${missao.id == 0}")
				$mudouDataHoraSaida.trigger('change');

			if($("#lersomente").length != 0)
				protegeDocumento();

   			if ($("#missao_estadoMissao").val() == "INICIADA") {
				protegeDocumento();
		  	    $('#infSaida').find('input, textarea, button, select').removeAttr('disabled');
		 	    $('#infRetorno').find('input, textarea, button, select').removeAttr('disabled');
			    $('#btnAcoes').find('input, textarea, button, select').removeAttr('disabled');
			    $('#inputdataHoraSaida').attr('disabled','disabled');
			    $('#veiculosDisponiveis').find('input, textarea, button, select').attr('disabled','disabled');
			    $('#condutoresDisponiveis').find('input, textarea, button, select').attr('disabled','disabled');
   			}

	   		if ($("#missao_estadoMissao").val() == "FINALIZADA") {
			    protegeDocumento();
		    	$('#btnVoltar').remove();
		    	$('#btnSalvar').remove();
	   		}

	   		if ($("#missao_estadoMissao").val() == "CANCELADA") {
			    protegeDocumento();
		    	$('#btnVoltar').remove();
		    	$('#btnSalvar').remove();
	   		}
		});
	</script>
	<br />

	<h3>Sa&iacute;da</h3>
	<div id ="infSaida" class="gt-content-box gt-form clearfix">
	 	<div class="coluna margemDireitaG">
	       	<label for="inputdataHoraSaida" class="obrigatorio">Data/Hora</label>
	       	<input type="text" id="inputdataHoraSaida" name="missao.dataHoraSaida" value="<fmt:formatDate pattern='dd/MM/yyyy HH:mm' value="${missao.dataHoraSaida.time}" />" size="16" class="dataHora" />
	    	<c:if test="${mostrarDadosIniciada}">
		       	<div>
					<label for="odometroSaidaEmKm" class="obrigatorio">Od&ocirc;metro</label>
					<input id="odometroSaidaEmKm" type="text" name="missao.odometroSaidaEmKm" value="${missao.odometroSaidaEmKm}" size="12" class="decimal" />
		        	<label for="nivelCombustivelSaida" class="obrigatorio">N&iacute;vel Combust&iacute;vel</label>
		        	<select name="missao.nivelCombustivelSaida">
		        		<c:forEach items="${niveisCombustivelSaida}" var="nivel">
		        			<option value="${nivel}" ${nivel == missao.nivelCombustivelSaida ? 'selected' : ''}>${nivel.descricao}</option>
		        		</c:forEach>
		        	</select>
		        	<label for="licenca" class="obrigatorio">Licen&ccedil;a</label>
		        	<select name="missao.licenca">
		        		<c:forEach items="${licencas}" var="licenca">
		        			<option value="${licenca}" ${licenca == missao.licenca ? 'selected' : ''}>${licenca.descricao}</option>
		        		</c:forEach>
		        	</select>
		       	</div>
	    	</c:if>
		</div>
	 	<div class="coluna margemDireitaG">
        	<label for="veiculosDisponiveis" class="obrigatorio">Ve&iacute;culo</label>
			<div id="veiculosDisponiveis">
				<c:choose>
					<c:when test="${(!exibirMenuAdministrar && !exibirMenuAdministrarMissao && !exibirMenuAdministrarMissaoComplexo)}">
						<select id="selveiculosdisponiveis" name="missao.veiculo.id" disabled>
							<c:forEach items="${veiculos}" var="veiculo">
								<option value="${veiculo.id}" ${veiculo.id == missao.veiculo.id ? 'selected' : ''}>${veiculo.dadosParaExibicao}</option>
							</c:forEach>
						</select>
					</c:when>
					<c:otherwise>
						<siga:select id="selveiculosdisponiveis" name="missao.veiculo.id" list="veiculos" listKey="id" listValue="dadosParaExibicao" value="${missao.veiculo.id}"/>
					</c:otherwise>
				</c:choose>
			</div>
			<c:if test="${mostrarDadosIniciada}">
		       	<div>
		        	<label for="estepe" class="obrigatorio">Estepe</label>
		        	<select name="missao.estepe">
		        		<c:forEach items="${estepes}" var="estepe">
		        			<option value="${estepe}" ${estepe == missao.estepe ? 'selected' : ''}>${estepe.descricao}</option>
		        		</c:forEach>
		        	</select>

		        	<label for="triangulos" class="obrigatorio">Tri&acirc;ngulo</label>
		        	<select name="missao.triangulos">
		        		<c:forEach items="${triangulos}" var="triangulo">
		        			<option value="${triangulo}" ${triangulo == missao.triangulos ? 'selected' : ''}>${triangulo.descricao}</option>
		        		</c:forEach>
		        	</select>

		        	<label for="cartaoSeguro" class="obrigatorio">Cart&atilde;o Seguro</label>
		        	<select name="missao.cartaoSeguro">
		        		<c:forEach items="${cartoesSeguro}" var="cartaoSeguro">
		        			<option value="${cartaoSeguro}" ${cartaoSeguro == missao.cartaoSeguro ? 'selected' : ''}>${cartaoSeguro.descricao}</option>
		        		</c:forEach>
		        	</select>
		       	</div>
			</c:if>
		</div>
	 	<div class="coluna margemDireitaG">
        	<label for="condutoresDisponiveis" class="obrigatorio">Condutor</label>
			<div id="condutoresDisponiveis">
				<c:choose>
					<c:when test="${(!exibirMenuAdministrar && !exibirMenuAdministrarMissao && !exibirMenuAdministrarMissaoComplexo)}">
						<select id="selcondutoresdisponiveis" name="missao.condutor.id" disabled>
							<c:forEach items="${condutores}" var="condutor">
								<option value="${condutor.id}" ${condutor.id == missao.condutor.id ? 'selected' : ''}>${condutor.dadosParaExibicao}</option>
							</c:forEach>
						</select>
					</c:when>
					<c:otherwise>
						<siga:select id="selcondutoresdisponiveis" name="missao.condutor.id" list="condutores" listKey="id" listValue="dadosParaExibicao" value="${missao.condutor.id}"/>
					</c:otherwise>
				</c:choose>
			</div>
			<c:if test="${mostrarDadosIniciada}">
		       	<div>
		       		<div class="coluna margemDireitaG">
		        		<label for="avariasAparentesSaida" class="obrigatorio">Avarias Aparentes</label>
		        		<select name="missao.avariasAparentesSaida">
		        			<c:forEach items="${avariasAparentesSaida}" var="avariaAparente">
		        				<option value="${avariaAparente}" ${avariaAparente == missao.avariasAparentesSaida ? 'selected' : ''}>${avariaAparente.descricao}</option>
		        			</c:forEach>
		        		</select>

			        	<label for="extintor" class="obrigatorio">Extintor</label>
		        		<select name="missao.extintor">
		        			<c:forEach items="${extintores}" var="extintor">
		        				<option value="${extintor}" ${extintor == missao.extintor ? 'selected' : ''}>${extintor.descricao}</option>
		        			</c:forEach>
		        		</select>

			        	<label for="cartaoAbastecimento" class="obrigatorio">Cart&atilde;o Abastecimento</label>
		        		<select name="missao.cartaoAbastecimento">
		        			<c:forEach items="${cartoesAbastecimento}" var="cartaoAbastecimento">
		        				<option value="${cartaoAbastecimento}" ${cartaoAbastecimento == missao.cartaoAbastecimento ? 'selected' : ''}>${cartaoAbastecimento.descricao}</option>
		        			</c:forEach>
		        		</select>
		       		</div>
		       		<div class="coluna">
						<label for="limpeza" class="obrigatorio">Limpeza</label>
		        		<select name="missao.limpeza">
		        			<c:forEach items="${limpeza}" var="limpo">
		        				<option value="${limpo}" ${limpo == missao.limpeza ? 'selected' : ''}>${limpo.descricao}</option>
		        			</c:forEach>
		        		</select>

						<label for="ferramentas" class="obrigatorio">Ferramentas</label>
		        		<select name="missao.ferramentas">
		        			<c:forEach items="${ferramentas}" var="ferramenta">
		        				<option value="${ferramenta}" ${ferramenta == missao.ferramentas ? 'selected' : ''}>${ferramenta.descricao}</option>
		        			</c:forEach>
		        		</select>

			        	<label for="cartaoSaida" class="obrigatorio">Cart&atilde;o Sa&iacute;da</label>
		        		<select name="missao.cartaoSaida">
		        			<c:forEach items="${cartoesSaida}" var="cartao">
		        				<option value="${cartao}" ${cartao == missao.cartaoSaida ? 'selected' : ''}>${cartao.descricao}</option>
		        			</c:forEach>
		        		</select>
		       		</div>
		       	</div>
			</c:if>
		</div>
	</div>
	<c:if test="${mostrarDadosFinalizada}">
		<h3>Retorno</h3>
		<div id ="infRetorno" class="gt-content-box gt-form clearfix">
			<div class="clearfix">
				<div class="coluna margemDireitaG">
		        	<label for="inputdataHoraRetorno" class="obrigatorio">Data/Hora</label>
		       		<input type="text" id="inputdataHoraRetorno" name="missao.dataHoraRetorno" value="<fmt:formatDate pattern='dd/MM/yyyy HH:mm' value="${missao.dataHoraRetorno.time}" />" size="14" class="dataHora" />
			        <label for="nivelCombustivelRetorno" class="obrigatorio">Combustivel</label>
	        		<select name="missao.nivelCombustivelRetorno">
	        			<c:forEach items="${nivelCombustivelRetorno}" var="nivelCombustivel">
	        				<option value="${nivelCombustivel}" ${nivelCombustivel == missao.nivelCombustivelRetorno ? 'selected' : ''}>${nivelCombustivel.descricao}</option>
	        			</c:forEach>
	        		</select>
				</div>

				<div class="coluna">
			    	<label for="odometroRetornoEmKm" class="obrigatorio">Od&ocirc;metro</label>
		       		<input type="text" name="missao.odometroRetornoEmKm" value="${missao.odometroRetornoEmKm}" size="12" class="decimal" />
			        <label for="avariasAparentesRetorno" class="obrigatorio">Avarias Aparentes</label>
	        		<select name="missao.avariasAparentesRetorno">
	        			<c:forEach items="${avariasAparentesRetorno}" var="avariaAparente">
	        				<option value="${avariaAparente}" ${avariaAparente == missao.avariasAparentesRetorno ? 'selected' : ''}>${avariaAparente.descricao}</option>
	        			</c:forEach>
	        		</select>
				</div>
			</div>
			<label for="ocorrencias" class="obrigatorio">Ocorr&ecirc;ncias</label>
			<textarea name="missao.ocorrencias" rows="7" cols="80">${null != missao ? missao.ocorrencias : ''}</textarea>
			<label for="itinerarioCompleto" class="obrigatorio">Itiner&aacute;rio Completo</label>
			<textarea name="missao.itinerarioCompleto" rows="7" cols="80">${null != missao ? missao.itinerarioCompleto : ''}</textarea>
		</div>
	</c:if>
	<span class="alerta menor"><fmt:message key="views.erro.preenchimentoObrigatorio"/></span>

	<script>
		function verificarVeiculosRequisicao() {
			var idVeiculo = "";

			$("[name='requisicoesSelecionadasVeiculo']").each(function() {
				$input = $(this), valor = $input.attr('value');
				idVeiculo += valor + ", ";
			});

			$('#veiculosDisp').val(idVeiculo);
		}

		 function protegeDocumento() {
	    	   $('#infbasicas').find('input, textarea, button, select').attr('disabled','disabled');
	    	   $('#gridRequisicoes').find('input, textarea, button, select').attr('disabled','disabled');
	    	   $('#btngridRequisicoes').find('input, textarea, button, select').attr('disabled','disabled');
	    	   $('#infSaida').find('input, textarea, button, select').attr('disabled','disabled');
	    	   $('#infRetorno').find('input, textarea, button, select').attr('disabled','disabled');
	    	   $('#btnAcoes').find('input, textarea, button, select').attr('disabled','disabled');

	    	   $('.linkExcluir').attr('disabled', 'disabled');
		 }

		 function desprotegeDocumento() {
	    	   $('#infbasicas').find('input, textarea, button, select').removeAttr('disabled');
	    	   $('#gridRequisicoes').find('input, textarea, button, select').removeAttr('disabled');
	    	   $('#btngridRequisicoes').find('input, textarea, button, select').removeAttr('disabled');
	    	   $('#infSaida').find('input, textarea, button, select').removeAttr('disabled');
	    	   $('#infRetorno').find('input, textarea, button, select').removeAttr('disabled');
	    	   $('#btnAcoes').find('input, textarea, button, select').removeAttr('disabled');

	    	   $('.linkExcluir').attr('disabled', 'disabled');
		 }

		 function submitForm(acao) {
	        $("#formMissoes").attr('action',acao);

			var x = 0;
			var y = 0;
			var z = 0;

			$("[name='requisicoesSelecionadas']").each(function() {
				$(this).attr('name', 'requisicoesTransporte[' + x + '].id');
				x++;
			});

			$("[name='requisicoesSelecionadas1']").each(function() {
				$(this).attr('name', 'requisicoesTransporteAlt[' + y + '].id');
				y++;
			});

			$("[name='requisicoesAntigas']").each(function() {
				$(this).attr('name', 'requisicoesTransporteAnt[' + z + '].id');
				z++;
			});

			verificarVeiculosRequisicao();

			$("#formMissoes").submit();
		}

		function inserirRequisicoesSelecionadas() {
			var chkRequisicoes = document.getElementsByName("chk");
			var html = "";

			for (var i = 0; i < chkRequisicoes.length; i++)
				if (chkRequisicoes[i].checked) {
					chkRequisicoes[i].setAttribute("checked", "false");
					var trRequisicoes = chkRequisicoes[i].parentNode.parentNode;
					var tdChkRequisicoes = chkRequisicoes[i].parentNode;
					trRequisicoes.removeChild(tdChkRequisicoes);
					i--;
					var htmlSelecionado = trRequisicoes.outerHTML;
					htmlSelecionado = htmlSelecionado.replace(/NaoSelecionad/g,"Selecionad");
 					var htmlSelecionadoEExcluir = htmlSelecionado.replace("none","inline");
 					html = html + htmlSelecionadoEExcluir;
					var tbody = trRequisicoes.parentNode;
					tbody.removeChild(trRequisicoes);
				}

			$("#htmlgridRequisicoes tbody" ).append(html);
		}

		function verificarMenorDataRequisicao() {
			menorData = "";

			$("[name='saidaDataReqSelecionada']").each(function() {
				if(menorData!="" && !(menorData.match(/^\s+$/)))
					menorData = compararDatas($(this).html(),menorData);
				else
					menorData = $(this).html();

			});

			$("#inputdataHoraSaida").attr("value", menorData);
			$("#inputdataHoraSaida").select();
			$("#inputdataHoraSaida").trigger('change');
		}

		function compararDatas(obj1, obj2) {
			return (stringToDate(obj1) >= stringToDate(obj2) ? obj2 : obj1);
		}

		function stringToDate(s)  {
			  s = s.split(/[/: ]/);
			  return new Date(s[2], s[1]-1, s[0], s[3], s[4]);
		}

		function apagaLinha(link) {
	    	if ($(link).attr('disabled'))
				return false;

			var html = "";
			if(confirm('Tem certeza de que deseja excluir esta requisicao da missao?')) {
				var trExcluir = link.parentNode.parentNode;
				var trIncluir = "#" + trExcluir.id;
				$( trIncluir ).prepend('<td><input type="checkbox"  name="chk"/></td>');
				var htmlSelecionado = trExcluir.outerHTML.replace(/Selecionad/g,"NaoSelecionad");
					var htmlSelecionadoEExcluir = htmlSelecionado.replace("inline","none");
					html = html + htmlSelecionadoEExcluir;
				$( "#htmlgridselrequisicoes tbody" ).append(html);
				var tabela = trExcluir.parentNode;
				tabela.removeChild(trExcluir);
			}
		}

	</script>

	<c:if test="${mostrarBotoesEditar}">
		<div id="btnAcoes" class="gt-table-buttons">
			<input type="button" id="btnSalvar" value="<fmt:message key='views.botoes.salvar'/>" onClick="submitForm('${linkTo[MissaoController].salvar}')" class="gt-btn-medium gt-btn-left" />
			<c:choose>
				<c:when test="${missao.id > 0}">
					<input type="button" id="btnVoltar"  value="<fmt:message key='views.botoes.voltar'/>" onClick="javascript:location.href='${linkTo[MissaoController].buscarPelaSequence[false][missao.sequence]}'" class="gt-btn-medium gt-btn-left" />
				</c:when>
				<c:otherwise>
					<input type="button" id="btnVoltar"  value="<fmt:message key='views.botoes.voltar'/>" onClick="javascript:location.href='${linkTo[MissaoController].listar}'" class="gt-btn-medium gt-btn-left" />
				</c:otherwise>
			</c:choose>
		</div>
	</c:if>

	<c:if test="${mostrarBotoesIniciarRapido}">
		<div id="btnAcoes" class="gt-table-buttons">
			<input type="button" id="btnIniciar" value="<fmt:message key='views.botoes.iniciar'/>" onClick="submitForm('${linkTo[MissaoController].iniciarMissaoRapido}')" class="gt-btn-medium gt-btn-left" />
			<input type="button" id="btnVoltar"  value="<fmt:message key='views.botoes.voltar'/>" onClick="javascript:location.href='${linkTo[MissaoController].buscarPelaSequence[false][missao.sequence]}'" class="gt-btn-medium gt-btn-left" />
		</div>
	</c:if>

	<c:if test="${mostrarBotoesIniciar}">
		<div id="btnAcoes" class="gt-table-buttons">
			<input type="button" id="btnIniciar" value="<fmt:message key='views.botoes.iniciar'/>" onClick="submitForm('${linkTo[MissaoController].iniciarMissao}')" class="gt-btn-medium gt-btn-left" />
			<input type="button" id="btnVoltar"  value="<fmt:message key='views.botoes.voltar'/>" onClick="javascript:location.href='${linkTo[MissaoController].buscarPelaSequence[false][missao.sequence]}'" class="gt-btn-medium gt-btn-left" />
		</div>
	</c:if>

	<c:if test="${mostrarBotoesFinalizar}">
		<div id="btnAcoes" class="gt-table-buttons">
			<input type="button" id="btnFinalizar" value="<fmt:message key='views.botoes.finalizar'/>" onClick="submitForm('${linkTo[MissaoController].finalizarMissao}')" class="gt-btn-medium gt-btn-left" />
			<input type="button" id="btnVoltar"  value="<fmt:message key='views.botoes.voltar'/>" onClick="javascript:location.href='${linkTo[MissaoController].buscarPelaSequence[false][missao.sequence]}'" class="gt-btn-medium gt-btn-left" />
		</div>
	</c:if>

</form>

<div id="dialog-form" title="Sele&ccedil;&atilde;o de requisi&ccedil;&otilde;es" class="gt-form">
	<p>Selecione uma ou mais requisi&ccedil;&otilde;es.</p>
	<form>
		<c:if test="${null != requisicoesTransporte && !requisicoesTransporte.isEmpty()}">
			<div class="gt-content-box gt-for-table">
		 		<table id="htmlgridselrequisicoes" class="gt-table" >
		    		<thead>
				    	<tr style="font-weight: bold;">
				    		<th width="5%"></th>
				    	    <th width="5%">Sa&iacute;da</th>
					   		<th width="5%">Retorno</th>
					   		<th >Outros Dados</th>
				    		<th width="8%"></th>
						</tr>
					</thead>
					<tbody id="tbodysel">
						<c:forEach items="${requisicoesTransporte}" var="requisicaoTransporte">
			   				<tr id="row_${requisicaoTransporte.id}">
			   					<input type="hidden" name="requisicoesNaoSelecionadas" readonly="readonly" value="${requisicaoTransporte.id}"/>
			   					<input type="hidden" name="requisicoesNaoSelecionadas1" readonly="readonly" value="${requisicaoTransporte.id}"/>
			   					<td id="tdChkReq_${requisicaoTransporte.id}">
			   						<input type="checkbox"  name="chk"/>
			   					</td>
		    					<td name="saidaDataReqNaoSelecionada">
		    						<fmt:formatDate pattern='dd/MM/yyyy HH:mm' value="${requisicaoTransporte.dataHoraSaidaPrevista.time}" />
								</td>
		    					<td>
		    						<c:choose>
		    							<c:when test="${requisicaoTransporte.dataHoraRetornoPrevisto != null}">
		    								<fmt:formatDate pattern='dd/MM/yyyy HH:mm' value="${requisicaoTransporte.dataHoraRetornoPrevisto.time}" />
		    							</c:when>
		    							<c:otherwise>
		    								<fmt:message key="no" />
		    							</c:otherwise>
		    						</c:choose>
		    					</td>
			   	    			<td>
			   	    				<tptags:link texto="${requisicaoTransporte.descricaoCompleta}"
			   	    							 parteTextoLink="${requisicaoTransporte.buscarSequence()}"
			   	    							 comando="${linkTo[RequisicaoController].buscarPelaSequence[true][requisicaoTransporte.buscarSequence()]}">
			   	    				</tptags:link>
								</td>
								<c:if test="${requisicaoTransporte.servicoVeiculo != null}">
									<input type="hidden" name="requisicoesNaoSelecionadasVeiculo"  readonly="readonly" value="${null != requisicaoTransporte.servicoVeiculo.veiculo ? requisicaoTransporte.servicoVeiculo.veiculo.id : ''}"/>
								</c:if>
			   	    			<td  width="8%" >
			   	    				<c:if test="${(mostrarDadosIniciada || mostrarDadosFinalizada) && !mostrarBotoesIniciar}">
			   	    					<div style="display: none;">
			   	    						<select name="requisicaoTransporte.ultimoEstado">
			   	    							<c:forEach items="${estadoRequisicao.valuesComboAtendimentoMissao()}" var="estado">
			   	    								<option value="${estado}" ${estado == requisicaoTransporte.ultimoEstado ? 'selected' : ''}>${estado.descricao}</option>
			   	    							</c:forEach>
			   	    						</select>
										</div>
			   	    				</c:if>
			   	    				<c:if test="${!mostrarDadosCancelada}">
			   	    					<a class="linkExcluir" name="linkExcluirNaoSelecionados" style="display:none" onclick="javascript:apagaLinha(this);" href="#"><fmt:message key="views.label.excluir" /></a>
			   	    				</c:if>
			   	    			</td>
							</tr>
						</c:forEach>
			 		</tbody>
		     	</table>
			</div>
		</c:if>
	</form>
</div>
