package com.suhorukov.commands;


import java.util.HashMap;
import java.util.Stack;

public class Sqrt implements Command{
    @Resource(type = "stack")
    public Stack<Double> stack;
    public void execute(String[] params){
        if (stack.size() == 0){
            System.out.println("на стеке нет элементов");
            return;
        }
        stack.push(Math.sqrt(stack.pop()));
    }
}
