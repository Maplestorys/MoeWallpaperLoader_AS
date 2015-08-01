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
package com.maplestory.moewallpaperloader;

import com.nostra13.universalimageloader.core.DisplayImageOptions;

/**
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 *
 */
public final class Constants {


	public static final String[] IMAGES = new String[] {
		            "http://g.hiphotos.baidu.com/image/w%3D310/sign=122a9afdd588d43ff0a997f34d1fd2aa/79f0f736afc37931e47cbba1e8c4b74543a91125.jpg",
					"http://e.hiphotos.baidu.com/image/w%3D310/sign=78a5e174d488d43ff0a997f34d1fd2aa/79f0f736afc379318ef3c028e9c4b74543a91113.jpg",
					"http://b.hiphotos.baidu.com/image/w%3D310/sign=53357884e8c4b7453494b117fffd1e78/0bd162d9f2d3572c48bf62718913632762d0c33a.jpg",
					"http://f.hiphotos.baidu.com/image/w%3D310/sign=42164f910855b3199cf9847473a88286/03087bf40ad162d91f06618c13dfa9ec8b13cdb2.jpg"
					};


	private Constants() {
	}


	public static class Config {
		public static final boolean DEVELOPER_MODE = false;
	}
	

	public static class Extra {
		public static final String IMAGES = "com.maplestory.moewallpaperloader.IMAGES";
		public static final String IMAGE_POSITION = "com.maplestory.moewallpaperloader.IMAGE_POSITION";
	}
}
