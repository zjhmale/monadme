class Functor f where
  fmap :: (a -> b) -> f a -> f b

--haskell中的functor是一个kind为* -> *的typeclass也就是说在要instance这个typeclass的时候必须是一个类型构造器也就是获取一个类型返回一个新类型的data 比如Maybe

instance Functor Maybe where
    fmap f (Just x) = Just (f x)
    fmap f Nothing = Nothing

--对于list来说 fmap的类型是

fmap :: (a -> b) -> [a] -> [b]

--而一般listfmap其实就是大多数FP语言中的map

instance Functor [] where
    fmap = map

--functor law

--Identity

fmap id functor = id functor

--Composition

fmap (f . g) functor = fmap f (fmap g functor)
