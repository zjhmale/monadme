package zjhmale.monadj.monoid;

import zjhmale.monadj.Functional;
import zjhmale.monadj.function.Function2;

import java.util.Collection;
import java.util.List;

/**
 * Created by zjh on 15/10/10.
 */
public abstract class AbstractMonoid<A> implements Monoid<A> {
    public Monoid<A> mconcat(final List<Monoid<A>> al) {
        return Functional.foldLeft(new Function2<Monoid<A>, Monoid<A>, Monoid<A>>() {
            public Monoid<A> apply(Monoid<A> a, Monoid<A> b) {
                return a.mappend(b);
            }
        }, this.mempty(), al);
    }

    public Monoid<A> mconcat(final Collection<Monoid<A>> ac) {
        return Functional.foldLeft(new Function2<Monoid<A>, Monoid<A>, Monoid<A>>() {
            public Monoid<A> apply(Monoid<A> a , Monoid<A> b) {
                return a.mappend(b);
            }
        }, this.mempty(), ac);
    }
}
