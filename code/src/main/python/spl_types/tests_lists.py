from spl_types.lists import Node, print_list


def test1():
    """
    Input:
    lst0 = 1 : 2 : 3 : []
    lst1 = lst0
    lst0.hd = 5
    :return:
    """
    # lst0 = LinkedList().add_node(3).add_node(2).add_node(1)
    lst0 = (Node(1) + (Node(2) + (Node(3) + Node())))
    print("lst0:", lst0)

    lst1 = lst0
    print("lst1:", lst1)

    print("Changing lst0")
    lst0[0] = 5

    print("lst0:", lst0)
    print("lst1", lst1)

    assert lst1[1][0] == 2
    assert lst0[1][0] == 2


def test2():
    a = Node(7) + Node()
    b = Node(10) + a

    assert a[0] == 7
    assert b[0] == 10
    assert b[1][0] == 7

    a[0] = 8
    b[0] = 11

    assert a[0] == 8
    assert b[0] == 11
    assert b[1][0] == 8


def test_infinite_lists():
    a = (Node(1) + (Node(2) + (Node(3) + Node())))
    n = 10
    res = a
    res[1][1][1] = a
    while (n > 0):
        print(res[0])
        res = res[1]
        n = (n - 1)

    # Warning this will never print
    print_list(res)
