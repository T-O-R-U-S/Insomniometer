package torus.insomniometer.client

import com.mojang.blaze3d.systems.RenderSystem
import net.fabricmc.api.Environment
import net.fabricmc.api.EnvType
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawableHelper
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.Identifier



var clientState = 0

@Environment(EnvType.CLIENT)
class Client : HudRenderCallback {
    val PHANTOM_GREEN = Identifier(torus.insomniometer.MOD_ID, "textures/insomnia/phantom.png")
    val PHANTOM_RED = Identifier(torus.insomniometer.MOD_ID, "textures/insomnia/phantom_red.png")

    // Instead of performing all these checks every frame, the state changes should be delegated to an external function
    override fun onHudRender(matrixStack: MatrixStack?, tickDelta: Float) {

        val window = MinecraftClient.getInstance()?.window ?: return

//        val stats = player.statHandler

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
                horizontalCenter-110,
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

//        for (i in 0..10) {
//            DrawableHelper.drawTexture(
//                    matrixStack,
//                    horizontal_center-94 + (i*9), // X position
//                    vertical_bottom-54, // Y position
//                    0f, // u
//                    0f, // v
//                    16, //width
//                    16, // height
//                    16, // textureWidth
//                    16 // textureHeight
//            )
//        }
    }
}