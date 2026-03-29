package dev.nuclr.platform.plugin;

public @interface ResourceContentPlugin {

	boolean singleton() default true;

	boolean isViewPlugin() default false;
	
	boolean isOfficial() default false;
	
}
