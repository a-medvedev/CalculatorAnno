package com.suhorukov.calculator;

import com.suhorukov.commands.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Properties;
import java.util.Stack;
import java.util.Scanner;
import java.util.HashMap;

public class Calculator {
    private static Scanner input;

    public static void main(String[] args) throws IllegalAccessException {
        HashMap<String, Command> cmds = new HashMap<String, Command>();

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
            System.out.println("Невозможно загрузить файл свойств");
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

        boolean isInteractive = false;//флаг для интерактивной подсказки
        if (args.length > 0){
            try {
                input = new Scanner(new FileInputStream(args[0]));
                System.out.println("Обрабатывается файл " + args[0]);
            } catch (FileNotFoundException e) {
                System.out.println("Файл с инструкциями не найден.");
                System.exit(1);
            }
        } else {
            isInteractive = true;
            input = new Scanner(System.in);
            System.out.println("Введите команды, завершая каждую нажатием <Enter>.\nВведите пустую строку для завершения работы.");
            System.out.print(">>> ");
        }

        Stack<Double> stack = new Stack<Double>();
        HashMap<String, Double> vars = new HashMap<String, Double>();
        while (input.hasNextLine()){
            //приглашение, если вводим команды с клавиатуры
            if (isInteractive){
                System.out.print(">>> ");
            }

            String cmd = input.nextLine();
            String[] splitCmd = cmd.split(" ");

            //Если в строке содержится комментарий
            if (splitCmd[0].startsWith("#")){
                continue;
            }

            Command c = cmds.get(splitCmd[0].toUpperCase());
            Class cls = null;
            try {
                cls = c.getClass();
            } catch (NullPointerException e){
                System.out.println("Ввод завершен.");
                System.exit(0);
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
        System.out.println("Программа завершена");
    }
}
