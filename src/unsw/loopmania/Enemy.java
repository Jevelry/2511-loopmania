package unsw.loopmania;

import org.junit.platform.engine.support.hierarchical.EngineExecutionContext;
import java.util.Random;

public class Enemy extends MovingEntity {
    private int battleRadius;
    private int supportRadius;
    private int attackDamage;
    private int goldAmount;
    private int allyTurnCount;
    private int health;

    public Enemy (PathPosition position, int battleRadius, int supportRadius, int attackDamage, int goldAmount, int health) {
        super(position, health);
        this.battleRadius = battleRadius;
        this.supportRadius = supportRadius;
        this.attackDamage  = attackDamage;
        this.goldAmount = goldAmount;
    }

    public Enemy (int battleRadius, int supportRadius, int attackDamage, int goldAmount, int health) {
        super(health);
        this.battleRadius = battleRadius;
        this.supportRadius = supportRadius;
        this.attackDamage  = attackDamage;
        this.goldAmount = goldAmount;
    }

    public int getHealth() {
        return health;
    }

    /**
     * Will subtract the given damage from the enemy 
     * @param attackDamage attack damage given to the enemy
     * @return returns true if the enemy died
     */
    public boolean damage (int attackDamage) {
        return true;
    }
    /**
     * Enemy attacks the character given
     * @param character
     * @return boolean if the character was killed returns true
     */
    public boolean attack (Character character) {
        return true;
    }

    /**
     * Enemy attacks the ally given
     * @param ally
     * @return boolean if the ally was killed returns true
     */
    public boolean attack (AlliedSoldier ally) {
        return true;
    }

    /**
     * Returns the gold that the enemy drops when they die
     * @param gold
     * @return
     */
    public int getGold(int gold) {
        return gold;
    }

    /**
     * Will stop the enemy doing damage for the given number of turns
     * @param turnNumber
     */
    public void convert(int turnNumber) {

    }

    /**
     * move the enemy
     */
    public void move(){
        // TODO = modify this, since this implementation doesn't provide the expected enemy behaviour
        // this basic enemy moves in a random direction... 25% chance up or down, 50% chance not at all...
        int directionChoice = (new Random()).nextInt(2);
        if (directionChoice == 0){
            moveUpPath();
        }
        else if (directionChoice == 1){
            moveDownPath();
        }
    }
}
