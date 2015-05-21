<%@ page language="java" contentType="text/html; charset=UTF-8" buffer="64kb"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://localhost/jeetags" prefix="siga"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib prefix="sigatp" tagdir="/WEB-INF/tags/" %>
<sigatp:erros />

<form action="${linkTo[ConfiguracaoGIController].salvar}" method="post" cssClass="form" enctype="multipart/form-data">
	<input type="hidden" name="cpConfiguracao.idConfiguracao" value="${cpConfiguracao?.idConfiguracao}" />
	<input type="hidden" name="cpConfiguracao.id" value="${cpConfiguracao?.id}" />	
	<div class="gt-content-box gt-form clearfix">
		<div class="coluna margemDireitaG">
		    <input type="hidden" name="cpConfiguracao.orgaoUsuario.idOrgaoUsu" value="${cpConfiguracao.orgaoUsuario.idOrgaoUsu}"/>
		    <label for= "cpConfiguracao.lotacao" > Lota&ccedil;&atilde;o</label>
		    
<%-- 				#{selecao 	tipo:'lotacao',  --%>
<%-- 							nome:'cpConfiguracao.lotacao',  --%>
<%-- 							value:cpConfiguracao?.lotacao/}  --%>
			
			<label for="cpConfiguracao.dpPessoa.id">Servidor: </label>
<%-- 			#{selecao tipo:'pessoa',nome:'cpConfiguracao.dpPessoa', value:cpConfiguracao?.dpPessoa/}							 --%>

		    <label for= "cpConfiguracao.cpSituacaoConfiguracao.idSitConfiguracao" class= "obrigatorio">  Situa&ccedil;&atilde;o Configura&ccedil;&atilde;o</label>	
			<select name="cpConfiguracao.cpSituacaoConfiguracao.idSitConfiguracao" size="1" >
				<c:forEach items="${cpSituacoesConfiguracao}" var="cpSituacaoConfiguracao">
					<c:choose>
						<c:when test="${cpConfiguracao.cpSituacaoConfiguracao != null && cpSituacaoConfiguracao.idSitConfiguracao == cpConfiguracao.cpSituacaoConfiguracao.idSitConfiguracao}">
  								<option value="${cpSituacaoConfiguracao.idSitConfiguracao}" selected=selected>${cpSituacaoConfiguracao.dscSitConfiguracao}</option>
						</c:when>
						<c:otherwise>
		     		 		<option value="${cpSituacaoConfiguracao.idSitConfiguracao}" >${cpSituacaoConfiguracao.dscSitConfiguracao}</option>
						</c:otherwise>
					</c:choose>
				</c:forEach>
   			</select>
			
			
			<label for= "cpConfiguracao.cpTipoConfiguracao.idTpConfiguracao" class= "obrigatorio"> Tipo de Configura&ccedil;&atilde;o</label>	
			<select name="cpConfiguracao.cpTipoConfiguracao.idTpConfiguracao" size="1" >
				<c:forEach items="${cpTiposConfiguracao}" var="cpTipoConfiguracao">
					<c:choose>
						<c:when test="${cpConfiguracao.cpTipoConfiguracao != null && cpTipoConfiguracao.idTpConfiguracao == cpConfiguracao.cpTipoConfiguracao.idTpConfiguracao}">
 							<option value="${cpTipoConfiguracao.idTpConfiguracao}" selected=selected>${cpTipoConfiguracao.dscTpConfiguracao}</option>
						</c:when>
						<c:otherwise>
 							<option value="${cpTipoConfiguracao.idTpConfiguracao}" >${cpTipoConfiguracao.dscTpConfiguracao}</option>
						</c:otherwise>
					</c:choose>
				</c:forEach>
			</select>
			
			<label for= "cpConfiguracao.complexo.idComplexo" class= "obrigatorio">Complexo</label>	
			<select name="cpConfiguracao.complexo.idComplexo" size="1" >
				<c:forEach items="${cpComplexos}" var="cpComplexo">
					<c:choose>
						<c:when test="${cpConfiguracao.complexo != null && cpComplexo.idComplexo == cpConfiguracao.complexo.idComplexo}">
								<option value="${cpComplexo.idComplexo}" selected=selected>${cpComplexo.nomeComplexo}</option>
						</c:when>
						<c:otherwise>
							<option value="${cpComplexo.idComplexo}" >${cpComplexo.nomeComplexo}</option>
						</c:otherwise>
					</c:choose>
				</c:forEach>
			</select>
		</div>
	</div>
	
	<span class="alerta menor"><fmt:message key="views.erro.preenchimentoObrigatorio"/></span>
	<div class="gt-table-buttons">
		<input type="submit" value="<fmt:message key="views.botoes.salvar"/>" class="gt-btn-medium gt-btn-left" />
		<input type="button" value="<fmt:message key="views.botoes.cancelar"/>" onClick="javascript:location.href='${linkTo[ConfiguracaoGIController].pesquisar[cpConfiguracao?.orgaoUsuario?.idOrgaoUsu]}'" class="gt-btn-medium gt-btn-left" />
	</div>
</form>