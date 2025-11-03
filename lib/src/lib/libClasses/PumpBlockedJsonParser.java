package lib.libClasses;

import arc.struct.Seq;
import arc.util.Nullable;
import mindustry.type.Liquid;

import java.util.List;
import java.util.Map;

import static lib.multicraft.ContentResolver.findFluid;
import static lib.multicraft.ParserUtils.parseJsonToObject;

/**
 * JSON parser for configuring PumpBlocked.
 * Supported fields:
 * - blockedLiquids: ["water", "oil", ...]
 * - blockPlacementOnBlockedLiquids: true|false
 */
public class PumpBlockedJsonParser {

    public void apply(PumpBlocked pump, Object raw){
        Object normalized = parseJsonToObject(raw);
        if(!(normalized instanceof Map)) return;
        Map<?,?> map = (Map<?,?>) normalized;

        Object blocked = map.get("blockedLiquids");
        if(blocked instanceof List){
            for(Object o : (List<?>) blocked){
                if(!(o instanceof String)) continue;
                Liquid liq = findFluid((String)o);
                if(liq != null) pump.blockedLiquids.add(liq);
            }
        } else if(blocked instanceof String){
            Liquid liq = findFluid((String)blocked);
            if(liq != null) pump.blockedLiquids.add(liq);
        }

        Object blockPlace = map.get("blockPlacementOnBlockedLiquids");
        if(blockPlace instanceof Boolean){
            pump.blockPlacementOnBlockedLiquids = (Boolean) blockPlace;
        }
    }
}


