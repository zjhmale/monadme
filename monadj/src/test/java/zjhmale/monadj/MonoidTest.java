package zjhmale.monadj;

import org.junit.Test;
import zjhmale.monadj.function.Function;
import zjhmale.monadj.monoid.List;
import zjhmale.monadj.monoid.Monoid;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.assertTrue;
import static zjhmale.monadj.monoid.List.empty;
import static zjhmale.monadj.monoid.List.list;

/**
 * Created by zjh on 15/10/10.
 */
public class MonoidTest {
    @Test
    public void listMonoid() {
        List l1 = list(3, list(2, list(1, empty())));
        List l2 = list(1, list(2, list(3, empty())));
        List l3 = (List) l1.mappend(l2);

        List mock = List.empty();
        List[] lists = new List[]{l1, l2, l3};
        List l4 = (List) mock.mconcat(new ArrayList<Monoid>(Arrays.asList(lists)));
        List l5 = ((List) List.empty().mappend(l1).mappend(l2).mappend(l3));

        List l6 = (List) l3.fmap(new Function<Integer, Integer>() {
            public Integer apply(Integer o) {
                return o + 1;
            }
        });

        assertTrue(l1.length() == 3);
        assertTrue(l2.length() == 3);
        assertTrue(l3.length() == 6);
        assertTrue(l4.length() == 12);
        assertTrue(l5.length() == 12);
        assertTrue(l6.length() == 6);
    }
}
