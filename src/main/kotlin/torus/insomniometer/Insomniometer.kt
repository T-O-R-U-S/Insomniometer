package torus.insomniometer

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.api.Environment
import net.fabricmc.api.EnvType
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents.EndWorldTick
import net.minecraft.client.MinecraftClient
import net.minecraft.stat.Stats
import net.minecraft.world.Difficulty
import net.minecraft.world.GameRules
// HUD rendering
import torus.insomniometer.client.Client
import torus.insomniometer.client.clientState

val MOD_ID = "insomniometer"

@Environment(EnvType.CLIENT)
class Insomniometer : ClientModInitializer {
    override fun onInitializeClient() {
        // Check at the end of every tick if the Phantom icon should be rendered
        EndWorldTick {
            val client = MinecraftClient.getInstance() ?: return@EndWorldTick

            val server = client.server ?: return@EndWorldTick
            val world = it

            // If the insomnia doesn't apply, don't render the alert.
            if (!server.gameRules.get(GameRules.DO_INSOMNIA).get())
                return@EndWorldTick
            // If phantoms can't spawn, don't render the alert
            if (world.difficulty == Difficulty.PEACEFUL || !world.gameRules.get(GameRules.DO_MOB_SPAWNING).get())
                return@EndWorldTick
            // If the player doesn't exist, don't render the alert.
            val player = client.player ?: return@EndWorldTick
            // If the player can't be damaged, don't render the alert.
            if(player.abilities.invulnerable)
                return@EndWorldTick

            // Getting the `client.player` directly doesn't seem to update it unless you open the stats screen.
            // This is why I need the server's player object.
            val serverStats = server.playerManager.getPlayer(player.gameProfile.name)?.statHandler ?: return@EndWorldTick

            val timeSinceRest = serverStats.getStat(Stats.CUSTOM, Stats.TIME_SINCE_REST)

            if (timeSinceRest < 60000) {
                // No phantom
                clientState = 0
                return@EndWorldTick
            }

            // Green-eyed phantom
            clientState = 1


            if (timeSinceRest > 71000) {
                // Phantom with red eyes
                clientState = 2
//                RenderSystem.setShaderTexture(0, PHANTOM_RED)
            }

//            DrawableHelper.drawTexture(
//                    matrixStack,
//                    horizontalCenter-110,
//                    verticalBottom - 18,
//                    16,
//                    16,
//                    0f,
//                    0f,
//                    16,
//                    16,
//                    16,
//                    16
//            )
        }

        // Initialize HUD rendering
        HudRenderCallback.EVENT.register(Client())
    }
}