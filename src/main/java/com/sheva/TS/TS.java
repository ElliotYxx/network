package com.sheva.TS;


import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * @author Sheva
 * @data 2019/11/5  下午3:47
 * @Version 1.0
 * 禁忌搜索算法
 */
public class TS {

    private static Integer[] initCity = new Integer[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10,
            11, 12, 13, 14, 15, 16, 17, 18, 19, 20,
            21, 22, 23, 24, 25, 26, 27, 28, 29};

    private List<Integer> roadList;

    private int cityNum = 30;
    /**
     * 禁忌长度
     */
    private int tabuSize;
    private int N;
    private double[][] distance;
    /**
     * 当前代数
     */
    private int t;
    /**
     * 最佳出现的代数
     */
    private int bestT;
    private int[][] tabu;
    private Integer[] road;
    private Integer[] bestRoad;
    private Integer[] currentRoad;
    private Integer[] tempRoad;
    private double tempLength;
    private double bestLength;
    private double currentLength;


    private Random random;
    /**
     * 生成随机距离单位
     */
    private int min = 2;
    private int max = 100;


    /**
     * 迭代次数
     */
    private int MAX_G = 100;

    public TS(int cityNum, int MAX_G, int N, int tabuSize){
        this.cityNum = cityNum;
        this.MAX_G = MAX_G;
        this.N = N;
        this.tabuSize = tabuSize;

    }
    
    private void init(){

        road = new Integer[cityNum];
        bestRoad = new Integer[cityNum];
        bestLength = Double.MAX_VALUE;
        currentRoad = new Integer[cityNum];
        currentLength = Double.MAX_VALUE;
        tempRoad = new Integer[cityNum];
        tempLength = Double.MAX_VALUE;
        tabu = new int[tabuSize][cityNum];
        bestT = 0;
        t = 0;
        random = new Random();
    }

    private void initDistance(boolean isSymmetric){
        distance = new double[cityNum][cityNum];
        for (int i = 0; i < cityNum; i++) {
            if (isSymmetric) {
                for (int j = i; j < cityNum; j++) {
                    if (i == j){
                        distance[i][j] = 0;
                    }else{
                        //产生2-100的随机数
                        distance[i][j] = distance[j][i] = min + ((max - min) * random.nextDouble());
                    }
                }
            }else{
                for (int j = 0; j < cityNum; j++) {
                    if (i == j){
                        distance[i][j] = Integer.MAX_VALUE;
                    }else{
                        distance[i][j] = min + ((max - min) * random.nextDouble());
                    }
                }
            }
        }
        printDistance();
    }

    /**
     * 格式化输出
     */
    private void printDistance(){
        System.out.printf("%5s", "");
        for (int i = 0; i < cityNum; i++) {
            System.out.printf("%5s", i);
        }
        System.out.println();

        for (int i = 0; i < cityNum; i++) {
            System.out.printf("%5s", i);
            for (int j = 0; j < cityNum; j++) {
                System.out.printf("%5s", (int)distance[i][j]);
            }
            System.out.println();
        }
    }

    private double calDistance(Integer[] road){
        double distance = 0;
        for (int i = 0; i < cityNum - 1; i++) {
            distance += getDistance(road[i], road[i+1]);
        }
        distance += getDistance(road[cityNum - 1], road[0]);
        return distance;
    }

    private double getDistance(int i, int j){return distance[i][j];}

    /**
     * 随机产生一个合法的初始解
     */
    /**
     * 生成随机解
     */
    private void initRoad(){
        roadList = Arrays.asList(initCity);
        Collections.shuffle(roadList);
        road = (Integer[]) roadList.toArray();
        System.out.println("初始解为： ");
        for (int i = 0; i < road.length; i++) {
            System.out.print(road[i] + ", ");
        }
        System.out.println();
    }

    /**
     * 邻域交换
     * @param
     * @return 1 存在  2 不存在
     */
    private void neighber(Integer[] road, Integer[] tempRoad){
        int temp;
        int random1, random2;

        for (int j = 0; j < cityNum; j++) {
            tempRoad[j] = road[j];
        }
        random1 = random.nextInt(cityNum);
        random2 = random.nextInt(cityNum);
        while (random1 == random2){
            random2 = random.nextInt(cityNum);
        }

        temp = tempRoad[random1];
        tempRoad[random1] = tempRoad[random2];
        tempRoad[random2] = temp;
    }

    /**
     * 判断是否在禁忌表中
     * @param tempRoad
     * @return
     */
    public int judge(Integer[] tempRoad){
        int flag = 0;
        int i, j;
        for (i = 0; i < tabuSize; i++) {
            flag = 0;
            for (j = 0; j < cityNum; j++) {
                if (tempRoad[j] != tabu[i][j]){
                    flag = 1;
                    break;
                }
            }
            if (flag == 0){
                break;
            }
        }
        if (i == tabuSize){
            return 0;
        }else{
            return 1;
        }
    }

    private void solveTabu(Integer[] tempRoad){
        //删除禁忌表第一个编码，后面编码往前移动
        for (int i = 0; i < tabuSize - 1; i++) {
            for (int j = 0; j < cityNum; j++) {
                tabu[i][j] = tabu[i + 1][j];
            }
        }
        //加入新的编码
        for (int i = 0; i < cityNum; i++) {
            tabu[tabuSize - 1][i] = tempRoad[i];
        }
    }

    private void copyRoad(Integer[] road1, Integer[] road2){
        for (int i = 0; i < cityNum; i++) {
            road2[i] = road1[i];
        }
    }

    private void start(){
        int nn;
        initDistance(true);
        initRoad();
        copyRoad(road, bestRoad);
        bestLength = calDistance(road);

        while (t < MAX_G){
            nn = 0;
            currentLength = Double.MAX_VALUE;
            while(nn < N){
                //得到邻域代码
                neighber(road, tempRoad);
                if (judge(tempRoad) == 0){
                    tempLength = calDistance(tempRoad);
                    if (tempLength < currentLength){
                        copyRoad(tempRoad, currentRoad);
                        currentLength = tempLength;
                    }
                    nn++;
                }
            }
            if (currentLength < bestLength){
                bestT = t;
                copyRoad(currentRoad, bestRoad);
                bestLength = currentLength;
            }
            copyRoad(currentRoad, road);
            solveTabu(currentRoad);
            t++;
        }

        System.out.println("最佳长度出现迭代次数：" + bestT);
        System.out.println("最佳的长度:" + bestLength);
        System.out.println("最佳路径: ");
        printRoad(bestRoad);


    }

    private void printRoad(Integer[] road){
        for (int i = 0; i < cityNum; i++) {
            System.out.print(road[i] + ", ");
        }
    }



    public static void main(String[] args) {

        TS ts = new TS(30, 100, 10, 20);
        ts.init();
        ts.start();
    }

}
