package com.edeqa.exgalleries.helpers;

public interface EntityInterface<T> {

	T getThis();

	boolean setProperty(String property, String value);

	long getId();

	T setId(long id);

	String getName();

	T setName(String name);

	String getTitle();

	T setTitle(String title);

	String getLink();

	T setLink(String link);

	String getDescription();

	T setDescription(String description);

	long getCreateDate();

	T setCreateDate(long createDate);

	long getUpdateDate();

	T setUpdateDate(long updateDate);

	long getAccessDate();

	T setAccessDate(long accessDate);

}
