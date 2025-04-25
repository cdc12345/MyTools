<#include "mcitems.ftl">
if (event instanceof BuildCreativeModeTabContentsEvent _event){
	_event.insertAfter(${mappedMCItemToItemStackCode(input$after, 1)},${mappedMCItemToItemStackCode(input$item, 1)}, CreativeModeTab.TabVisibility.${field$tabvisible});
}