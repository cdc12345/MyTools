<#include "mcitems.ftl">
<@head>if (event instanceof BuildCreativeModeTabContentsEvent _event){</@head>
	_event.insertAfter(${mappedMCItemToItemStackCode(input$after, 1)},${mappedMCItemToItemStackCode(input$item, 1)}, CreativeModeTab.TabVisibility.${field$tabvisible});
<@tail>}</@tail>