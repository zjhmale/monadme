package zjhmale.monadj;

/**
 * Created by zjh on 15/10/8.
 * use unit to replace nothing
 */
public class Unit {
    private static final Unit unit = new Unit();

    public static Unit unit() {
        return unit;
    }
}
