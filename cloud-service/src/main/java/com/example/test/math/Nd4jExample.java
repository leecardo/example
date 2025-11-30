package com.example.test.math;

import org.nd4j.linalg.api.buffer.DataType;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

public class Nd4jExample {
    public static void main(String[] args) {
        INDArray x = Nd4j.zeros(3,4);

// 数组的轴数（维度）。
        int dimensions = x.rank();

// 数组的维数。每个维度的大小。
        long[] shape = x.shape();

// 元素的总数。
        long length = x.length();

// 数组元素的类型。
        DataType dt = x.dataType();

        double arr_2d[][]={{1.0,2.0,3.0},{4.0,5.0,6.0},{7.0,8.0,9.0}};
        INDArray x_2d = Nd4j.createFromArray(arr_2d);

        double arr_1d[]={1.0,2.0,3.0};
        INDArray  x_1d = Nd4j.createFromArray(arr_1d);

    }
}
