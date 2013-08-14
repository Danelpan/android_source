/*******************************************************************************
 * Copyright 2011-2013 Sergey Tarasevich
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.android.kit.bitmap.cache.disc.impl;

import java.io.File;

import com.android.kit.bitmap.cache.disc.BaseDiscCache;
import com.android.kit.bitmap.cache.disc.DiscCacheAware;
import com.android.kit.bitmap.cache.disc.naming.FileNameGenerator;
import com.android.kit.bitmap.core.DefaultConfigurationFactory;

/**
 * Default implementation of {@linkplain DiscCacheAware disc cache}. Cache size is unlimited.
 * 
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 * @since 1.0.0
 * @see BaseDiscCache
 */
public class UnlimitedDiscCache extends BaseDiscCache {

	/** @param cacheDir Directory for file caching */
	public UnlimitedDiscCache(File cacheDir) {
		this(cacheDir, DefaultConfigurationFactory.createFileNameGenerator());
	}

	/**
	 * @param cacheDir Directory for file caching
	 * @param fileNameGenerator Name generator for cached files
	 */
	public UnlimitedDiscCache(File cacheDir, FileNameGenerator fileNameGenerator) {
		super(cacheDir, fileNameGenerator);
	}

	@Override
	public void put(String key, File file) {
		// Do nothing
	}
}
