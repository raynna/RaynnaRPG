package net.raynna.silentrpg.client.player;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.raynna.silentrpg.server.player.skills.Skills;

public interface ISkillsCapability {

    // Get the skills of the player
    Skills getSkills();

    // Set the skills of the player
    void setSkills(Skills skills);

    // Serialize the skills to NBT
    CompoundTag saveSkills();

    // Load skills from NBT
    void loadSkills(CompoundTag nbt);
}
