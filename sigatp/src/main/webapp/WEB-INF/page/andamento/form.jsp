<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://localhost/jeetags" prefix="siga"%>
<%@ taglib prefix="sigatp" tagdir="/WEB-INF/tags/" %>

<form id="formAndamentos" action="${linkTo[AndamentoController].salvar}" enctype="multipart/form-data" >
		<input type="hidden" name="andamento.requisicaoTransporte.id" value="${andamento.requisicaoTransporte.id}" />
		<input type="hidden" name="andamento.estadoRequisicao" value="${andamento.estadoRequisicao}" />
	<h3> A requisi&ccedil;&atilde;o ${andamento.requisicaoTransporte.sequence} ser&aacute; ${andamento.estadoRequisicao}</h3>
	<sigatp:erros />
	<div class="gt-content-box gt-form clearfix">
	    <c:choose>
	        <c:when test="${andamento.estadoRequisicao == andamento.estadoRequisicao.AUTORIZADA}">	
		       	<label for="andamento.descricao">Motivo</label>
			</c:when>
			<c:otherwise>
		       	<label for="andamento.descricao" class= "obrigatorio">Motivo</label>
			</c:otherwise>
		</c:choose>
        <textarea name="andamento.descricao" rows="5" cols="80">${andamento.descricao}</textarea>
	</div>
	<br/>
    <c:if test="${andamento.estadoRequisicao != andamento.estadoRequisicao.AUTORIZADA}">
		<span class="alerta menor">* Preenchimento obrigat&oacute;rio</span>
	</c:if>
	<div class="gt-table-buttons">
		<input type="submit" value="${botaoAcao}" class="gt-btn-medium gt-btn-left" />
		<c:choose>
			<c:when test="${andamento.estadoRequisicao == andamento.estadoRequisicao.CANCELADA}">
				<input type="button" value="<fmt:message key="views.botoes.voltar" />" onClick="javascript:location.href='${linkTo[Requisicao].buscarPelaSequence[andamento.requisicaoTransporte.sequence][popUp]}'" class="gt-btn-medium gt-btn-left" />
			</c:when> 
			<c:otherwise> 
				<input type="button" value="<fmt:message key="views.botoes.voltar" />" onClick="javascript:location.href='${linkTo[Requisicao].listarPAprovar}'" class="gt-btn-medium gt-btn-left" />		
			</c:otherwise>
		</c:choose>
	</div>
</form>	
