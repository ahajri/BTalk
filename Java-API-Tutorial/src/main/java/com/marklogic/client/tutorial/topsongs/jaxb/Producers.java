/*
 * Copyright 2012 MarkLogic Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.marklogic.client.tutorial.topsongs.jaxb;

import java.util.List;

public class Producers {
	public Producers() {
		super();
	}

	private List<String> producer;

	public List<String> getProducer() {
		return producer;
	}
	public void setProducer(List<String> producer) {
		this.producer = producer;
	}

	@Override
	public String toString() {
		return "Producers [producer=" + producer + "]";
	}
}
