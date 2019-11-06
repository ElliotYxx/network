package com.sheva.ga;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author Sheva
 * @data 2019/11/4  下午2:11
 * @Version 1.0
 * 遗传算法实体类
 */
public class Entity {
    private static Integer[] initRoad = new Integer[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10,
            11, 12, 13, 14, 15, 16, 17, 18, 19, 20,
            21, 22, 23, 24, 25, 26, 27, 28, 29};

    private int num;
    private List<Integer> roadList;
    private Integer[] road;
    /**
     * 个体适应度
     */
    private double entityAdapt = 0.0;
    /**
     * 幸存概率
     */
    private double pLuck = 0.0;

    Entity(int num, String s){
        this.num = num;
        roadList = new ArrayList<Integer>();
        road = new Integer[num];
        InitRoad();
    }

    Entity(int num){
        this.num = num;
        roadList = new ArrayList<Integer>();
        road = new Integer[num];
    }

    /**
     * 生成随机解
     */
    private void InitRoad(){
        roadList = Arrays.asList(initRoad);
        Collections.shuffle(roadList);
        road = (Integer[]) roadList.toArray();
    }


    public void setRoad(Entity parentEntity, int position1, int position2){
        roadList.clear();
        for (; position1 <= position2; position1++){
            road[position1] = parentEntity.getRoad(position1);
            roadList.add(road[position1]);
        }
    }

    public int getRoad(int i){
        return road[i];
    }

    public double getEntityAdapt(){
        return entityAdapt;
    }

    public double getpLuck(){
        return pLuck;
    }


    public String printRoad(){
        String p = "";
        for (int i = 0; i < num; i++) {
            p += "  " + road[i] + ";";
        }
        p += "  幸存概率: " + pLuck;
        return p;
    }

    public double calAdapt(){
        entityAdapt = 0.0;
        for (int i = 0; i < num - 1; i++) {
            entityAdapt += CityDistance.getDistance(road[i], road[i+1]);
        }
        entityAdapt += CityDistance.getDistance(road[num - 1], road[0]);
        return entityAdapt;
    }

    public double calPreLuck(double allAdapt){
        pLuck = 1 - entityAdapt / allAdapt;
        return pLuck;
    }

    /**
     * 归一化
     * @param allLuck  幸存度
     */
    public void calLuck(double allLuck){
        pLuck = pLuck / allLuck;
    }

    public void exchange(int p1, int p2){
        int temp = road[p1];
        road[p1] = road[p2];
        road[p2] = temp;
    }

    public void modifyRoad(Entity parent, int position1, int position2, Table table, boolean ifChild){
        int roadNum;
        boolean ifModify = false;
        if (ifChild){
            for (int i = 0; i < num; i++) {
                if (i >= position1 && i <= position2){
                    i = position2;
                    continue;
                }

                roadNum = parent.getRoad(i);
                ifModify = checkRoad(roadNum);
                while(ifModify){
                    roadNum = table.getRoadNum(false, roadNum);
                    ifModify = checkRoad(roadNum);
                }
                road[i] = roadNum;
                roadList.add(roadNum);
            }
        }else{
            for (int i = 0; i < num; i++) {
                if (i >= position1 && i <= position2){
                    i = position2;
                    continue;
                }
                roadNum = parent.getRoad(i);
                ifModify = checkRoad(roadNum);
                while (ifModify){
                    roadNum = table.getRoadNum(true, roadNum);
                    ifModify = checkRoad(roadNum);
                }
                road[i] = roadNum;
                roadList.add(roadNum);
            }
        }
    }
    private boolean checkRoad(int roadNum){
        if (roadList.contains(roadNum)){
            return true;
        }
        return false;
    }

    public boolean checkDifferent(Entity entity){
        for (int i = 0; i < num; i++) {
            if (road[i] == entity.getRoad(i)){
                continue;
            }else{
                return true;
            }
        }
        return true;
    }


}
