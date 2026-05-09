package com.ntf.mod;

import net.fabricmc.api.ModInitializer;

public class NtfMod implements ModInitializer {
    public static final String MOD_ID = "ntf.mod";

    @Override
    public void onInitialize() {
        System.out.println("Hello from " + MOD_ID);
        
        // Регистрируем команду
        TeleportCommand.register();
    }
}