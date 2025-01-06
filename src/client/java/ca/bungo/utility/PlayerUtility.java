package ca.bungo.utility;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.List;

public class PlayerUtility {

    public static PlayerEntity getTargetPlayer(MinecraftClient client, double range) {
        if(client.player == null || client.world == null)
            return null;

        Vec3d cameraPos = client.player.getCameraPosVec(1.0F);
        Vec3d lookVec = client.player.getRotationVec(1.0F).multiply(range);
        Vec3d endVec = cameraPos.add(lookVec);
        Box box = new Box(cameraPos, endVec).expand(1.0, 1.0, 1.0);

        List<Entity> entities = client.world.getEntitiesByClass(Entity.class, box, entity -> entity instanceof PlayerEntity && entity != client.player);
        EntityHitResult entityHitResult = null;
        double closestDistance = Double.MAX_VALUE;

        for (Entity entity : entities) {
            Box entityBox = entity.getBoundingBox().expand(0.1);
            Vec3d hitResult = entityBox.raycast(cameraPos, endVec).isPresent() ? entityBox.raycast(cameraPos, endVec).get() : null;
            if(hitResult == null) continue;
            double distance = hitResult.squaredDistanceTo(cameraPos);
            if (distance < closestDistance) {
                closestDistance = distance;
                entityHitResult = new EntityHitResult(entity, hitResult);
            }
        }

        if(entityHitResult == null) return null;

        if(entityHitResult.getType() == HitResult.Type.ENTITY){
            Entity entity = entityHitResult.getEntity();
            if(entity instanceof PlayerEntity player){
                return player;
            }
        }

        return null;
    }

}
