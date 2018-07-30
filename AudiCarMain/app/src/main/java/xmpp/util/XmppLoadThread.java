package xmpp.util;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.beautyyan.beautyyanapp.R;


public abstract class XmppLoadThread {

	boolean isHint;
	public ProgressDialog mdialog;
	private Context c;
//	private ExecutorService FULL_TASK_EXECUTOR;

	@SuppressLint("NewApi")
	public XmppLoadThread(Context _mcontext) {
		isHint = true;
		c = _mcontext;
//		FULL_TASK_EXECUTOR = (ExecutorService) Executors.newCachedThreadPool();
		new AsyncTask<Void, Integer, Object>() {

			@Override
			protected Object doInBackground(Void... arg0) {
				return load();
			}

			@Override
			protected void onPostExecute(Object result) {
				if (isHint && (mdialog == null || !mdialog.isShowing())) {
					return;
				} else {
					try {
						result(result);
						if (isHint && (mdialog != null && mdialog.isShowing())) {
							mdialog.dismiss();

						}
					} catch (Throwable e) {
						e.printStackTrace();
						if (mdialog != null && mdialog.isShowing()) {
							mdialog.dismiss();
						}
					}
				}
			}

			@Override
			protected void onPreExecute() {
				if (isHint) {
					try {
						mdialog =  ProgressDialog.show(c, null, c
								.getResources().getString(R.string.dialog_load_content));
						mdialog.setCancelable(false);
//						mdialog.setContentView(R.layout.dialog_loadding);
						mdialog.getWindow().setLayout(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);
						mdialog.setIndeterminateDrawable(c.getResources().getDrawable(R.drawable.progress_dialog_style));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}

		}.execute();
	}

	protected abstract Object load();

	protected abstract void result(Object object);

}
