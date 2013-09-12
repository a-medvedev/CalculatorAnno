package com.suhorukov.commands;


import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.HashMap;
import java.util.Stack;

public class Define implements Command{

    @Resource(type = "vars")
    public HashMap<String, Double> vars;

    public void execute(String[] params){

        vars.put(params[1], Double.valueOf(params[2]));
    }
}
