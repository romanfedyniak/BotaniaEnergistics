package com.botaeng.mixin;

import java.util.Collections;
import java.util.List;

import zone.rong.mixinbooter.ILateMixinLoader;

/**
 * The mixins target AE2UD's classes, which are there only once mods load.
 */
public class BotaEngLateMixinLoader implements ILateMixinLoader {

    @Override
    public List<String> getMixinConfigs() {
        return Collections.singletonList("mixins.botaeng.json");
    }
}
