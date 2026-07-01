package com.seedxray;

import com.mojang.brigadier.Command;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;
import net.minecraft.commands.Commands;

@Mod.EventBusSubscriber(modid = SeedXrayMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class SeedXrayClientEvents {

    public static boolean xrayEnabled = false;

    public static final KeyMapping TOGGLE_KEY = new KeyMapping(
            "key.seed_xray.toggle",
            GLFW.GLFW_KEY_X,
            "key.categories.seed_xray"
    );

    // ── Mod bus event (registered via addListener in SeedXrayMod) ────────────
    public static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(TOGGLE_KEY);
    }

    // ── Game tick — check keybind ─────────────────────────────────────────────
    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Minecraft mc = Minecraft.getInstance();
        while (TOGGLE_KEY.consumeClick()) {
            toggle(mc);
        }
    }

    // ── World render ──────────────────────────────────────────────────────────
    @SubscribeEvent
    public static void onRenderLevel(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) return;
        if (!xrayEnabled) return;
        XrayRenderer.render(event);
    }

    // ── Client-side chat command /seedxray ────────────────────────────────────
    @SubscribeEvent
    public static void onRegisterCommands(RegisterClientCommandsEvent event) {
        event.getDispatcher().register(
            Commands.literal("seedxray")
                .executes(ctx -> { toggle(Minecraft.getInstance()); return Command.SINGLE_SUCCESS; })
                .then(Commands.literal("on")
                    .executes(ctx -> { setXray(true,  Minecraft.getInstance()); return Command.SINGLE_SUCCESS; }))
                .then(Commands.literal("off")
                    .executes(ctx -> { setXray(false, Minecraft.getInstance()); return Command.SINGLE_SUCCESS; }))
                .then(Commands.literal("refresh")
                    .executes(ctx -> {
                        Minecraft mc = Minecraft.getInstance();
                        if (mc.level != null && xrayEnabled) {
                            OrePredictor.computeOres(getWorldSeed(mc), mc);
                            sendMessage(mc, "§bSeed X-Ray §arefreshed.");
                        }
                        return Command.SINGLE_SUCCESS;
                    }))
        );
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    /** Gets the world seed. Works in singleplayer; returns 0 on multiplayer servers. */
    static long getWorldSeed(Minecraft mc) {
        if (mc.getSingleplayerServer() != null) {
            return mc.getSingleplayerServer().overworld().getSeed();
        }
        return 0L; // seed not available on multiplayer servers
    }

    static void toggle(Minecraft mc) {
        if (mc.level == null) { sendMessage(mc, "§eEnter a world first."); return; }
        setXray(!xrayEnabled, mc);
    }

    static void setXray(boolean enable, Minecraft mc) {
        if (mc.level == null) return;
        xrayEnabled = enable;
        if (enable) {
            long seed = getWorldSeed(mc);
            if (seed == 0L && mc.getSingleplayerServer() == null) {
                sendMessage(mc, "§eSeed X-Ray: seed unavailable on multiplayer.");
            }
            OrePredictor.computeOres(seed, mc);
            sendMessage(mc, "§bSeed X-Ray §aON §7— predicting ores from seed...");
        } else {
            OrePredictor.clearCache();
            sendMessage(mc, "§bSeed X-Ray §cOFF");
        }
    }

    static void sendMessage(Minecraft mc, String msg) {
        if (mc.player != null)
            mc.player.displayClientMessage(Component.literal(msg), true);
    }
}
