package unsw.loopmania;

import javafx.beans.property.SimpleIntegerProperty;

public class Helmet extends Protection {
    private int damageReduction = 4;
    private double debuff;
    
    public Helmet(SimpleIntegerProperty x, SimpleIntegerProperty y, int level) {
        super(level, 400*(1+(level-1)*15/100), x, y);
        debuff = 10 - level;
        damageReduction += level;
        super.setType("helmet");
    }
    
    public Helmet(int level) {
        super(level, 400*(1+(level-1)*15/100));
        debuff = 10 - level;
        damageReduction += level;
        super.setType("helmet");
    }


    @Override
    public double protect(double damage) {
        //Will implement the drop by the scalar value
        return damage - damageReduction;
    }

    public double calcAttackDamage(double Attackdamage) {

        return Attackdamage * (1- debuff*0.01);
    }
    
}
