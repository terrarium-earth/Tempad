package me.codexadrian.tempad.common.blocks;

import me.codexadrian.tempad.common.data.ConfiguredLocationData;
import me.codexadrian.tempad.common.data.LocationData;
import me.codexadrian.tempad.common.data.WarpHandler;
import me.codexadrian.tempad.common.registry.TempadBlockEntities;
import me.codexadrian.tempad.common.teams.GlobalTeamConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.UUID;

public class WarpBlockEntity extends BlockEntity {
    private UUID id = UUID.fromString("00000000-0000-0000-0000-000000000000");
    private UUID owner = UUID.fromString("00000000-0000-0000-0000-000000000000");
    private ResourceLocation configId = GlobalTeamConfig.ID;
    private String name = "Warp Pad";

    public WarpBlockEntity(BlockPos pos, BlockState blockState) {
        super(TempadBlockEntities.WARP_PAD.get(), pos, blockState);
    }

    public void initialize() {
        this.id = UUID.randomUUID();
        setChanged();
        updateLocation();
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        this.id = tag.getUUID("locationId");
        this.owner = tag.getUUID("owner");
        this.name = tag.getString("name");
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putUUID("locationId", id);
        tag.putString("name", name);
        tag.putUUID("owner", owner);
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        this.setChanged();
        this.updateLocation();
    }

    public void setOwner(UUID owner) {
        this.owner = owner;
        this.setChanged();
        this.updateLocation();
    }

    public void updateLocation() {
        WarpHandler.addWarp(level, id, getConfiguredLocationData());
    }

    public ConfiguredLocationData getConfiguredLocationData() {
        return new ConfiguredLocationData(getLocationData(), configId, owner);
    }

    public LocationData getLocationData() {
        return new LocationData(name, level.dimension(), worldPosition.above(), getBlockState().getValue(WarpBlock.FACING).get2DDataValue() * 90, id, false, false);
    }
}
