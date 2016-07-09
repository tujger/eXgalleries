package ru.wtg.exgalleries.helpers;

public interface EntityInterface<T> {

	abstract T getThis();

	public boolean setProperty(String property, String value);

	public long getId();

	public T setId(long id);

	public String getName();

	public T setName(String name);

	public String getTitle();

	public T setTitle(String title);

	public String getLink();

	public T setLink(String link);

	public String getDescription();

	public T setDescription(String description);

	public long getCreateDate();

	public T setCreateDate(long createDate);

	public long getUpdateDate();

	public T setUpdateDate(long updateDate);

	public long getAccessDate();

	public T setAccessDate(long accessDate);

}
