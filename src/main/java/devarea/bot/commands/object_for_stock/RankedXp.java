package devarea.bot.commands.object_for_stock;

public class RankedXp {

    protected int xp;
    protected int rank;

    public RankedXp(final int xp, final int rank) {
        this.xp = xp;
        this.rank = rank;
    }

    public RankedXp(final int xp) {
        this.xp = xp;
    }


    public int getRank() {
        return rank;
    }

    public int getXp() {
        return xp;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public void setXp(int xp) {
        this.xp = xp;
    }

    public void upOne() {
        this.xp += 1;
    }
}
