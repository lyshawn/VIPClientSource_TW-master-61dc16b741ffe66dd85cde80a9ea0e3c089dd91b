package com.nsplay.vip;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.TextView;

public class NPVIPProgressDialog extends Dialog {

	private Context context = null;

	private static NPVIPProgressDialog customProgressDialog = null;

	private static int layout_id, image_id, txt_id, style_id, animation_id;

	private ImageView imageView;

	public NPVIPProgressDialog(Context context) {

		super(context);

		this.context = context;

	}

	public NPVIPProgressDialog(Context context, int theme) {

		super(context, theme);

		this.context = context;
	}

	public static NPVIPProgressDialog createDialog(Context context) {

		layout_id = context.getResources().getIdentifier("np_vip_progressdialog", "layout", context.getPackageName());

		image_id = context.getResources().getIdentifier("loadingImageView", "id", context.getPackageName());

		txt_id = context.getResources().getIdentifier("id_tv_loadingmsg", "id", context.getPackageName());

		style_id = context.getResources().getIdentifier("VIPProgressDialog", "style", context.getPackageName());

		animation_id = context.getResources().getIdentifier("toollsit_progress_round", "drawable", context.getPackageName());

		customProgressDialog = new NPVIPProgressDialog(context , style_id);

		customProgressDialog.setContentView(layout_id);

		customProgressDialog.getWindow().getAttributes().gravity = Gravity.CENTER;

		return customProgressDialog;
	}
	
	

	public void onWindowFocusChanged(boolean hasFocus) {

		if (customProgressDialog == null) {
			return;
		}

		ImageView imageView = (ImageView) findViewById(image_id);

		imageView.setBackgroundResource(animation_id);

		AnimationDrawable animationDrawable = (AnimationDrawable) imageView.getBackground();

		animationDrawable.start();
	}

	public NPVIPProgressDialog setTitile(String strTitle) {

		return customProgressDialog;
	}

	public NPVIPProgressDialog setMessage(String strMessage) {

		TextView tvMsg = (TextView) findViewById(txt_id);

		if (tvMsg != null) {

			tvMsg.setText(strMessage);
		}

		return customProgressDialog;
	}

}
