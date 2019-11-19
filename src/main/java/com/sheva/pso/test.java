package com.sheva.pso;

import java.util.Random;

/**
 * @author Sheva
 * @data 2019/11/19  下午3:30
 * @Version 1.0
 */
public class test {
    public static Random random = new Random();
    public static void main(String[] args) {
        int cityNum = 30;
        int i = 0;
        while(i < 1000){
            System.out.println(random.nextInt(cityNum - 1) % cityNum);
            System.out.println(random.nextInt(cityNum - 1) % cityNum);
            i++;
        }

    }
}
