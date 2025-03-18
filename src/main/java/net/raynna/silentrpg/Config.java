package net.raynna.silentrpg;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

@EventBusSubscriber(modid = SilentRPG.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class Config {



    public static final class Common {
        static final ModConfigSpec SPEC;

        private static final ModConfigSpec.BooleanValue MOD_IS_ENABLED_SERVER;

        static {
            ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
            {
                builder.push("general");
                {
                    MOD_IS_ENABLED_SERVER = builder.comment("Wheather if mod should be enabled or not; DEFAULT: true").define("mod_is_enabled", true);
                    builder.pop();
                }
            }
            SPEC = builder.build();
        }
    }

    public static final class Client {
        static final ModConfigSpec SPEC;

        private static final ModConfigSpec.BooleanValue MOD_IS_ENABLED_CLIENT;

        static {
            ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
            MOD_IS_ENABLED_CLIENT = builder.comment("Wheather if mod should be enabled or not; DEFAULT: true").define("mod_is_enabled", true);

            SPEC = builder.build();
        }
    }


    public static boolean logDirtBlock;
    public static int magicNumber;
    public static String magicNumberIntroduction;
    public static Set<Item> items;

    private static boolean validateItemName(final Object obj) {
        return obj instanceof String itemName && BuiltInRegistries.ITEM.containsKey(ResourceLocation.parse(itemName));
    }

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {

    }
}
