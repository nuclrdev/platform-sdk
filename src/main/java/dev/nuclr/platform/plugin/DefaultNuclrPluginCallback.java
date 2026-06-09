/*

	Copyright 2026 Sergio, Nuclr (https://nuclr.dev)
	
	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at
	
	http://www.apache.org/licenses/LICENSE-2.0
	
	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.

*/
package dev.nuclr.platform.plugin;

/**
 * No-op implementation of {@link NuclrPluginCallback} intended for use in
 * tests or as a placeholder where progress reporting is not needed. All
 * callback methods are empty; {@link #isCancelled()} always returns
 * {@code false}.
 */
public class DefaultNuclrPluginCallback implements NuclrPluginCallback {

	/** Creates a new {@code DefaultNuclrPluginCallback}. */
	public DefaultNuclrPluginCallback() {}

	@Override
	public void onStart(String description) {}

	@Override
	public void onProgress(long current, long total) {}

	@Override
	public void onComplete() {}

	@Override
	public void onError(String description, Exception e) {}

	@Override
	public boolean isCancelled() {
		return false;
	}

}
