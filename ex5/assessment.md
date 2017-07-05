## Group 3

### Implementation

- 87 failed test cases
- expression `this` not supported on the right hand side of assignment, but this is valid Java
  ```
  class A {
    int f;
    int m() {
      A a;
      a = this;
      return a.f;
    }
  }
  ```
- translation of fields ok
  - struct for class ok
  - initialization of fields ok
  - access to fields ok
- pointer to virtual method table in class struct, but never used
  - vtable not assigned when creating an object instance
  - method calls do not use the vtable to do dynamic dispatch
- null checks for method calls
- creation of class structs is order sensitive

  ```
  class A {
    B b;
  }
  class B {}
  ```

  In this case the translation fails, because the struct for `B` is not yet available when translating `A`

7/12 points

### Theory

- the control-flow-graph is ok
- correct placement of phi-nodes and correct translation to SSA

3/3 points

Total: 10/15
