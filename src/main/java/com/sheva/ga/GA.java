package com.sheva.ga;

import java.util.Random;

/**
 * @author Sheva
 * @data 2019/11/4  下午2:29
 * @Version 1.0
 */
public class GA {
    /**
     * 初始化各参数（种群规模、迭代次数、个体选择方式、交叉概论、变异概率等）
     */
    private int entitySize = 100;
    private int n = 100;
    /**
     * 变异概率
     */
    private double pVart = 0.05;
    /**
     * 交配概率
     */
    private double pMate = 0.8;
    private int cityNum = 30;
    private CityDistance cityDistance;
    private Entity[] gaEntity;
    private Entity[] tempEntity;
    private Entity bestEntity;
    private double allAdapt;
    private double shortestRoad;
    private Table table;

    private void initDistance(){
        cityDistance = new CityDistance(cityNum, true);
    }

    /**
     * 初始化种群，随机产生100种路径，
     */
    private void initEntity(){
        gaEntity = new Entity[entitySize];
        tempEntity = new Entity[entitySize];
        for (int i = 0; i < entitySize; i++) {
            gaEntity[i] = new Entity(cityNum, "");
            System.out.println("初始种群" + i + ":" + gaEntity[i].printRoad());
        }
    }

    /**
     * 计算染色体适应度（每个染色体的路径总和）和幸存程度
     */
    private void calAdaptAndLuck(){
        allAdapt = 0.0;
        double allLuck = 0.0;
        for (int i = 0; i < entitySize; i++) {
            allAdapt += gaEntity[i].calAdapt();
        }
        System.out.println("路径总长度: " + allAdapt);
        for (int i = 0; i < entitySize; i++) {
            allLuck += gaEntity[i].calPreLuck(allAdapt);
        }
        System.out.println("总幸存度： " + allLuck);

        for (int i = 0; i < entitySize; i++) {
            gaEntity[i].calLuck(allLuck);
        }
        for (int i = 0; i < entitySize; i++) {
            System.out.println("更新幸存度: " + i + " : " + gaEntity[i].printRoad());
        }
        System.out.println();
    }

    /**
     * 轮盘赌，根据幸存度选择，重构解空间
     */
    private void chooseSample(){
        double p = 0.0;
        double allPreLuck = 0.0;
        for (int i = 0; i < entitySize; i++) {
            p = Math.random();
            allPreLuck = 0.0;
            tempEntity[i] = gaEntity[entitySize - 1];
            for (int j = 0; j < entitySize; j++) {
                allPreLuck += gaEntity[j].getpLuck();
                if (p <= allPreLuck){
                    tempEntity[i] = gaEntity[j];
                    break;
                }
            }
        }
        //更新解空间
        for (int i = 0; i < entitySize; i++) {
            //gaEntity[i] = null;
            gaEntity[i] = tempEntity[i];
//            System.out.println("样本选择后： " + i + ": " + gaEntity[i].printRoad());
        }
    }

    /**
     * 个体交叉,部分匹配法PMX
     */
    private void Mating(){
        //染色体交配概率
        double[] mating = new double[entitySize];
        //染色体可交配情况
        boolean[] matingFlag = new boolean[100];
        boolean findMating1 = false;
        Random random = new Random();
        table = new Table(cityNum);
        int mating1 = 0;
        int mating2 = -1;
        int position1, position2;
        int matingNum = 0;
        //随机产生交配概率，确定可以交配的染色体
        for (int i = 0; i < entitySize; i++) {
            mating[i] = Math.random();
            if (mating[i] < pMate){
                matingFlag[i] = true;
                matingNum++;
            }else{
                matingFlag[i] = false;
            }
        }
        matingNum = matingNum/2*2;
        for (int i = 0; i < matingNum / 2; i++) {
            findMating1 = false;
            position1 = random.nextInt(cityNum);
            position2 = random.nextInt(cityNum);
            if (position1 > position2){
                int t = position1;
                position1 = position2;
                position2 = t;
            }
            //寻找可以交配的染色体
            for (mating2++; mating2 < entitySize; mating2++){
                if (matingFlag[mating2]){
                    if (findMating1){
                        break;
                    }else{
                        mating1 = mating2;
                        findMating1 = true;
                    }
                }
            }
            //开始进行交配（部分匹配法）
            table.setTable(gaEntity[mating1], gaEntity[mating2], position1, position2);
            //进行交叉
            Entity tempEntity1 = new Entity(cityNum);
            Entity tempEntity2 = new Entity(cityNum);
            if (!gaEntity[mating1].checkDifferent(gaEntity[mating2])){
                tempEntity1 = gaEntity[mating1];
                tempEntity2 = gaEntity[mating2];
            }else{
                tempEntity1.setRoad(gaEntity[mating2], position1, position2);
                tempEntity2.setRoad(gaEntity[mating1], position1, position2);
                tempEntity1.modifyRoad(gaEntity[mating1], position1, position2, table, true);
                tempEntity2.modifyRoad(gaEntity[mating2], position1, position2, table, false);
            }
            gaEntity[mating1] = tempEntity1;
            gaEntity[mating2] = tempEntity2;
        }
    }

    /**
     * 个体变异
     */
    private void Variating(){
        //染色体的变异概率
        double[] rating = new double[entitySize];
        //染色体的可变异情况
        boolean[] ratingFlag = new boolean[entitySize];
        Random random = new Random();
        int p1, p2;
        //随机产生变异概率
        for (int i = 0; i < entitySize; i++) {
            rating[i] = Math.random();
            if (rating[i] < pVart){
                ratingFlag[i] = false;
            }else{
                ratingFlag[i] = true;
            }
        }

        for (int i = 0; i < entitySize; i++) {
            if (!ratingFlag[i]){
                p1 = 0;
                p2 = 0;
                while (p1 == p2){
                    p1 = random.nextInt(cityNum);
                    p2 = random.nextInt(cityNum);
                }
                gaEntity[i].exchange(p1, p2);
            }
        }
    }

    /**
     * 选择最佳路径
     */
    private void ChooseBestSolution(Boolean initBest){
        Double roadLength = Double.MAX_VALUE;
        int bestRoadIndex = 0;

        for (int i = 0; i < entitySize; i++) {
            if (roadLength > gaEntity[i].getEntityAdapt()){
                roadLength = gaEntity[i].getEntityAdapt();
                bestRoadIndex = i;
            }
        }
        System.out.println("本次迭代最好的路径为： " + gaEntity[bestRoadIndex].printRoad());
        System.out.println("本次迭代的最低代价为： " + roadLength);
        if (initBest){
            shortestRoad = roadLength;
            bestEntity = gaEntity[bestRoadIndex];
        }else if(shortestRoad > roadLength){
            shortestRoad = roadLength;
            bestEntity = gaEntity[bestRoadIndex];
        }
    }

    private void start(){
        initDistance();
        initEntity();
        boolean initBest = true;
        for (int i = 0; i < n; i++) {
            System.out.println("第" + i + "次迭代： ");
            calAdaptAndLuck();
            ChooseBestSolution(initBest);
            initBest = false;
            chooseSample();
            Mating();
            Variating();
        }

        calAdaptAndLuck();
        ChooseBestSolution(false);
        System.out.println("最好的路径： " + bestEntity.printRoad());
        System.out.println("最低的代价： " + shortestRoad);
    }

    public static void main(String[] args) {
        GA ga = new GA();
        ga.start();

    }





}
