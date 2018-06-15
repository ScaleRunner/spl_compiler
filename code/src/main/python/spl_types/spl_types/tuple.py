class Tuple:
    def __init__(self, left, right):
        self.left = left
        self.right = right

    def __getitem__(self, index):
        """
        Getting an item from the Tuple
        index 0: tuple.fst
        index 1: tuple.snd
        :param index
        """
        if index == 0:
            return self.left
        elif index == 1:
            return self.right
        else:
            raise IndexError("Only index 0 (.fst) or 1 (.snd) is allowed")

    def __setitem__(self, index, value):
        """
        Setting an item in the tuple
        index 0: tuple.fst
        index 1: tuple.snd
        :param index:
        :param value:
        """
        if index == 0:
            self.left = value
        elif index == 1:
            self.right = value
        else:
            raise IndexError("Only index 0 (.fst) or 1 (.snd) is allowed")

    def __str__(self):
        return "({}, {})".format(self.left, self.right)
