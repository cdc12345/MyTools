<#if field$mode == "TEXTURE">
if (begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR, ${input$update})) {
<#else>
if (begin(VertexFormat.Mode.${field$mode}, DefaultVertexFormat.POSITION_COLOR, ${input$update})) {
</#if>
  ${statement$do}
  end();
}