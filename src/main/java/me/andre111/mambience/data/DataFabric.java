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
package me.andre111.mambience.data;

import java.io.IOException;
import java.io.InputStream;

import net.minecraft.resource.Resource;

public class DataFabric implements Data {
	private final Resource resource;
	
	public DataFabric(Resource resource) {
		this.resource = resource;
	}

	@Override
	public InputStream openInputStream() throws IOException {
		return resource.getInputStream();
	}
}
