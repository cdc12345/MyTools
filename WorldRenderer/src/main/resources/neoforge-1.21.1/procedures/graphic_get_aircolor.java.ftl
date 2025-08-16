<#include "mcelements.ftl">
/*@int*/(255 << 24 | world.getBiome(${toBlockPos(input$x,input$y,input$z)}).value().getSkyColor())