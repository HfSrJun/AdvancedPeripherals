package de.srendi.advancedperipherals.common.addons.mekanism;

import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.shared.peripheral.generic.data.DataHelpers;
import de.srendi.advancedperipherals.lib.peripherals.TileEntityIntegrationPeripheral;
import mekanism.api.chemical.IChemicalTank;
import mekanism.api.chemical.merged.MergedChemicalTank;
import mekanism.common.capabilities.merged.MergedTank;
import mekanism.common.content.tank.TankMultiblockData;
import mekanism.common.tile.TileEntityChemicalTank;
import mekanism.common.tile.multiblock.TileEntityDynamicValve;
import net.minecraft.tileentity.TileEntity;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

public class DynamicTankIntegration extends TileEntityIntegrationPeripheral<TileEntityDynamicValve> {

    public DynamicTankIntegration(TileEntity entity) {
        super(entity);
    }

    @Nonnull
    @Override
    public String getType() {
        return "dynamicTank";
    }

    @LuaFunction(mainThread = true)
    public final Map<String, Object> getStored() {
        Map<String, Object> result = new HashMap<>(3);
        switch(getTank().getCurrentType()) {
            case GAS:
                result.put("name", getTank().getGasTank().getType().toString());
                result.put("amount", getTank().getGasTank().getStored());
            case FLUID:
                result.put("name", getTank().getFluidTank().getFluid().getFluid().toString());
                result.put("amount", getTank().getFluidTank().getFluidAmount());
            case SLURRY:
                result.put("name", getTank().getSlurryTank().getType().toString());
                result.put("amount", getTank().getSlurryTank().getStored());
            case PIGMENT:
                result.put("name", getTank().getPigmentTank().getType().toString());
                result.put("amount", getTank().getPigmentTank().getStored());
            case INFUSION:
                result.put("name", getTank().getInfusionTank().getType().toString());
                result.put("amount", getTank().getInfusionTank().getStored());
            default:
                result.put("name", "");
                result.put("amount", 0);
        }
        result.put("type", getTank().getCurrentType().toString());
        return result;
    }

    @LuaFunction(mainThread = true)
    public final long getCapacity() {
        return tileEntity.getMultiblock().getTankCapacity();
    }

    @LuaFunction(mainThread = true)
    public final double getFilledPercentage() {
        switch(getTank().getCurrentType()) {
            case GAS:
                return getTank().getGasTank().getStored() / (double) getCapacity();
            case FLUID:
                return getTank().getFluidTank().getFluidAmount() / (double) getCapacity();
            case SLURRY:
                return getTank().getSlurryTank().getStored() / (double) getCapacity();
            case PIGMENT:
                return getTank().getPigmentTank().getStored() / (double) getCapacity();
            case INFUSION:
                return getTank().getInfusionTank().getStored() / (double) getCapacity();
            default:
                return 0D;
        }
    }

    @LuaFunction(mainThread = true)
    public final long getNeeded() {
        switch(getTank().getCurrentType()) {
            case GAS:
                return getTank().getGasTank().getNeeded();
            case FLUID:
                return getTank().getFluidTank().getNeeded();
            case SLURRY:
                return getTank().getSlurryTank().getNeeded();
            case PIGMENT:
                return getTank().getPigmentTank().getNeeded();
            case INFUSION:
                return getTank().getInfusionTank().getNeeded();
            default:
                return getCapacity();
        }
    }

    @LuaFunction(mainThread = true)
    public final boolean isEmpty() {
        return tileEntity.getMultiblock().isEmpty();
    }

    private MergedTank getTank() {
        return tileEntity.getMultiblock().mergedTank;
    }

}