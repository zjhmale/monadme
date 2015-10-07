package zjhmale.monadj;

import zjhmale.monadj.function.Function;

/**
 * Created by zjh on 15/10/8.
 */
public class Functional {

    //f :: A -> B g :: B -> C g . f :: A -> C
    public static <A, B, C> Function<A, C> compose(final Function<A, B> f, final Function<B, C> g) {
        return new Function<A, C>() {
            public C apply(final A a) {
                return g.apply(f.apply(a));
            }
        };
    }
}
