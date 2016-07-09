package ru.wtg.exgalleries.helpers;

import java.io.File;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;

public abstract class FileFromUri {

	protected Uri uri = null;
	protected Context context = null;
	protected boolean bSuccess;
	protected ProgressDialog progressDialog;
	protected String sError;

	public FileFromUri(Context context, Uri uri) {
		bSuccess = false;
		this.context = context;
		this.uri = uri;
		sError = "";
	}

	public FileFromUri() {
		bSuccess = false;
	}

	public FileFromUri setUri(Uri uri) {
		this.uri = uri;
		return this;
	}

	public Uri getUri() {
		return uri;
	}

	public FileFromUri setContext(Context context) {
		this.context = context;
		return this;
	}

	public boolean isSuccess() {
		return bSuccess;
	}

	public void setProgressDialog(ProgressDialog progressDialog) {
		this.progressDialog = progressDialog;
	}

	public boolean read(Context context, Uri uri) {
		this.context = context;
		this.uri = uri;
		return read();
	}

	public String getErrorMessage() {
		return sError;
	}

	public abstract String toString();

	public abstract boolean read();

	public abstract boolean writeFile(File file);

}
