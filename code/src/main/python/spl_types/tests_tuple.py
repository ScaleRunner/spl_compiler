from spl_types.tuple import Tuple
from spl_types.lists import Node

def test1():
    a = Tuple(5, 3)

    assert a[0] == 5
    assert a[1] == 3
    assert str(a) == "(5, 3)"


def test2():
    a = Tuple(Tuple(5, 7), 3)

    assert a[0][0] == 5

def test3():
    a = Tuple(5, 3)
    b0 = Tuple(10, True)
    b = Tuple(a[0] + 10, 7 < 3)
    c = Tuple(Tuple(5, 3), 42)
    d = Tuple(Tuple(True, False), a)
    e = Tuple(Node(1) + Node(2) + Node(3) + Node(), 7)

    f = a[0]
    g = c[0][1]
    h = d[0][1]
    i = e[0][0]

    assert a[0] == 5
    assert a[1] == 3

    assert b[0] == 15
    assert b[1] == False

    assert c[0][0] == 5
    assert c[0][1] == 3
    assert c[1] == 42

    assert d[0][0] == True
    assert d[0][1] == False
    assert d[1][0] == 5
    assert d[1][1] == 3

    assert e[0][0] == 1

