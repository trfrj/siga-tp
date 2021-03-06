<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8"
	buffer="64kb"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="siga" uri="http://localhost/jeetags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="sigatp" tagdir="/WEB-INF/tags/"%>

<jsp:include page="../tags/calendario.jsp" />
<jsp:include page="../tags/decimal.jsp" />

<siga:pagina titulo="Transportes">
	<div class="gt-bd clearfix">
		<div class="gt-content clearfix">
			<h2>${veiculo.dadosParaExibicao}</h2>
			<h3>Abastecimentos</h3>

			<jsp:include page="../veiculo/form.jsp" />

			<c:choose>
				<c:when test="${abastecimentos.size() > 0}">
					<table id="htmlgrid" class="gt-table">
						<thead>
							<tr style="font-weight: bold;">
								<th>Data e Hora</th>
								<th>N&iacute;vel de Comb. (l)</th>
								<th>Qtd.(l)</th>
								<th>Od&ocirc;metro (Km)</th>
								<th>Dist. Percorrida (Km)</th>
								<th>Consumo M&eacute;dio (Km/l)</th>
							</tr>
						</thead>
						<tbody>
							<c:forEach items="${abastecimentos}" var="abastecimento">
								<tr>
									<td><fmt:formatDate pattern="dd/MM/yyyy" value="${abastecimento.dataHora.time}" /></td>
									<td>${abastecimento.nivelDeCombustivel.descricao}</td>
									<td>${abastecimento.formataMoedaBrasileiraSemSimbolo(abastecimento.quantidadeEmLitros)}</td>
									<td>${abastecimento.formataMoedaBrasileiraSemSimbolo(abastecimento.odometroEmKm)}</td>
									<td>${abastecimento.formataMoedaBrasileiraSemSimbolo(abastecimento.distanciaPercorridaEmKm)}</td>
									<td>${abastecimento.formataMoedaBrasileiraSemSimbolo(abastecimento.consumoMedioEmKmPorLitro)}</td>
								</tr>
							</c:forEach>
						</tbody>
					</table>
				</c:when>
				<c:otherwise>
					<br />
					<h3>N&atilde;o existem abastecimentos cadastrados para este
						ve&iacute;culo.</h3>
				</c:otherwise>
			</c:choose>
		</div>
	</div>
</siga:pagina>