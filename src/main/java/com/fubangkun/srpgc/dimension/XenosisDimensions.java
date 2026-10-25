package com.fubangkun.srpgc.dimension;

import com.fubangkun.srpgc.config.ConfigManagerSRPGC;
import net.minecraft.world.DimensionType;

public final class XenosisDimensions {

    private static DimensionType type;

    private XenosisDimensions() {
    }

    public static DimensionType findType() {
        int dimensionId = ConfigManagerSRPGC.dimensionId;
        for (DimensionType candidate : DimensionType.values()) {
            if (candidate.getId() == dimensionId) {
                return candidate;
            }
        }
        return null;
    }

    public static DimensionType getType() {
        if (type == null) {
            type = findType();
        }
        return type == null ? DimensionType.OVERWORLD : type;
    }

    public static void setType(DimensionType dimensionType) {
        type = dimensionType;
    }
}
