package unsw.loopmania;

import javafx.beans.property.SimpleIntegerProperty;

public class HerosCastleBuilding extends StaticEntity implements Building, BuildingOnMove{

    public HerosCastleBuilding(SimpleIntegerProperty x, SimpleIntegerProperty y) {
        super(x, y);
        super.setType("heros_castle");
        //TODO Auto-generated constructor stub
    }

    @Override
    public void updateOnMove(MovingEntity movingEntity) {
        // TODO Auto-generated method stub
        
    }

    // @Override
    // public void updateOnCycle() {
    //     // TODO Auto-generated method stub
        
    // }
    
}
