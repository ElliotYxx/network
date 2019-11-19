package com.sheva.pso;

import org.apache.commons.lang3.ArrayUtils;

import java.util.*;

/**
 * @author Sheva
 * @data 2019/11/14  上午10:20
 * @Version 1.0
 * 微粒群算法
 */
public class Pso {
    private static Integer[] initCity = new Integer[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10,
            11, 12, 13, 14, 15, 16, 17, 18, 19, 20,
            21, 22, 23, 24, 25, 26, 27, 28, 29};


    private int cityNum = 30;

    private int bestFitness;

    public static int[][] distance;

    private HashMap<Integer, int[]> resultMap = new HashMap<Integer, int[]>();

    int[] bestRoad = new int[]{};
    /**
     * 最佳出现的代数
     */
    private int bestT;
    private double w;
    /**
     * 种群规模
     */
    private int scale;
    /**
     * 粒子群
     */
    private ArrayList<Entity> mUnits = new ArrayList<Entity>();
    /**
     * 每个例子的初始交换顺序
     */
    private ArrayList<ArrayList<SO>> listV = new ArrayList<ArrayList<SO>>();
    /**
     * 一颗粒子历代中出现最好的解
     */
    private HashMap<Integer, Entity> Pd = new HashMap<Integer, Entity>();

    /**
     * 整个粒子群经历过的最好的解，每个粒子都能记住自己搜索到的最好解
     */
    private Entity Pgd;


    private Random random = new Random();
    /**
     * 生成随机距离单位
     */
    private int min = 2;
    private int max = 100;
    /**
     * 迭代次数
     */
    private int MAX_G;

    public Pso(int g, int s, float w){
        this.MAX_G = g;
        this.scale = s;
        this.w = w;
    }

    private void initDistance(boolean isSymmetric){
        distance = new int[cityNum][cityNum];
        for (int i = 0; i < cityNum; i++) {
            if (isSymmetric) {
                for (int j = i; j < cityNum; j++) {
                    if (i == j){
                        distance[i][j] = 0;
                    }else{
                        //产生2-100的随机数
                        distance[i][j] = distance[j][i] = (int)(min + ((max - min) * random.nextDouble()));
                    }
                }
            }else{
                for (int j = 0; j < cityNum; j++) {
                    if (i == j){
                        distance[i][j] = Integer.MAX_VALUE;
                    }else{
                        distance[i][j] = min + ((max - min) * random.nextInt());
                    }
                }
            }
        }
        printDistance();
    }

    public static int getDistance(int i, int j){return distance[i][j];}

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
                System.out.printf("%5s", distance[i][j]);
            }
            System.out.println();
        }
    }





    /**
     * 生成若干随机解
     */
    private void initRoad(){
        for (int i = 0; i < scale; i++) {
            List<Integer>tempList = Arrays.asList(initCity);
            Collections.shuffle(tempList);
            Integer[] road = (Integer[]) tempList.toArray();
            Entity entity = new Entity(ArrayUtils.toPrimitive(road));
            mUnits.add(entity);
        }
        System.out.println("初始解为： ");
        for (int i = 0; i < mUnits.size(); i++) {
            mUnits.get(i).printRoad();
            System.out.println();
        }
        System.out.println();
    }

    private void initListV(){
        for (int i = 0; i < scale; i++) {
            ArrayList<SO> list = new ArrayList<SO>();
            int n = random.nextInt(cityNum - 1) % cityNum;
            for (int j = 0; j < n; j++) {
                int x = random.nextInt(cityNum - 1) % cityNum;
                int y = random.nextInt(cityNum - 1) % cityNum;
                while(x == y){
                    y = random.nextInt(cityNum - 1) % cityNum;
                }

                SO so = new SO(x, y);
                list.add(so);
            }
            listV.add(list);
        }
    }
    private void solve(){
        initDistance(true);
        initRoad();
        initListV();
        for (int i = 0; i < scale; i++) {
            Pd.put(i, mUnits.get(i));
        }
        Pgd = Pd.get(0);
        for (int i = 0; i < scale; i++) {
            if (Pgd.getFitness() > Pd.get(i).getFitness()){
                Pgd = Pd.get(i);
            }
        }
        System.out.println("初始化数据中最好的路径是：");
        Pgd.printRoad();
        System.out.println("路径代价为： " + Pgd.getFitness());

        System.out.println();
        evolution();
        System.out.println("-----------------最好的粒子群--------------");
        System.out.println("最佳代价出现迭代数：" + bestT);
        System.out.println("最佳长度：" + bestFitness);
        System.out.println("最佳路径：");
        for (int i = 0; i < bestRoad.length; i++) {
            System.out.print(bestRoad[i] + " ;");
        }

    }

    /**
     * 进化吧少年！
     */
    private void evolution(){
        bestFitness = Pd.get(0).calFitness();
        for (int t = 0; t < MAX_G; t++) {
            for (int k = 0; k < scale; k++) {
                ArrayList<SO> vii = new ArrayList<SO>();
                //更新公式
                int len = (int) (w*listV.get(k).size());
                for (int i = 0; i < len; i++) {
                    vii.add(listV.get(k).get(i));
                }
                //得出交换序列
                ArrayList<SO> a = minus(mUnits.get(k).getPath(), Pd.get(k).getPath());
                float ra = random.nextFloat();
                len = (int) (ra*a.size());
                for (int i = 0; i < len; i++) {
                    vii.add(a.get(i));
                }

                //和全局最优比较，得出交换序列
                ArrayList<SO> b = minus(mUnits.get(k).getPath(), Pgd.getPath());
                float rb = random.nextFloat();
                len = (int)(rb*b.size());
                for (int i = 0; i < len; i++) {
                    vii.add(b.get(i));
                }
                listV.remove(0);
                listV.add(vii);

                exchange(mUnits.get(k).getPath(), vii);
            }

            //更新适应度
            for (int i = 0; i < scale; i++) {
                mUnits.get(i).updateFitness();
                if (Pd.get(i).getFitness() > mUnits.get(i).getFitness()){
                    Pd.put(i, mUnits.get(i));
                }
                if(Pgd.getFitness() > Pd.get(i).getFitness()){
                    Pgd = Pd.get(i);
                }
            }
            if (Pgd.getFitness() < bestFitness){
                bestFitness = Pgd.getFitness();
                bestT = t;
                bestRoad = Pgd.getPath();
            }

//            if (t % 100 == 0){
                //resultMap.put(t, Pgd.getPath());
//                System.out.println("第" + t + "代的最佳代价为： " + Pgd.getFitness());
//                System.out.println("最佳路径为：");
//                Pgd.printRoad();
//                if (Pgd.getFitness() < bestFitness){
//                    bestFitness = Pgd.getFitness();
//                    bestT = t;
//                    bestRoad = resultMap.get(bestT);
//                }
//            }
        }
    }



    /**
     * 生成交换序列
     * @param a
     * @param b
     * @return
     */
    private ArrayList<SO> minus(int[] a, int[] b){
        int[] tmp = a.clone();
        ArrayList<SO> list = new ArrayList<SO>();
        int index = 0;
        for (int i = 0; i < b.length; i++) {
            if (tmp[i] != b[i]){
                //tmp中找到和b[i]相等的值，储存index
                for (int j = i + 1; j < tmp.length; j++) {
                    if (tmp[j] == b[i]){
                        index = j;
                        break;
                    }
                }
                SO so = new SO(i, index);
                list.add(so);
            }
        }
        return list;
    }

    /**
     * 交换更新
     * @param
     */
    private void exchange(int[] path, ArrayList<SO> vii){
        int tmp;
        for (SO so : vii) {
            tmp = path[so.getX()];
            path[so.getX()] = path[so.getY()];
            path[so.getY()] = tmp;
        }
    }
    public static void main(String[] args) {
        Pso pso = new Pso(1000, 100, 0.5f);
        pso.solve();
    }

}
