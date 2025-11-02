package lib;

import mindustry.mod.Mod;
import lib.tests.*;

public class MultiCrafterMod extends Mod {

    boolean debug = true;

    public void loadContent(){
        if (debug) {
            TestBlocks.load();
        }
    }

}
