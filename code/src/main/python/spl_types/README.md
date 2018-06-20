## SPL DataTypes
This folder contains the setup files for installing spl_datatypes.

## Datatypes
* LinkedList implementation for python, using the ``Node`` class
```
// SPL Code
lst = 1 : 2 : 3 : [];

// Python Code
lst = Node(1) + Node(2) + Node(3) + Node()
```
* Mutable Tuples for python, using the ``Tuple`` class.
```
// SPL Code
tpl = (1, ('a', True));

// Python Code
tpl = Tuple(1, Tuple('a', True))
```