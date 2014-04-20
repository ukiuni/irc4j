package org.ukiuni.irc4j;

/*
 * Copyright [2013] [ukiuni]
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
/**
 * @see org.ukiuni.irc4j.IRCEventHandler
 * @author ukiuni
 */
public abstract class IRCEventAdapter implements IRCEventHandler {

	@Override
	public void onMessage(String channelName, String from, String message) {
	}

	@Override
	public void onServerMessage(int id, String message) {
	}

	@Override
	public void onError(Throwable e) {
	}

	@Override
	public void onJoinToChannel(String channelName, String nickName) {
	}

	@Override
	public void onPartFromChannel(String channelName, String nickName, String message) {
	}

	@Override
	public void onDisconnectedOnce() {
	}
}
