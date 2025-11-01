package lib.tests;

import lib.libClasses.UnitAssemblerWithMenu;
import mindustry.content.Blocks;
import mindustry.type.PayloadStack;
import mindustry.content.Items;
import mindustry.content.Liquids;
import mindustry.content.UnitTypes;
import mindustry.type.Category;
import mindustry.world.Block;


import static mindustry.type.ItemStack.with;

public class TestBlocks {

    public static Block
    testAssembler;

    public static void load(){
        testAssembler = new UnitAssemblerWithMenu("test-assembler"){{
            requirements(Category.units, with(Items.thorium, 500, Items.oxide, 150, Items.carbide, 80, Items.silicon, 650));
            size = 5;
            areaSize = 13;
            researchCostMultiplier = 0.4f;

            plans.add(
                    new CustomAssemblerUnitPlan(UnitTypes.dagger, 60f * 50f, PayloadStack.list(UnitTypes.stell, 4, Blocks.tungstenWallLarge, 10)),
                    new CustomAssemblerUnitPlan(UnitTypes.mace, 60f * 60f * 3f, PayloadStack.list(UnitTypes.locus, 6, Blocks.carbideWallLarge, 20)),
                    new CustomAssemblerUnitPlan(UnitTypes.fortress, 60f * 50f, PayloadStack.list(UnitTypes.stell, 4, Blocks.tungstenWallLarge, 10)),
                    new CustomAssemblerUnitPlan(UnitTypes.scepter, 60f * 60f * 3f, PayloadStack.list(UnitTypes.locus, 6, Blocks.carbideWallLarge, 20))
            );
            plans.add(
                    new CustomAssemblerUnitPlan(UnitTypes.reign, 60f * 50f, PayloadStack.list(UnitTypes.stell, 4, Blocks.tungstenWallLarge, 10)),
                    new CustomAssemblerUnitPlan(UnitTypes.nova, 60f * 60f * 3f, PayloadStack.list(UnitTypes.locus, 6, Blocks.carbideWallLarge, 20)),
                    new CustomAssemblerUnitPlan(UnitTypes.pulsar, 60f * 50f, PayloadStack.list(UnitTypes.stell, 4, Blocks.tungstenWallLarge, 10)),
                    new CustomAssemblerUnitPlan(UnitTypes.quasar, 60f * 60f * 3f, PayloadStack.list(UnitTypes.locus, 6, Blocks.carbideWallLarge, 20))
            );
            plans.add(
                    new CustomAssemblerUnitPlan(UnitTypes.vela, 60f * 50f, PayloadStack.list(UnitTypes.stell, 4, Blocks.tungstenWallLarge, 10)),
                    new CustomAssemblerUnitPlan(UnitTypes.risso, 60f * 60f * 3f, PayloadStack.list(UnitTypes.locus, 6, Blocks.carbideWallLarge, 20)),
                    new CustomAssemblerUnitPlan(UnitTypes.minke, 60f * 50f, PayloadStack.list(UnitTypes.stell, 4, Blocks.tungstenWallLarge, 10)),
                    new CustomAssemblerUnitPlan(UnitTypes.bryde, 60f * 60f * 3f, PayloadStack.list(UnitTypes.locus, 6, Blocks.carbideWallLarge, 20))
            );
            plans.add(
                    new CustomAssemblerUnitPlan(UnitTypes.sei, 60f * 50f, PayloadStack.list(UnitTypes.stell, 4, Blocks.tungstenWallLarge, 10)),
                    new CustomAssemblerUnitPlan(UnitTypes.omura, 60f * 60f * 3f, PayloadStack.list(UnitTypes.locus, 6, Blocks.carbideWallLarge, 20)),
                    new CustomAssemblerUnitPlan(UnitTypes.retusa, 60f * 50f, PayloadStack.list(UnitTypes.stell, 4, Blocks.tungstenWallLarge, 10)),
                    new CustomAssemblerUnitPlan(UnitTypes.oxynoe, 60f * 60f * 3f, PayloadStack.list(UnitTypes.locus, 6, Blocks.carbideWallLarge, 20))
            );
            plans.add(
                    new CustomAssemblerUnitPlan(UnitTypes.cyerce, 60f * 50f, PayloadStack.list(UnitTypes.stell, 4, Blocks.tungstenWallLarge, 10)),
                    new CustomAssemblerUnitPlan(UnitTypes.aegires, 60f * 60f * 3f, PayloadStack.list(UnitTypes.locus, 6, Blocks.carbideWallLarge, 20)),
                    new CustomAssemblerUnitPlan(UnitTypes.navanax, 60f * 50f, PayloadStack.list(UnitTypes.stell, 4, Blocks.tungstenWallLarge, 10)),
                    new CustomAssemblerUnitPlan(UnitTypes.zenith, 60f * 60f * 3f, PayloadStack.list(UnitTypes.locus, 6, Blocks.carbideWallLarge, 20))
            );

            consumePower(2.5f);
            consumeLiquid(Liquids.cyanogen, 9f / 60f);
        }};
    }

}
