class Node:
    def __init__(self, value=None, next_node=None):
        self.value = value
        self.next_node: Node = next_node

    def __str__(self):
        return str(self.value) if self.value is not None else ""

    def traverse(self):
        return self.next_node


class LinkedList:
    """
    LinkedList implementation for Python
    """

    def __init__(self, head: Node=None):
        self.head: Node = head
        self._len = 0

    def add_node(self, value):
        """
        Adding one Node to the LinkedList

        Example:
        Say we have the following line:
            1 : 2 : 3 : 4 : []
        The cons in SPL is right associative, so:
            1 : (2 : (3 : (4 : [])))

        4 : []
        [] is the LinkedList, so we can set the head to the new node containing 4

        3 : (4 : [])
        Now we set the head to the new Node 3 and set the next value to the previous head

        :param value: Value to be added
        """
        self.head = Node(value, next_node=self.head)
        return self

    def __getitem__(self, index):
        """
        Getting an item from the linkedList
        index 0: lst.hd
        index 1: lst.tl
        :param index
        """
        if index == 0:
            return self.head.value
        elif index == 1:
            return LinkedList(head=self.head.traverse())
        else:
            raise IndexError("Only index 0 (.hd) or 1 (.tl) is allowed")

    def __setitem__(self, index, value):
        """
        Setting an item in the linkedList
        index 0: lst.hd
        index 1: lst.tl
        :param index:
        :param value:
        """
        if index == 0:
            self.head.value = value
        elif index == 1:
            self.head.next_node = value if isinstance(value, Node) else Node(value)
        else:
            raise IndexError("Only index 0 (.hd) or 1 (.tl) is allowed")

    def __str__(self):
        str_values = "" if self.head is None else str(self.head)

        tail = self.head.traverse()
        while tail is not None:
            str_values += ", " + str(tail)
            tail = tail.traverse()

        return "[{}]".format(str_values)


def test_list():
    """
    Input:
    lst0 = 1 : 2 : 3
    lst1 = lst0
    lst0.hd = 5
    :return:
    """
    lst0 = LinkedList().add_node(3).add_node(2).add_node(1)
    print("lst0:", lst0)

    lst1 = lst0
    print("lst1:", lst1)

    print("Changing lst0")
    lst0[0] = 5

    print("lst0:", lst0)
    print("lst1", lst1)

    assert lst1[1][0] == 2
    assert lst0[1][0] == 2


if __name__ == "__main__":
    test_list()
