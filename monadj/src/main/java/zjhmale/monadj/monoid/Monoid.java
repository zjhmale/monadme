package zjhmale.monadj.monoid;

/**
 * Created by zjh on 15/10/10.
 */
public interface Monoid<A> {
    Monoid<A> mempty();

    Monoid<A> mappend(final Monoid<A> a);
}
