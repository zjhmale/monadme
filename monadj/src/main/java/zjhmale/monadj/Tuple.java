package zjhmale.monadj;

/**
 * Created by zjh on 15/10/7.
 */
public class Tuple<A, B> {
    private A a;
    private B b;

    public static <A, B> Tuple<A, B> tuple(final A a, final B b) {
        if (a == null)
            throw new IllegalArgumentException("first argument has no value");
        if (b == null)
            throw new IllegalArgumentException("second argument has no value");
        return new Tuple<A, B>(a, b);
    }

    private Tuple(final A a, final B b) {
        this.a = a;
        this.b = b;
    }

    public A first() {
        return this.a;
    }

    public A getFirst() {
        return this.a;
    }

    public B second() {
        return this.b;
    }

    public B getSecond() {
        return this.b;
    }
}
