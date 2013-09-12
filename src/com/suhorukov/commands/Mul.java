package com.suhorukov.commands;


import java.util.HashMap;
import java.util.Stack;

public class Mul implements Command{
    @Resource(type = "stack")
    public Stack<Double> stack;
    public void execute(String[] params){
        if (stack.size() < 2){
            System.out.println("на стеке меньше 2-х элементов");
            return;
        }
        stack.push(stack.pop() * stack.pop());
    }
}
