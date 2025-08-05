private static ItemStack getPlayerSkull(String name,String uuid){
	ItemStack itemStack = new ItemStack(Items.PLAYER_HEAD);
	itemStack.set(DataComponents.PROFILE,new ResolvableProfile(new GameProfile(UUID.fromString(uuid),name)));
	return itemStack;
}