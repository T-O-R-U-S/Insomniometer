package torus.insomniometer

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.api.Environment
import net.fabricmc.api.EnvType
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback
// HUD rendering
import torus.insomniometer.client.Client
import torus.insomniometer.client.clientState

val MOD_ID = "insomniometer"

@Environment(EnvType.CLIENT)
class Insomniometer : ClientModInitializer {
    override fun onInitializeClient() {
        // Initialize HUD rendering
        HudRenderCallback.EVENT.register(Client())
    }
}