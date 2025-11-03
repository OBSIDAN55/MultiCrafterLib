package lib.libClasses;

import arc.struct.Seq;
import arc.util.Nullable;
import mindustry.type.Item;
import mindustry.type.ItemStack;
import mindustry.type.Liquid;
import mindustry.type.LiquidStack;
import mindustry.type.UnitType;
import mindustry.ctype.UnlockableContent;

import java.util.*;

import static lib.multicraft.ContentResolver.*;
import static lib.multicraft.ParserUtils.parseFloat;
import static lib.multicraft.ParserUtils.parseInt;
import static lib.multicraft.ParserUtils.parseJsonToObject;

/**
 * Парсер JSON-конфига планов для UnitAssemblerWithMenu.
 * Поддерживает структуры:
 * - Список планов или одиночный план (Map)
 * - Поля плана: unit (String, обяз.), time (float), requirements (payloads), items, liquids
 * - requirements/items/liquids принимают: String "id/amount", Map {payload|item|fluid, amount}, List из String/Map
 */
public class UnitAssemblerJsonParser {

    public Seq<UnitAssemblerWithMenu.CustomAssemblerUnitPlan> parse(Object raw) {
        Object normalized = parseJsonToObject(raw);
        Seq<UnitAssemblerWithMenu.CustomAssemblerUnitPlan> out = new Seq<>(UnitAssemblerWithMenu.CustomAssemblerUnitPlan.class);
        if (normalized instanceof List) {
            for (Object o : (List<?>) normalized) {
                UnitAssemblerWithMenu.CustomAssemblerUnitPlan plan = parsePlan(o);
                if (plan != null) out.add(plan);
            }
        } else if (normalized instanceof Map) {
            UnitAssemblerWithMenu.CustomAssemblerUnitPlan plan = parsePlan(normalized);
            if (plan != null) out.add(plan);
        }
        return out;
    }

    @Nullable
    @SuppressWarnings({"rawtypes", "unchecked"})
    private UnitAssemblerWithMenu.CustomAssemblerUnitPlan parsePlan(Object obj) {
        if (!(obj instanceof Map)) return null;
        Map map = (Map) obj;

        Object unitRaw = map.get("unit");
        if (!(unitRaw instanceof String)) return null;
        UnitType unit = findUnit((String) unitRaw);
        if (unit == null) return null;

        float time = parseFloat(map.get("time"));
        if (time <= 0f) time = 60f;

        UnitAssemblerWithMenu.CustomAssemblerUnitPlan plan = new UnitAssemblerWithMenu.CustomAssemblerUnitPlan();
        plan.unit = unit;
        plan.time = time;

        // requirements (payloads)
        Object reqRaw = map.get("requirements");
        if (reqRaw != null) {
            Seq<mindustry.type.PayloadStack> req = new Seq<>();
            if (reqRaw instanceof List) {
                for (Object e : (List) reqRaw) addPayloadEntry(e, req);
            } else {
                addPayloadEntry(reqRaw, req);
            }
            plan.requirements = req;
        }

        // items
        Object itemsRaw = map.get("items");
        if (itemsRaw != null) {
            Seq<ItemStack> items = new Seq<>();
            if (itemsRaw instanceof List) {
                for (Object e : (List) itemsRaw) addItemEntry(e, items);
            } else {
                addItemEntry(itemsRaw, items);
            }
            plan.itemReq = items.isEmpty() ? null : items.toArray(ItemStack.class);
        }

        // liquids
        Object liquidsRaw = map.get("liquids");
        if (liquidsRaw != null) {
            Seq<LiquidStack> liquids = new Seq<>();
            if (liquidsRaw instanceof List) {
                for (Object e : (List) liquidsRaw) addLiquidEntry(e, liquids);
            } else {
                addLiquidEntry(liquidsRaw, liquids);
            }
            plan.liquidReq = liquids.isEmpty() ? null : liquids.toArray(LiquidStack.class);
        }

        return plan;
    }

    private void addItemEntry(Object raw, Seq<ItemStack> into) {
        if (raw instanceof String) {
            String[] parts = ((String) raw).split("/");
            Item item = findItem(parts[0]);
            if (item == null) return;
            int amount = parts.length > 1 ? safeInt(parts[1]) : 1;
            ItemStack st = new ItemStack();
            st.item = item;
            st.amount = Math.max(1, amount);
            into.add(st);
        } else if (raw instanceof Map) {
            Map map = (Map) raw;
            Object id = map.get("item");
            if (id instanceof String) {
                Item item = findItem((String) id);
                if (item == null) return;
                ItemStack st = new ItemStack();
                st.item = item;
                st.amount = Math.max(1, parseInt(map.get("amount")));
                into.add(st);
            }
        }
    }

    private void addLiquidEntry(Object raw, Seq<LiquidStack> into) {
        if (raw instanceof String) {
            String[] parts = ((String) raw).split("/");
            Liquid liq = findFluid(parts[0]);
            if (liq == null) return;
            float amount = parts.length > 1 ? safeFloat(parts[1]) : 1f;
            LiquidStack st = new LiquidStack(liq, Math.max(0.0001f, amount));
            into.add(st);
        } else if (raw instanceof Map) {
            Map map = (Map) raw;
            Object id = map.get("fluid");
            if (id instanceof String) {
                Liquid liq = findFluid((String) id);
                if (liq == null) return;
                float amount = parseFloat(map.get("amount"));
                LiquidStack st = new LiquidStack(liq, Math.max(0.0001f, amount));
                into.add(st);
            }
        }
    }

    private void addPayloadEntry(Object raw, Seq<mindustry.type.PayloadStack> into) {
        if (raw instanceof String) {
            String[] parts = ((String) raw).split("/");
            UnlockableContent payload = findPayload(parts[0]);
            if (payload == null) return;
            int amount = parts.length > 1 ? safeInt(parts[1]) : 1;
            mindustry.type.PayloadStack st = new mindustry.type.PayloadStack();
            st.item = payload;
            st.amount = Math.max(1, amount);
            into.add(st);
        } else if (raw instanceof Map) {
            Map map = (Map) raw;
            Object id = map.get("payload");
            if (id instanceof String) {
                UnlockableContent payload = findPayload((String) id);
                if (payload == null) return;
                mindustry.type.PayloadStack st = new mindustry.type.PayloadStack();
                st.item = payload;
                st.amount = Math.max(1, parseInt(map.get("amount")));
                into.add(st);
            }
        }
    }

    private int safeInt(String s) {
        try { return Integer.parseInt(s); } catch (Exception e) { return 1; }
    }

    private float safeFloat(String s) {
        try { return Float.parseFloat(s); } catch (Exception e) { return 1f; }
    }
}

