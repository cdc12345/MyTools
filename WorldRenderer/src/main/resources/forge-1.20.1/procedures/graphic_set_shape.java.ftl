<#if field$mode == "TEXTURE">
  mode(VertexFormat.Mode.QUADS, true);
<#else>
  mode(VertexFormat.Mode.${field$mode}, false);
</#if>