class Node:
    def __init__(self, value=None, next_node=None):
        self.value = value
        self.next_node: Node = next_node

    def traverse(self):
        return self.next_node

    def __add__(self, right):
        self.next_node = right
        return self

    def __getitem__(self, index):
        """
        Getting an item from the linkedList
        index 0: lst.hd
        index 1: lst.tl
        :param index
        """
        if index == 0:
            return self.value
        elif index == 1:
            return self.next_node
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
            self.value = value
        elif index == 1:
            self.next_node = value if isinstance(value, Node) else Node(value)
        else:
            raise IndexError("Only index 0 (.hd) or 1 (.tl) is allowed")

    def __str__(self):
        return "" if self.value is None else str(self.value)


def print_list(node: Node):
    str_values = str(node.value)

    tail = node.traverse()
    while tail is not None:
        str_values += ", " + str(tail)
        tail = tail.traverse()

    print("[{}]".format(str_values))
