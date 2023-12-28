/*
 * Copyright (c) 2023 Andre Schweiger
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
package me.andre111.mambience.condition;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import me.andre111.mambience.MAPlayer;

public final class ConditionEntities extends Condition {
	private final String entityOrTag;
	private final int minCount;
	
	private List<String> cachedEntities;
	
	public ConditionEntities(String entityOrTag, int minCount) {
		if(entityOrTag == null) throw new IllegalArgumentException("Entity / Entitytag cannot be null");
		if(minCount < 1) throw new IllegalArgumentException("Minimum count is outside valid range [1,...]");
		
		this.entityOrTag = entityOrTag;
		this.minCount = minCount;
	}

	@Override
	public boolean matches(MAPlayer player) {
		// cache actual entity names
		if(cachedEntities == null) {
			cachedEntities = new ArrayList<>();
			if(entityOrTag.startsWith("#")) {
				cachedEntities.addAll(player.getAccessor().getEntityTag(entityOrTag.substring(1)));
			} else {
				cachedEntities.add(entityOrTag);
			}
		}
		
		// get data
		Map<String, Integer> scanData = player.getScanner().getScanEntityData();
		
		int count = 0;
		if(scanData != null) {
			for(String entity : cachedEntities) {
				count += scanData.getOrDefault(entity, 0);
			}
		}
		
		return count >= minCount;
	}
}
