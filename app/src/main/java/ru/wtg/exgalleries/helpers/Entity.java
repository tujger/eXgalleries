package ru.wtg.exgalleries.helpers;

import java.io.File;
import java.io.Serializable;
import java.util.Date;

import android.os.Environment;
import android.text.format.DateFormat;

abstract public class Entity<T extends Entity<T>> implements Serializable {

	private static final long serialVersionUID = 7539584620295610582L;

	public final static int DATEFORMAT_SHORT = 0;
	public final static int DATEFORMAT_LONG = 1;
	public final static int DATEFORMAT_ADAPTIVE = 2;
	public final static int DATEFORMAT_TIME = 3;
	public final static int DATEFORMAT_DATE = 4;

	private long id;
	private String name;
	private String title;
	private String link;// link to html-resource that contains the link to image
	private String imageLink;// link to image parsed from html-resource | or set
								// from library-file
	private String author;
	private String description;
	private Date createDate;
	private Date updateDate;
	private Date accessDate;
	private long parentId;
	private boolean visible;
	private boolean updatable;
	private boolean album;
	private boolean failed;
	private int rating;

	protected abstract T getThis();

	public Entity() {
		setParentId(0);
		setName("");
		setTitle("");
		setLink("");
		setImageLink("");
		setAuthor("");
		setDescription("");
		setCreateDate(null);
		setUpdateDate(null);
		setAccessDate(null);
		setVisible(true);
		setUpdatable(true);
		setFailed(false);
		setAlbum(false);
		setRating(0);
	}

	public boolean setProperty(String property, String value) {
		boolean res = false;
		if (property.equals("name")) {
			setName(value);
			res = true;
		} else if (property.equals("title")) {
			setTitle(value);
			res = true;
		} else if (property.equals("link")) {
			setLink(value);
			res = true;
		} else if (property.equals("image")) {
			setImageLink(value);
			res = true;
		} else if (property.equals("description")) {
			setDescription(value);
			res = true;
		} else if (property.equals("author")) {
			setAuthor(value);
			res = true;
		}
		return res;
	}

	public long getId() {
		return id;
	}

	public T setId(long id) {
		this.id = id;
		return getThis();
	}

	public String getName() {
		return name;
	}

	public T setName(String name) {
		this.name = name.trim();
		return getThis();
	}

	public String getTitle() {
		return title;
	}

	public T setTitle(String title) {
		this.title = title.trim();
		return getThis();
	}

	public String getLink() {
		return link;
	}

	public T setLink(String link) {
		this.link = link.trim();
		return getThis();
	}

	public String getDescription() {
		return description;
	}

	public T setDescription(String description) {
		this.description = description.trim();
		return getThis();
	}

	public Date getCreateDate() {
		return createDate;
	}

	public T setCreateDate(Date createDate) {
		this.createDate = createDate;
		return getThis();
	}

	public T setCreateDate(long createDate) {
		if (createDate > 0)
			this.createDate = new Date(createDate);
		return getThis();
	}

	public String getFormattedCreateDate(int DATEFORMAT_mode) {
		return getDateFormat(DATEFORMAT_mode, createDate);
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public String getFormattedUpdateDate(int DATEFORMAT_mode) {
		return getDateFormat(DATEFORMAT_mode, updateDate);
	}

	@SuppressWarnings("deprecation")
	protected String getDateFormat(int DATEFORMAT_mode, Date date) {
		String str = "";
		if (date == null)
			return "<never>";

		switch (DATEFORMAT_mode) {
		case DATEFORMAT_SHORT:
			str = "yyyy-MM-dd hh:mm:ss";
			break;
		case DATEFORMAT_LONG:
			str = "yyyy-MM-dd hh:mm:ss";
			break;
		case DATEFORMAT_TIME:
			str = "hh:mm:ss";
			break;
		case DATEFORMAT_DATE:
			str = "yyyy-MM-dd";
			break;
		case DATEFORMAT_ADAPTIVE:
			if (date.getDay() == new Date().getDay())
				str = "hh:mm:ss";
			else
				str = "yyyy-MM-dd";
			break;
		}
		return DateFormat.format(str, date).toString();
	}

	public T setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
		return getThis();
	}

	public T setUpdateDate(long updateDate) {
		if (updateDate > 0)
			this.updateDate = new Date(updateDate);
		return getThis();
	}

	public Date getAccessDate() {
		return accessDate;
	}

	public T setAccessDate(Date accessDate) {
		this.accessDate = accessDate;
		return getThis();
	}

	public T setAccessDate(long accessDate) {
		if (accessDate > 0)
			this.accessDate = new Date(accessDate);
		return getThis();
	}

	public String getFormattedAccessDate(int DATEFORMAT_mode) {
		return getDateFormat(DATEFORMAT_mode, accessDate);
	}

	public long getParentId() {
		return parentId;
	}

	public T setParentId(long parentId) {
		this.parentId = parentId;
		return getThis();
	}

	protected String toString(String str) {
		return "ID=" + getId() + ": {" + (getName().length() > 0 ? "NAME=[" + getName() + "] " : "")
				+ (getTitle().length() > 0 ? "TITLE=[" + getTitle() + "] " : "")
				+ (getLink().length() > 0 ? "LINK=[" + getLink() + "] " : "")
				+ (getImageLink().length() > 0 ? "IMAGELINK=[" + getImageLink() + "] " : "")
				+ (getAuthor().length() > 0 ? "AUTHOR=[" + getAuthor() + "] " : "")
				+ (getDescription().length() > 0 ? "DESCRIPTION=[" + getDescription() + "] " : "")
				+ (getCreateDate() != null ? "CREATED=[" + getFormattedCreateDate(DATEFORMAT_ADAPTIVE) + "] " : "")
				+ (getUpdateDate() != null ? "UPDATED=[" + getFormattedUpdateDate(DATEFORMAT_ADAPTIVE) + "] " : "")
				+ (getAccessDate() != null ? "ACCESSED=[" + getFormattedAccessDate(DATEFORMAT_ADAPTIVE) + "] " : "")
				+ (getParentId() > 0 ? "PARENTID=[" + getParentId() + "] " : "")
				+ (isVisible() ? "VISIBLE=[" + isVisible() + "] " : "")
				+ (isUpdatable() ? "UPDATABLE=[" + isUpdatable() + "] " : "")
				+ (isAlbum() ? "ALBUM=[" + isAlbum() + "] " : "") + (isFailed() ? "FAILED=[" + isFailed() + "] " : "")
				+ str + "}";
	}

	@Override
	public abstract String toString();
	// private int thumbType;// type of shown thumbnail: 0 - imageLink, 1 - mask
	// (system), 2 - collage

	public String getImageLink() {
		
		return imageLink;
	}

	public String getImageCache() {
		File file = getImageCachePath();
		String path = null;

		if (file.exists())
			path = "file://" + file.getAbsolutePath();
		else
			path = getImageLink();

		return path;

	}

	public File getImageCachePath() {
		return new File(Environment.getExternalStorageDirectory().getAbsolutePath()
				+ "/Android/data/ru.wtg.exgalleries/cache/" + getParentId() + "/" + getId() + ".jpg");
	}

	public boolean isImageCached() {
		File file = getImageCachePath();
		return file.exists();
	}

	public T setImageLink(String imageLink) {
		
		this.imageLink = imageLink.trim();
		return getThis();
	}

	public String getAuthor() {
		return author;
	}

	public T setAuthor(String author) {
		this.author = author.trim();
		return getThis();
	}

	public int getRating() {
		return rating;
	}

	public T setRating(int rating) {
		if (rating > 5)
			rating = 5;
		else if (rating < 0)
			rating = 0;
		this.rating = rating;
		return getThis();
	}

	public boolean isVisible() {
		return visible;
	}

	public T setVisible(boolean visible) {
		this.visible = visible;
		return getThis();
	}

	public boolean isUpdatable() {
		return updatable;
	}

	public T setUpdatable(boolean updatable) {
		this.updatable = updatable;
		return getThis();
	}

	public boolean isAlbum() {
		return album;
	}

	public T setAlbum(boolean album) {
		this.album = album;
		return getThis();
	}

	public boolean isFailed() {
		return failed;
	}

	public T setFailed(boolean failed) {
		this.failed = failed;
		return getThis();
	}

}
