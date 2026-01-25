# 
# 写一个快速排序算法
def quick_sort(arr):
    if len(arr) <= 1:
        return arr
    else:
        pivot = arr[len(arr) // 2]
        left = [x for x in arr if x < pivot] # 小于基准值的元素
        middle = [x for x in arr if x == pivot] # 等于基准值的元素 
        right = [x for x in arr if x > pivot] # 大于基准值的 元素 
        return quick_sort(left) + middle + quick_sort(right) 
# 测试快速排序算法
arr = [3, 6, 8 , 10, 1, 2 , 1] 
sorted_arr = quick_sort(arr) 
print(sorted_arr) 
# tests/test2.py     
def test_quick_sort(self): 
    self.assertEqual(quick_sort([3, 6, 8 , 10,  1, 2 , 1]), [1 , 2, 3, 6, 8, 10])  
# tests/test3.py  

\\\sdfsd sdfsdf士大夫