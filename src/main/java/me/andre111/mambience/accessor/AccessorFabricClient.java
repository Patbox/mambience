/*
 * Copyright (c) 2024 Andre Schweiger
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
package me.andre111.mambience.accessor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import me.andre111.mambience.fabric.ClientsideDataLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntryList.Named;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Identifier;

public class AccessorFabricClient extends AccessorFabric {
	public AccessorFabricClient(UUID playerUUID) {
		super(playerUUID);
	}

	// Player related methods
	@SuppressWarnings("resource")
	@Override
	public boolean updatePlayerInstance() {
		player = MinecraftClient.getInstance().player;
		return player != null;
	}

	// Sound related methods
	@Override
	public void playSound(String sound, float volume, float pitch) {
		MinecraftClient.getInstance().getSoundManager().play(new PositionedSoundInstance(new Identifier(sound), SoundCategory.AMBIENT, volume, pitch, SoundInstance.createRandom(), false, 0, SoundInstance.AttenuationType.LINEAR, (float)player.getX(), (float)player.getY(), (float)player.getZ(), false));
	}
	
	@Override
	public void playSound(String sound, double x, double y, double z, float volume, float pitch) {
		MinecraftClient.getInstance().getSoundManager().play(new PositionedSoundInstance(new Identifier(sound), SoundCategory.AMBIENT, volume, pitch, SoundInstance.createRandom(), false, 0, SoundInstance.AttenuationType.LINEAR, x, y, z, false));
	}

	@Override
	public void playGlobalSound(String sound, double x, double y, double z, float volume, float pitch) {
		MinecraftClient.getInstance().getSoundManager().play(new PositionedSoundInstance(new Identifier(sound), SoundCategory.PLAYERS, volume, pitch, SoundInstance.createRandom(), false, 0, SoundInstance.AttenuationType.LINEAR, x, y, z, false));
	}

	@Override
	public void stopSound(String sound) {
		MinecraftClient.getInstance().getSoundManager().stopSounds(new Identifier(sound), SoundCategory.AMBIENT);
	}

	// Particle related methods
	@SuppressWarnings("resource")
	@Override
	public void addParticle(String type, String parameters, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
		if(MinecraftClient.getInstance().world == null) return;
		
		ParticleEffect particle = getParticleEffect(type, parameters);
		if(particle != null) {
			MinecraftClient.getInstance().world.addParticle(particle, x, y, z, velocityX, velocityY, velocityZ);
		}
	}
	
	@Override
	protected <T> List<Identifier> getTagEntries(RegistryKey<? extends Registry<T>> key, Identifier id) {
		// check for presence of server defined tag and use it, otherwise fall back to clientside data loader
		TagKey<T> tagKey = TagKey.of(key, id);
		Optional<Named<T>> optional = player.getEntityWorld().getRegistryManager().get(key).getEntryList(tagKey);
		if(optional.isPresent()) {
			return optional.get().stream().map(entry -> entry.getKey().get().getValue()).collect(Collectors.toList());
		} else {
			return ClientsideDataLoader.getTag(key.getValue(), id);
		}
	}
}
