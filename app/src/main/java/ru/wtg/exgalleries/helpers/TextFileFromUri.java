package ru.wtg.exgalleries.helpers;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.net.Uri;
import ru.wtg.exgalleries.R;
import ru.wtg.exgalleries.interfaces.TextFileFromUriInterface;

public class TextFileFromUri extends FileFromUri implements TextFileFromUriInterface {

	private StringBuffer sb = null;

	public TextFileFromUri() {
		super();
		sb = new StringBuffer("");
	}

	public TextFileFromUri(Context context, Uri uri) {
		super(context, uri);
		sb = new StringBuffer("");
	}

	public boolean read() {

		BufferedInputStream br = null;

		try {
			br = new BufferedInputStream(context.getContentResolver().openInputStream(uri));
			int n;

			byte[] buffer = new byte[1024];

			while ((n = br.read(buffer)) != -1)
				sb.append(new String(buffer, 0, n));

		} catch (IOException e) {
			e.printStackTrace();
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
		} finally {
			if (br != null)
				try {
					br.close();
					bSuccess = true;
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		if (sb.length() > 1e6) {
			sb = new StringBuffer("");
			sError = context.getResources().getString(R.string.file_is_too_big);
			bSuccess = false;
		}

		return bSuccess;
	}

	public StringBuffer getStringBuffer() {
		return sb;
	}

	public String toString() {
		String str = "";
		try {
			str = sb.toString();
		} catch (OutOfMemoryError e) {
			bSuccess = false;
			sError = context.getResources().getString(R.string.out_of_memory);
		}
		return str;
	}

	public boolean writeFile(File file) {
		boolean bWrite = false;

		FileOutputStream fout = null;
		try {
			fout = new FileOutputStream(file);
			fout.write(sb.toString().getBytes("UTF-8"));
			fout.close();
		} catch (FileNotFoundException e) {
			sError = e.getMessage();
			e.printStackTrace();
		} catch (IOException e) {
			sError = e.getMessage();
			e.printStackTrace();
		} finally {
			try {
				fout.close();
				bWrite = true;
			} catch (IOException e) {
				sError = e.getMessage();
				e.printStackTrace();
			}
		}
		return bWrite;
	}

}
