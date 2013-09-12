package com.suhorukov.calculator;

import com.suhorukov.commands.*;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Properties;
import java.util.Scanner;
import java.util.Stack;


public class TestCalculator {
    HashMap<String, Command> cmds = new HashMap<String, Command>();
    Stack<Double> stack;
    HashMap<String, Double> vars = new HashMap<String, Double>();
    Scanner codewalker;

    public TestCalculator() throws IllegalAccessException {
        FileReader fReader = null;
        try {
            fReader = new FileReader("./src/com/suhorukov/calculator/comm.prop");
        } catch (FileNotFoundException e) {
            System.out.println("Файл свойств не найден");
            System.exit(1);
        }

        Properties prop = new Properties();
        try {
            prop.load(fReader);
        } catch (IOException e) {
            System.out.println("Невозможно загрузить файл свойств.");
            System.exit(2);
        }

        for (String propertyName : prop.stringPropertyNames()){
            try {
                Command command = (Command)(Class.forName(prop.getProperty(propertyName))).newInstance();
                cmds.put(propertyName, command);
            } catch (ClassNotFoundException e) {
                System.out.println("Класс не определен. Прерывание.");
                System.exit(4);
            } catch (InstantiationException e) {
                System.out.println("Невозможно создать экземпляр класса. Прерывание.");
                System.exit(4);
            }
        }
    }

    @Test
    public void FirstRoot() throws IllegalAccessException {
        stack  = new Stack<Double>();
        StringBuilder code = new StringBuilder();
        code.append("def a 2.0\n");
        code.append("def b 3.0\n");
        code.append("def c 1.0\n");
        code.append("psh 2\n");
        code.append("psh a\n");
        code.append("mul\n");
        code.append("psh 4\n");
        code.append("psh a\n");
        code.append("mul\n");
        code.append("psh c\n");
        code.append("mul\n");
        code.append("psh b\n");
        code.append("psh b\n");
        code.append("mul\n");
        code.append("sub\n");
        code.append("sqr\n");
        code.append("psh b\n");
        code.append("psh 0\n");
        code.append("sub\n");
        code.append("add\n");
        code.append("div");

        //используем строку с переносами в качестве источника для сканера
        codewalker = new Scanner(code.toString());
        while (codewalker.hasNext()){
            String cmd = codewalker.nextLine();
            String[] splitCmd = cmd.split(" ");
            Command c = cmds.get(splitCmd[0].toUpperCase());
            Class cls = null;
            cls = c.getClass();


            Field[] fields = cls.getDeclaredFields();
            for (Field f : fields){
                Resource r = f.getAnnotation(Resource.class);
                if (r.type().equals("stack")){
                    f.set(c, stack);
                }
                if (r.type().equals("vars")){
                    f.set(c, vars);
                }
            }
            c.execute(splitCmd);
        }

        assert (stack.peek() == -0.5);
    }

    @Test
    public void SecondRoot() throws IllegalAccessException {
        stack  = new Stack<Double>();
        StringBuilder code = new StringBuilder();
        code.append("def a 2.0\n");
        code.append("def b 3.0\n");
        code.append("def c 1.0\n");
        code.append("psh 2\n");
        code.append("psh a\n");
        code.append("mul\n");
        code.append("psh 4\n");
        code.append("psh a\n");
        code.append("mul\n");
        code.append("psh c\n");
        code.append("mul\n");
        code.append("psh b\n");
        code.append("psh b\n");
        code.append("mul\n");
        code.append("sub\n");
        code.append("sqr\n");
        code.append("psh b\n");
        code.append("psh 0\n");
        code.append("sub\n");
        code.append("sub\n");
        code.append("div\n");

        codewalker = new Scanner(code.toString());
        while (codewalker.hasNext()){
            String cmd = codewalker.nextLine();
            String[] splitCmd = cmd.split(" ");
            Command c = cmds.get(splitCmd[0].toUpperCase());
            Class cls = null;
            try {
                cls = c.getClass();
            } catch (NullPointerException e){
                System.out.println("2 Ввод завершен.");
                //System.exit(0);
            }

            Field[] fields = cls.getDeclaredFields();
            for (Field f : fields){
                Resource r = f.getAnnotation(Resource.class);
                if (r.type().equals("stack")){
                    f.set(c, stack);
                }
                if (r.type().equals("vars")){
                    f.set(c, vars);
                }
            }
            c.execute(splitCmd);
        }

        assert (stack.peek() == -1.0);
    }
}
