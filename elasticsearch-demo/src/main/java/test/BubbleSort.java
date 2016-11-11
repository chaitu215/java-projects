package test;

import java.util.Arrays;

/**
 * 作者：wangsy
 * 日期：2016/7/27 17:55
 * 描述：
 */
public class BubbleSort {

    /**
     * 对r数组中的无序数据进行排序
     */
    public void bubbleSort(int[] r) {
        boolean exchange = false;
        int i, j;
        int n = r.length - 1;
        System.out.println("最多进行" + n + "趟排序");
        for (i = 0; i < n; i++) {
            //内循环对当前无序区r[i..n]自下向上扫描
            for (j = 0; j < n - i; j++) {
                if (r[j + 1] < r[j]) {
                    int temp = r[j];
                    r[j] = r[j + 1];
                    r[j + 1] = temp;
                    exchange = true;
                }
            }
            //本趟排序未发生交换，提前终止算法
            if (!exchange) {
                break;
            }
        }
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        int[] mySeq = {11, 888, 33, 129, 66, 4, 34, 123, 345, 3, 1};
        BubbleSort bs = new BubbleSort();
        bs.bubbleSort(mySeq);
        System.out.println(Arrays.toString(mySeq));
    }

}
