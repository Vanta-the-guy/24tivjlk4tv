package com.seedxray;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Predicts ore positions by replicating Minecraft's RNG salt-mixing
 * used during world generation. No chunk scanning required.
 */
public class OrePredictor {

    public static final Map<BlockPos, Block> predictedOres = new ConcurrentHashMap<>();

    private static final OreRule[] RULES = {
        new OreRule(Blocks.DIAMOND_ORE,              "diamond",        1, -64,  -4,  8),
        new OreRule(Blocks.DEEPSLATE_DIAMOND_ORE,    "diamond_buried", 2, -64,  -4,  8),
        new OreRule(Blocks.ANCIENT_DEBRIS,            "ancient_debris", 1,   8,  24,  3),
        new OreRule(Blocks.GOLD_ORE,                  "gold",           4, -64,  32,  9),
        new OreRule(Blocks.DEEPSLATE_GOLD_ORE,        "gold_lower",     2, -64, -48,  9),
        new OreRule(Blocks.IRON_ORE,                  "iron_upper",     6,  80, 384,  9),
        new OreRule(Blocks.IRON_ORE,                  "iron_mid",       4, -32,  56,  9),
        new OreRule(Blocks.DEEPSLATE_IRON_ORE,        "iron_lower",     5, -64,  72,  9),
        new OreRule(Blocks.EMERALD_ORE,               "emerald",        3, -16, 480,  3),
        new OreRule(Blocks.LAPIS_ORE,                 "lapis",          2, -32,  32,  7),
        new OreRule(Blocks.DEEPSLATE_LAPIS_ORE,       "lapis_lower",    4, -64,  64,  7),
        new OreRule(Blocks.REDSTONE_ORE,              "redstone",       4, -64,  16,  8),
        new OreRule(Blocks.DEEPSLATE_REDSTONE_ORE,    "redstone_lower", 8, -64, -32,  8),
        new OreRule(Blocks.COPPER_ORE,                "copper",         6, -16, 112, 10),
        new OreRule(Blocks.DEEPSLATE_COPPER_ORE,      "copper_large",   3, -16, 112, 20),
        new OreRule(Blocks.COAL_ORE,                  "coal",          20,   0, 256, 17),
    };

    public static void computeOres(long worldSeed, Minecraft mc) {
        predictedOres.clear();
        BlockPos player = mc.player.blockPosition();
        int cx0 = (player.getX() >> 4) - 4;
        int cz0 = (player.getZ() >> 4) - 4;

        Thread t = new Thread(() -> {
            Map<BlockPos, Block> result = new ConcurrentHashMap<>();
            for (int dx = 0; dx <= 8; dx++) {
                for (int dz = 0; dz <= 8; dz++) {
                    int cx = cx0 + dx, cz = cz0 + dz;
                    for (OreRule rule : RULES) {
                        for (BlockPos pos : veinsInChunk(worldSeed, cx, cz, rule)) {
                            result.putIfAbsent(pos, rule.block());
                        }
                    }
                }
            }
            predictedOres.putAll(result);
        }, "SeedXray-Predictor");
        t.setDaemon(true);
        t.start();
    }

    private static List<BlockPos> veinsInChunk(long worldSeed, int cx, int cz, OreRule rule) {
        List<BlockPos> out = new ArrayList<>();
        int range = Math.max(1, rule.maxY() - rule.minY());

        for (int attempt = 0; attempt < rule.count(); attempt++) {
            long seed = worldSeed
                ^ ((long) cx * 341873128712L)
                ^ ((long) cz * 132897987541L)
                ^ fnv(rule.salt())
                ^ ((long) attempt * 9871234567L);

            Random rng = new Random(seed);
            int originX = (cx << 4) + rng.nextInt(16);
            int originY = rule.minY() + rng.nextInt(range);
            int originZ = (cz << 4) + rng.nextInt(16);

            for (int v = 0; v < rule.veinSize(); v++) {
                int ox = rng.nextInt(3) - 1;
                int oy = rng.nextInt(3) - 1;
                int oz = rng.nextInt(3) - 1;
                out.add(new BlockPos(originX + ox, originY + oy, originZ + oz));
            }
        }
        return out;
    }

    private static long fnv(String s) {
        long h = 0xcbf29ce484222325L;
        for (char c : s.toCharArray()) { h ^= c; h *= 0x100000001b3L; }
        return h;
    }

    public static void clearCache() { predictedOres.clear(); }

    private record OreRule(Block block, String salt, int count, int minY, int maxY, int veinSize) {}
}
