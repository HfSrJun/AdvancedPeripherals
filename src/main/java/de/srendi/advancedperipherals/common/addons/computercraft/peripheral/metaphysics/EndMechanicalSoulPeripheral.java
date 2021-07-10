package de.srendi.advancedperipherals.common.addons.computercraft.peripheral.metaphysics;

import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleSide;
import de.srendi.advancedperipherals.common.configuration.AdvancedPeripheralsConfig;
import de.srendi.advancedperipherals.common.util.NBTUtil;
import de.srendi.advancedperipherals.common.util.Pair;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.Optional;


public class EndMechanicalSoulPeripheral extends WeakMechanicSoulPeripheral {

    private final static String POINT_DATA_MARK = "warp_points";
    private final static String WORLD_DATA_MARK = "warp_world";
    private final static String WARP_OPERATION = "warp";

    public EndMechanicalSoulPeripheral(String type, ITurtleAccess turtle, TurtleSide side) {
        super(type, turtle, side);
    }

    @Override
    public boolean isEnabled() {
        return AdvancedPeripheralsConfig.enableEndMechanicSoul;
    }

    public int getInteractionRadius() {
        return AdvancedPeripheralsConfig.endMechanicSoulInteractionRadius;
    }

    @Override
    protected int getRawCooldown(String name) {
        if (name.equals(WARP_OPERATION))
            return AdvancedPeripheralsConfig.warpCooldown;
        return super.getRawCooldown(name);
    }

    @Override
    protected int getMaxFuelConsumptionRate() {
        return AdvancedPeripheralsConfig.endMechanicSoulMaxFuelConsumptionLevel;
    }

    protected @Nonnull Pair<MethodResult, CompoundNBT> getPointData() {
        CompoundNBT settings = owner.getDataStorage();
        if (!settings.contains(WORLD_DATA_MARK)) {
            settings.putString(WORLD_DATA_MARK, getWorld().dimension().location().toString());
        } else {
            String worldName = settings.getString(WORLD_DATA_MARK);
            if (!getWorld().dimension().location().toString().equals(worldName)) {
                return Pair.onlyLeft(MethodResult.of(null, "Incorrect world for this upgrade"));
            }
        }
        if (!settings.contains(POINT_DATA_MARK)) {
            settings.put(POINT_DATA_MARK, new CompoundNBT());
        }
        return Pair.onlyRight(settings.getCompound(POINT_DATA_MARK));
    }

    private int getWarpCost(BlockPos warpTarget) {
        return (int) Math.sqrt(warpTarget.distManhattan(getPos())) * fuelConsumptionMultiply();
    }

    @Override
    public Map<String, Object> getPeripheralConfiguration() {
        Map<String, Object> result = super.getPeripheralConfiguration();
        result.put("warpCooldown", AdvancedPeripheralsConfig.warpCooldown);
        return result;
    }

    @LuaFunction
    public int getWarpCooldown() {
        return getCurrentCooldown(WARP_OPERATION);
    }

    @LuaFunction
    public final MethodResult savePoint(@Nonnull IComputerAccess access, String name) {
        addRotationCycle();
        Pair<MethodResult, CompoundNBT> pairData = getPointData();
        if (pairData.leftPresent()) {
            return pairData.getLeft();
        }
        CompoundNBT data = pairData.getRight();
        data.put(name, NBTUtil.toNBT(getPos()));
        return MethodResult.of(true);
    }

    @LuaFunction
    public final MethodResult points(@Nonnull IComputerAccess access) {
        Pair<MethodResult, CompoundNBT> pairData = getPointData();
        if (pairData.leftPresent()) {
            return pairData.getLeft();
        }
        CompoundNBT data = pairData.getRight();
        return MethodResult.of(data.getAllKeys());
    }

    @LuaFunction(mainThread = true)
    public final MethodResult warpToPoint(@Nonnull IComputerAccess access, String name) {
        Pair<MethodResult, CompoundNBT> pairData = getPointData();
        if (pairData.leftPresent()) {
            return pairData.getLeft();
        }
        Optional<MethodResult> checkResults = cooldownCheck(WARP_OPERATION);
        if (checkResults.isPresent()) return checkResults.get();
        World world = getWorld();
        addRotationCycle();
        CompoundNBT data = pairData.getRight();
        BlockPos newPosition = NBTUtil.blockPosFromNBT(data.getCompound(name));
        if (owner.isMovementPossible(world, newPosition))
            return MethodResult.of(null, "Move forbidden");
        int warpCost = getWarpCost(newPosition);
        if (consumeFuel(warpCost, true)) {
            boolean teleportResult = owner.move(world, newPosition);
            if (teleportResult) {
                consumeFuel(warpCost, false);
                return MethodResult.of(true);
            } else {
                return MethodResult.of(null, "Cannot teleport to location");
            }
        }
        trackOperation(WARP_OPERATION);
        return MethodResult.of(null, String.format("Not enough fuel, %d needed", warpCost));
    }

    @LuaFunction
    public final MethodResult estimateWarpCost(@Nonnull IComputerAccess access, String name) {
        Pair<MethodResult, CompoundNBT> pairData = getPointData();
        if (pairData.leftPresent()) {
            return pairData.getLeft();
        }
        CompoundNBT data = pairData.getRight();
        BlockPos newPosition = NBTUtil.blockPosFromNBT(data.getCompound(name));
        return MethodResult.of(getWarpCost(newPosition));
    }

    @LuaFunction
    public final MethodResult distanceToPoint(@Nonnull IComputerAccess access, String name) {
        Pair<MethodResult, CompoundNBT> pairData = getPointData();
        if (pairData.leftPresent()) {
            return pairData.getLeft();
        }
        CompoundNBT data = pairData.getRight();
        BlockPos newPosition = NBTUtil.blockPosFromNBT(data.getCompound(name));
        return MethodResult.of(newPosition.distManhattan(getPos()));
    }

}
