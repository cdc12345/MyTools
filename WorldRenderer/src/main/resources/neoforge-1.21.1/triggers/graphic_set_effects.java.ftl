<#include "procedures.java.ftl">
@EventBusSubscriber(value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class ${name}Procedure {
  private static int ticks = 0;
  private static float partialTick = 0.0F;
  private static ParticleOptions genericParticle = ParticleTypes.RAIN;
  private static ParticleOptions specialParticle = ParticleTypes.SMOKE;
  private static net.minecraft.sounds.SoundEvent genericSound = SoundEvents.WEATHER_RAIN;
  private static net.minecraft.sounds.SoundEvent specialSound = SoundEvents.WEATHER_RAIN_ABOVE;
  private static final Predicate<Object[]> PREDICATE = params -> {
    Minecraft minecraft = Minecraft.getInstance();
    Entity entity = minecraft.gameRenderer.getMainCamera().getEntity();
    partialTick = minecraft.getTimer().getGameTimeDeltaPartialTick(false);
    ticks = (Integer) params[1];
    if (entity != null) {
      ClientLevel level = minecraft.level;
      Vec3 pos = entity.getPosition(partialTick);
      <#assign dependenciesCode><#compress>
        <@procedureDependenciesCode dependencies, {
          "dimension": "level.dimension()",
          "entity": "entity",
          "partialTick": "partialTick",
          "ticks": "ticks",
          "x": "pos.getX()",
          "y": "pos.getY()",
          "z": "pos.getZ()",
          "world": "level",
          "event": "null"
        }/>
      </#compress></#assign>
			<#if generator.procedureNamesToObjects(name).get(0).getReturnValueType(generator.getWorkspace()) == "logic">
      	return execute(null<#if dependenciesCode?has_content>, </#if>${dependenciesCode});
			<#else>
				execute(null<#if dependenciesCode?has_content>, </#if>${dependenciesCode});
        return true;
			</#if>
    }
    return false;
  };

  public static void setParticles(ParticleOptions genericParticle, ParticleOptions specialParticle) {
    ${name}Procedure.genericParticle = genericParticle;
    ${name}Procedure.specialParticle = specialParticle;
  }

  public static void setSounds(net.minecraft.sounds.SoundEvent genericSound, net.minecraft.sounds.SoundEvent specialSound) {
    ${name}Procedure.genericSound = genericSound;
    ${name}Procedure.specialSound = specialSound;
  }

  public static void addEffects(int target, float power, int range, boolean particles, boolean sounds, boolean constant) {
    if (!particles && !sounds)
      return;
    Minecraft minecraft = Minecraft.getInstance();
    ClientLevel level = minecraft.level;
    float factor = (constant ? 1.0F : level.getRainLevel(1.0F)) / (Minecraft.useFancyGraphics() ? 1.0F : 2.0F);
    Camera camera = minecraft.gameRenderer.getMainCamera();
    RandomSource randomSource = RandomSource.create((long) ticks * 312987231L);
    BlockPos iPos = BlockPos.containing(camera.getPosition());
    BlockPos surface;
    BlockPos ground;
    int ix;
    int iz;
    boolean effects = false;
    Biome.Precipitation precipitation;
    ParticleStatus particleStatus = minecraft.options.particles().get();
    boolean visible = particleStatus != ParticleStatus.MINIMAL;
    int amount = (int) (100.0F * factor * factor) / (particleStatus == ParticleStatus.DECREASED ? 2 : 1);
    amount = (int) (amount * power);
    for(int i = 0; i < amount; ++i) {
      ix = randomSource.nextInt((range << 1) + 1) - range;
      iz = randomSource.nextInt((range << 1) + 1) - range;
      surface = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, iPos.offset(ix, 0, iz));
      if (surface.getY() > level.getMinBuildHeight() && surface.getY() <= iPos.getY() + range && surface.getY() >= iPos.getY() - range) {
        precipitation = level.getBiome(surface).value().getPrecipitationAt(surface);
        switch (target) {
          case 1:
            effects = precipitation == Biome.Precipitation.RAIN;
            break;
          case 2:
            effects = precipitation == Biome.Precipitation.SNOW;
            break;
          case 4:
            effects = precipitation == Biome.Precipitation.RAIN || precipitation == Biome.Precipitation.SNOW;
            break;
          case 8:
            effects = precipitation == Biome.Precipitation.NONE;
            break;
          case 16:
            effects = true;
            break;
        }
        if (effects) {
          ground = surface.below();
          if (particles && visible) {
            double dx = randomSource.nextDouble();
            double dz = randomSource.nextDouble();
            BlockState blockState = level.getBlockState(ground);
            FluidState fluidState = level.getFluidState(ground);
            VoxelShape voxelShape = blockState.getCollisionShape(level, ground);
            double maxHeight = Math.max(voxelShape.max(Direction.Axis.Y, dx, dz), fluidState.getHeight(level, ground));
            ParticleOptions particleOptions = !fluidState.is(FluidTags.LAVA) && !blockState.is(Blocks.MAGMA_BLOCK) && !CampfireBlock.isLitCampfire(blockState) ? genericParticle : specialParticle;
            level.addParticle(particleOptions, ground.getX() + dx, ground.getY() + maxHeight, ground.getZ() + dz, 0.0D, 0.0D, 0.0D);
          }
          if (sounds && i == 0 && ground != null && (ticks & 3) > randomSource.nextInt(3)) {
            if (ground.getY() > iPos.getY() + 1 && level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, iPos).getY() > Mth.floor(iPos.getY())) {
              level.playLocalSound(ground, specialSound, SoundSource.WEATHER, 0.1F, 0.5F, false);
            } else {
              level.playLocalSound(ground, genericSound, SoundSource.WEATHER, 0.2F, 1.0F, false);
            }
          }
        }
      }
    }
  }
  
  @SubscribeEvent
  public static void effectsSetup(FMLClientSetupEvent event) {
    try {
      Field field = DimensionSpecialEffectsManager.class.getDeclaredField("EFFECTS");
      field.setAccessible(true);
      for (DimensionSpecialEffects dimensionSpecialEffects : ((com.google.common.collect.ImmutableMap<ResourceLocation, DimensionSpecialEffects>) field.get(null)).values()) {
        Class<?> effects = dimensionSpecialEffects.getClass();
			  ((Set<Predicate<Object[]>>) effects.getField("CUSTOM_EFFECTS").get(null)).add(PREDICATE);
      }
    } catch (Exception e) {}
  }