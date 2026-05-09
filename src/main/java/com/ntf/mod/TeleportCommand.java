tpackage com.ntf.mod;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class TeleportCommand {
    
    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(literal("tp")
                .then(argument("x", DoubleArgumentType.doubleArg())
                    .then(argument("y", DoubleArgumentType.doubleArg())
                        .then(argument("z", DoubleArgumentType.doubleArg())
                            .executes(TeleportCommand::fastTp)
                        )
                    )
                )
            );
        });
    }
    
    private static int fastTp(CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity player = context.getSource().getPlayer();
        if (player == null) return 0;
        
        // Получаем координаты из аргументов команды
        double targetX = DoubleArgumentType.getDouble(context, "x");
        double targetY = DoubleArgumentType.getDouble(context, "y");
        double targetZ = DoubleArgumentType.getDouble(context, "z");
        
        // Вектор от игрока к цели
        Vec3d toTarget = new Vec3d(
            targetX - player.getX(),
            targetY - player.getY(),
            targetZ - player.getZ()
        );
        
        // Расстояние до цели
        double distance = toTarget.length();
        
        // Скорость зависит от расстояния (чем дальше — тем быстрее, но не быстрее 8.0)
        double maxSpeed = 6.0;
        double speed = Math.min(distance * 0.5, maxSpeed);
        
        // Нормализуем вектор и умножаем на скорость
        Vec3d velocity = toTarget.normalize().multiply(speed);
        
        // Добавляем небольшой подброс вверх для плавности
        velocity = velocity.add(0, 0.3, 0);
        
        // Устанавливаем скорость игрока
        player.setVelocity(velocity);
        
        // Звуки для эпичности
        player.getWorld().playSound(null, player.getBlockPos(), 
            SoundEvents.ENTITY_FIREWORK_ROCKET_BLAST, SoundCategory.PLAYERS, 1.5F, 0.8F);
        player.getWorld().playSound(null, player.getBlockPos(), 
            SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 1.0F, 1.2F);
        
        // Сообщение игроку
        player.sendMessage(Text.literal("§a✧ Летим к §e" + (int)targetX + " " + (int)targetY + " " + (int)targetZ + " §a✧"), false);
        
        return 1;
    }
}