package com.suhorukov.commands;


import java.util.HashMap;
import java.util.Stack;

public class Pop implements Command{
    @Resource(type = "vars")
    public HashMap vars;
    @Resource(type = "stack")
    public Stack<Double> stack;
    public void execute(String[] params){
        if (stack.size() == 0){
            System.out.println("на стеке нет элементов");
            return;
        }
        vars.put(params[1], stack.pop());
    }
}
