package com.cmdpro.databank.megablock;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class MegablockShape {
    public List<Vec3i> shape;
    public MegablockShape(List<Vec3i> shape) {
        this.shape = shape;
    }
    public MegablockShape(Vec3i corner1, Vec3i corner2) {
        List<Vec3i> shape = new ArrayList<>();
        applyLogicToSection(shape::add, corner1, corner2);
        this.shape = shape;
    }
    public MegablockShape removeSection(Vec3i corner1, Vec3i corner2) {
        applyLogicToSection(pos -> shape.remove(pos), corner1, corner2);
        return this;
    }
    public MegablockShape addSection(Vec3i corner1, Vec3i corner2) {
        applyLogicToSection(pos -> { if (!shape.contains(pos)) { shape.add(pos); } }, corner1, corner2);
        return this;
    }
    public void applyLogicToSection(Consumer<Vec3i> logic, Vec3i corner1, Vec3i corner2) {
        Vec3i lowest = new Vec3i(
                Math.min(corner1.getX(), corner2.getX()),
                Math.min(corner1.getY(), corner2.getY()),
                Math.min(corner1.getZ(), corner2.getZ())
        );
        Vec3i highest = new Vec3i(
                Math.max(corner1.getX(), corner2.getX()),
                Math.max(corner1.getY(), corner2.getY()),
                Math.max(corner1.getZ(), corner2.getZ())
        );
        for (int x = lowest.getX(); x <= highest.getX(); x++) {
            for (int y = lowest.getY(); y <= highest.getY(); y++) {
                for (int z = lowest.getZ(); z <= highest.getZ(); z++) {
                    logic.accept(new Vec3i(x, y, z));
                }
            }
        }
    }
    public MegablockShape getRotated(Rotation rotation) {
        List<Vec3i> rotated = new ArrayList<>();
        for (Vec3i i : shape) {
            rotated.add(new BlockPos(i).rotate(rotation));
        }
        return new MegablockShape(rotated);
    }
}
