package huka.com.repli;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.view.View;
import android.widget.Button;

/**
 * Created by hugo on 3/8/15.
 */
public class CustomDialog extends Dialog implements View.OnClickListener {
    Button okButton, cancelButton;
    Activity mActivity;

    public CustomDialog(Activity activity) {
        super(activity);
        mActivity = activity;
     //   setContentView(R.layout.custom_dialog);
     //   okButton = (Button) findViewById(R.id.button_ok);
        okButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == cancelButton)
            dismiss();
        else {
            Intent i = new Intent(mActivity, LoginActivity.class);
            mActivity.startActivity(i);
        }
    }
}