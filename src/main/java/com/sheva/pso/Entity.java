package com.sheva.pso;

import static com.sheva.pso.Pso.getDistance;

/**
 * @author Sheva
 * @date 2019/11/19  下午4:04
 * @Version 1.0
 */
public class Entity {
    private int[] mPath;
    private int mFitness;

    public Entity(int[] path){
        mPath = path;
        mFitness = calFitness();
    }

    /**
     * 输出路径
     */
    public void printRoad(){
        for (int i = 0; i < mPath.length; i++) {
            System.out.print(mPath[i] + " ;");
        }
        System.out.println();
    }

    /**
     * 更新适应度
     */
    public void updateFitness(){
        this.mFitness  =calFitness();
    }

    public int[] getPath(){
        return mPath;
    }

    /**
     * 计算适应度
     * @return
     */
    public int calFitness(){
        int distance = 0;
        int n = mPath.length;
        for (int i = 0; i < n-1; i++) {
            distance += getDistance(mPath[i], mPath[i+1]);
        }
        distance += getDistance(mPath[n - 1], mPath[0]);
        return distance;
    }

    public int getFitness(){
        return mFitness;
    }



}
