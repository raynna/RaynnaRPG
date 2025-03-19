package net.raynna.raynnarpg.server.player.skills;

    public enum SkillType {
        MINING("Mining"),
        SMELTING("Smelting"),
        CRAFTING("Crafting"),
        COMBAT("Combat");


        private final String name;


        SkillType(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public static SkillType getSkillByName(String name) {
            for (SkillType skill : SkillType.values()) {
                if (skill.name().equalsIgnoreCase(name) || skill.getName().equalsIgnoreCase(name)) {
                    return skill;
                }
            }
            return null;
        }

    }
