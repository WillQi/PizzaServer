package io.github.pizzaserver.api.block.impl;

import io.github.pizzaserver.api.block.BlockID;
import io.github.pizzaserver.api.block.data.SlabType;
import io.github.pizzaserver.api.item.data.ToolTier;
import io.github.pizzaserver.api.item.data.ToolType;

public class BlockDeepslateTileSlab extends BlockSlab {

    public BlockDeepslateTileSlab() {
        this(SlabType.SINGLE);
    }

    public BlockDeepslateTileSlab(SlabType slabType) {
        super(slabType);
    }

    @Override
    public String getBlockId() {
        return this.isDouble() ? BlockID.DEEPSLATE_TILE_DOUBLE_SLAB : BlockID.DEEPSLATE_TILE_SLAB;
    }

    @Override
    public String getName() {
        return "Deepslate Tile Slab";
    }

    @Override
    public float getHardness() {
        return 3.5f;
    }

    @Override
    public float getBlastResistance() {
        return 6;
    }

    @Override
    public ToolType getToolTypeRequired() {
        return ToolType.PICKAXE;
    }

    @Override
    public ToolTier getToolTierRequired() {
        return ToolTier.WOOD;
    }

}
