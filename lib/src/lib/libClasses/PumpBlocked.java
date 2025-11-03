package lib.libClasses;

import arc.scene.ui.Image;
import arc.scene.ui.layout.Table;
import arc.struct.ObjectSet;
import mindustry.game.Team;
import mindustry.type.Liquid;
import mindustry.ui.Styles;
import mindustry.world.Tile;
import mindustry.world.blocks.production.Pump;
import mindustry.world.meta.Stat;

import static lib.Vars.*;

public class PumpBlocked extends Pump {

    /** Liquids that this pump is not allowed to extract. */
    public final ObjectSet<Liquid> blockedLiquids = new ObjectSet<>();
    /** If true, disallow placing the pump on tiles that would extract blocked liquids. */
    public boolean blockPlacementOnBlockedLiquids = true;

    public PumpBlocked(String name) {
        super(name);
    }

    @Override
    public void setStats(){
        super.setStats();

        stats.add(BlockedLiquidsStat, t -> {
            t.left();
            t.row();
            t.table(Styles.grayPanel, cont -> {
                cont.left();
                int i = 0;
                for(Liquid liq : blockedLiquids){
                    Table cell = new Table();
                    cell.left();
                    cell.add(new Image(liq.uiIcon)).size(24f).padRight(6f);
                    cell.add(liq.localizedName).left();
                    cont.add(cell).pad(2f).left();
                    if(++i % 2 == 0) cont.row();
                }
                if(blockedLiquids.isEmpty()){
                    cont.add("-");
                }
            }).growX().padTop(4f).left();
        });
    }

    

    @Override
    public boolean canPlaceOn(Tile tile, Team team, int rotation){
        if(tile == null) return false;
        if(blockPlacementOnBlockedLiquids){
            Liquid under = tile.floor().liquidDrop;
            if(under != null && blockedLiquids.contains(under)){
                drawPlaceText("[scarlet]Blocked:[] " + under.localizedName, tile.x, tile.y, false);
                return false;
            }
        }
        return super.canPlaceOn(tile, team, rotation);
    }

    public class PumpBlockedBuild extends PumpBuild{

        private boolean isBlocked(){
            Tile t = tile;
            if(t == null) return false;
            Liquid under = t.floor().liquidDrop;
            return under != null && ((PumpBlocked)block).blockedLiquids.contains(under);
        }

        @Override
        public void updateTile(){
            // Prevent extraction entirely if the liquid under this pump is blocked
            if(isBlocked()){
                warmup = 0f;
                // still run minimal base update like dumping, but skip production
                // do not call super.updateTile() to avoid adding liquid
                return;
            }
            super.updateTile();
        }
    }
}
