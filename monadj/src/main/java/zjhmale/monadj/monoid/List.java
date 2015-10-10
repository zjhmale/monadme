package zjhmale.monadj.monoid;

import zjhmale.monadj.Functor;
import zjhmale.monadj.function.Function;

import java.util.Collection;

/**
 * Created by zjh on 15/10/10.
 */
public class List<A> extends AbstractMonoid<A> implements Functor<A> {
    private A head;
    private List<A> tail;
    private static final List empty = new List(null, null);

    public static <A> List<A> list(final A head, final List<A> tail) {
        if (head == null) {
            throw new IllegalArgumentException("head has no value");
        }
        if (tail == null) {
            throw new IllegalArgumentException("tail has no value");
        }
        return new List<A>(head, tail);
    }

    public static <A> List<A> list(final Collection<A> ac) {
        List<A> l = null;
        List<A> r = null;

        for (final A a : ac) {
            if (l == null) {
                l = new List<A>(a, null);
                //r 和 l持有相同的引用
                r = l;
            } else {
                l.tail = new List<A>(a, null);
                l = l.tail;
            }
        }

        if (r == null) {
            return (List<A>) List.empty();
        } else {
            l.tail = List.empty();
        }

        return r;
    }

    public static <A> List<A> empty() {
        return empty;
    }

    //functional list的定义就是这样递归的定义head和tail
    private List(final A head, final List<A> tail) {
        this.head = head;
        this.tail = tail;
    }

    public A value() {
        if (this.head == null)
            throw new IllegalStateException("head has no value");
        return this.head;
    }

    public A getValue() {
        if (this.head == null)
            throw new IllegalStateException("head has no value");
        return this.head;
    }

    public int length() {
        int len = 0;
        List<A> l = this;

        while (l.hasValue()) {
            len++;
            l = l.tail();
        }

        return len;
    }

    public A head() {
        if (this.head == null)
            throw new IllegalStateException("head has no value");
        return this.head;
    }

    public List<A> tail() {
        if (this.tail == null)
            throw new IllegalStateException("has no tail");
        return this.tail;
    }

    public boolean isEmpty() {
        return this.head == null;
    }

    public boolean isEmptyTail() {
        return this.tail.isEmpty();
    }

    public boolean hasValue() {
        return this.head != null;
    }

    public Monoid<A> mempty() {
        return List.empty();
    }

    public Monoid<A> mappend(final Monoid<A> a) {
        if (isEmpty()) {
            return a;
        }

        List<A> r = this;
        //因为java是拷贝引用的值所以需要复制一份出来 防止修改原来的引用
        List<A> rr = (List<A>) List.list(r.value(), List.empty());
        List<A> rrr = rr;
        //先要找到最后一个尾指针然后把a给append上去
        while (!r.isEmptyTail()) {
            r = r.tail();
            rrr.tail = (List<A>) List.list(r.value(), List.empty());
            rrr = rrr.tail();
        }

        rrr.tail = (List<A>) a;

        return rr;
    }

    public <B> Functor<B> fmap(final Function<A, B> f) {
        if (isEmpty()) {
            return List.empty();
        }

        List<A> al = this;
        List<B> r = new List<B>(f.apply(al.value()), (List<B>) List.empty());
        List<B> rr = r;

        while (!al.isEmptyTail()) {
            al = al.tail();
            rr.tail = new List<B>(f.apply(al.value()), rr.tail);
            rr = rr.tail();
        }

        return r;
    }
}
