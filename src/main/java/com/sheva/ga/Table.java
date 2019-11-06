package com.sheva.ga;

/**
 * @author Sheva
 * @data 2019/11/4  下午7:14
 * @Version 1.0
 * 解决路径重复问题
 */
public class Table {
    private int[] table1;
    private int[] table2;
    Entity entity1, entity2;
    int p1, p2;
    int num;

    Table(int cityNum){
        this.num = cityNum;
        table1 = new int[cityNum];
        table2 = new int[cityNum];
    }

    private void ClearPP(){
        table1 = null;
        table2 = null;
        table1 = new int[num];
        table2 = new int[num];
    }

    public void setTable(Entity ga1, Entity ga2, int position1, int position2){
        ClearPP();
        entity1 = ga1;
        entity2 = ga2;
        p1 = position1;
        p2 = position2;

        for (; p1 <= p2; p1++){
            table1[entity1.getRoad(p1)] = entity2.getRoad(p1);
            table2[entity2.getRoad(p1)] = entity1.getRoad(p1);
        }
    }

    public int getRoadNum(boolean ifParent1, int roadIndex){
        if (ifParent1){
            return table1[roadIndex];
        }else{
            return table2[roadIndex];
        }
    }
}
