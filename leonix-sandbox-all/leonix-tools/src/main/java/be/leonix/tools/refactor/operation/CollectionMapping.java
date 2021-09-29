package be.leonix.tools.refactor.operation;

import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

/**
 * @author Ken Audenaert
 */
public enum CollectionMapping {
	
	GUAVA_ARRAY_LIST     ("com.google.common.collect.Lists","java.util.ArrayList"),
	GUAVA_HASH_SET       ("com.google.common.collect.Sets" ,"java.util.HashSet"),
	GUAVA_LINKED_HASH_SET("com.google.common.collect.Sets" ,"java.util.LinkedHashSet"),
	GUAVA_TREE_SET       ("com.google.common.collect.Sets" ,"java.util.TreeSet"),
	GUAVA_HASH_MAP       ("com.google.common.collect.Maps" ,"java.util.HashMap"),
	GUAVA_LINKED_HASH_MAP("com.google.common.collect.Maps" ,"java.util.LinkedHashMap"),
	GUAVA_TREE_MAP       ("com.google.common.collect.Maps" ,"java.util.TreeMap");
	
	private final String factoryType;
	private final String createdType;
	
	CollectionMapping(String factoryType, String createdType) {
		this.factoryType = Objects.requireNonNull(factoryType);
		this.createdType = Objects.requireNonNull(createdType);
	}
	
	public String getFactoryType() {
		return factoryType;
	}
	
	public String getFactoryTypeRef() {
		return StringUtils.substringAfterLast(factoryType, ".");
	}
	
	public String getCreatedType() {
		return createdType;
	}
	
	public String getCreatedTypeRef() {
		return StringUtils.substringAfterLast(createdType, ".");
	}
}
