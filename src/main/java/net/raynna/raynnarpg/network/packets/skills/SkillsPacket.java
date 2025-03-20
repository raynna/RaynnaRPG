package net.raynna.raynnarpg.network.packets.skills;


import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.raynna.raynnarpg.server.player.skills.Skill;
import net.raynna.raynnarpg.server.player.skills.SkillType;
import net.raynna.raynnarpg.server.player.skills.Skills;


public record SkillsPacket(Skills skills) implements CustomPacketPayload {

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static final Type<SkillsPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("raynnarpg", "player_skills"));

    public static final StreamCodec<FriendlyByteBuf, SkillsPacket> CODEC = new StreamCodec<>() {
        @Override
        public void encode(FriendlyByteBuf buf, SkillsPacket packet) {
            Skills skills = packet.skills;

            buf.writeInt(SkillType.values().length);
            for (SkillType type : SkillType.values()) {
                Skill skill = skills.getSkill(type);
                buf.writeUtf(type.getName());
                buf.writeInt(skill.getLevel());
                buf.writeDouble(skill.getXp());
            }
        }

        @Override
        public SkillsPacket decode(FriendlyByteBuf buf) {
            int size = buf.readInt();
            Skills skills = new Skills();

            for (int i = 0; i < size; i++) {
                String skillName = buf.readUtf(Short.MAX_VALUE);
                int skillLevel = buf.readInt();
                double skillXp = buf.readDouble();
                SkillType skillType = SkillType.valueOf(skillName.toUpperCase());
                skills.getSkill(skillType).setLevel(skillLevel);
                skills.getSkill(skillType).setXp(skillXp);
            }

            return new SkillsPacket(skills);
        }
    };
}