package unsw.loopmania;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

/**
 * Character contains the character that moves around the map.
 * It contains all the stats and fields for the character object,
 * as well as the character's inventory.
 * @author Group FRIDGE
 */
public class Character extends MovingEntity implements Hero {
    private IntegerProperty experience;
    private IntegerProperty gold;
    private IntegerProperty cycles;
    private Item equippedWeapon;
    private Item equippedHelmet;
    private Item equippedShield;
    private Item equippedArmour;
    private BonusDamageStrategy appliedBuff;
    private CharacterStats stats;
    private SimpleIntegerProperty aliveSoldiers;
    private List<AlliedSoldier> soldiers;
    private Inventory inventory;
    private boolean isStunned = false;
    private List<String> rareItems;
    private double strengthPotionBuff;
    private boolean canTakeDamage;
    private IntegerProperty bossKills;

    /**
     * Constructor for the character Class
     * @param position PathPosition: The starting position for the character
     */
    public Character(PathPosition position) {
        super(position, 100);
        experience = new SimpleIntegerProperty(0);
        gold = new SimpleIntegerProperty(0);
        cycles = new SimpleIntegerProperty(0);
        bossKills = new SimpleIntegerProperty(0);
        equippedWeapon = null;
        equippedHelmet = null;
        equippedShield = null;
        appliedBuff = new NormalState();
        stats = new CharacterStats();
        soldiers = new ArrayList<AlliedSoldier>();
        aliveSoldiers = new SimpleIntegerProperty(0);
        inventory = new Inventory(this);
        canTakeDamage = true;
    }

    /**
     * Constructor for the character class.
     * This is only used in tests.
     */
    public Character() {
        super(100);
        experience = new SimpleIntegerProperty(0);
        gold = new SimpleIntegerProperty(0);
        cycles = new SimpleIntegerProperty(0);
        equippedWeapon = null;
        equippedHelmet = null;
        equippedShield = null;
        appliedBuff = new NormalState();
        stats = new CharacterStats();
        soldiers = new ArrayList<AlliedSoldier>();
        inventory = new Inventory(this);
        aliveSoldiers = new SimpleIntegerProperty(0);
    }

    /**
     * Calculates damage reduction caused by all of Character's equipped
     * protective items
     * @param damage double: The incoming damage
     */
    public void takeDamage(double damage, Enemy e){
        if (!canTakeDamage) return;
        double newDamage = damage;
        if (!Objects.isNull(equippedShield)) {
            if (equippedShield.isTreeStump()) {
                newDamage = ((Protection) equippedShield).protect(damage, e);
            }
            newDamage = ((Protection) equippedShield).protect(damage);
        }
        if (!Objects.isNull(equippedHelmet)) {

            newDamage = ((Protection) equippedHelmet).protect(damage);
        }
        if (!Objects.isNull(equippedArmour)) {

            newDamage = ((Protection) equippedArmour).protect(damage);
        }
        super.takeDamage(newDamage);
    }

    /**
     * Character deaals damage to an enemy. This method has access to the
     * battlerunner class so the staff can convert enemies into converted Soldiers.
     * @param enemy Enemy: The enemy being attacked
     * @param b BattleRunner: BattleRunner class running the current battle
     */
    public void attack(Enemy enemy, BattleRunner b) {
        double newDamage = 5;
        
        if (equippedWeapon instanceof Stake) {
            newDamage = ((Stake)equippedWeapon).getDamage(enemy);
        }
        else if (equippedWeapon instanceof Staff) {
            if (((Staff)equippedWeapon).castSpell(enemy, b)) {
                return;
            }
            newDamage = ((Staff)equippedWeapon).getDamage();
        }
        else if (equippedWeapon.isWeapon()) {
            if (equippedWeapon instanceof Sword) {
                newDamage = ((Sword)equippedWeapon).getDamage(enemy);
            }
            else {
                // Item is a confused rare item that thinks its an Anduril
                newDamage = ((ConfusedRareItem)equippedWeapon).getDamage(enemy);
            }
        }

        if (!Objects.isNull(equippedHelmet)) {
            newDamage = ((Helmet) equippedHelmet).calcAttackDamage(newDamage);
        }
        newDamage += strengthPotionBuff;
        newDamage = appliedBuff.ApplyBonusDamge(newDamage);
        enemy.takeDamage(newDamage);
    }


    /**
     * Checks whether the character has a potion in their inventory and
     * if so, drinks it (to heal)
     */
    public void drinkHealthPotion() {
        
        HealthPotion potion = inventory.getHealthPotion();
        if (!Objects.isNull(potion)) {
            potion.use(this);
        }
    }

    public void drinkStrengthPotion() {
        StrengthPotion potion = inventory.getStrengthPotion();
        if (!Objects.isNull(potion)) {
            potion.use(this);
        }
    }

    public void drinkInvincibilityPotion() {
        Item potion = inventory.getInvinciblePotion();
        if (!Objects.isNull(potion)) {
            // instanceof is only used for typecasting.
            // e.g. Can't typecast a confusing TheOneRing into a potion
            if (potion instanceof InvinciblePotion) {
                ((InvinciblePotion)potion).use(this);
            }
            else {
                // Item is a confused rare item that thinks its an InvinciblePotion
                ((ConfusedRareItem)potion).use(this);
            }
        }
    }


    /**
     * Checks whether player has The One Ring in their inventory
     * @return Boolean depending on whether player has the Ring
     */
    public boolean hasRing() {
        return inventory.hasRing();
    }

    public boolean hasNuke() {
        return inventory.hasNuke();
    }
    
    /**
     * Removes any dead allied soldiers from the soldier list.
     * This is called after the battle has been done
     */
    public void updateAlliedSoldierAmount() {
        for (AlliedSoldier s : soldiers) {
            if (s.getHealth() <= 0) {
                soldiers.remove(s);
            }
        }
        aliveSoldiers.set(soldiers.size());
    }

    /**
     * Adds an allied soldier to the character's ally list
     * if there is space
     * @param soldier AlliedSoldier: soldier to be added to the list
     */
    public void addAlliedSoldier(AlliedSoldier soldier) {
        if (aliveSoldiers.getValue() < 3) {
            soldiers.add(0,soldier);
            aliveSoldiers.set(soldiers.size());
        }
    }
        /**
     * Equips a given item. Assumes item is valid to be equipped
     * (e.g. Item is not a HealthPotion)
     * @param i Item: item to be equipped.
     */
    public void equip(Item i) {
        inventory.removeUnequippedItem(i);
        stats.updateHighestLevel(i);
        if (i.isWeapon()) {
            equippedWeapon = i;
        } else if (i.isShield()) {
            equippedShield = i;
        } else if (i instanceof Armour) {
            equippedArmour = i;
        } else {
            equippedHelmet = i;
        }
    }

    // Getters and Setters and other incrementors
    ///////////////////////////////////////////////////////
    /**
     * Gets the character's health
     * @return int: The character's health
     */
    public int getHealth() {
        return health.get();
    }
    public IntegerProperty getHealthProperty() {
        return health;
    }

    /**
     * Gets the character's experience
     * @return int: The character's experience
     */
    public IntegerProperty getXP() {
        return experience;
    }

    /**
     * Gets the character's gold
     * @return int: The character's gold
     */
    public int getGold() {
        return gold.get();
    }
    public IntegerProperty getGoldProperty() {
        return gold;
    }

    /**
     * Gets the character's cycles
     * @return int: The character's cycles
     */
    public IntegerProperty getCycles() {
        return cycles;
    }

    /**
     * Sets the character's cycles only used in tests
     * @return void
     */
    public void setCycles(int cycle) {
        this.cycles.set(cycle);
    }

    public void increaseBossKills() {
        bossKills.set(bossKills.get() + 1);
    }

    public void setBossKills(int num) {
        bossKills.set(num);
    }

    public int getBossKills() {
        return bossKills.get();
    }

    public IntegerProperty bossKills() {
        return bossKills;
    }

    public void setStats(String type, int level) {
        stats.setStats(type, level);
    }

    public void setAliveSoldiers(int num) {
        aliveSoldiers.set(num);
        HeroFactory hF = new HeroFactory();
        for (int i = 0; i < num; i++) {
            addAlliedSoldier((AlliedSoldier)hF.create());
        }
    }

    public List<String> getRareItems() {
        return rareItems;
    }

    public void setRareItems(List<String> items) {
        rareItems = items;
        inventory.setRareItems(rareItems);
    }

    public void setConfusingMode() {
        inventory.setConfusingMode();
    }

    public boolean canTakeDamage() {
        return canTakeDamage;
    }

    public void makeInvincible() {
        canTakeDamage = false;
    }

    public void makeVincible() {
        canTakeDamage = true;
    }

    /**
     * Gets the character's allied soldier count
     * @return int: The character's allied soldier count
     */
    public int getAlliedSoldierCount() {
        return aliveSoldiers.get();
    }

    /**
     * Gets the character's allied soldier count as an IntegerProperty
     * @return IntegerProperty: The character's allied soldier count
     */
    public IntegerProperty getAlliedSoldierProperty() {
        return aliveSoldiers;
    }

    /**
     * Gets the character's allied soldiers
     * @return List<AlliedSoldier>: The character's allied soldiers
     */
    public List<AlliedSoldier> getAlliedSoldiers() {
        return soldiers;
    }

    /**
     * Gets the character's bonus damage strategy
     * @return BonusDamageStrategy: The character's bonus damage strategy
     */
    public BonusDamageStrategy getBonusDamageStrategy() {
        return appliedBuff;
    }

    public double getStrengthPotionBuff() {
        return strengthPotionBuff;
    }

    /**
     * Gets the character's item level stats
     * @return CharacterStats: The character's item level stats class
     */
    public CharacterStats getStats() {
        return stats;
    }

    /**
     * Sets the character's bonus damage strategy
     * @param buff BonusDamageStrategy: The selected BonusDamageStrategy
     */
    public void setBonusDamageStrategy(BonusDamageStrategy buff) {
        appliedBuff = buff;
    }

    /**
     * Sets the character's health (used so health doesn't go above 100)
     * @param health int: The character's health
     */
    public void setHealth(int health) {
        this.health.set(health);
    } 
    public void takeRawDamage(int damage) {
        takeDamage((double)damage);
    } 

    /**
     * Increases the character's health
     * @param health int: The character's maximum health increase
     */
    public void restoreHealth(int amount) {
        health.set(health.get() + amount);
        if (health.get() > 100) {
            setHealth(100);
        }
    }

    /**
     * Increases the character's gold amount
     * @param amount int: Amount to increase
     */
    public void gainGold(int amount) {
        gold.set(gold.get() + amount);
    }

    /**
     * Decreases the character's gold amount
     * @param amount int: Amount to decrease
     */
    public void loseGold(int amount) {
        gold.set(gold.get() - amount);
    }

    /**
     * Increases the character's experience amount
     * @param amount int: Amount to increase
     */
    public void gainXP(int amount) {
        experience.set(experience.get() + amount);
    }
    /**
     * Increases character's cycle count by 1
     */
    public void gainCycle() {
        cycles.set(cycles.get() + 1);
    }

    public void setCycle(int amount) {
        cycles.set(amount);
    }
    /**
     * Decreases the character's health
     * @param damage double: Amount to decrease
     */
    public void loseHealth(double damage){
        health.set(health.get() - (int)damage);
    }

    /**
     * Gets highest level of a particular type of item
     * @param item Item: Item who's highest level is being inquired
     * @return int: Highest level for item
     */
    public int getHighestLevel(Item item) {
        return stats.getHighestLevel(item.getType());
    }

    /**
     * Gets highest level of a particular type of item
     * @param item Item: Item who's highest level is being inquired
     * @return int: Highest level for item
     */
    public int getHighestLevel(String item) {
        return stats.getHighestLevel(item);
    }

    /**
     * Updates character's highest level for particular item by 1
     * @param item Item: Type of item to increase highest level of
     */
    public void updateHighest(Item item) {
        stats.updateHighestLevel(item);
    }

    /**
     * Gets currently equipped weapon
     * @return Item: the currently equipped weapon
     */
    public Item getWeapon() {
        return equippedWeapon;
    }

    /**
     * Gets currently equipped shield
     * @return Item: the currently equipped shield
     */
    public Item getShield() {
        return equippedShield;
    }

    /**
     * Gets currently equipped armour
     * @return Item: the currently equipped armour
     */
    public Item getArmour() {
        return equippedArmour;
    }

    /**
     * Gets currently equipped helmet
     * @return Item: the currently equipped helmet
     */
    public Item getHelmet() {
        return equippedHelmet;
    }


    /**
     * Gets a List of items that don't have levels
     * @return List<String>: A List of items that don't have levels
     */
    public List<String> getNonLevelItems() {
        return inventory.getNonLevelItems();
    }


    /**
     * Gets a list of all unequipped items in the character's inventory
     * @return List<Item>: a list of all unequipped items in the character's inventory
     */
    public List<Item> getunequippedInventoryItems() {
        return inventory.getunequippedInventoryItems();
    }

    /**
     * Adds an item to the player's unequipped inventory
     * @param type String: Type of item to add
     * @param i int: Level of item to add
     * @return StaticEntity: Item added to unequipped inventory
     */
    public StaticEntity addUnequippedItem(String type, int level) {
        return inventory.addUnequippedItem(type, level);
    }

    /**
     * Used when loading ConfusedRareItems
     * @param type
     * @param additional
     * @return
     */
    public StaticEntity addUnequippedConfusedItem(String type, String additional) {
        return inventory.addUnequippedConfusedItem(type, additional);
    }

    /**
     * Gets character's inventory object (used in the shop)
     * @return Inventory: character's inventory object
     */
    public Inventory getInventory() {
        return inventory;
    }

    /**
     * Gets an item from the character's unequipped inventory from its coordinates
     * @param x int: Column coordinate in unequipped inventory
     * @param y int: Row coordinate in unequipped inventory
     * @return Item: The item
     */
    public Item getUnequippedInventoryItemEntityByCoordinates(int x, int y){
        return inventory.getUnequippedInventoryItemEntityByCoordinates(x, y);
    }

    /**
     * Removes an item from the character's unequipped inventory from its coordinates
     * @param x int: Column coordinate in unequipped inventory
     * @param y int: Row coordinate in unequipped inventory
     */
    public void removeUnequippedInventoryItemByCoordinates(int nodeX, int nodeY) {
        inventory.removeUnequippedInventoryItemByCoordinates(nodeX, nodeY);
    }

    /**
     * Adds a card to the character's card inventory
     * @param type String: type of card to add
     * @param width int: Total length of rows
     * @return StaticEntity: The card that was added
     */
    public StaticEntity loadCard(String type, int width) {
        return inventory.loadCard(type, width);
    }

    /**
     * Gets a card from the character's card inventory from its coordinates
     * @param cardNodeX int: Column coordinate in unequipped inventory
     * @param cardNodeY int: Row coordinate in unequipped inventory
     * @return Card: The card
     */
    public Card getMatchingCard(int cardNodeX, int cardNodeY) {
        return inventory.getMatchingCard(cardNodeX, cardNodeY);
    }

    /**
     * Removes a card from the character's card inventory
     * @param card Card: Card to remove
     * @param cardNodeX int: index of card
     */
    public void destroyCard(Card card, int cardNodeX) {
        inventory.destroyCard(card, cardNodeX);
    }

    /**
     * Gets a card from the character's card inventory from its index
     * @param x int: index of the card
     * @return Card: The card
     */
    public Card getCardByCoordinate(int x) {
        return inventory.getCardByCoordinate(x);
    }

    /**
     * Gets number of unequipped items
     * @return int: number of unequipped items
     */
    public int getUnequippedInventoryItemsNum() {
        return inventory.getUnequippedInventoryItemsNum();
    }

    /**
     * Gets number of cards
     * @return int: number of cards
     */
    public int getCardsNum() {
        return inventory.getCardsNum();
    }

    public void setStunned(boolean b) {
        isStunned = b;
    }

    public boolean isStunned() {
        return isStunned;
    }

    public void setBuff(double buffdmg) {
        this.strengthPotionBuff = buffdmg;
    }
    public void removeBuff() {
        strengthPotionBuff = 0;
    }
}