<#include "mcitems.ftl">
if (event instanceof BuildCreativeModeTabContentsEvent _event){
	_event.insertBefore(${mappedMCItemToItemStackCode(input$before, 1)},${mappedMCItemToItemStackCode(input$item, 1)}, CreativeModeTab.TabVisibility.${field$tabvisible});
}