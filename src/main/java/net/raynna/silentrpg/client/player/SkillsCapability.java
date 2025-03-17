package net.raynna.silentrpg.client.player;

import net.minecraft.nbt.CompoundTag;
import net.raynna.silentrpg.server.player.skills.Skills;

public class SkillsCapability implements ISkillsCapability {

    private Skills skills;

    public SkillsCapability() {
        this.skills = new Skills();
    }

    @Override
    public Skills getSkills() {
        return this.skills;
    }

    @Override
    public void setSkills(Skills skills) {
        this.skills = skills;
    }

    @Override
    public CompoundTag saveSkills() {
        return this.skills.toNBT();
    }

    @Override
    public void loadSkills(CompoundTag nbt) {
        this.skills = Skills.fromNBT(nbt);
    }
}
