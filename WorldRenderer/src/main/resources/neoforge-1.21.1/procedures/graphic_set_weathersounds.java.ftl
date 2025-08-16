<#assign generic = generator.map(field$generic, "sounds")?replace("CUSTOM:", "${modid}:")>
<#assign special = generator.map(field$special, "sounds")?replace("CUSTOM:", "${modid}:")>
<#if generic?has_content && special?has_content>
  setSounds(BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("${generic}")), BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("${special}")));
</#if>