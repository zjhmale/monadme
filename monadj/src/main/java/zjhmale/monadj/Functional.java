package zjhmale.monadj;

import zjhmale.monadj.function.Function;
import zjhmale.monadj.function.Function2;

import java.util.ArrayList;
import java.util.Collection;

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

    public static <A, B> Collection<B> map(final Function<A, B> f, final Collection<A> ac, final Collection<B> bc) {
        for (A a : ac) {
            bc.add(f.apply(a));
        }
        return bc;
    }

    public static <A, B> Collection<B> map(final Function<A, B> f, final Collection<A> ac) {
        return Functional.map(f, ac, new ArrayList<B>(ac.size()));
    }

    public static <A, B> void foreach(final Function<A, B> f, final Collection<A> ac) {
        for (A a : ac) {
            f.apply(a);
        }
    }

    public static <A, B> A foldLeft(final Function2<A, B, A> f, final A a, final Collection<B> bc) {
        A acc = a;
        for (B b : bc) {
            acc = f.apply(acc, b);
        }
        return acc;
    }

    public static <A, B, C> Function<Tuple<A, B>, C> toFunction(final Function2<A, B, C> f) {
        return new Function<Tuple<A, B>, C>() {
            public C apply(Tuple<A, B> t) {
                return f.apply(t.first(), t.second());
            }
        };
    }

    public static <A, B, C> Function2<A, B, C> toFunction2(final Function<Tuple<A, B>, C> f) {
        return new Function2<A, B, C>() {
            public C apply(A a, B b) {
                return f.apply(Tuple.tuple(a, b));
            }
        };
    }

    public static <A, B, C> Function2<B, A, C> flip(final Function2<A, B, C> f) {
        return new Function2<B, A, C>() {
            public C apply(B b, A a) {
                return f.apply(a, b);
            }
        };
    }
}
