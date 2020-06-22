/*
 * Copyright (c) 2020 André Schweiger
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
package me.andre111.mambience.sound;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import me.andre111.mambience.condition.Condition;
import me.andre111.mambience.config.EngineConfig;
import me.andre111.mambience.player.MAPlayer;

public final class Sound {
	private static final Random RANDOM = new Random();
	
	private final String id;
	private final String sound;
	private final float volume;
	private final float pitch;
	private final List<Condition> conditions;
	private final List<Condition> restrictions;
	private final int cooldownMin;
	private final int cooldownMax;
	
	public Sound(String id, String sound, float volume, float pitch, List<Condition> conditions,
			List<Condition> restrictions, int cooldownMin, int cooldownMax) {
		this.id = id;
		this.sound = sound;
		this.volume = volume;
		this.pitch = pitch;
		this.conditions = new ArrayList<>(conditions);
		this.restrictions = new ArrayList<>(restrictions);
		this.cooldownMin = cooldownMin;
		this.cooldownMax = cooldownMax;
	}

	public void update(MAPlayer maplayer) {
		if(conditionsMet(maplayer)) {
			if(maplayer.updateCooldown(id) <= 0) {
				maplayer.getLogger().log("Play sound "+sound+" at "+volume * EngineConfig.GLOBALVOLUME);
				maplayer.getAccessor().playSound(sound, volume * EngineConfig.GLOBALVOLUME, pitch);
				maplayer.setCooldown(id, cooldownMin + RANDOM.nextInt(cooldownMax - cooldownMin + 1));
			}
		} else if (EngineConfig.STOPSOUNDS && maplayer.getCooldown(id) > 0 /*&& isRestricted(maplayer)(so it doesn't get cut of in so many cases?)*/) {
			//TODO: needs fading in and out, sadly not possible with current protocol
			//      for now disabled with config option to reenable, sound stopping without fadeout is just to abrupt
			maplayer.getLogger().log("Stop sound "+sound);
			maplayer.getAccessor().stopSound(sound);
			maplayer.setCooldown(id, 0);
		}
	}
	
	private boolean conditionsMet(MAPlayer maplayer) {
		return isInConditions(maplayer) && !isRestricted(maplayer);
	}
	
	private boolean isInConditions(MAPlayer maplayer) {
		for(Condition condition : conditions) {
			if(!condition.matches(maplayer)) return false;
		}
		return true;
	}
	
	private boolean isRestricted(MAPlayer maplayer) {
		for(Condition restriction : restrictions) {
			if(restriction.matches(maplayer)) return true;
		}
		return false;
	}
}
