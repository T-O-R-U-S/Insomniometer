package torus.insomniometer.client

import com.mojang.blaze3d.systems.RenderSystem

import net.fabricmc.api.Environment
import net.fabricmc.api.EnvType
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback

import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawableHelper
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.Identifier
import net.minecraft.stat.Stats
import net.minecraft.world.Difficulty
import net.minecraft.world.GameRules

var clientState = 0

@Environment(EnvType.CLIENT)
class Client : HudRenderCallback {
    val PHANTOM_GREEN = Identifier(torus.insomniometer.MOD_ID, "textures/insomnia/phantom.png")
    val PHANTOM_RED = Identifier(torus.insomniometer.MOD_ID, "textures/insomnia/phantom_red.png")

    // Instead of performing all these checks every frame, the state changes should be delegated to an external function
    // that is scheduled for every tick
    override fun onHudRender(matrixStack: MatrixStack?, tickDelta: Float) {
        val client = MinecraftClient.getInstance() ?: return

        val server = client.server ?: return
        val window = MinecraftClient.getInstance()?.window ?: return
        val world = client.world ?: return
        val player = client.player ?: return

        // Getting the `client.player` directly doesn't seem to update it unless you open the stats screen.
        // This is why I need the server's player stats object.
        val serverStats = server.playerManager.getPlayer(player.gameProfile.name)?.statHandler ?: return

        val timeSinceRest = serverStats.getStat(Stats.CUSTOM, Stats.TIME_SINCE_REST)

        // If insomnia doesn't apply, don't render the alert.
        if (!server.gameRules.get(GameRules.DO_INSOMNIA).get())
            return
        // If phantoms can't spawn, don't render the alert
        if (world.difficulty == Difficulty.PEACEFUL || !world.gameRules.get(GameRules.DO_MOB_SPAWNING).get())
            return
        // If the player can't be damaged, don't render the alert.
        if(player.abilities.invulnerable)
            return

        if (timeSinceRest < 60000) {
            return
        }

        // Green-eyed phantom
        RenderSystem.setShaderTexture(0, PHANTOM_GREEN)

        // If phantoms are very close to actively spawning, turn the phantom's eyes red
        if (timeSinceRest > 71000) {
            RenderSystem.setShaderTexture(0, PHANTOM_RED)
        }

        val horizontalCenter = window.scaledWidth / 2
        val verticalBottom = window.scaledHeight

        // Getting the `client.player` directly doesn't seem to update it unless you open the stats screen.
        // This is why I need the server's player object.
        val texture = when(clientState) {
            1 -> PHANTOM_GREEN
            2 -> PHANTOM_RED
            else -> return
        }

        RenderSystem.setShaderTexture(0, texture)

        DrawableHelper.drawTexture(
                matrixStack,
                horizontalCenter + 110,
                verticalBottom - 18,
                16,
                16,
                0f,
                0f,
                16,
                16,
                16,
                16
        )
    }
}