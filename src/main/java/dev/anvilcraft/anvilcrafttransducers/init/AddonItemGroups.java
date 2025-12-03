//package dev.anvilcraft.anvilcrafttransducers.init;
//
//import dev.anvilcraft.anvilcrafttransducers.AnvilCraftTransducers;
//import dev.dubhe.anvilcraft.init.block.ModBlocks;
//import dev.dubhe.anvilcraft.init.item.ModItemGroups;
//import net.minecraft.core.registries.Registries;
//import net.minecraft.world.item.CreativeModeTab;
//import net.neoforged.bus.api.IEventBus;
//import net.neoforged.neoforge.registries.DeferredHolder;
//import net.neoforged.neoforge.registries.DeferredRegister;
//
//import static dev.anvilcraft.anvilcrafttransducers.AnvilCraftTransducers.REGISTRATE;
//
//
//public class AddonItemGroups {
//    private static final DeferredRegister<CreativeModeTab> DEFERRED_REGISTER = DeferredRegister.create(
//        Registries.CREATIVE_MODE_TAB,
//        AnvilCraftTransducers.MOD_ID
//    );
//
//    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> ADDON_ITEMS = DEFERRED_REGISTER.register(
//        "addon_items",
//        () -> CreativeModeTab.builder()
//            .icon(ModBlocks.POWER_CONVERTER_BIG::asStack)
//            .displayItems((ctx, entries) -> {
//            })
//            .title(
//                REGISTRATE.addLang(
//                    "itemGroup",
//                        AnvilCraftTransducers.of("addon_items"),
//                    "AnvilCraft: Addon Template"
//                )
//            )
//            .withTabsBefore(ModItemGroups.ANVILCRAFT_BUILD_BLOCK.getId())
//            .build()
//    );
//
//    public static void register(IEventBus modEventBus) {
//        DEFERRED_REGISTER.register(modEventBus);
//    }
//}
