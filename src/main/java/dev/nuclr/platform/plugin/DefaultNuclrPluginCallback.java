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

public class DefaultNuclrPluginCallback implements NuclrPluginCallback {

	@Override
	public void onStart(String description) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProgress(long current, long total) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onComplete() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onError(String description, Exception e) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isCancelled() {
		// TODO Auto-generated method stub
		return false;
	}

}
