package com.testWork;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class Main {
    private static String url="";
    private static Integer requestPerSecond=0;
    private static Integer duration=0;

    public static void main(String[] args) throws Exception {
        if(!config()){
            System.err.println("check or delete config.ini");
            return;
        }
        Request request = new Request(url);
        ExecutorService threadPool = Executors.newFixedThreadPool(8);
        List<Future<Boolean>> resultList = new ArrayList<>();
        Long startTime=System.currentTimeMillis();

        startStressTest(resultList, request, threadPool);
        //Общее время отправки всех запросов за весь временной интервал
        System.err.println(System.currentTimeMillis()-startTime);

        if(!threadPool.isShutdown()) {
            threadPool.shutdown();
        }
        countSuccessRequest(resultList);
        System.out.println("end");
    }

    private static boolean config(){
        boolean result=false;
        ReadConfig readConfig = new ReadConfig();
        if(readConfig.getConfigFile()){
            try {
                url = readConfig.getResultByName("url", url.getClass());
                requestPerSecond = readConfig.getResultByName("requestPerSecond", requestPerSecond.getClass());
                duration = readConfig.getResultByName("duration", Integer.class);
            }catch (IllegalArgumentException e){
                System.err.println(e.getStackTrace()[0]);
                result = false;
            }
        }
        if(url!=null && !url.isEmpty()
                && requestPerSecond!=null && requestPerSecond>0
                && duration!=null && duration>0){
            result=true;
            System.out.println("URL:"+url+"\r\n"+
                    " requestPerSecond:" + requestPerSecond+"\r\n"+
                    " duration in second: " + duration);
        }else{
            result=false;
            System.err.println("check config variable");
        }
        return result;

    }

    private static void startStressTest(List<Future<Boolean>> resultList, Request request, ExecutorService threadPool) throws InterruptedException {
        Long endBalanceTime = 0L;
        Long delay;
        List<Long> delayList = new ArrayList<>();
        //Количество оставшихся итераций в текущем временном промежутке
        int remnantIteration = 0;
        int i = 0;
        //Запускаем цикл на указанное время
        while (i<requestPerSecond * duration){
            //Если мы отправили последний запрос сбрасываем настройки
            if(remnantIteration<1){
                remnantIteration=requestPerSecond;
                //Устанавливаем конец интервала балансировки
                endBalanceTime = System.nanoTime()+1000000000;
                //Выводим статистику за последний интервал
                printStatisticByList("delay time", delayList);
            }
            i++;
            //Вызываем поток и вощвращаем результат его работы в list
            resultList.add(CompletableFuture.supplyAsync(
                    () -> request.send(), threadPool));
            //Пересчитываем время задержки для данной итерации
            delay = countDelay(endBalanceTime, remnantIteration--);
            TimeUnit.NANOSECONDS.sleep(delay);
            delayList.add(delay/1000000);
        }
        printStatisticByList("delay time", delayList);

    }

    private static void countSuccessRequest(List<Future<Boolean>> futureList) throws ExecutionException, InterruptedException {
        int successCounter=0;
        for(Future<Boolean> future : futureList){
            if(future.get()){
                successCounter++;
            }
        }
        System.out.println("Success request - "+successCounter);

    }

    private static Long countDelay(Long endBalanceTime, int remnantIteration){
        Long time = (endBalanceTime-System.nanoTime())/remnantIteration;
        return time;
    }

    private static void printStatisticByList(String text, List<Long> list){
        if(list.size()==0) return;
        System.out.println("Min "+text+": "+list.stream().parallel().mapToLong(a->a).min().getAsLong()+" "+
        " Max "+text+": "+list.stream().parallel().mapToLong(a->a).max().getAsLong()+" "+
        " Average "+text+": "+list.stream().parallel().mapToLong(a->a).average().getAsDouble());
        list.clear();
    }
}
