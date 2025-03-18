package net.raynna.silentrpg.server.player.skills;

    public enum SkillType {
        MINING("Mining"),
        SMITHING("Smithing"),
        CRAFTING("Crafting"),
        COMBAT("Combat");


        private final String name;


        SkillType(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
