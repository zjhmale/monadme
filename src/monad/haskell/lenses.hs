fmap . fmap :: (a -> b) -> f (f1 a) -> f (f1 b)

--we can find the definition of function compisition

(.) :: (b -> c) -> (a -> b) -> a -> c
f . g = \x -> f (g x)

--can curry it first
fmap . fmap = \func -> fmap (fmap func)

fmap . fmap func f (f1 a) = fmap (fmap func) f (f1 a)

--如果是嵌套的Maybe

fmap . fmap (+3) Just (Just 3) = fmap (fmap (+3)) Just (Just 3)
                               = Just (fmap (+3) (Just 3))
                               = Just (Just (3 + 3))
                               = Just (Just 6)

--lenses is just to deal with nested functor data type

--Setters are for functors and Folds are for folds, but lenses are a more general type. They allow us to compose functors, functions, folds and traversals together!
