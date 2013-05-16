package org.irc4j;

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
 * Error recieve from IRC server.
 * 
 * @author ukiuni
 */
@SuppressWarnings("serial")
public class RecievedFromIRCServerException extends Exception {
	private String id;
	private String idString;

	public RecievedFromIRCServerException(String id, String idString, String message) {
		super(message);
		this.id = id;
		this.idString = idString;
	}

	public String getId() {
		return id;
	}

	public String getIdString() {
		return idString;
	}
}
