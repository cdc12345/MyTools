package ${package}.mixins;

import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.Mixin;

<#assign mixinclass=data.mixinClass>
@Mixin(${mixinclass}.class)
<#if "TitleScreen" == mixinclass>
public abstract class ${name}Mixin extends Screen{
	@Unique
	//${data.t("comment.disableSingle")}
	private final boolean $disableSingle = false;
	@Unique
	//${data.t("comment.disableMultiple")}
	private final boolean $disableMultiple = false;
	@Unique
	//${data.t("comment.disableRealm")}
	private final boolean $disableRealm = false;

	@Shadow
	@Nullable
	protected abstract Component getMultiplayerDisabledReason();

	@Shadow
	protected abstract void realmsButtonClicked();

	protected ${name}Mixin(Component p_96550_) {
		super(p_96550_);
	}

	/**
	 * @author liquid
	 * @reason titleScreen
	 */
	@Overwrite
	private void createNormalMenuOptions(int p_96764_, int p_96765_) {
		this.addRenderableWidget(Button.builder(Component.translatable("menu.singleplayer"), (p_280832_) -> {
			this.minecraft.setScreen(new SelectWorldScreen(this));
		}).bounds(this.width / 2 - 100, p_96764_, 200, 20).build()).active = !$disableSingle;
		Component component = this.getMultiplayerDisabledReason();
		boolean flag = component == null;
		Tooltip tooltip = component != null ? Tooltip.create(component) : null;
		(this.addRenderableWidget(Button.builder(Component.translatable("menu.multiplayer"), (p_280833_) -> {
			Screen screen = (Screen) (this.minecraft.options.skipMultiplayerWarning ? new JoinMultiplayerScreen(this) : new SafetyScreen(this));
			this.minecraft.setScreen(screen);
		}).bounds(this.width / 2 - 100, p_96764_ + p_96765_, 200, 20).tooltip(tooltip).build())).active = !$disableMultiple;
		(this.addRenderableWidget(Button.builder(Component.translatable("menu.online"), (p_210872_) -> {
			this.realmsButtonClicked();
		}).bounds(this.width / 2 + 2, p_96764_ + p_96765_ * 2, 98, 20).tooltip(tooltip).build())).active = !$disableRealm;
	}
}
<#elseif "Minecraft" == mixinclass >
public abstract class ${name}Mixin {
    @Unique
	//${data.t("comment.disableTitleUpdate")}
    private final boolean $disableTitleUpdate = false;


	@Inject(method= "updateTitle()V",at=@At(value = "HEAD"),cancellable = true)
	public void cancel(CallbackInfo ci) {
		if ($disableTitleUpdate)
			ci.cancel();
	}
}
<#elseif "SelectWorldScreen" == mixinclass>
public abstract class ${name}Mixin extends Screen {

    @Unique
	//禁用创建世界
    private final boolean $disableCreateWorld = false;
    @Unique
	//禁用编辑世界
    private final boolean $disableEditWorld = false;
    @Unique
	//禁用删除世界
    private final boolean $disableDeleteWorld = false;
    @Unique
	//禁用重建世界
    private final boolean $disableRebuildWorld = false;

    @Shadow private Button deleteButton;
    @Shadow private Button renameButton;
    @Shadow private Button copyButton;


    protected ${name}Mixin(Component p_96550_) {
        super(p_96550_);
    }

    @Redirect(method = "init()V",at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/worldselection/SelectWorldScreen;addRenderableWidget(Lnet/minecraft/client/gui/components/events/GuiEventListener;)Lnet/minecraft/client/gui/components/events/GuiEventListener;",ordinal = 1))
    protected <T extends GuiEventListener & Renderable & NarratableEntry> T addCreateWorld(SelectWorldScreen instance, T guiEventListener) {
        if (guiEventListener instanceof Button button && $disableCreateWorld){
            button.active = false;
        }
        return this.addRenderableWidget(guiEventListener);
    }

    @Redirect(method = "init()V",at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/worldselection/SelectWorldScreen;addRenderableWidget(Lnet/minecraft/client/gui/components/events/GuiEventListener;)Lnet/minecraft/client/gui/components/events/GuiEventListener;",ordinal = 2))
    protected <T extends GuiEventListener & Renderable & NarratableEntry> T addEditWorld(SelectWorldScreen instance, T guiEventListener) {
        var result = this.addRenderableWidget(guiEventListener);
        if (guiEventListener instanceof Button button && $disableEditWorld){
            button.active = false;
        }
        return result;
    }
    @Redirect(method = "init()V",at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/worldselection/SelectWorldScreen;addRenderableWidget(Lnet/minecraft/client/gui/components/events/GuiEventListener;)Lnet/minecraft/client/gui/components/events/GuiEventListener;",ordinal = 3))
    protected <T extends GuiEventListener & Renderable & NarratableEntry> T addDeleteWorld(SelectWorldScreen instance, T guiEventListener) {
        if (guiEventListener instanceof Button button && $disableDeleteWorld){
            button.active = false;
        }
        return this.addRenderableWidget(guiEventListener);
    }
    @Redirect(method = "init()V",at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/worldselection/SelectWorldScreen;addRenderableWidget(Lnet/minecraft/client/gui/components/events/GuiEventListener;)Lnet/minecraft/client/gui/components/events/GuiEventListener;",ordinal = 4))
    protected <T extends GuiEventListener & Renderable & NarratableEntry> T addRebuildWorld(SelectWorldScreen instance, T guiEventListener) {
        if (guiEventListener instanceof Button button && $disableRebuildWorld){
            button.active = false;
        }
        return this.addRenderableWidget(guiEventListener);
    }

    @Inject(method = "updateButtonStatus(ZZ)V",at = @At(value = "RETURN"))
    public void updateButtonStatus(boolean p_276122_, boolean p_276113_, CallbackInfo ci) {
        if ($disableDeleteWorld)
            this.deleteButton.active = false;
        if ($disableEditWorld)
            this.renameButton.active = false;
        if ($disableRebuildWorld)
            this.copyButton.active = false;
    }
}
<#elseif "net.minecraft.world.entity.ai.goal.GoalSelector" == mixinclass>
public abstract class ${name}Mixin {
    @Final
    @Mutable
    @Shadow
    private Set<WrappedGoal> availableGoals;

    @Inject(method = "<init>(Ljava/util/function/Supplier;)V",at = @At(value = "TAIL"))
    public void synEdit(Supplier pProfiler, CallbackInfo ci){
        this.availableGoals = new CopyOnWriteArraySet<>();
    }
}
<#elseif "AbstractFurnaceBlockEntity" == mixinclass>
public abstract class ${name}Mixin {
    @Shadow
    int litTime;

	@Inject(method = "canBurn(Lnet/minecraft/core/RegistryAccess;Lnet/minecraft/world/item/crafting/Recipe;Lnet/minecraft/core/NonNullList;I)Z", at = @At(value = "HEAD"), cancellable = true)
	public void checkRecipe(RegistryAccess pRegistryAccess, Recipe<?> pRecipe, NonNullList<ItemStack> pInventory, int pMaxStackSize, CallbackInfoReturnable<Boolean> cir) {
		var item = pInventory.get(0);
		var fuel = pInventory.get(1);
		FurnaceCanBurnEvent event = new FurnaceCanBurnEvent(item, fuel,litTime);
		MinecraftForge.EVENT_BUS.post(event);
		if (event.isCanceled()) {
			cir.setReturnValue(false);
		}
	}
}
<#else>
public abstract class ${name}Mixin {
	//Try to do something interesting
	${data.mixinBody}
}
</#if>