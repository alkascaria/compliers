- 34 tests fail, but compiles
- checking for loop in inheritance relation is too complex, but works (4 points)
- unreadable code with lots of if-cascades and instance-of checks! Use Visitor pattern!!!
- subtyping incomplete and not correct (e.g null, subtype of classes, ...) (0 points)
- problems with return type of method calls
- correct method override is not checked for inherited methods (0 points)
- very much tailored to the special cases in the provided tests, not a generic solution to the typechecking problem!!! (3 points)

7 points

### Theory

for-rule:

- your premise does not describe a for loop which declares variable `i`, but a loop which only assigns to the variable.
- additionally, `(id:int)\in \Gamma` is also not correct, since the tuples in gamma consist of a marker (field or local variable) and the type binding. So `(_, id:int)\in \Gamma` would check that any varialbe named `id` exists with type `int`

0 points

derivation:

- the bottom-most application of the sequence rule is completely wrong. You end up with a statement `int[] y;` for which you cannot apply any rule. The rule you apply (var-use) does not make sense here
- what you really need it to incoorporate the information about `y` into the type-context (using the var-decl rule) and proceed from there with the new environment
- the remaining derivation seams to make sense, but you should have noticed that in the var-use of `y` you are missing the information about `y` in Gamma

1 point

overall: 8/18
