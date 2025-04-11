package net.raynna.raynnarpg.newconfig.combat;

import com.iafenvoy.jupiter.ConfigManager;
import com.iafenvoy.jupiter.ServerConfigManager;
import com.iafenvoy.jupiter.api.JupiterConfig;
import com.iafenvoy.jupiter.api.JupiterConfigEntry;
import net.minecraft.resources.ResourceLocation;
import net.raynna.raynnarpg.RaynnaRPG;
import net.raynna.raynnarpg.newconfig.RaynnaServerConfig;

@JupiterConfig
public class CombatConfigEntry implements JupiterConfigEntry {

    @Override
    public ResourceLocation getId() {
        return ResourceLocation.fromNamespaceAndPath(RaynnaRPG.MOD_ID, "common");
    }

    @Override
    public void initializeCommonConfig(ConfigManager manager) {
        manager.registerConfigHandler(RaynnaServerConfig.INSTANCE);
        manager.registerServerConfig(RaynnaServerConfig.INSTANCE, ServerConfigManager.PermissionChecker.IS_OPERATOR);

    }


}

