package unsw.loopmania.Enemies;

import java.util.List;

import unsw.loopmania.Heroes.AlliedSoldier;
import unsw.loopmania.BattleRunner;
import unsw.loopmania.Heroes.Character;
import unsw.loopmania.Heroes.Hero;
import unsw.loopmania.LoopManiaWorld;
import unsw.loopmania.PathPosition;
import unsw.loopmania.Entities.StaticEntity;

import java.util.ArrayList;

public class Zombie extends Enemy {
    public static final int BATTLERADIUS = 2;
    public static final int SUPPORTRADIUS = 2;
    public static final int DAMAGE = 18;
    public static final int GOLDAMOUNT = 250;
    public static final int HEALTH = 100;
    public static final int XP = 500;
    private boolean canMove;
    private String[] cardDrops;
    
    /**
     * 
     * @param position
     */
    public Zombie (PathPosition position) {
        super(position, BATTLERADIUS, SUPPORTRADIUS, DAMAGE, GOLDAMOUNT, HEALTH, XP);
        super.setType("zombie");
        canMove = false;
        cardDrops = new String[]{"campfire", "barracks", "tower", "trap", "bank", "village", "vampirecastle"};
    }

    public Zombie() {
        super(BATTLERADIUS, SUPPORTRADIUS, DAMAGE, GOLDAMOUNT, HEALTH, XP);
        super.setType("zombie");
        canMove = false;
        cardDrops = new String[]{"campfire", "barracks", "tower", "trap", "bank", "village", "vampirecastle"};
    }

    /**
     * Deals damage to Hero
     * @param h
     * @param b
     */
    public void attack(Hero hero, BattleRunner bR) {
        // Accounts for zombie critical bite against AS
        if (hero instanceof AlliedSoldier) {
            int randomInt = LoopManiaWorld.getRandNum();
            if (randomInt >= 0 && randomInt <= 9) {
                hero.setHealth(0);
                bR.convertAllyToZombie((AlliedSoldier)hero);
            } else {
                hero.takeDamage(this.getAttackDamage(), this);
            }

        } else {
            hero.takeDamage(this.getAttackDamage(), this);
        }
    }

    /**
     * move the enemy
     */
    @Override
    public void move() {
        if (canMove) {
            super.move();
            canMove = false;
        }
        else {
            canMove = true;
        }
    }

    /**
     * Generates random loot for player for zombie
     * @param character
     * @param width
     * @param rareItems
     * @return StaticEntity loot
     */
    public List<StaticEntity> getLoot(Character character, int width, List<String> rareItems) {
        int num = LoopManiaWorld.getRandNum();
        List<StaticEntity> loot = new ArrayList<StaticEntity>();
        if (num < 20) {
            String itemType;
            if (num < 1 && !rareItems.isEmpty()) {
                itemType = rareItems.get(LoopManiaWorld.getRandNum() % rareItems.size());
            }
            else {
                itemType = itemList[LoopManiaWorld.getRandNum() % itemList.length];
            }
            if (character.getNonLevelItems().contains(itemType)) {
                loot.add(character.addUnequippedItem(itemType, 0));
            }
            else if (num < 10) {
                int level = character.getHighestLevel(itemType) + 1;
                if (level > 10) {
                    level = 10;
                }
                loot.add(character.addUnequippedItem(itemType, level));
                System.out.println("up one level");
            }
            else {
                int level = character.getHighestLevel(itemType);
                loot.add(character.addUnequippedItem(itemType, level));
                System.out.println("same level");

            }
        }
        else if (num < 35) {
            String cardType = cardDrops[LoopManiaWorld.getRandNum() % cardDrops.length];
            loot.add(character.loadCard(cardType, width));
        }
        return loot;
    }
}
