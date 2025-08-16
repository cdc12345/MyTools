if (${input$animal} instanceof Animal _animal && ${input$player} instanceof Player _player){
	_animal.setInLove(_player);
	<#if input$in_love="false">
	_animal.resetLove();
	</#if>
}