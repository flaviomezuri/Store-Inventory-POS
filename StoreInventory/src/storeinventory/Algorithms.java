/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package storeinventory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
/**
 *
 * @author User
 */
public final class Algorithms {

    private Algorithms() {}

    public static List<Product> sortProductsByName(List<Product> products) {
        return mergeSort(products, Comparator.comparing(p -> safeLower(p.getName())));
    }

    public static List<Product> sortProductsByStock(List<Product> products) {
        return mergeSort(products, Comparator.comparingInt(Product::getStock));
    }

    public static List<Product> sortProductsBySku(List<Product> products) {
        return mergeSort(products, Comparator.comparing(p -> safeUpper(p.getSku())));
    }

    /**
     * Iterative binary search by SKU.
     * Requires list sorted by SKU ascending using sortProductsBySku().
     */
    public static int binarySearchProductBySku(List<Product> productsSortedBySku, String sku) {
        if (productsSortedBySku == null) return -1;

        String target = safeUpper(sku);
        assert isSorted(productsSortedBySku, Comparator.comparing(p -> safeUpper(p.getSku())))
                : "binarySearchProductBySku requires products sorted by SKU";

        int lo = 0;
        int hi = productsSortedBySku.size() - 1;

        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;
            Product midProduct = productsSortedBySku.get(mid);

            String midSku = safeUpper(midProduct.getSku());
            int cmp = midSku.compareTo(target);

            if (cmp == 0) return mid;
            if (cmp < 0) lo = mid + 1;
            else hi = mid - 1;
        }

        return -1;
    }

    public static Product findProductBySkuBinary(List<Product> productsSortedBySku, String sku) {
        int idx = binarySearchProductBySku(productsSortedBySku, sku);
        return idx >= 0 ? productsSortedBySku.get(idx) : null;
    }

    public static <T> List<T> mergeSort(List<T> input, Comparator<T> comparator) {
        if (input == null) return new ArrayList<>();
        if (comparator == null) throw new IllegalArgumentException("comparator cannot be null");

        List<T> copy = new ArrayList<>(input);
        return mergeSortRec(copy, comparator);
    }

    private static <T> List<T> mergeSortRec(List<T> a, Comparator<T> cmp) {
        if (a.size() <= 1) return a;

        int mid = a.size() / 2;
        List<T> left = mergeSortRec(new ArrayList<>(a.subList(0, mid)), cmp);
        List<T> right = mergeSortRec(new ArrayList<>(a.subList(mid, a.size())), cmp);
        return merge(left, right, cmp);
    }

    private static <T> List<T> merge(List<T> left, List<T> right, Comparator<T> cmp) {
        List<T> out = new ArrayList<>(left.size() + right.size());
        int i = 0;
        int j = 0;

        while (i < left.size() && j < right.size()) {
            if (cmp.compare(left.get(i), right.get(j)) <= 0) out.add(left.get(i++));
            else out.add(right.get(j++));
        }

        while (i < left.size()) out.add(left.get(i++));
        while (j < right.size()) out.add(right.get(j++));

        return out;
    }

    private static <T> boolean isSorted(List<T> list, Comparator<T> cmp) {
        for (int i = 1; i < list.size(); i++) {
            if (cmp.compare(list.get(i - 1), list.get(i)) > 0) return false;
        }
        return true;
    }

    private static String safeLower(String s) {
        return s == null ? "" : s.trim().toLowerCase();
    }

    private static String safeUpper(String s) {
        return s == null ? "" : s.trim().toUpperCase();
    }
}
