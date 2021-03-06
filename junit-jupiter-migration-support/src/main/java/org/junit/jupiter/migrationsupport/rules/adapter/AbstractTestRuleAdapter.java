/*
 * Copyright 2015-2016 the original author or authors.
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.junit.jupiter.migrationsupport.rules.adapter;

import static org.junit.platform.commons.meta.API.Usage.Internal;
import static org.junit.platform.commons.util.ReflectionUtils.findMethod;
import static org.junit.platform.commons.util.ReflectionUtils.invokeMethod;

import java.lang.reflect.Method;

import org.junit.jupiter.migrationsupport.rules.member.TestRuleAnnotatedMember;
import org.junit.platform.commons.JUnitException;
import org.junit.platform.commons.meta.API;
import org.junit.platform.commons.util.Preconditions;
import org.junit.platform.commons.util.StringUtils;
import org.junit.rules.TestRule;

/**
 * @since 5.0
 */
@API(Internal)
public abstract class AbstractTestRuleAdapter implements GenericBeforeAndAfterAdvice {

	private final TestRule target;

	public AbstractTestRuleAdapter(TestRuleAnnotatedMember annotatedMember, Class<? extends TestRule> adapteeClass) {
		this.target = annotatedMember.getTestRule();
		Preconditions.condition(adapteeClass.isAssignableFrom(this.target.getClass()),
			() -> adapteeClass + " is not assignable from " + this.target.getClass());
	}

	protected Object executeMethod(String name) {
		return executeMethod(name, new Class<?>[0]);
	}

	protected Object executeMethod(String methodName, Class<?>[] parameterTypes, Object... arguments) {
		Method method = findMethod(this.target.getClass(), methodName, parameterTypes).orElseThrow(
			() -> new JUnitException(String.format("Failed to find method %s(%s) in class %s", methodName,
				StringUtils.nullSafeToString(parameterTypes), this.target.getClass().getName())));

		return invokeMethod(method, this.target, arguments);
	}

}
