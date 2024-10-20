package com.edeqa.exgalleries.helpers;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import com.edeqa.exgalleries.R;
import com.edeqa.exgalleries.interfaces.TaskCompletedInterface;

public class ReadFileTask extends AsyncTask<Void, Void, TextFileFromUri> {

	public static final int DIALOG_INDETERMINATE = 0;
	public static final int DIALOG_PROGRESS = 1;

	private final Context context;
	private final Uri uri;
	private TextFileFromUri fileFromUri;
	private int iDialogMode = 0;
	private ProgressDialog dialog = null;
	private final TaskCompletedInterface listener;

	public ReadFileTask(Context context, Uri uri, TextFileFromUri fileFromUri) {
		this.context = context;
		this.uri = uri;
		this.fileFromUri = fileFromUri;
		this.listener = (TaskCompletedInterface) context;
	}

	public void setProgressDialog(int DIALOG_MODE) {
		this.iDialogMode = DIALOG_MODE;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		dialog = new ProgressDialog(context);
		if (iDialogMode == 0) {
			dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			dialog.setMessage(context.getString(R.string.please_wait));
			dialog.setIndeterminate(true);
			// dialog.setCanceledOnTouchOutside(false);
			dialog.show();
		}
	}

	@Override
	protected TextFileFromUri doInBackground(Void... params) {
		fileFromUri = new TextFileFromUri(context, uri);
		fileFromUri.read();
		return fileFromUri;
	}

	@Override
	protected void onPostExecute(TextFileFromUri result) {
		super.onPostExecute(result);
		if (isCancelled())
			return;
		if (dialog.isShowing()) {
			dialog.dismiss();
			listener.onTaskCompleted(result);
		}
	}

}
