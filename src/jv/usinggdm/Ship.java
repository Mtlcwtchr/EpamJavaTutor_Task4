package jv.usinggdm;

import java.util.Iterator;
import java.util.concurrent.TimeUnit;

public class Ship{

    private int carryingCapacity;
         private int carryingCargo;
            public int state;
                public Thread loader;
                public Thread unloader;

         Ship( int carryingCapacity, int currentCargo){
             this.carryingCapacity = carryingCapacity;
             this.carryingCargo = currentCargo;
         }

         public void load(){
             this.state = 1;
             this.loader = new Loader(this);
                this.loader.start();
         }
         public void unload(){
             this.state = 0;
             this.unloader = new Unloader(this);
                this.unloader.start();
         }

         private class Loader extends Thread{

                private Ship ship;

                    Loader(Ship ship){
                        this.ship = ship;
                    }

                    private boolean load() throws InterruptedException{
                        if(this.ship.carryingCargo>=this.ship.carryingCapacity){
                            System.out.println("Ship "+ship+" loaded");
                            return false;
                        } if(Port.cargoes<=0){
                            System.out.println("Port warehouses are empty\nLoader waits");
                            synchronized (this){
                                this.wait();
                            }
                        }
                        Port.cargoes -= 20;
                        this.ship.carryingCargo += 20;
                            if(Port.cargoes<Port.MAXIMUM_CARGOES)
                                synchronized(Port.producer){
                                    Port.producer.notify();
                                }
                        Iterator iterator = Port.ships.iterator();
                        while (iterator.hasNext()){
                            Ship cship = (Ship) iterator.next();
                            if(((cship.state==0) && cship.unloader!=null)){
                                synchronized (cship.unloader){
                                    cship.unloader.notify();
                                }
                            }
                        }
                        return true;
                    }

                @Override
                public void run(){
                        try {
                            while (load()) {
                                System.out.println(this.ship + " + 20 c.u");
                                TimeUnit.SECONDS.sleep(1);
                            }
                        } catch (InterruptedException ex){
                            ex.printStackTrace();
                        }
                }

         }
         private class Unloader extends Thread{

                private Ship ship;

                    Unloader(Ship ship){
                        this.ship = ship;
                    }

                    private boolean unload() throws InterruptedException{
                    if(this.ship.carryingCargo<=0){
                        System.out.println("Ship "+ship+" unloaded");
                        return false;
                    } if(Port.cargoes>=Port.MAXIMUM_CARGOES){
                        System.out.println("Port warehouses are full\nUnloader waits");
                            synchronized (this){
                                this.wait();
                            }
                            Iterator iterator = Port.ships.iterator();
                            while (iterator.hasNext()){
                                Ship cship = (Ship) iterator.next();
                                if(((cship.state==1)) && cship.loader!=null && Port.cargoes>0){
                                    synchronized (cship.loader){
                                        cship.loader.notify();
                                    }
                                }
                            }
                    }
                    this.ship.carryingCargo -= 20;
                    Port.cargoes += 20;
                    return true;
                }

                @Override
                public void run(){
                    try {
                        while (unload()) {
                            System.out.println(this.ship + " - 20 c.u");
                            TimeUnit.SECONDS.sleep(1);
                        }
                    } catch (InterruptedException ex){
                            ex.printStackTrace();
                    }
             }

         }

}
