package com.lin.learn.eureka;

public class MergeSort {

    public static void mergeSort(int[] arr, int low, int high) {
        int mid = (low + high) / 2;
        mergeSort(arr, low, mid);
        mergeSort(arr, mid + 1, high);
        merge(arr, low, high, mid);
    }

    public static void merge(int[] arr, int low, int high, int mid) {
        int temp[] = new int[high - low + 1];
        int start = low;
        int secondStart = mid + 1;
        int i = 0;
        while (start <= mid && secondStart <= high) {
            temp[i++] = arr[start] < arr[secondStart] ? arr[start++] : arr[secondStart++];
        }

        while (start <= mid) {
            temp[i++] = arr[start++];
        }

        while (secondStart <= high) {
            temp[i++] = arr[secondStart++];
        }

        for (i = 0; i < temp.length; i++) {
            arr[low + i] = temp[i];
        }
    }
}
