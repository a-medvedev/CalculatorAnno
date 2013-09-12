package com.suhorukov.calculator;

import com.suhorukov.commands.*;

import java.io.*;
import java.lang.reflect.Field;
import java.util.Properties;
import java.util.Stack;
import java.util.Scanner;
import java.util.HashMap;

public class Calculator {
    private static Scanner input;

    public static void main(String[] args) throws IllegalAccessException {
        HashMap<String, Command> cmds = new HashMap<String, Command>();
        Properties prop = new Properties();

        //Загрузка команд из файла свойств
        boolean isLoaded = loadCommands(prop, cmds, "com1m.prop");

        //Если загрузка из файла не удалась, грузим стандартный набор команд
        if (!isLoaded){
            loadDefaults(cmds);
            System.out.println("Создание команд из класса не произведено, загружен стандартный набор команд.");
        }

        boolean isInteractive = false;//флаг интерактивного режима
        if (args.length > 0){
            try {
                input = new Scanner(new FileInputStream(args[0]));
                System.out.println("Обрабатывается файл " + args[0]);
            } catch (FileNotFoundException e) {
                System.out.println("Файл с инструкциями не найден. переход в интерактивный режим.");
                isInteractive = true;
            }
        }

        if (((args.length > 0) && (isInteractive == true)) ||  //аргументы переданы, но 1-ый аргумент не имя файла
            (args.length == 0)){                               //аргументы не переданы
            input = new Scanner(System.in);
            System.out.println("Введите команды, завершая каждую нажатием <Enter>.\nВведите пустую строку для завершения работы.");
            System.out.print(">>> ");
        }

        Stack<Double> stack = new Stack<Double>();
        HashMap<String, Double> vars = new HashMap<String, Double>();
        while (input.hasNextLine()){

            String cmd = input.nextLine();
            String[] splitCmd = cmd.split(" ");

            //Если в строке содержится комментарий
            if (splitCmd[0].startsWith("#")){
                continue;
            }

            //Если введена команда выхода
            if (splitCmd[0].toUpperCase().equals("QUIT")){
                break;
            }

            Command c = cmds.get(splitCmd[0].toUpperCase());
            Class cls = null;
            try {
                cls = c.getClass();
            } catch (NullPointerException e){ //введена пустая или некорректная строка
                System.out.print("Введена неправильная команда!\n>>>");
                continue;
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

            //приглашение, если вводим команды с клавиатуры
            if (isInteractive){
                System.out.print(">>> ");
            }

        }
        System.out.println("Программа завершена");
    }

    //загрузка файла свойств и создание на его основе HashMap с командами
    private static boolean loadCommands(Properties properties, HashMap commands, String resourceName) {

        try (InputStream in = Calculator.class.getResourceAsStream(resourceName)) {
            properties.load(in);
        } catch (IOException e) {
            return false;
        } catch (NullPointerException e) { //при имени несуществующего файла: in = null
            return false;
        }

        for (String propertyName : properties.stringPropertyNames()){
            try {
                Command command = (Command)(Class.forName(properties.getProperty(propertyName))).newInstance();
                commands.put(propertyName, command);
            } catch (ClassNotFoundException e) {
                System.out.println("Класс не определен.");
                return false;
            } catch (InstantiationException e) {
                System.out.println("Невозможно создать экземпляр класса.");
                return false;
            } catch (IllegalAccessException e) {
                return false;
            }
        }

        return true;
    }

    //Загрузка списка команд по умолчанию, вне зависимости от содержимого property-файла
    private static void loadDefaults(HashMap commands){
        commands.put("DEF", new Define());
        commands.put("PSH", new Push());
        commands.put("POP", new Pop());
        commands.put("ADD", new Add());
        commands.put("SUB", new Sub());
        commands.put("MUL", new Mul());
        commands.put("DIV", new Div());
        commands.put("SQR", new Sqrt());
        commands.put("PRN", new Print());
    }
}
