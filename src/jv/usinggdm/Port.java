package jv.usinggdm;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

public class Port {

        public static final int MAXIMUM_CARGOES = 1200;
        private static final int MAXIMUM_BERTHS = 6;

        public static int cargoes;
            public static ArrayList<Ship> ships;
                public static final Thread producer = new Thread(new Producer());

            Port(int cargoes){
                Port.cargoes = cargoes;
                ships = new ArrayList<>();
                    producer.start();
                runLogic();
                Iterator iterator = ships.iterator();
                    for(int i=0; i<3; i++){
                        ((Ship)iterator.next()).load();
                    }
                    for(int i=0; i<3; i++){
                        ((Ship)iterator.next()).unload();
                    }
            }

            private void runLogic() {
                for (int i = 0; i < MAXIMUM_BERTHS; i++) {
                    ships.add(new Ship((i + 1) * 200, i * 40));
                }
            }

            private static class Producer implements Runnable{

                    @Override
                    public void run(){
                        try{
                            if(!Thread.currentThread().isInterrupted())
                            while (true){
                                produce();
                            }
                        } catch (InterruptedException ex){
                            ex.printStackTrace();
                        }
                    }

                    private void produce() throws InterruptedException{
                        if(Port.cargoes>=MAXIMUM_CARGOES){
                            System.out.println("No space available\nProducer waits");
                            synchronized (Port.producer) {
                                Port.producer.wait();
                            }
                        }
                        TimeUnit.MILLISECONDS.sleep(5000);
                            Port.cargoes+=100;
                        System.out.println("Produced 100 c.u");
                        System.out.println("Port congestion: "+cargoes);
                            Iterator iterator = ships.iterator();
                                while (iterator.hasNext()){
                                    Ship cship = (Ship) iterator.next();
                                    if(((cship.state==1))&& cship.loader!=null && Port.cargoes>0){
                                        synchronized (cship.loader){
                                            cship.loader.notify();
                                        }
                                    }
                                }
                    }
            }

}
