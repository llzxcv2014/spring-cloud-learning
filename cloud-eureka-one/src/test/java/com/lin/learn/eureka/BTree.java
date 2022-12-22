package com.lin.learn.eureka;

public class BTree<Key extends Comparable<Key>, Value> {

    private static final int M = 4;

    private Node root;

    //树的高度  最底层为0 表示外部节点    根具有最大高度
    private int height;

    //键值对总数
    private int n;

    private static final class Node {
        private int m;
        private Entry[] children = new Entry[M];
        private Node(int k) {
            m = k;
        }
    }

    private static class Entry {
        private Comparable key;

        private final Object val;

        private Node next;

        public Entry(Comparable key, Object val, Node next) {
            this.key = key;
            this.val = val;
            this.next = next;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("Entry [key=");
            builder.append(key);
            builder.append("]");
            return builder.toString();
        }
    }

    public Value get(Key key) {
        return search(root, key, height);
    }

    private Value search(Node x, Key key, int ht) {
        Entry[] children = x.children;

        if (ht == 0) {
            for (int j = 0; j < x.m; j++) {
                if (key.equals(x.children[j].key)) {
                    return (Value) children[j].val;
                }
            }
//            else{
//                for(int j=0; j<x.m; j++){
//                    //最后一个节点  或者 插入值小于某个孩子节点值
//                    if(j+1==x.m || less(key, x.children[j+1].key))
//                        return search(x.children[j].next, key, ht-1);
//                }
//            }
            return null;
        }
        return null;
    }
}
