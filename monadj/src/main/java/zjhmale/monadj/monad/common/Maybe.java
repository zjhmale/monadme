package zjhmale.monadj.monad.common;

import zjhmale.monadj.Functor;
import zjhmale.monadj.function.Function;
import zjhmale.monadj.monad.base.AbstractMonad;
import zjhmale.monadj.monad.base.Monad;

/**
 * Created by zjh on 15/10/9.
 */
public class Maybe<A> extends AbstractMonad<A> {
    private A a;
    private static final Maybe nothing = new Maybe(null);

    public static <A> Maybe<A> just(final A a) {
        if (a == null) {
            throw new IllegalArgumentException("argument has no value");
        } else {
            return new Maybe<A>(a);
        }
    }

    public static <A> Maybe<A> nothing() {
        return (Maybe<A>) nothing;
    }

    private Maybe(A a) {
        this.a = a;
    }

    public A value() {
        if(this.a == null) {
            throw new IllegalStateException("has no value");
        } else {
            return this.a;
        }
    }

    public A getValue() {
        if(this.a == null) {
            throw new IllegalStateException("has no value");
        } else {
            return this.a;
        }
    }

    public boolean isNothing() {
        return this.a == null;
    }

    public boolean hasValue() {
        return this.a != null;
    }

    public <B> Monad<B> bind(final Function<A, Monad<B>> f) {
        if (isNothing()) {
            return Maybe.nothing();
        } else {
            return f.apply(this.value());
        }
    }

    public <B> Monad<B> ret(final B b) {
        return Maybe.just(b);
    }

    public <B> Functor<B> fmap(final Function<A, B> f) {
        if (isNothing()) {
            return Maybe.nothing();
        } else {
            return Maybe.just(f.apply(this.value()));
        }
    }
}
