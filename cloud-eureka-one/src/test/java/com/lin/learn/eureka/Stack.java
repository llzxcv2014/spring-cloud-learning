package com.lin.learn.eureka;

import java.util.ArrayDeque;
import java.util.Deque;

public class Stack<Value> {

    private Deque<Value> deque;

    public Stack() {
        deque = new ArrayDeque<>();
    }

    public Stack(int size) {
        deque = new ArrayDeque<>(size);
    }

    public void push(Value value) {
        deque.push(value);
    }

    public void pop() {
        deque.pop();
    }

    public Value peek() {
        return deque.peek();
    }

    public boolean isEmpty() {
        return deque.isEmpty();
    }
}
