<link rel="stylesheet" href="@{'/public/stylesheets/andamentos.css'}" type="text/css" media="screen"/>

	   	<p class="gt-table-action-list">
	   		<c:if test="${menuPenalidadesMostrarVoltar}"> 
	   			<img src="/sigatp/public/images/filter-icon.png"/><a class="filtro_Voltar"
				href="${linkTo[PenalidadeController].listar}"></a><a href="${linkTo[PenalidadeController].listar}">Voltar</a>&nbsp;&nbsp;&nbsp;
			</c:if>
	   		<c:if test="${menuPenalidadesMostrarTodas}"> 
	   			<img src="/sigatp/public/images/filter-icon.png"/><a class="filtro_T"
				href="${linkTo[PenalidadeController].listar}"></a><a href="${linkTo[PenalidadeController].listar}"><U>T</U>odas</a>&nbsp;&nbsp;&nbsp;
			</c:if>
		</p>