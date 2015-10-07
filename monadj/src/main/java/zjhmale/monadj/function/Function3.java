package zjhmale.monadj.function;

/**
 * Created by zjh on 15/10/7.
 */
public interface Function3<A, B, C, D> {
    D apply(final A a, final B b, final C c);
}