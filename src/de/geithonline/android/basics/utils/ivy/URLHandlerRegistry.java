/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package de.geithonline.android.basics.utils.ivy;

import android.util.Log;

/**
 *
 */
public final class URLHandlerRegistry {
	private URLHandlerRegistry() {
	}

	private static URLHandler defaultHandler = new BasicURLHandler();

	public static URLHandler getDefault() {
		return defaultHandler;
	}

	public static void setDefault(final URLHandler def) {
		defaultHandler = def;
	}

	/**
	 * This method is used to get appropriate http downloader dependening on Jakarta Commons HttpClient availability in
	 * classpath, or simply use jdk url handling in other cases.
	 * 
	 * @return most accurate http downloader
	 */
	public static URLHandler getHttp() {
		try {
			Class.forName("org.apache.commons.httpclient.HttpClient");

			// temporary fix for IVY-880: only use HttpClientHandler when
			// http-client-3.x is available
			Class.forName("org.apache.commons.httpclient.params.HttpClientParams");

			final Class handler = Class.forName("org.apache.ivy.util.url.HttpClientHandler");
			Log.d("Ivy", "jakarta commons httpclient detected: using it for http downloading");
			return (URLHandler) handler.newInstance();
		} catch (final ClassNotFoundException e) {
			Log.d("Ivy", "jakarta commons httpclient not found: using jdk url handling");
			return new BasicURLHandler();
		} catch (final NoClassDefFoundError e) {
			Log.d("Ivy", "error occurred while loading jakarta commons httpclient: " + e.getMessage());
			Log.d("Ivy", "Using jdk url handling instead.");
			return new BasicURLHandler();
		} catch (final InstantiationException e) {
			Log.d("Ivy", "couldn't instantiate HttpClientHandler: using jdk url handling");
			return new BasicURLHandler();
		} catch (final IllegalAccessException e) {
			Log.d("Ivy", "couldn't instantiate HttpClientHandler: using jdk url handling");
			return new BasicURLHandler();
		}
	}

}
