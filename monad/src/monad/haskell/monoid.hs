--monoid描述了一个特殊的类型 包括一个幺元和一个二元函数 其中任何这个范畴上的值和幺元被作用在这个二元函数上得到的值都是自身

class Monoid m where
    mempty :: m
    mappend :: m -> m -> m
    mconcat :: [m] -> m
    mconcat ms = foldr mappend mempty ms

--mempty成为幺元 是一个identity value
--mappend a binary function receives two arguments of type m and returns a value of type m
--mconcat it simply calls foldr with the binary function mappend, a starting value of mempty and the list of Monoid values ms

instance Monoid a => Monoid (Maybe a) where
    mempty = Nothing
    Nothing `mappend` m = m
    m `mappend` Nothing = m
    Just m1 `mappend` Just m2 = Just (m1 `mappend` m2)

a = Nothing `mappend` Just "andy" --Just "andy"
b = Just LT `mappend` Nothing --Just LT
c = Just (Sum 3) `mappend` Just (Sum 4) --Just (Sum {getSum = 7})

--构造Maybe Monoid类型的时候传入的类型本身也要是一个Monoid类型

instance Monoid [a] where
    mempty = []
    mappend = (++)

d = [1,2,3] `mappend` [4,5,6] --[1,2,3,4,5,6]
e = mconcat [[1,2],[3,6],[9]] --[1,2,3,6,9]

--monoid law

--Identity

mappend mempty x = x
mappend x mempty = x

--Associativity(结合律)

mappend x (mappend y z) = mappend (mappend x y) z