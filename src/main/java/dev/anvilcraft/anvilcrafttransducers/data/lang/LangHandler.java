package dev.anvilcraft.anvilcrafttransducers.data.lang;

import com.tterrag.registrate.providers.RegistrateLangProvider;
import dev.anvilcraft.anvilcrafttransducers.AddonConfig;
import dev.anvilcraft.lib.config.ConfigData;

public class LangHandler {

    /**
     * 语言文件初始化
     *
     * @param provider 提供器
     */
    public static void init(RegistrateLangProvider provider) {
        ConfigData.readConfigClass(provider, AddonConfig.class);
    }
}
