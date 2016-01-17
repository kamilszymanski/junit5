/*
 * Copyright 2015-2016 the original author or authors.
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.junit.gen5.engine.junit5.descriptor;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import org.junit.gen5.api.extension.ExtensionContext;
import org.junit.gen5.engine.EngineExecutionListener;
import org.junit.gen5.engine.TestDescriptor;

abstract class AbstractExtensionContext implements ExtensionContext {

	private final Map<String, Object> attributes = new HashMap<>();

	//Will replace attributes if done
	private final ExtensionValuesStore store;

	private final ExtensionContext parent;
	private final EngineExecutionListener engineExecutionListener;
	private final TestDescriptor testDescriptor;

	AbstractExtensionContext(ExtensionContext parent, EngineExecutionListener engineExecutionListener,
			TestDescriptor testDescriptor) {
		this.parent = parent;
		this.engineExecutionListener = engineExecutionListener;
		this.testDescriptor = testDescriptor;
		this.store = createStore(parent);
	}

	private final ExtensionValuesStore createStore(ExtensionContext parent) {
		ExtensionValuesStore parentStore = null;
		if (parent != null) {
			parentStore = ((AbstractExtensionContext) parent).store;
		}
		return new ExtensionValuesStore(parentStore);
	}

	@Override
	public void publishReportEntry(Map<String, String> entry) {
		engineExecutionListener.reportingEntryPublished(this.testDescriptor, entry);
	}

	@Override
	public Optional<ExtensionContext> getParent() {
		return Optional.ofNullable(parent);
	}

	@Override
	public Object getAttribute(String key) {
		Object value = attributes.get(key);
		if (value == null && parent != null)
			return parent.getAttribute(key);
		return value;
	}

	@Override
	public void putAttribute(String key, Object value) {
		attributes.put(key, value);
	}

	@Override
	public Object removeAttribute(String key) {
		return attributes.remove(key);
	}

	protected TestDescriptor getTestDescriptor() {
		return testDescriptor;
	}

	//Storing methods. Not done yet.

	public Object get(Object key) {
		return store.get(key);
	}

	public void put(Object key, Object value) {
		store.put(key, value);
	}

	public Object getOrComputeIfAbsent(Object key, Function<Object, Object> defaultCreator) {
		return store.getOrComputeIfAbsent(key, defaultCreator);
	}

	public void remove(Object key) {
		store.remove(key);
	}

}