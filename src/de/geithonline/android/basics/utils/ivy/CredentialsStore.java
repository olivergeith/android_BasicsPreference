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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import android.util.Log;

/**
 * 
 */
public final class CredentialsStore {
	/**
	 * A Map of Credentials objects keyed by the 'key' of the Credentials.
	 */
	private static final Map<String, Credentials> KEYRING = new HashMap<String, Credentials>();

	private static final Set<String> SECURED_HOSTS = new HashSet<String>();

	public static final CredentialsStore INSTANCE = new CredentialsStore();

	private CredentialsStore() {
	}

	public void addCredentials(final String realm, final String host, final String userName, final String passwd) {
		if (userName == null) {
			return;
		}
		final Credentials c = new Credentials(realm, host, userName, passwd);
		Log.d("Ivy", "credentials added: " + c);
		KEYRING.put(c.getKey(), c);
		SECURED_HOSTS.add(host);
	}

	public Credentials getCredentials(final String realm, final String host) {
		final String key = Credentials.buildKey(realm, host);
		Log.d("Ivy", "try to get credentials for: " + key);
		return KEYRING.get(key);
	}

	public boolean hasCredentials(final String host) {
		return SECURED_HOSTS.contains(host);
	}

}
