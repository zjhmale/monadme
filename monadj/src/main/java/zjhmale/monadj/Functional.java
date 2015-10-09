package zjhmale.monadj;

import zjhmale.monadj.function.Function;
import zjhmale.monadj.function.Function2;
import zjhmale.monadj.monad.common.Maybe;
import zjhmale.monadj.predicate.Predicate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created by zjh on 15/10/8.
 */
public class Functional {

    //f :: A -> B g :: B -> C g . f :: A -> C
    public static <A, B, C> Function<A, C> compose(final Function<B, C> g, final Function<A, B> f) {
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

    public static <A, B, C> Function<Tuple<A, B>, C> toFunction(final Function2<A, B, C> f) {
        return new Function<Tuple<A, B>, C>() {
            public C apply(final Tuple<A, B> t) {
                return f.apply(t.first(), t.second());
            }
        };
    }

    public static <A, B, C> Function2<A, B, C> toFunction2(final Function<Tuple<A, B>, C> f) {
        return new Function2<A, B, C>() {
            public C apply(final A a, final B b) {
                return f.apply(Tuple.tuple(a, b));
            }
        };
    }

    public static <A, B, C> Function2<B, A, C> flip(final Function2<A, B, C> f) {
        return new Function2<B, A, C>() {
            public C apply(final B b, final A a) {
                return f.apply(a, b);
            }
        };
    }

    public static <A, B, C> Function<B, C> partialApply(final Function2<A, B, C> f, final A a) {
        return new Function<B, C>() {
            public C apply(final B b) {
                return f.apply(a, b);
            }
        };
    }

    public static <A, B> Function<Unit, B> lazyApply(final Function<A, B> f, final A a) {
        return new Function<Unit, B>() {
            public B apply(final Unit unit) {
                return f.apply(a);
            }
        };
    }

    //foldl :: Foldable t => (a -> b -> a) -> a -> t b -> a
    //foldl (\ys y -> ys ++ [y]) [] [1,2,3] => [1,2,3]
    public static <A, B> A foldLeft(final Function2<A, B, A> f, final A a, final Collection<B> bc) {
        A acc = a;
        for (B b : bc) {
            acc = f.apply(acc, b);
        }
        return acc;
    }

    //foldr :: Foldable t => (a -> b -> b) -> b -> t a -> b
    //foldr (\y ys -> ys ++ [y]) [] [1,2,3] => [3,2,1]
    public static <A, B> B foldRight(final Function2<A, B, B> f, final B b, final Collection<A> ac) {
        if (ac.size() == 0) {
            return b;
        } else {
            List<A> reversed = new ArrayList<A>(ac);
            Collections.reverse(reversed);
            return Functional.foldLeft(Functional.flip(f), b, reversed);
        }
    }

    public static <A> Maybe<A> find(final Predicate<A> f, final Collection<A> ac) {
        for (A a : ac) {
            if (f.apply(a)) {
                return Maybe.just(a);
            }
        }
        return Maybe.nothing();
    }

    public static <A> boolean exists(final Predicate<A> f, final Collection<A> ac) {
        return Functional.find(f, ac).hasValue();
    }
}
