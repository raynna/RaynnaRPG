package net.raynna.silentrpg.network;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.raynna.silentrpg.network.packets.skills.SkillsPacket;
import net.raynna.silentrpg.network.packets.skills.SkillsPacketHandler;

public class Packets {

    public static void register(IEventBus eventBus) {
        eventBus.addListener(Packets::registerPackets);
    }

    private static void registerPackets(RegisterPayloadHandlersEvent event) {
        String version = "1.0";
        PayloadRegistrar registrar = event.registrar(version);
        registrar.playToClient(
                SkillsPacket.TYPE,
                SkillsPacket.CODEC,
                new SkillsPacketHandler()
        );
    }
}