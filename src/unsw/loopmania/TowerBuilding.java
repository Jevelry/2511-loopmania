package unsw.loopmania;

import javafx.beans.property.SimpleIntegerProperty;

public class TowerBuilding extends StaticEntity implements BuildingOnMove{

    public TowerBuilding(SimpleIntegerProperty x, SimpleIntegerProperty y) {
        super(x, y);
        //TODO Auto-generated constructor stub
    }

    @Override
    public void updateOnMove() {
        // TODO Auto-generated method stub
        
    }

    public void attack(Enemy e) {
    }
    
}
