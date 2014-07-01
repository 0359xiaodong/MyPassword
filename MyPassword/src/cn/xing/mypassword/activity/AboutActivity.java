package cn.xing.mypassword.activity;

import android.app.ActionBar;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import cn.xing.mypassword.R;
import cn.xing.mypassword.app.BaseActivity;
import cn.zdx.lib.annotation.FindViewById;

/**
 * ���ڽ���
 * 
 * @author zengdexing
 * 
 */
public class AboutActivity extends BaseActivity {

	/** Դ���ַ */
	private static final String GITHUB_SOURCE = "https://github.com/o602075123/MyPassword";

	/** �汾��ʾ�ؼ� */
	@FindViewById(R.id.about_version)
	private TextView textView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);

		initActionBar();

		/** ��ʾ�汾 */
		textView.setText(getMyApplication().getVersionName());
	}

	public void onFeedbackClick(View view) {
		startActivity(new Intent(this, FeedbackActivity.class));
	}

	public void onSourceClick(View view) {
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_VIEW);
		intent.setData(Uri.parse(GITHUB_SOURCE));
		try {
			startActivity(intent);
		} catch (ActivityNotFoundException e) {
			showToast(R.string.about_source_open_failed);
		}
	}

	private void initActionBar() {
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);
	}
}
