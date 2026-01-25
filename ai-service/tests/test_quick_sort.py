# tests/test_quick_sort.py
import unittest
# 假设 quick_sort 函数定义在 src/quick_sort.py 中
from src.quick_sort import quick_sort  # 或使用 relative import

class TestQuickSort(unittest.TestCase):
    def test_quick_sort(self):
        self.assertEqual(quick_sort([3, 6, 8, 10, 1, 2, 1]), [1, 1, 2, 3, 6, 8, 10])
        self.assertEqual(quick_sort([]), [])
        self.assertEqual(quick_sort([5]), [5])
        self.assertEqual(quick_sort([2, 2, 2, 2]), [2, 2, 2, 2])
        self.assertEqual(quick_sort([5, 4, 3, 2, 1]), [1, 2, 3, 4, 5])
        self.assertEqual(quick_sort([10, 9, 8, 7, 6, 5, 4, 3, 2, 1]), [1, 2, 3, 4, 5, 6, 7, 8, 9, 10])
        self.assertEqual(quick_sort([1, 3, 2, 5, 4, 6]), [1, 2, 3, 4, 5, 6])
        self.assertEqual(quick_sort([5, 5, 5, 5, 5]), [5, 5, 5, 5, 5])
        self.assertEqual(quick_sort([1, 2, 3, 4, 5, 6, 7, 8, 9, 10]), [1, 2, 3, 4, 5, 6, 7, 8, 9, 10])
        self.assertEqual(quick_sort([10, 1, 2, 3, 4, 5, 6, 7, 8, 9]), [1, 2, 3, 4, 5, 6, 7, 8, 9, 10])

if __name__ == '__main__':
    unittest.main()