package test;
import unsw.loopmania.BarracksBuilding;
import unsw.loopmania.BarracksCard;
import unsw.loopmania.BonusDamageStrategy;
import unsw.loopmania.CampfireBuilding;
import unsw.loopmania.CampfireState;
import unsw.loopmania.Character;
import unsw.loopmania.Vampire;
import unsw.loopmania.VampireCastleBuilding;
import unsw.loopmania.VillageBuilding;
import unsw.loopmania.ZombiePitBuilding;
import unsw.loopmania.Enemy;
import unsw.loopmania.LoopManiaWorld;
import unsw.loopmania.NormalState;
import unsw.loopmania.PathPosition;
import unsw.loopmania.Slug;
import unsw.loopmania.TowerBuilding;
import unsw.loopmania.TrapBuilding;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.javatuples.Pair;
import org.json.JSONObject;
import org.junit.Test;

import javafx.beans.property.SimpleIntegerProperty;
public class BuildingTests {
    private List<Pair<Integer, Integer>> path;
    private SimpleIntegerProperty x;
    private SimpleIntegerProperty y;
    private PathPosition position;
    
    public BuildingTests() {
        path = createPath();
        x = new SimpleIntegerProperty();
        y = new SimpleIntegerProperty();
        x.set(0);
        y.set(0);
        position = new PathPosition(0, path);
    }

    private List<Pair<Integer, Integer>> createPath() {
        List<Pair<Integer, Integer>> l = new ArrayList<Pair<Integer, Integer>>();
        l.add(new Pair<Integer, Integer>(0, 0));
        l.add(new Pair<Integer, Integer>(0, 1));
        l.add(new Pair<Integer, Integer>(0, 2));
        l.add(new Pair<Integer, Integer>(1, 2));
        l.add(new Pair<Integer, Integer>(2, 2));
        l.add(new Pair<Integer, Integer>(2, 1));
        l.add(new Pair<Integer, Integer>(2, 0));
        l.add(new Pair<Integer, Integer>(1, 0));
        return l;
    }
    //BarracksTests
    //////////////////////////////////
    @Test
    public void AddAlliedSoldiersTest() {
        
        Character c = new Character(new PathPosition(0, path));
        BarracksBuilding b = new BarracksBuilding(x, y);
        assertEquals(b.getType(), "barracks");
        assertEquals(c.getAlliedSoldierCount(), 0);
        b.updateOnMove(c);
        assertEquals(c.getAlliedSoldierCount(), 1);
        b.updateOnMove(c);
        assertEquals(c.getAlliedSoldierCount(), 2);
        b.updateOnMove(c);
        assertEquals(c.getAlliedSoldierCount(), 3);
        b.updateOnMove(c);
        assertEquals(c.getAlliedSoldierCount(), 3);
    }

    @Test
    public void NoAlliedSoldiersAddedTest() {
        Character c = new Character(new PathPosition(0, path));
        x.set(1);
        BarracksBuilding b = new BarracksBuilding(x, y);
        assertEquals(c.getAlliedSoldierCount(), 0);
        b.updateOnMove(c);
        assertEquals(c.getAlliedSoldierCount(), 0);
    }
    //////////////////////////////////

    //CampfireTests
    //////////////////////////////////
    @Test
    public void CampfireInRangeTest() {
        Character c = new Character(new PathPosition(0, path));
        CampfireBuilding campfire = new CampfireBuilding(x, y);
        
        BonusDamageStrategy b = c.getBonusDamageStrategy();
        assert(b instanceof NormalState);
        campfire.updateOnMove(c);
        BonusDamageStrategy newB = c.getBonusDamageStrategy();
       
        assert(newB instanceof CampfireState);
        campfire.updateOnMove(c);
        BonusDamageStrategy newB2 = c.getBonusDamageStrategy();
        assert(newB2 instanceof CampfireState);
    }
    @Test
    public void CampfireNotInRangeTest() {
        Character c = new Character(new PathPosition(0, path));
        x.set(5);
        y.set(5);
        CampfireBuilding campfire = new CampfireBuilding(x, y);
        campfire.updateOnMove(c);
        BonusDamageStrategy b = c.getBonusDamageStrategy();
        assert(b instanceof NormalState);
    }
    //////////////////////////////////

    // TrapTests
    //////////////////////////////////
    @Test
    public void trapactiveTest(){
        Enemy e = new Slug(position);
        TrapBuilding t = new TrapBuilding(x, y);
        t.updateOnMove(e);
        assertEquals(20, e.getHealth());
    }
    @Test
    public void trapinactiveTest(){
        Enemy e = new Slug(position);
        Enemy e1 = new Slug(position);
        TrapBuilding t = new TrapBuilding(x, y);
        t.updateOnMove(e);
        boolean x = t.shouldExist().get();
        assertFalse(x);
        assertEquals(20, e.getHealth());
    }
    //////////////////////////////////

    // TowerTests
    //////////////////////////////////
    @Test
    public void rangeTest(){
        Character c = new Character(new PathPosition(0, path));
        TowerBuilding t = new TowerBuilding(x, y);
        assertTrue(t.isInRange(c));
        x.set(8);
        y.set(9);
        TowerBuilding t1 = new TowerBuilding(x, y);
        assertFalse(t1.isInRange(c));
    }
    @Test
    public void attackTest() {
        Enemy e = new Slug(position);
        TowerBuilding t = new TowerBuilding(x, y);
        t.attack(e);
        assertTrue(e.getHealth() < 50);
    }
    //////////////////////////////////

    // VillageTests
    //////////////////////////////////
    @Test
    public void VillageTest() {
        VillageBuilding v = new VillageBuilding(x, y);
        Character c = new Character(new PathPosition(0, path));
        c.setHealth(50);
        v.updateOnMove(c);
        assertEquals(75, c.getHealth());
    }
    //////////////////////////////////

    // ZombiePitTests
    //////////////////////////////////

    @Test
    public void ZombiePitSpawnTest() {
        SimpleIntegerProperty x = new SimpleIntegerProperty(0);
        SimpleIntegerProperty y = new SimpleIntegerProperty(0);
        ZombiePitBuilding zombiePit = new ZombiePitBuilding();
        zombiePit.spawnEnemy(new PathPosition(0, path));
        assertTrue(LoopManiaWorld.getEnemies().size() == 1 || LoopManiaWorld.getEnemies().size() == 2
            || LoopManiaWorld.getEnemies().size() == 3);
        for (Enemy enemy: LoopManiaWorld.getEnemies()) {
            assertTrue(enemy instanceof Zombie);
        }
    }

    @Test
    public void ZombiePitPositionTest() {
        SimpleIntegerProperty x = new SimpleIntegerProperty(0);
        SimpleIntegerProperty y = new SimpleIntegerProperty(0);
        ZombiePitBuilding zombiePit = new ZombiePitBuilding(x, y);
        zombiePit.spawnEnemy(new PathPosition(1, path));
        for (Enemy enemy: LoopManiaWorld.getEnemies()) {
            assertTrue(enemy.getX() == x.get() || enemy.getX() - 1 == x.get() || enemy.getX() + 1 == x.get());
            assertTrue(enemy.getY() == y.get() || enemy.getY() - 1 == y.get() || enemy.getY() + 1 == y.get());
        }
    }

    @Test
    public void ZombiePitCycleTest() {
        LoopManiaWorld.setSeed(10);
        SimpleIntegerProperty x = new SimpleIntegerProperty(0);
        SimpleIntegerProperty y = new SimpleIntegerProperty(0);
        ZombiePitBuilding z = new ZombiePitBuilding(x, y);
        assertEquals(1, z.generateNumberOfEnemies());
        assertEquals(2, z.generateNumberOfEnemies());
        assertEquals(3, z.generateNumberOfEnemies());

    }
    //////////////////////////////////

    // VampireCastleTests
    //////////////////////////////////
    @Test
    public void VampireCastleSpawnTest() {
        VampireCastleBuilding vampireCastle = new VampireCastleBuilding();
        vampireCastle.spawnEnemy(new PathPosition(1, path))
        assertTrue(LoopManiaWorld.getEnemies().size() == 1 || LoopManiaWorld.getEnemies().size() == 2
            || LoopManiaWorld.getEnemies().size() == 3);
        for (Enemy enemy: LoopManiaWorld.getEnemies()) {
            assertTrue(enemy instanceof Vampire);
        }
    }

    @Test
    public void VampireCastlePositionTest() {
        SimpleIntegerProperty x = new SimpleIntegerProperty(0);
        SimpleIntegerProperty y = new SimpleIntegerProperty(0);
        VampireCastleBuilding vampireCastle = new ZombiePitBuilding(x, y);
        vampireCastle.spawnEnemy(new PathPosition(1, path));
        for (Enemy enemy: LoopManiaWorld.getEnemies()) {
            assertTrue(enemy.getX() == x.get() || enemy.getX() - 1 == x.get() || enemy.getX() + 1 == x.get());
            assertTrue(enemy.getY() == y.get() || enemy.getY() - 1 == y.get() || enemy.getY() + 1 == y.get());
        }
    }

    @Test
    public void ZombiePitCycleTest() {
        LoopManiaWorld.setSeed(10);
        SimpleIntegerProperty x = new SimpleIntegerProperty(0);
        SimpleIntegerProperty y = new SimpleIntegerProperty(0);
        ZombiePitBuilding v = new VampireCastleBuilding(x, y);
        assertEquals(1, v.generateNumberOfEnemies());
        assertEquals(2, v.generateNumberOfEnemies());

    }
    //////////////////////////////////
}
