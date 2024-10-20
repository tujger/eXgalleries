package com.edeqa.exgalleries.helpers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PictureLoader {

	private static ExecutorService es = null;
	private static final Object sync = new Object();

	private static ExecutorService getExecutor() {
		if (es == null) {
			synchronized (sync) {
				if (es == null) {
					es = Executors.newFixedThreadPool(3);
				}
			}
		}
		return es;
	}

	public static void saveImage(final String imageUrl, final String destinationFile) {
//if(true)return;
		Thread saveThread=new Thread(new Runnable() {

			@Override
			public void run()
			{
				try {
					new File(destinationFile).getParentFile().mkdirs();
					InputStream is = null;
					OutputStream os = null;

					URL url = new URL(imageUrl);
					is = url.openStream();
					os = new FileOutputStream(destinationFile);

					byte[] b = new byte[2048];
					Integer length;

					while ((length = is.read(b)) != -1) {
						os.write(b, 0, length);
					}
					is.close();
					os.close();
					is = null;
					os = null;
					url = null;
					b = null;
					length = null;
					// is.reset();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		getExecutor().execute(saveThread);
//		saveThread.start();

	}
}
