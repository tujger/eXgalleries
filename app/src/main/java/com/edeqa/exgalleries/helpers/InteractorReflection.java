package com.edeqa.exgalleries.helpers;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class InteractorReflection {

	private static final Map<String, Set<Method>> METHOD_CACHE = new HashMap<String, Set<Method>>();

	private InteractorReflection() {
	}

	/**
	 * Finds methods with the given name on the given class.
	 *
	 * @param clazz
	 *            the class
	 * @param name
	 *            the method name
	 * @return the methods
	 */
	public static Set<Method> findMethods(Class<?> clazz, String name) {
		String cacheKey = clazz.getName().concat("::").concat(name);
		if (METHOD_CACHE.containsKey(cacheKey)) {
			return METHOD_CACHE.get(cacheKey);
		}
		Set<Method> methods = new HashSet<Method>();
		for (Method method : clazz.getMethods()) {
			if (method.getName().equals(name)) {
				methods.add(method);
			}
		}
		methods = Collections.unmodifiableSet(methods);
		METHOD_CACHE.put(cacheKey, methods);
		return methods;
	}

	/**
	 * Finds methods with the given name on the given class.
	 *
	 * @param clazz
	 *            the class
	 * @param name
	 *            the method name
	 * @return the methods
	 * @throws NoSuchMethodException
	 */
	public static Method findOnlyMethod(Class<?> clazz, String name) throws NoSuchMethodException {
		Set<Method> set = findMethods(clazz, name);
		if (set.size() != 1)
			throw new NoSuchMethodException(name + " zero or not exactly one");
		return set.iterator().next();
	}
}
