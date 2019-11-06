package com.sheva.ga;

import java.util.Random;

/**
 * @author Sheva
 * @data 2019/11/4  下午1:51
 * @Version 1.0
 * 初始化城市距离类
 */
public class CityDistance {
    private static double[][] distance;
    private int num;
    Random random = new Random();
    private int min = 2;
    private int max = 100;

    public CityDistance(int num, boolean isSymmetric){
        this.num = num;
        initDistance(isSymmetric);
    }

    private void initDistance(boolean isSymmetric){
        distance = new double[num][num];
        for (int i = 0; i < num; i++) {
            if (isSymmetric) {
                for (int j = i; j < num; j++) {
                    if (i == j){
                        distance[i][j] = 0;
                    }else{
                        //产生2-100的随机数
                        distance[i][j] = distance[j][i] = min + ((max - min) * random.nextDouble());
                    }
                }
            }else{
                for (int j = 0; j < num; j++) {
                    if (i == j){
                        distance[i][j] = Double.MAX_VALUE;
                    }else{
                        distance[i][j] = min + ((max - min) * random.nextDouble());
                    }
                }
            }
        }
        printDistance();

    }

    public static double getDistance(int i, int j){return distance[i][j];}

    /**
     * 格式化输出
     */
    private void printDistance(){
        System.out.printf("%5s", "");
        for (int i = 0; i < num; i++) {
            System.out.printf("%5s", i);
        }
        System.out.println();

        for (int i = 0; i < num; i++) {
            System.out.printf("%5s", i);
            for (int j = 0; j < num; j++) {
                System.out.printf("%5s", (int)distance[i][j]);
            }
            System.out.println();
        }
    }

    public static void main(String[] args) {
        new CityDistance(30, true);
    }
}
