package zjhmale.monadj.function;

/**
 * Created by zjh on 15/10/7.
 */

//can also define this use Tuple and Function interface like Function<Tuple<A,B>,C>
public interface Function2<A, B, C> {
    C apply(final A a, final B b);
}
