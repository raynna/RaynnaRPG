package com.raynna.silentrpg.player.skills;

    public enum SkillType {
        MINING("Mining"),
        SMITHING("Smithing"),
        COMBAT("Combat");


        private final String name;


        SkillType(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
