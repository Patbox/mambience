/*
 * Copyright (c) 2019 André Schweiger
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package me.andre111.mambience.fabric.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.andre111.mambience.MAmbienceFabric;
import net.minecraft.client.MinecraftClientGame;

@Mixin(MinecraftClientGame.class)
public class MixinMinecraftClientGame {

	@Inject(at = @At("HEAD"), method = "onStartGameSession")
	public void onStartGameSession(CallbackInfo callbackInfo) {
		MAmbienceFabric.instance.onStartGameSession();
	}
}