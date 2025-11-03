package lib;

import arc.assets.Loadable;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatCat;

public class Vars implements Loadable {

    public static Stat BlockedLiquidsStat;

    public static void load(){

        BlockedLiquidsStat = new Stat("BlockedLiquidsStat", StatCat.liquids);
    }



}
