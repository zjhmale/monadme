package zjhmale.monadj.monad.common;

import zjhmale.monadj.Functor;
import zjhmale.monadj.function.Function;
import zjhmale.monadj.monad.base.AbstractMonad;
import zjhmale.monadj.monad.base.Monad;

/**
 * Created by zjh on 15/10/9.
 */
public class Either<A, B> extends AbstractMonad<B> {
    private A a;
    private B b;

    public static <A, B> Either<A, B> left(final A a) {
        if (a == null) {
            throw new IllegalArgumentException("argument has no value");
        } else {
            return new Either<A, B>(a, null);
        }
    }

    public static <A, B> Either<A, B> right(final B b) {
        if (b == null) {
            throw new IllegalArgumentException("argument has no value");
        } else {
            return new Either<A, B>(null, b);
        }
    }

    private Either(A a, B b) {
        this.a = a;
        this.b = b;
    }

    public boolean isLeft() {
        return this.b == null;
    }

    public boolean isRight() {
        return this.a == null;
    }

    public B right() {
        if (this.b == null)
            throw new IllegalStateException("is not right");
        return this.b;
    }

    public B getRight() {
        if (this.b == null)
            throw new IllegalStateException("is not right");
        return this.b;
    }

    public A left() {
        if (this.a == null)
            throw new IllegalStateException("is not left");
        return this.a;
    }

    public A getLeft() {
        if (this.a == null)
            throw new IllegalStateException("is not left");
        return this.a;
    }

    public <C> Monad<C> bind(Function<B, Monad<C>> f) {
        if (this.isRight()) {
            return f.apply(this.right());
        } else {
            return (Monad<C>) this;
        }
    }

    public <C> Monad<C> ret(C c) {
        return Either.right(c);
    }

    public <C> Functor<C> fmap(Function<B, C> f) {
        if (this.isRight()) {
            return ret(f.apply(this.right()));
        } else {
            return (Functor<C>) this;
        }
    }
}
