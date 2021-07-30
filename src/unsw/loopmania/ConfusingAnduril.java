package unsw.loopmania;

import javafx.beans.property.SimpleIntegerProperty;

public class ConfusingAnduril extends Anduril implements ConfusedRareItem{
    private Item additional;

	public ConfusingAnduril(SimpleIntegerProperty x, SimpleIntegerProperty y, int level, Item additional) {
		super(x, y, level);
        this.additional = additional;
	}

    @Override
    public Item getAdditional() {
        return additional;
    }

	@Override
	public double protect(double damage, Enemy enemy) {
		return ((TreeStump)additional).protect(damage, enemy);
	}

    @Override
    public void use(Character character) {
        ((InvinciblePotion)additional).use(character);
    }
}
