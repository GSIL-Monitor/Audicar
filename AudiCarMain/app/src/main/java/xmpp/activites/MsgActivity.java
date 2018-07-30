/**
 *
 */
package xmpp.activites;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.beautyyan.beautyyanapp.R;
import com.beautyyan.beautyyanapp.YuYuanApp;

import butterknife.Bind;
import butterknife.ButterKnife;
import xmpp.d3View.D3View;
import xmpp.fragments.MsgFragment;

/**
 * @author MZH
 */
@SuppressLint("ResourceAsColor")
public class MsgActivity extends XMPPBaseActivity {
    @Bind(R.id.view_auto)
    View viewAuto;
    private MsgFragment msgFragment;
    @D3View
    FrameLayout page1;
    @D3View
    View back_btn;
    @D3View
    TextView title_text;


    @Override
    protected void onCreate(Bundle arg0) {
        if (null != arg0) {
            arg0 = null;
        }
        super.onCreate(arg0);
        setContentView(R.layout.acti_main);
        ButterKnife.bind(this);
        YuYuanApp.getIns().setStatusBarForDetail(this, viewAuto);
        title_text.setText("消息");
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishSlowly();
            }
        });
        if (msgFragment == null) {
            msgFragment = new MsgFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(page1.getId(), msgFragment).commit();
        }
//        if (Constant.getInstance().isXmppHasLogined()) {
//            Constants.loginUser = new User(XmppConnection.getInstance().getUserInfo(null));
//        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finishSlowly();
        }
        return false;
    }

    private void finishSlowly() {
        YuYuanApp.getIns().handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 200);
    }
}
