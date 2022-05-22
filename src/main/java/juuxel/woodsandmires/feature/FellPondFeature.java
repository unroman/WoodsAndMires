package juuxel.woodsandmires.feature;

import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;

import java.util.HashSet;
import java.util.Set;

public final class FellPondFeature extends Feature<FellPondFeatureConfig> {
    public FellPondFeature(Codec<FellPondFeatureConfig> configCodec) {
        super(configCodec);
    }

    @Override
    public boolean generate(FeatureContext<FellPondFeatureConfig> context) {
        FellPondFeatureConfig config = context.getConfig();
        var random = context.getRandom();
        int depth = config.depth().get(random);
        int semiMajor = config.radius().get(random);
        int semiMinor = config.radius().get(random);
        BlockPos.Mutable origin = context.getOrigin().mutableCopy().move(0, -1, 0);
        BlockPos.Mutable mut = new BlockPos.Mutable();
        Set<BlockPos> filledPositions = new HashSet<>();

        // Check for air layers
        boolean foundAir;
        do {
            foundAir = false;
            float semiMajorSq = semiMajor * semiMajor;
            float semiMinorSq = semiMinor * semiMinor;

            outer: for (int x = -semiMajor; x <= semiMajor; x++) {
                for (int z = -semiMinor; z <= semiMinor; z++) {
                    if (isInsideEllipse(x, z, semiMajorSq, semiMinorSq) && isAir(context.getWorld(), mut.set(origin).move(x, 0, z))) {
                        foundAir = true;
                        break outer;
                    }
                }
            }

            if (foundAir) {
                for (int x = -semiMajor; x <= semiMajor; x++) {
                    for (int z = -semiMinor; z <= semiMinor; z++) {
                        if (isInsideEllipse(x, z, semiMajorSq, semiMinorSq)) {
                            setBlockState(context.getWorld(), mut.set(origin).move(x, 0, z), Blocks.AIR.getDefaultState());
                        }
                    }
                }

                origin.move(0, -1, 0);
            }
        } while (foundAir);

        for (int yo = 0; yo < depth; yo++) {
            float semiMajorSq = semiMajor * semiMajor;
            float semiMinorSq = semiMinor * semiMinor;

            for (int x = -semiMajor; x <= semiMajor; x++) {
                for (int z = -semiMinor; z <= semiMinor; z++) {
                    if (isInsideEllipse(x, z, semiMajorSq, semiMinorSq)) {
                        mut.set(origin.getX() + x, origin.getY() - yo, origin.getZ() + z);
                        setBlockState(context.getWorld(), mut, config.fillBlock().getBlockState(random, mut));
                        filledPositions.add(new BlockPos(mut));

                        for (Direction d : Direction.Type.HORIZONTAL) {
                            mut.move(d);

                            if (!filledPositions.contains(mut) && shouldPlaceBorder(context.getWorld(), mut)) {
                                setBlockState(context.getWorld(), mut, config.border().getBlockState(random, mut));
                            }

                            mut.move(d.getOpposite());
                        }

                        if (random.nextFloat() < config.bottomReplaceChance()) {
                            mut.move(0, -1, 0);
                            setBlockState(context.getWorld(), mut, config.bottomBlock().getBlockState(random, mut));
                        }
                    }
                }
            }

            semiMajor--;
            semiMinor--;
        }

        return true;
    }

    private static boolean isInsideEllipse(int x, int y, float semiMajorSq, float semiMinorSq) {
        return x * x / semiMajorSq + y * y / semiMinorSq <= 1f;
    }

    private static boolean shouldPlaceBorder(StructureWorldAccess world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        return state.isAir() || !state.isFullCube(world, pos);
    }
}
