<#assign generic = generator.map(field$generic, "sounds")?replace("CUSTOM:", "${modid}:")>
<#assign special = generator.map(field$special, "sounds")?replace("CUSTOM:", "${modid}:")>
<#if generic?has_content && special?has_content>
  setSounds(ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("${generic}")), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("${special}")));
</#if>