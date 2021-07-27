package unsw.loopmania;

public interface ConfusedRareItem {
    public double protect(double damage, Enemy e);
    public double getDamage(Enemy e);
    public void use(Character character);
    public Item getAdditional();
}
