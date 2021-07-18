package test;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.javatuples.Pair;
import org.junit.Test;

import javafx.beans.property.SimpleIntegerProperty;
import unsw.loopmania.BuildingFactory;
import unsw.loopmania.CampfireBuilding;
import unsw.loopmania.Enemy;
import unsw.loopmania.EnemyFactory;
import unsw.loopmania.PathPosition;
import unsw.loopmania.Building;
import unsw.loopmania.Vampire;

public class VampireTest {
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

    @Test
    public void moveVampire() {
        EnemyFactory eF = new EnemyFactory();
        BuildingFactory bF = new BuildingFactory();
        Enemy vampire = eF.create(new PathPosition(0, createPath()), "vampire");
        Building campfire = bF.create(new SimpleIntegerProperty(1), new SimpleIntegerProperty(1), "campfire");
        ((Vampire)vampire).move((CampfireBuilding)campfire);
        assertEquals(vampire.getX(), 0);
        assertEquals(vampire.getY(), 1);
    }
}
