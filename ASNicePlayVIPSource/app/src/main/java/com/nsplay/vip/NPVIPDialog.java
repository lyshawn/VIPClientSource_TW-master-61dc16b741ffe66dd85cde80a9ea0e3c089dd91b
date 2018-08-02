package com.nsplay.vip;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextPaint;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by 9splay on 2017/10/30.
 */

public class NPVIPDialog extends Dialog {

    private Activity mAct;

    public static int NP_LANDSCAPE_ORIENTATION = Configuration.ORIENTATION_LANDSCAPE;

    public static int NP_PORTRAIT_ORIENTATION = Configuration.ORIENTATION_PORTRAIT;

    private static int DefaultScreenOrientation = NP_LANDSCAPE_ORIENTATION;

    private int orientation = -1;

    private boolean isTestState = false;

    private int vip_dialog_Height = -1;

    private int vip_dialog_Width = -1;

    private int bannershortside = -1;

    private int bannerlongside = -1;

    //標題字體大小
    private float titleTextHeight = 0;
    //q字體大小
    private float qTextHeight = 0;
    //btn字體大小
    private float btnTextHeight = 0;

    private RelativeLayout baseRelativeLayout = null;

    private LinearLayout baseLinearLayout = null;

    private RelativeLayout baseContentRelativeLayout = null;

    private ImageView checkBoxImage = null;

    private String vipTitle = "";

    private String webURL = "";

    private String vipImageURL = "";

    private String checkBoxTxt = "";

    private String nextPageTxt = "";

    private String secondContentTitle = "";

    private String secondPhoneTitle = "";

    private String secondSmsTitle = "";

    private String sendPhoneBtnTxt = "";

    private String sendVerificationPhoneBtnTxt = "";

    private String ButtonText3 = "";

    private String ButtonText2_2 = "";

    private String ButtonText2_3 = "";

    private String ButtonText1_3 = "";

    private String TextBox_Placeholder1 = "";

    private String TextBox_Placeholder2 = "";

    private String thirdContentTitle = "";

    private String thirdRemainTitle = "";

    private String thirdRemainTxt = "";

    private String thirdLineHint = "";

    private String thirdLineRemind = "";

    private String sendDataBtnTxt = "";

    private String fourthContentTitle = null;

    private String fourthGiftTxt = "";

    private String fourthCloseBtnTxt = "";

    private String EventBNUrl = "";

    private String areaCode = "";

    private boolean isRemindToday = true;

    private String[] titleArray = null;

    private int currentPage = 0;

    private String[] AreaArray = null;

    private String[] AreaCodeArray = null;

    private String[] DescriptionArray = null;

    private EditText countryCodesEditText = null;

    private EditText smsEditText = null;

    private NPVIPHttpClient npvipHttpClient = null;

    private boolean isBinded = false;

    private String bindedPhoneNumber = "";

    private boolean isLoading = false;

    private String selectAreaCode = "";

    private Button smsBtn = null;

    private Button phoneBtn = null;

    private boolean isSendSMS = false;

    private String sendPhoneNumber = "";

    private String smsPhoneNumber = "";

    private Spinner countryCodesSpr = null;

    private NPVIPProgressDialog vipProgressDialog = null;

    private OnVIPBindingListener mlistener = null;

    private String CommunicationSoftWare = "";

    private String GiftName = "";

    private EditText clearEditText = null;

    private int type;

    public interface OnVIPBindingListener {
        public void onEvent(int Code, String Message);
    }

//    public NPVIPDialog(Activity act) {
////        this(act, false, new Bundle(), DefaultScreenOrientation, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
//        this(act, false, new Bundle(), DefaultScreenOrientation, android.R.style.Theme_Holo_Light, null);
//
//
//    }
//
//    public NPVIPDialog(Activity act, int screenOrientation, OnVIPBindingListener onVIPBindingListener) {
////        this(act, false, new Bundle(), screenOrientation, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
//        this(act, false, new Bundle(), screenOrientation, android.R.style.Theme_Holo_Light, onVIPBindingListener);
//    }


    public NPVIPDialog(Activity act, boolean isTest, Bundle dataBundle,int vipCommandType, int screenOrientation, int style, OnVIPBindingListener onVIPBindingListener) {
        super(act, style);
        currentPage = 0;
        this.mAct = act;
        this.type = vipCommandType;
        this.orientation = screenOrientation;
        this.isTestState = isTest;
        this.mlistener = onVIPBindingListener;
		processBundle(dataBundle);
        StartCount();
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setCanceledOnTouchOutside(false);
        initMainUI(currentPage);
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        if (vip_dialog_Width > 0 && vip_dialog_Height > 0) {
            params.width = vip_dialog_Width;
            params.height = vip_dialog_Height;
        }
        params.gravity = Gravity.CENTER;
        params.dimAmount = 0.4f;
        window.setAttributes(params);
        window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        createVIPHttpClient();
    }


    private void createVIPHttpClient() {
        Log.e("", "createVIPHttpClient");
        if (npvipHttpClient == null)
            npvipHttpClient = new NPVIPHttpClient(mAct);
        npvipHttpClient.setVIPHttpListener(new NPVIPHttpClient.OnVIPHttpListener() {
            @Override
            public void onEvent(int Code, final String Message, String jsonData, int type) {
                Log.e("NPVIPLog", "Code = " + Code);
                if (vipProgressDialog != null && vipProgressDialog.isShowing()) {
                    vipProgressDialog.dismiss();
                }
                switch (Code) {
                    case 1:
                        if (type == NPVIPCommandType.CheckBinded.getIntValue()) {
                            isBinded = false;
                            isLoading = false;
                            initMainUI(currentPage);
                        } else if (type == NPVIPCommandType.CheckVerifyCode.getIntValue()) {
                            if (smsBtn != null) {
                                smsPhoneNumber = sendPhoneNumber;
                                smsBtn.setBackgroundResource(NPToolUtils.getIDFromDrawable(mAct, "vip_btn_seletor"));
                                smsBtn.setText(ButtonText2_2);
                                smsBtn.setTextColor(Color.parseColor("#FFFFFF"));
                                isSendSMS = true;
                            }
                            mAct.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(mAct, Message, Toast.LENGTH_LONG).show();
                                }
                            });
                        } else if (type == NPVIPCommandType.FianlCheckBinded.getIntValue()) {
                            mAct.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(mAct, Message, Toast.LENGTH_LONG).show();
                                }
                            });
                        } else if (type == NPVIPCommandType.BindPhoneAccount.getIntValue()) {
                            bindedPhoneNumber = smsPhoneNumber;
                            smsBtn.setBackgroundResource(NPToolUtils.getIDFromDrawable(mAct, "vip_btn"));
                            smsBtn.setText(ButtonText2_3);
                            smsBtn.setTextColor(Color.parseColor("#B3A26A"));
                            smsBtn.setOnClickListener(null);
                            phoneBtn.setBackgroundResource(NPToolUtils.getIDFromDrawable(mAct, "vip_btn"));
                            phoneBtn.setText(ButtonText1_3);
                            phoneBtn.setTextColor(Color.parseColor("#B3A26A"));
                            phoneBtn.setOnClickListener(null);
                            countryCodesEditText.setFocusable(false);
                            countryCodesEditText.setFocusableInTouchMode(false);
                            countryCodesEditText.setKeyListener(null);
                            smsEditText.setFocusable(false);
                            smsEditText.setFocusableInTouchMode(false);
                            smsEditText.setKeyListener(null);
                            countryCodesSpr.setEnabled(false);
                            mAct.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(mAct, Message, Toast.LENGTH_LONG).show();
                                }
                            });
                        } else if (type == NPVIPCommandType.CommunicationBIND.getIntValue()) {
                            ++currentPage;
                            initMainUI(currentPage);
                            if (mlistener != null)
                                mlistener.onEvent(1, "Binding Success");
                        }
                        break;
                    case -2:
                        if (type == NPVIPCommandType.CheckBinded.getIntValue()) {
                            isBinded = true;
                            isLoading = false;
                            bindedPhoneNumber = Message;
                            initMainUI(currentPage);
                        } else if (type == NPVIPCommandType.FianlCheckBinded.getIntValue()) {
                            ++currentPage;
                            initMainUI(currentPage);
                        } else if (type == NPVIPCommandType.CommunicationBIND.getIntValue()) {
                            mAct.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(mAct, Message, Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                        break;
                    default:
                        mAct.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(mAct, Message, Toast.LENGTH_LONG).show();
                            }
                        });
                        break;
                }
            }
        });
    }


    private void processBundle(Bundle dataBundle) {

        this.titleArray = null;
        this.titleArray = new String[]{dataBundle.getString("firstTitle", "認證尊爵VIP會員"), dataBundle.getString("secondTitle", "尊爵VIP用戶資料填寫區"), dataBundle.getString("thirdTitle", "尊爵VIP用戶資料填寫區"), dataBundle.getString("fourthTitle", "尊爵VIP用戶獎勵"),dataBundle.getString("firstTitle", "認證金牌VIP會員")};


        this.vipImageURL = dataBundle.getString("imageURL", "http://www.9splay.com/");
        this.checkBoxTxt = dataBundle.getString("checkBoxTxt", "今日不再顯示");
        this.nextPageTxt = dataBundle.getString("verificationBtnTxt", "前往認證");

        this.webURL = dataBundle.getString("webURL", "http://www.9splay.com/");
        this.secondContentTitle = dataBundle.getString("secondContentTitle", "1.手機綁定");
        this.secondPhoneTitle = dataBundle.getString("secondPhoneTitle", "輸入手機號碼");
        this.secondSmsTitle = dataBundle.getString("secondSmsTitle", "回填簡訊驗證碼");
        this.sendPhoneBtnTxt = dataBundle.getString("phoneBtnTxt", "發送驗證簡訊");
        this.sendVerificationPhoneBtnTxt = dataBundle.getString("verificationPhoneBtnTxt", "請先發送驗證簡訊");
        this.TextBox_Placeholder1 = dataBundle.getString("TextBox_Placeholder1", "e.x 0912345678");
        this.TextBox_Placeholder2 = dataBundle.getString("TextBox_Placeholder2", "請輸入您收到的簡訊驗證碼");
        this.ButtonText3 = dataBundle.getString("ButtonText3", "下一步");
        this.ButtonText2_2 = dataBundle.getString("ButtonText2_2", "確定綁定");
        this.ButtonText2_3 = dataBundle.getString("ButtonText2_3", "已成功綁定");
        this.ButtonText1_3 = dataBundle.getString("ButtonText1_3", "此手機號碼已綁定");
        this.thirdContentTitle = dataBundle.getString("thirdContentTitle", "2.您的LINE ID");
        this.thirdLineHint = dataBundle.getString("thirdLineHint", "請輸入您的LINE ID");
        this.thirdLineRemind = dataBundle.getString("thirdLineRemind", "*如沒有使用LINE請填寫您的手機號碼");
        this.thirdRemainTitle = dataBundle.getString("thirdRemainTitle", "注意事項");
        this.thirdRemainTxt = dataBundle.getString("thirdRemainTxt", "1. 表單送出後就無法更改。\n2. 兌換如有異常，請直接聯繫Line@大額儲值VIP客服中心。\n3. 玩家如有冒用他人電話或帳號填寫資料之行為，玩家應自負法律責任，並就他人因冒用所受之損失負完全賠償責任，概與本公司無涉。");
        this.sendDataBtnTxt = dataBundle.getString("sendDataBtnTxt", "資料送出");
        this.CommunicationSoftWare = dataBundle.getString("CommunicationSoftWare", "FB");

        this.fourthContentTitle  = dataBundle.getString("fourthContentTitle", "恭喜您成為尊爵VIP會員，已經將專屬綁定豪禮，寄送到您的背包(郵箱)，成為尊爵VIP會員後，能夠享有更多優質服務！");
        this.fourthGiftTxt = dataBundle.getString("fourthGiftTxt", "專屬綁定豪禮");
        this.fourthCloseBtnTxt = dataBundle.getString("fourthCloseBtnTxt", "關閉");
        this.EventBNUrl = dataBundle.getString("EventBNUrl", "https://banner.9splay.com/EventHTML/Tools/ToolsList/Images/U57YL88.jpg");
        this.GiftName = dataBundle.getString("GiftName", "好大一支槍");
        this.areaCode = dataBundle.getString("areaCode", "");

        try {
            JSONArray jsonArray = new JSONArray(this.areaCode);
            this.AreaArray = new String[jsonArray.length()];
            this.AreaCodeArray = new String[jsonArray.length()];
            this.DescriptionArray = new String[jsonArray.length()];

            for (int i = 0; i <= jsonArray.length(); ++i) {
                this.AreaArray[i] = jsonArray.getJSONObject(i).getString("Area");
                this.AreaCodeArray[i] = jsonArray.getJSONObject(i).getString("AreaCode");
                this.DescriptionArray[i] = jsonArray.getJSONObject(i).getString("Description");
            }
        } catch (Exception e) {
            Log.e("", "Exception e = " + e.toString());
        }
    }


    private void StartCount(){

        DisplayMetrics metrics = new DisplayMetrics();
        mAct.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int ScreenHeight;
        int ScreenWidth;
        if (metrics == null) return;
        ScreenHeight = metrics.heightPixels;
        ScreenWidth = metrics.widthPixels;
        Bundle b = null;
        if (ScreenHeight > ScreenWidth) {
            b = NPResolutionCounter.countbannerratio("", ScreenHeight, ScreenWidth);
        } else {
            b = NPResolutionCounter.countbannerratio("", ScreenWidth, ScreenHeight);
        }
        bannershortside = b.getInt("bannershortside");
        bannerlongside = b.getInt("bannerlongside");
        Log.d("vip", "bannershortside = " + bannershortside + " , bannerlongside = " + bannerlongside);
        if (ScreenHeight > ScreenWidth) {
            //直的
            this.vip_dialog_Width = bannershortside;
            this.vip_dialog_Height = bannerlongside;
        } else {
            //橫的
            this.vip_dialog_Width = bannerlongside;
            this.vip_dialog_Height = bannershortside;
        }
    }

    private void initMainUI(int page) {

        vipTitle = titleArray[page];

        //底
        baseRelativeLayout = null;
        baseRelativeLayout = new RelativeLayout(mAct);
        RelativeLayout.LayoutParams baseRelativeLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        baseRelativeLayout.setBackgroundColor(Color.parseColor("#fffdf0"));
        baseRelativeLayout.setLayoutParams(baseRelativeLayoutParams);

        //底
        baseLinearLayout = null;
        baseLinearLayout = new LinearLayout(mAct);
        RelativeLayout.LayoutParams baseLinearLayoutParams = new RelativeLayout.LayoutParams(vip_dialog_Width - vip_dialog_Width / 10, vip_dialog_Height);
        baseLinearLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        baseLinearLayout.setLayoutParams(baseLinearLayoutParams);
        baseLinearLayout.setOrientation(LinearLayout.VERTICAL);
        baseRelativeLayout.addView(baseLinearLayout);

        //標題layout
        RelativeLayout titleLayout = new RelativeLayout(mAct);
        //titleLayout.setGravity(Gravity.CENTER);
        titleLayout.setBackgroundResource(NPToolUtils.getIDFromDrawable(mAct, "titie"));
        RelativeLayout.LayoutParams titleLayoutParams = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, bannershortside * 2 / 11);
        titleLayoutParams.setMargins(0, 0, 0, bannershortside / 30);
        titleLayout.setLayoutParams(titleLayoutParams);
        baseLinearLayout.addView(titleLayout);

        //標題
        TextView titleView = new TextView(mAct);
        RelativeLayout.LayoutParams titleViewParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        titleViewParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        titleView.setLayoutParams(titleViewParams);
        titleView.setTextSize(53);
        float titleHeight = adjustTvTextHeightSize(titleView, bannershortside / 11, vipTitle);
        titleView.setTextSize(TypedValue.COMPLEX_UNIT_PX, titleHeight);
        titleView.setText(vipTitle);
        titleView.setTextColor(Color.parseColor("#b3a36b"));
        titleLayout.addView(titleView);

        //關閉
        ImageButton vip_clearBtn = new ImageButton(mAct);
        RelativeLayout.LayoutParams vip_clearBtnParams = new RelativeLayout.LayoutParams(bannershortside / 12, bannershortside / 12);
        vip_clearBtnParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        vip_clearBtnParams.addRule(RelativeLayout.CENTER_VERTICAL);
        vip_clearBtn.setLayoutParams(vip_clearBtnParams);
        vip_clearBtn.setBackgroundResource(NPToolUtils.getIDFromDrawable(mAct, "vip_clear"));
        titleLayout.addView(vip_clearBtn);
        vip_clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (mlistener != null)
//                    mlistener.onEvent(-1, "Close VIPDialog");

                if (currentPage == 0 && !isRemindToday) {
                    TimeZone defZone = TimeZone.getDefault();
                    Calendar rightNow = Calendar.getInstance(defZone);
                    CharSequence s = DateFormat.format("yyyy-MM-dd", rightNow.getTime());
                    Saveaccountandpassword.clearVIPDate(mAct);
                    Saveaccountandpassword.saveVIPDate(s.toString(), mAct);
                }
                dismiss();
            }
        });

        //內容layout
        baseContentRelativeLayout = null;
        baseContentRelativeLayout = new RelativeLayout(mAct);
        LinearLayout.LayoutParams baseContentParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        baseContentParams.weight = 1;
        baseContentRelativeLayout.setLayoutParams(baseContentParams);
        baseLinearLayout.addView(baseContentRelativeLayout);

        if (isLoading) {
            if (page == 1) {
                npvipHttpClient.vipHttpConnection(mAct, NPVIPCommandType.CheckBinded, NicePlayVIPClient.Uid, NicePlayVIPClient.AppID, "", "", "");
                baseContentRelativeLayout.addView(vipLoadingPageContent());
            }
        } else {
            if (page == 0) {
                baseContentRelativeLayout.addView(vipFirstPageContent());
            } else if (page == 1) {
                Log.d("DEMO1" , "w");
                baseContentRelativeLayout.addView(vipSecondPageContent());
//                baseContentRelativeLayout.addView(new LinearLayout(mAct));
                Log.d("DEMO1" , "l");
            } else if (page == 2) {
                baseContentRelativeLayout.addView(vipThirdPageContent());
            } else if (page == 3) {
                baseContentRelativeLayout.addView(vipFourthPageContent());
            }else if (page == 4) {
                baseContentRelativeLayout.addView(vipWebviewPageContent());
            }
        }
        Log.d("DEMO1" , "r");
        setContentView(baseRelativeLayout);
        Log.d("DEMO1" , "c");
    }

    private ViewGroup vipLoadingPageContent() {

        //內容底
        RelativeLayout vipContentRelativeLayout = new RelativeLayout(mAct);
        RelativeLayout.LayoutParams vipContentParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        vipContentParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        vipContentRelativeLayout.setLayoutParams(vipContentParams);

        ProgressBar progressBar = new ProgressBar(mAct);
        progressBar.setId(NPToolUtils.getIDFromItem(mAct , "progressBar"));
        RelativeLayout.LayoutParams progressBarParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        progressBarParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        progressBar.setLayoutParams(progressBarParams);
        progressBar.setIndeterminateDrawable(mAct.getResources().getDrawable(NPToolUtils.getIDFromDrawable(mAct, "toollsit_progress_round")));
        vipContentRelativeLayout.addView(progressBar);

        return vipContentRelativeLayout;
    }

    private ViewGroup vipFirstPageContent() {

        //內容底
        LinearLayout vipContentLinearLayout = new LinearLayout(mAct);
        RelativeLayout.LayoutParams vipContentParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        vipContentParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        vipContentLinearLayout.setLayoutParams(vipContentParams);
        vipContentLinearLayout.setOrientation(LinearLayout.VERTICAL);

        //圖片Layout
        RelativeLayout imageRelativeLayout = new RelativeLayout(mAct);
        LinearLayout.LayoutParams imageRelativeLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        imageRelativeLayoutParams.weight = 1;
        imageRelativeLayout.setLayoutParams(imageRelativeLayoutParams);
        vipContentLinearLayout.addView(imageRelativeLayout);

        //imageview
        ImageView vipImageView = new ImageView(mAct);
        RelativeLayout.LayoutParams imageViewParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        vipImageView.setLayoutParams(imageViewParams);
        vipImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        new NPVIPDownLoadImageTask(vipImageView).execute(vipImageURL);
        imageRelativeLayout.addView(vipImageView);

        //ButtonLayout
        RelativeLayout buttonRelativeLayout = new RelativeLayout(mAct);
        LinearLayout.LayoutParams buttonRelativeLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        buttonRelativeLayoutParams.setMargins(0, bannershortside / 30, 0, bannershortside / 30);
        buttonRelativeLayout.setLayoutParams(buttonRelativeLayoutParams);
        vipContentLinearLayout.addView(buttonRelativeLayout);

        //Button
        Button nextPageBtn = new Button(mAct);
        nextPageBtn.setPadding(0, bannershortside / 80, 0, 0);
        nextPageBtn.setId(NPToolUtils.getIDFromItem(mAct , "nextPageBtn"));
        nextPageBtn.setBackgroundResource(NPToolUtils.getIDFromDrawable(mAct, "vip_btn_seletor"));
        //nextPageBtn.setBackgroundColor(Color.parseColor("#B3A26A"));
        RelativeLayout.LayoutParams nextPageBtnParams = new RelativeLayout.LayoutParams(bannershortside * 8 / 12, bannerlongside / 12);
        if (orientation == NP_LANDSCAPE_ORIENTATION) {
            nextPageBtnParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        } else if (orientation == NP_PORTRAIT_ORIENTATION) {
            nextPageBtnParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            nextPageBtnParams.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
        }
        nextPageBtn.setLayoutParams(nextPageBtnParams);
        nextPageBtn.setTextSize(50);
        float nextPageBtnHeight = adjustTvTextHeightSize(nextPageBtn, bannershortside / 12, nextPageTxt);
        nextPageBtn.setTextSize(TypedValue.COMPLEX_UNIT_PX, nextPageBtnHeight);
        nextPageBtn.setText(nextPageTxt);
        nextPageBtn.setTextColor(Color.parseColor("#FFFFFF"));
        nextPageBtn.setTypeface(Typeface.DEFAULT_BOLD);
        buttonRelativeLayout.addView(nextPageBtn);
        nextPageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NPVIPDialog.this.type == 2) {
                    currentPage = 4;
                    initMainUI(currentPage);
                }else if (NPVIPDialog.this.type == 3){
                    ++currentPage;
                    isLoading = true;
                    initMainUI(currentPage);
                }
            }
        });

        //checkbutton
        LinearLayout checkLinearLayout = new LinearLayout(mAct);
        RelativeLayout.LayoutParams checkLinearLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        if (orientation == NP_LANDSCAPE_ORIENTATION) {
            checkLinearLayoutParams.setMargins(0, 0, bannershortside / 40, 0);
            checkLinearLayoutParams.addRule(RelativeLayout.LEFT_OF, nextPageBtn.getId());
            checkLinearLayoutParams.addRule(RelativeLayout.ALIGN_BOTTOM, nextPageBtn.getId());
        } else if (orientation == NP_PORTRAIT_ORIENTATION) {
            checkLinearLayoutParams.setMargins(0, bannershortside / 40, 0, 0);
            Log.d("TAG" , "id = " + nextPageBtn.getId());
            checkLinearLayoutParams.addRule(RelativeLayout.BELOW, nextPageBtn.getId());
            checkLinearLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
        }
        checkLinearLayout.setLayoutParams(checkLinearLayoutParams);
        checkLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
        checkLinearLayout.setGravity(Gravity.CENTER);
        checkLinearLayout.setClickable(true);
        checkLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isRemindToday) {
                    checkBoxImage.setImageDrawable(mAct.getResources().getDrawable(NPToolUtils.getIDFromDrawable(mAct, "vipcheckbox_tick")));
                    isRemindToday = false;
                } else {
                    checkBoxImage.setImageDrawable(mAct.getResources().getDrawable(NPToolUtils.getIDFromDrawable(mAct, "vipcheckbox")));
                    isRemindToday = true;
                }
            }
        });
        buttonRelativeLayout.addView(checkLinearLayout);

        checkBoxImage = null;
        checkBoxImage = new ImageView(mAct);
        checkBoxImage.setImageDrawable(mAct.getResources().getDrawable(NPToolUtils.getIDFromDrawable(mAct, "vipcheckbox")));
        checkBoxImage.setLayoutParams(new LinearLayout.LayoutParams(bannershortside / 15, bannershortside / 15));
        checkLinearLayout.addView(checkBoxImage);

        TextView checkBoxText = new TextView(mAct);
        LinearLayout.LayoutParams checkBoxTextParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        checkBoxTextParams.setMargins(bannershortside / 40, 0, 0, 0);
        checkBoxText.setLayoutParams(checkBoxTextParams);
        float checkBoxTextHeight = adjustTvTextHeightSize(checkBoxText, bannershortside / 12, checkBoxTxt);
        checkBoxText.setTextSize(TypedValue.COMPLEX_UNIT_PX, checkBoxTextHeight);
        checkBoxText.setTextColor(Color.parseColor("#b3a36b"));
        checkBoxText.setTypeface(Typeface.DEFAULT_BOLD);
        checkBoxText.setText(checkBoxTxt);
        checkLinearLayout.addView(checkBoxText);


        return vipContentLinearLayout;
    }

    private ViewGroup vipSecondPageContent() {

        //內容底
        LinearLayout vipContentLinearLayout = new LinearLayout(mAct);
        RelativeLayout.LayoutParams vipContentParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        vipContentParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        vipContentLinearLayout.setLayoutParams(vipContentParams);
        vipContentLinearLayout.setOrientation(LinearLayout.VERTICAL);

        //電話驗證Layout
        LinearLayout contentLinearLayout = new LinearLayout(mAct);
        LinearLayout.LayoutParams contentLinearLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        contentLinearLayoutParams.weight = 1;
        contentLinearLayout.setLayoutParams(contentLinearLayoutParams);
        contentLinearLayout.setOrientation(LinearLayout.VERTICAL);
        vipContentLinearLayout.addView(contentLinearLayout);


        //標題TV
        TextView contentTV = new TextView(mAct);
        LinearLayout.LayoutParams contentTVParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        contentTVParams.setMargins(0, bannershortside / 80, 0, bannershortside / 80);
        contentTV.setLayoutParams(contentTVParams);
        contentTV.setText(secondContentTitle);
        contentTV.setTextColor(Color.parseColor("#6B6A65"));
        contentTV.setTextSize(53);
        float contentTitleHeight = adjustTvTextHeightSize(contentTV, bannershortside / 13, vipTitle);
        contentTV.setTextSize(TypedValue.COMPLEX_UNIT_PX, contentTitleHeight);
        contentTV.setTypeface(Typeface.DEFAULT_BOLD);
        contentLinearLayout.addView(contentTV);


        //editLayout
        LinearLayout editLayout = new LinearLayout(mAct);
        LinearLayout.LayoutParams editLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        editLayout.setLayoutParams(editLayoutParams);
        if (orientation == NP_LANDSCAPE_ORIENTATION) {
            editLayout.setOrientation(LinearLayout.HORIZONTAL);
        } else if (orientation == NP_PORTRAIT_ORIENTATION) {
            editLayout.setOrientation(LinearLayout.VERTICAL);
        }
        contentLinearLayout.addView(editLayout);


        //phoneLayout
        LinearLayout phoneLayout = new LinearLayout(mAct);
        LinearLayout.LayoutParams phoneLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        phoneLayoutParams.weight = 1;
        if (orientation == NP_LANDSCAPE_ORIENTATION) {
            phoneLayoutParams.setMargins(0, 0, bannershortside / 60, 0);
        } else if (orientation == NP_PORTRAIT_ORIENTATION) {
            phoneLayoutParams.setMargins(0, 0, 0, bannershortside / 60);
        }
        phoneLayout.setLayoutParams(phoneLayoutParams);
        phoneLayout.setOrientation(LinearLayout.VERTICAL);
        editLayout.addView(phoneLayout);

        //phoneTitleTV
        TextView phoneTitleTV = new TextView(mAct);
        phoneTitleTV.setText(secondPhoneTitle);
        phoneTitleTV.setTextColor(Color.parseColor("#6B6A65"));
        phoneTitleTV.setTextSize(50);
        float phoneTitleHeight = adjustTvTextHeightSize(phoneTitleTV, bannershortside / 14, secondPhoneTitle);
        phoneTitleTV.setTextSize(TypedValue.COMPLEX_UNIT_PX, phoneTitleHeight);
        phoneLayout.addView(phoneTitleTV);
        //phoneLayout
        LinearLayout phoneEditLayout = new LinearLayout(mAct);
        LinearLayout.LayoutParams phoneEditLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        phoneEditLayoutParams.setMargins(0, 0, 0, bannershortside / 90);
        phoneEditLayout.setLayoutParams(phoneEditLayoutParams);
        phoneEditLayout.setOrientation(LinearLayout.HORIZONTAL);
        phoneLayout.addView(phoneEditLayout);


        //國碼edit
        countryCodesEditText = null;
        countryCodesEditText = new EditText(mAct);
        LinearLayout.LayoutParams countryCodesETParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, bannerlongside / 13);
        countryCodesETParams.weight = 1;
        countryCodesETParams.setMargins(bannershortside / 90, 0, 0, 0);
        countryCodesEditText.setLayoutParams(countryCodesETParams);
        if (isBinded) {
            countryCodesEditText.setFocusable(false);
            countryCodesEditText.setFocusableInTouchMode(false);
            countryCodesEditText.setKeyListener(null);
            countryCodesEditText.setText(bindedPhoneNumber);
        }
//        countryCodesEditText.setRawInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        countryCodesEditText.setRawInputType(InputType.TYPE_CLASS_PHONE);
        countryCodesEditText.setPadding(0, 0, 0, 0);
        countryCodesEditText.setTextColor(Color.BLACK);
        countryCodesEditText.setSingleLine(true);
        countryCodesEditText.setTextSize(TypedValue.COMPLEX_UNIT_PX, phoneTitleHeight);
        countryCodesEditText.setHint(DescriptionArray[0]);
        countryCodesEditText.setHintTextColor(Color.GRAY);
        countryCodesEditText.setBackgroundResource(NPToolUtils.getIDFromDrawable(mAct, "np_edittext_border"));
        countryCodesEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});

        int drop_down_item_id = mAct.getResources().getIdentifier("np_drop_down_item", "layout", mAct.getPackageName());
        int spinner_item_id = mAct.getResources().getIdentifier("np_spinner_item", "layout", mAct.getPackageName());
        ArrayAdapter countryCodesAdapter = null;
        if (isBinded) {
            for (int i = 0; i < AreaCodeArray.length; i++) {
                if (AreaCodeArray[i].equals("")) {
                    countryCodesAdapter = new ArrayAdapter(mAct, spinner_item_id, new String[]{AreaArray[i]});
                    countryCodesAdapter.setDropDownViewResource(drop_down_item_id);
                }
            }
        } else {
            countryCodesAdapter = new ArrayAdapter(mAct, spinner_item_id, AreaArray);
            countryCodesAdapter.setDropDownViewResource(drop_down_item_id);
        }
        //國碼spinner
        countryCodesSpr = null;
        countryCodesSpr = new Spinner(mAct, null);
        if (isBinded) {
            countryCodesSpr.setEnabled(false);
        }
        countryCodesSpr.setBackgroundResource(NPToolUtils.getIDFromDrawable(mAct, "vip_seletor"));
        LinearLayout.LayoutParams countryCodesSprParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, bannerlongside / 14);
        countryCodesSpr.setLayoutParams(countryCodesSprParams);
        countryCodesSpr.setPadding(0, 0, 0, 0);
        if (countryCodesAdapter != null)
            countryCodesSpr.setAdapter(countryCodesAdapter);
        phoneEditLayout.addView(countryCodesSpr);

        countryCodesSpr.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectAreaCode = AreaCodeArray[position];
                countryCodesEditText.setHint(DescriptionArray[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        phoneEditLayout.addView(countryCodesEditText);

        //phoneBtn
        phoneBtn = null;
        phoneBtn = new Button(mAct);
        phoneBtn.setPadding(0, bannershortside / 90, 0, 0);
        phoneBtn.setId(NPToolUtils.getIDFromItem(mAct , "phoneBtn"));
        RelativeLayout.LayoutParams phoneBtnParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, bannerlongside / 12);
        phoneBtn.setLayoutParams(phoneBtnParams);
        if (isBinded) {
            phoneBtn.setBackgroundResource(NPToolUtils.getIDFromDrawable(mAct, "vip_btn"));
            phoneBtn.setText(ButtonText1_3);
            phoneBtn.setTextColor(Color.parseColor("#B3A26A"));
            phoneBtn.setOnClickListener(null);
        } else {
            phoneBtn.setText(sendPhoneBtnTxt);
            phoneBtn.setBackgroundResource(NPToolUtils.getIDFromDrawable(mAct, "vip_btn_seletor"));
            phoneBtn.setTextColor(Color.parseColor("#FFFFFF"));
            phoneBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (countryCodesEditText.getText().toString().trim().equalsIgnoreCase("")) {
                        sendPhoneNumber = "";
                    } else {
                        sendPhoneNumber = selectAreaCode + countryCodesEditText.getText().toString().trim();
                    }
                    npvipHttpClient.vipHttpConnection(mAct, NPVIPCommandType.CheckVerifyCode, NicePlayVIPClient.Uid, NicePlayVIPClient.AppID, sendPhoneNumber, "", "");
                }
            });
        }
        phoneBtn.setTextSize(50);
        float phoneBtnHeight = adjustTvTextHeightSize(phoneBtn, bannershortside / 12, sendPhoneBtnTxt);
        phoneBtn.setTextSize(TypedValue.COMPLEX_UNIT_PX, phoneBtnHeight);
        phoneBtn.setTypeface(Typeface.DEFAULT_BOLD);
        phoneLayout.addView(phoneBtn);
        //smsLayout
        LinearLayout smsLayout = new LinearLayout(mAct);
        LinearLayout.LayoutParams smsLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        smsLayoutParams.weight = 1;
        if (orientation == NP_LANDSCAPE_ORIENTATION) {
            smsLayoutParams.setMargins(bannershortside / 60, 0, 0, 0);
        } else if (orientation == NP_PORTRAIT_ORIENTATION) {
            smsLayoutParams.setMargins(0, bannershortside / 60, 0, 0);
        }
        smsLayout.setLayoutParams(smsLayoutParams);
        smsLayout.setOrientation(LinearLayout.VERTICAL);
        editLayout.addView(smsLayout);
        //smsTitleTV
        TextView smsTitleTV = new TextView(mAct);
        smsTitleTV.setText(secondSmsTitle);
        smsTitleTV.setTextColor(Color.parseColor("#6B6A65"));
        smsTitleTV.setTextSize(TypedValue.COMPLEX_UNIT_PX, phoneTitleHeight);
        smsLayout.addView(smsTitleTV);


        //smsedit
        smsEditText = null;
        smsEditText = new EditText(mAct);
        LinearLayout.LayoutParams smsEditTextParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, bannerlongside / 13);
        smsEditTextParams.weight = 1;
        smsEditTextParams.setMargins(0, 0, 0, bannershortside / 90);
        smsEditText.setLayoutParams(smsEditTextParams);
        if (isBinded) {
            smsEditText.setFocusable(false);
            smsEditText.setFocusableInTouchMode(false);
            smsEditText.setKeyListener(null);
        }
        smsEditText.setPadding(0, 0, 0, 0);
        smsEditText.setSingleLine(true);
        smsEditText.setTextColor(Color.BLACK);
        smsEditText.setTextSize(TypedValue.COMPLEX_UNIT_PX, phoneTitleHeight);
        smsEditText.setHint(TextBox_Placeholder2);
        smsEditText.setHintTextColor(Color.GRAY);
        smsEditText.setRawInputType(InputType.TYPE_CLASS_PHONE);
//        smsEditText.setRawInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        smsEditText.setBackgroundResource(NPToolUtils.getIDFromDrawable(mAct, "np_edittext_border"));
        smsEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});
        smsLayout.addView(smsEditText);
        //Button
        smsBtn = null;
        smsBtn = new Button(mAct);
        smsBtn.setPadding(0, bannershortside / 90, 0, 0);
        smsBtn.setId(NPToolUtils.getIDFromItem(mAct , "phoneBtn"));
        RelativeLayout.LayoutParams smsBtnParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, bannerlongside / 12);
        smsBtn.setLayoutParams(smsBtnParams);
        if (isBinded) {
            smsBtn.setBackgroundResource(NPToolUtils.getIDFromDrawable(mAct, "vip_btn"));
            smsBtn.setText(ButtonText2_3);
            smsBtn.setTextColor(Color.parseColor("#B3A26A"));
            smsBtn.setOnClickListener(null);
        } else {
            smsBtn.setBackgroundResource(NPToolUtils.getIDFromDrawable(mAct, "vip_btn"));
            smsBtn.setText(sendVerificationPhoneBtnTxt);
            smsBtn.setTextColor(Color.parseColor("#B3A26A"));
            smsBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isSendSMS) {
                        if (vipProgressDialog == null) {
                            vipProgressDialog = NPVIPProgressDialog.createDialog(mAct);
                        }
                        vipProgressDialog.setCancelable(false);
                        vipProgressDialog.show();
                        npvipHttpClient.vipHttpConnection(mAct, NPVIPCommandType.BindPhoneAccount,NicePlayVIPClient.Uid, NicePlayVIPClient.AppID, smsPhoneNumber, smsEditText.getText().toString().trim(), "4");
                    }
                }
            });
        }
        smsBtn.setTextSize(TypedValue.COMPLEX_UNIT_PX, phoneBtnHeight);
        smsBtn.setTypeface(Typeface.DEFAULT_BOLD);
        smsLayout.addView(smsBtn);

        if (orientation == NP_LANDSCAPE_ORIENTATION) {
            //分隔線
            View dividerView = new View(mAct);
            LinearLayout.LayoutParams dividerViewParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, bannershortside / 200);
            dividerView.setBackgroundColor(Color.parseColor("#CEC9AD"));
            dividerView.setLayoutParams(dividerViewParams);
            vipContentLinearLayout.addView(dividerView);
        } else if (orientation == NP_PORTRAIT_ORIENTATION) {

        }

        //ButtonLayout
        LinearLayout buttonLinearLayout = new LinearLayout(mAct);
        LinearLayout.LayoutParams buttonLinearLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        buttonLinearLayoutParams.setMargins(0, bannershortside / 30, 0, bannershortside / 30);
        buttonLinearLayout.setLayoutParams(buttonLinearLayoutParams);
        if (orientation == NP_LANDSCAPE_ORIENTATION) {
            buttonLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
        } else if (orientation == NP_PORTRAIT_ORIENTATION) {
            buttonLinearLayout.setOrientation(LinearLayout.VERTICAL);
        }
        vipContentLinearLayout.addView(buttonLinearLayout);

        if (orientation == NP_LANDSCAPE_ORIENTATION) {
            LinearLayout noneLinearLayout = new LinearLayout(mAct);
            LinearLayout.LayoutParams noneLinearLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            noneLinearLayoutParams.weight = 1;
            noneLinearLayoutParams.setMargins(0, 0, bannershortside / 60, 0);
            noneLinearLayout.setLayoutParams(noneLinearLayoutParams);
            buttonLinearLayout.addView(noneLinearLayout);
        }


        //Button
        Button nextPageBtn = new Button(mAct);
        nextPageBtn.setPadding(0, bannershortside / 90, 0, 0);
        nextPageBtn.setId(NPToolUtils.getIDFromItem(mAct , "nextPageBtn"));
//        nextPageBtn.setBackgroundColor(Color.parseColor("#B3A26A"));
        nextPageBtn.setBackgroundResource(NPToolUtils.getIDFromDrawable(mAct, "vip_btn_seletor2"));
        LinearLayout.LayoutParams nextPageBtnParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, bannerlongside / 12);
        nextPageBtnParams.weight = 1;
        if (orientation == NP_LANDSCAPE_ORIENTATION) {
            nextPageBtnParams.setMargins(bannershortside / 60, 0, 0, 0);
        }
        nextPageBtn.setLayoutParams(nextPageBtnParams);
        nextPageBtn.setTextSize(TypedValue.COMPLEX_UNIT_PX, phoneBtnHeight);
        nextPageBtn.setText(ButtonText3);
        nextPageBtn.setTextColor(Color.parseColor("#FFFFFF"));
        nextPageBtn.setTypeface(Typeface.DEFAULT_BOLD);
        buttonLinearLayout.addView(nextPageBtn);
        nextPageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (vipProgressDialog == null) {
                    vipProgressDialog = NPVIPProgressDialog.createDialog(mAct);
                }
                vipProgressDialog.setCancelable(false);
                vipProgressDialog.show();
                npvipHttpClient.vipHttpConnection(mAct, NPVIPCommandType.FianlCheckBinded, NicePlayVIPClient.Uid, NicePlayVIPClient.AppID, "", "", "");
//                ++currentPage;
//                initMainUI(currentPage);
            }
        });

        return vipContentLinearLayout;
    }


    private ViewGroup vipThirdPageContent() {

        //內容底
        LinearLayout vipContentLinearLayout = new LinearLayout(mAct);
        RelativeLayout.LayoutParams vipContentParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        vipContentParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        vipContentLinearLayout.setLayoutParams(vipContentParams);
        vipContentLinearLayout.setOrientation(LinearLayout.VERTICAL);

        //LineID Layout
        LinearLayout contentLinearLayout = new LinearLayout(mAct);
        LinearLayout.LayoutParams contentLinearLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        contentLinearLayoutParams.weight = 1;
        contentLinearLayout.setLayoutParams(contentLinearLayoutParams);
        contentLinearLayout.setOrientation(LinearLayout.VERTICAL);
        vipContentLinearLayout.addView(contentLinearLayout);

        //標題TV
        TextView contentTV = new TextView(mAct);
        LinearLayout.LayoutParams contentTVParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        contentTVParams.setMargins(0, bannershortside / 80, 0, 0);
        contentTV.setLayoutParams(contentTVParams);
        contentTV.setText(thirdContentTitle);
        contentTV.setTextColor(Color.parseColor("#6B6A65"));
        contentTV.setTextSize(53);
        float contentTitleHeight = adjustTvTextHeightSize(contentTV, bannershortside / 13, vipTitle);
        contentTV.setTextSize(TypedValue.COMPLEX_UNIT_PX, contentTitleHeight);
        contentTV.setTypeface(Typeface.DEFAULT_BOLD);
        contentLinearLayout.addView(contentTV);

        //editLayout
        LinearLayout editLayout = new LinearLayout(mAct);
        LinearLayout.LayoutParams editLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        editLayout.setLayoutParams(editLayoutParams);
        editLayout.setOrientation(LinearLayout.VERTICAL);
        contentLinearLayout.addView(editLayout);


        //line Layout
        LinearLayout lineLayout = new LinearLayout(mAct);
        LinearLayout.LayoutParams lineLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        if (orientation == NP_LANDSCAPE_ORIENTATION) {
            lineLayout.setOrientation(LinearLayout.HORIZONTAL);
        } else if (orientation == NP_PORTRAIT_ORIENTATION) {
            lineLayout.setOrientation(LinearLayout.VERTICAL);
        }
        lineLayoutParams.setMargins(0, 0, 0, bannershortside / 60);
        lineLayout.setLayoutParams(lineLayoutParams);
        editLayout.addView(lineLayout);

        LinearLayout clearLinearLayout = new LinearLayout(mAct);
        clearLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
        clearLinearLayout.setPadding(0, 0, 0, 0);
        clearLinearLayout.setBackgroundResource(NPToolUtils.getIDFromDrawable(mAct, "np_edittext_border"));
        clearLinearLayout.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams clearRelativeLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, bannerlongside / 13);
        clearRelativeLayoutParams.weight = 1;
        clearLinearLayout.setLayoutParams(clearRelativeLayoutParams);
        lineLayout.addView(clearLinearLayout);

        clearEditText = null;
        clearEditText = new EditText(mAct);
        clearEditText.setBackgroundResource(0);
        LinearLayout.LayoutParams clearEditTextParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, bannerlongside / 13);
        clearEditTextParams.setMargins(0, 0, 0, 0);
        clearEditTextParams.weight = 1;
        clearEditText.setLayoutParams(clearEditTextParams);
        clearEditText.setTextSize(TypedValue.COMPLEX_UNIT_PX, contentTitleHeight);
        clearEditText.setHint(thirdLineHint);
        clearEditText.setHintTextColor(Color.GRAY);
        clearEditText.setSingleLine(true);
        clearEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(50)});
        clearEditText.setTextColor(Color.BLACK);
        clearLinearLayout.addView(clearEditText);


//        //line edit
//        lineEditText = null;
//        lineEditText = new NPClearEditText(mAct);
//        lineEditText.setClearBtnSizeAndInit(bannershortside / 14);
//        LinearLayout.LayoutParams lineETParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, bannerlongside / 13);
//        lineETParams.weight = 1;
//        lineEditText.setLayoutParams(lineETParams);
//        lineEditText.setBackgroundResource(NPToolUtils.getIDFromDrawable(mAct, "np_edittext_border"));
//        lineEditText.setSingleLine(true);
//        lineEditText.setTextSize(TypedValue.COMPLEX_UNIT_PX, contentTitleHeight);
//        lineEditText.setHint(thirdLineHint);
//        lineEditText.setHintTextColor(Color.GRAY);
//        lineEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(50)});
//        lineEditText.setPadding(10, 0, 0, 0);
//        lineEditText.setTextColor(Color.BLACK);
//        lineLayout.addView(lineEditText);


        TextView lineTV = new TextView(mAct);
        LinearLayout.LayoutParams lineTVParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lineTVParams.weight = 1;
        lineTV.setLayoutParams(lineTVParams);
        lineTV.setTextSize(48);
        float lineTVHeight = adjustTvTextHeightSize(lineTV, bannershortside / 15, thirdLineRemind);
        lineTV.setTextSize(TypedValue.COMPLEX_UNIT_PX, lineTVHeight);
        lineTV.setText(thirdLineRemind);
        lineLayout.addView(lineTV);


        //remindLayout
        LinearLayout remindLayout = new LinearLayout(mAct);
        LinearLayout.LayoutParams remindLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        remindLayoutParams.setMargins(0, bannershortside / 60, 0, 0);
        remindLayout.setLayoutParams(remindLayoutParams);
        remindLayout.setOrientation(LinearLayout.VERTICAL);
        editLayout.addView(remindLayout);

        //remindTitleTV
        TextView remindTitleTV = new TextView(mAct);
        remindTitleTV.setText(thirdRemainTitle);
        remindTitleTV.setTextColor(Color.parseColor("#6B6A65"));
        remindTitleTV.setTextSize(50);
        float remindTitleHeight = adjustTvTextHeightSize(remindTitleTV, bannershortside / 14, thirdRemainTitle);
        remindTitleTV.setTextSize(TypedValue.COMPLEX_UNIT_PX, remindTitleHeight);
        remindTitleTV.setTypeface(Typeface.DEFAULT_BOLD);
        remindLayout.addView(remindTitleTV);

        //remind ScrollView
        ScrollView remindScrollView = new ScrollView(mAct);
        LinearLayout.LayoutParams rremindScrollViewParams = null;
        if (orientation == NP_LANDSCAPE_ORIENTATION) {
            rremindScrollViewParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, bannershortside / 3);
        } else {
            rremindScrollViewParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, bannerlongside / 3);
        }
        rremindScrollViewParams.weight = 1;
        remindScrollView.setLayoutParams(rremindScrollViewParams);
        remindScrollView.setBackgroundResource(NPToolUtils.getIDFromDrawable(mAct, "np_edittext_border"));
        remindLayout.addView(remindScrollView);


//        CharSequence html1 = "";
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
//            html1 = Html.fromHtml(thirdRemainTxt, Html.FROM_HTML_MODE_LEGACY);
//        } else {
//            html1 = Html.fromHtml(thirdRemainTxt);
//        }
//        //remind text
//        TextView remindTextView = new TextView(mAct);
//        remindTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, remindTitleHeight);
//        LinearLayout.LayoutParams remindTextViewParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
//        remindTextView.setLayoutParams(remindTextViewParams);
//        remindTextView.setHint(html1);
//        remindScrollView.addView(remindTextView);



        WebView browser = new WebView(mAct);
        browser.setFocusable(false);
        LinearLayout.LayoutParams remindTextViewParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        browser.setLayoutParams(remindTextViewParams);
        browser.getSettings().setJavaScriptEnabled(true);
//        browser.loadData(thirdRemainTxt, "text/html", "UTF-8");
        browser.loadDataWithBaseURL(null , thirdRemainTxt, "text/html",  "utf-8" , null);
        remindScrollView.addView(browser);

        //ButtonLayout
        LinearLayout buttonLinearLayout = new LinearLayout(mAct);
        LinearLayout.LayoutParams buttonLinearLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        buttonLinearLayoutParams.setMargins(0, bannershortside / 30, 0, bannershortside / 30);
        buttonLinearLayout.setLayoutParams(buttonLinearLayoutParams);
        if (orientation == NP_LANDSCAPE_ORIENTATION) {
            buttonLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
        } else if (orientation == NP_PORTRAIT_ORIENTATION) {
            buttonLinearLayout.setOrientation(LinearLayout.VERTICAL);
        }
        vipContentLinearLayout.addView(buttonLinearLayout);


        if (orientation == NP_LANDSCAPE_ORIENTATION) {
            LinearLayout noneLinearLayout = new LinearLayout(mAct);
            LinearLayout.LayoutParams noneLinearLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            noneLinearLayoutParams.weight = 1;
            noneLinearLayoutParams.setMargins(0, 0, bannershortside / 60, 0);
            noneLinearLayout.setLayoutParams(noneLinearLayoutParams);
            buttonLinearLayout.addView(noneLinearLayout);
        }

        //Button
        Button nextPageBtn = new Button(mAct);
        nextPageBtn.setPadding(0, bannershortside / 90, 0, 0);
        nextPageBtn.setId(NPToolUtils.getIDFromItem(mAct , "nextPageBtn"));
//        nextPageBtn.setBackgroundColor(Color.parseColor("#B3A26A"));
        nextPageBtn.setBackgroundResource(NPToolUtils.getIDFromDrawable(mAct, "vip_btn_seletor"));
        LinearLayout.LayoutParams nextPageBtnParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, bannerlongside / 12);
        if (orientation == NP_LANDSCAPE_ORIENTATION) {
            nextPageBtnParams.setMargins(bannershortside / 60, 0, 0, 0);
        }
        nextPageBtnParams.weight = 1;
        nextPageBtn.setLayoutParams(nextPageBtnParams);
        nextPageBtn.setTextSize(50);
        float nextPageBtnHeight = adjustTvTextHeightSize(nextPageBtn, bannershortside / 12, sendPhoneBtnTxt);
        nextPageBtn.setTextSize(TypedValue.COMPLEX_UNIT_PX, nextPageBtnHeight);
        nextPageBtn.setText(sendDataBtnTxt);
        nextPageBtn.setTextColor(Color.parseColor("#FFFFFF"));
        nextPageBtn.setTypeface(Typeface.DEFAULT_BOLD);
        buttonLinearLayout.addView(nextPageBtn);
        nextPageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (vipProgressDialog == null) {
                    vipProgressDialog = NPVIPProgressDialog.createDialog(mAct);
                }
                vipProgressDialog.setCancelable(false);
                vipProgressDialog.show();
                npvipHttpClient.bindingCommunication(bindedPhoneNumber, CommunicationSoftWare, clearEditText.getText().toString().trim());

            }
        });

        return vipContentLinearLayout;
    }

    private ViewGroup vipFourthPageContent() {

        //內容底
        LinearLayout vipContentLinearLayout = new LinearLayout(mAct);
        RelativeLayout.LayoutParams vipContentParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        vipContentParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        vipContentLinearLayout.setLayoutParams(vipContentParams);
        vipContentLinearLayout.setOrientation(LinearLayout.VERTICAL);

        //Layout
        LinearLayout contentLinearLayout = new LinearLayout(mAct);
        LinearLayout.LayoutParams contentLinearLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        contentLinearLayoutParams.weight = 1;
        contentLinearLayout.setLayoutParams(contentLinearLayoutParams);
        contentLinearLayout.setOrientation(LinearLayout.VERTICAL);
        vipContentLinearLayout.addView(contentLinearLayout);

        //標題TV
        TextView contentTV = new TextView(mAct);
        LinearLayout.LayoutParams contentTVParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        contentTVParams.setMargins(0, bannershortside / 80, 0, 0);
        contentTV.setLayoutParams(contentTVParams);
        contentTV.setText(fourthContentTitle);
        contentTV.setTextColor(Color.parseColor("#666666"));
        contentTV.setTextSize(53);
        float contentTitleHeight = adjustTvTextHeightSize(contentTV, bannershortside / 14, vipTitle);
        contentTV.setTextSize(TypedValue.COMPLEX_UNIT_PX, contentTitleHeight);
        contentLinearLayout.addView(contentTV);

        //Layout
        RelativeLayout giftLayout = new RelativeLayout(mAct);
        LinearLayout.LayoutParams giftLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        giftLayoutParams.weight = 1;
        giftLayout.setLayoutParams(giftLayoutParams);
        contentLinearLayout.addView(giftLayout);

        FrameLayout giftFrameLayout = new FrameLayout(mAct);
        giftFrameLayout.setId(NPToolUtils.getIDFromItem(mAct , "giftFrameLayout"));
        giftFrameLayout.setBackgroundResource(NPToolUtils.getIDFromDrawable(mAct, "vip_btn"));
        RelativeLayout.LayoutParams giftFrameLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        giftFrameLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        giftFrameLayout.setLayoutParams(giftFrameLayoutParams);
        giftLayout.addView(giftFrameLayout);

        //gift edit
        EditText giftEditText = new EditText(mAct);
        giftEditText.setFocusable(false);
        giftEditText.setFocusableInTouchMode(false);
        giftEditText.setTextColor(Color.parseColor("#D7B16A"));
        giftEditText.setBackgroundResource(0);
        FrameLayout.LayoutParams giftEditTextParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        giftEditTextParams.gravity = Gravity.CENTER;
        giftEditText.setLayoutParams(giftEditTextParams);
        giftEditText.setSingleLine(true);
        giftEditText.setTextSize(TypedValue.COMPLEX_UNIT_PX, contentTitleHeight);
        giftEditText.setText(GiftName);
        giftFrameLayout.addView(giftEditText);


        TextView giftTV = new TextView(mAct);
        giftTV.setTextColor(Color.parseColor("#666666"));
        RelativeLayout.LayoutParams giftTVParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        if (orientation == NP_LANDSCAPE_ORIENTATION) {
            giftTVParams.addRule(RelativeLayout.LEFT_OF, giftFrameLayout.getId());
            giftTVParams.addRule(RelativeLayout.CENTER_VERTICAL);
            giftTVParams.setMargins(0, 0, bannershortside / 60, 0);
        } else if (orientation == NP_PORTRAIT_ORIENTATION) {
            giftTVParams.setMargins(0, 0, 0, bannershortside / 60);
            giftTVParams.addRule(RelativeLayout.ABOVE, giftFrameLayout.getId());
            giftTVParams.addRule(RelativeLayout.ALIGN_LEFT, giftFrameLayout.getId());
        }
        giftTV.setLayoutParams(giftTVParams);
        giftTV.setTextSize(TypedValue.COMPLEX_UNIT_PX, contentTitleHeight);
        giftTV.setText(fourthGiftTxt);
        giftLayout.addView(giftTV);


        //ButtonLayout
        LinearLayout buttonLinearLayout = new LinearLayout(mAct);
        LinearLayout.LayoutParams buttonLinearLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        buttonLinearLayoutParams.setMargins(0, bannershortside / 30, 0, bannershortside / 30);
        buttonLinearLayout.setLayoutParams(buttonLinearLayoutParams);
        if (orientation == NP_LANDSCAPE_ORIENTATION) {
            buttonLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
        } else if (orientation == NP_PORTRAIT_ORIENTATION) {
            buttonLinearLayout.setOrientation(LinearLayout.VERTICAL);
        }
        vipContentLinearLayout.addView(buttonLinearLayout);


        ImageView vipImageView = new ImageView(mAct);
        LinearLayout.LayoutParams vipImageViewParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, bannerlongside / 9);
        vipImageViewParams.weight = 1;
        if (orientation == NP_LANDSCAPE_ORIENTATION) {
            vipImageViewParams.setMargins(0, 0, bannershortside / 60, 0);
        } else if (orientation == NP_PORTRAIT_ORIENTATION) {
            vipImageViewParams.setMargins(0, 0, 0, bannershortside / 60);
        }

        vipImageView.setLayoutParams(vipImageViewParams);
        vipImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        new NPVIPDownLoadImageTask(vipImageView).execute(EventBNUrl);
        buttonLinearLayout.addView(vipImageView);


        //Button
        Button nextPageBtn = new Button(mAct);
        nextPageBtn.setPadding(0, bannershortside / 90, 0, 0);
        nextPageBtn.setId(NPToolUtils.getIDFromItem(mAct , "nextPageBtn"));
//        nextPageBtn.setBackgroundColor(Color.parseColor("#B3A26A"));
        nextPageBtn.setBackgroundResource(NPToolUtils.getIDFromDrawable(mAct, "vip_btn_seletor"));
        LinearLayout.LayoutParams nextPageBtnParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, bannerlongside / 12);
        nextPageBtnParams.gravity = Gravity.BOTTOM;
        nextPageBtnParams.weight = 1;
        if (orientation == NP_LANDSCAPE_ORIENTATION) {
            nextPageBtnParams.setMargins(bannershortside / 60, 0, 0, 0);
        } else if (orientation == NP_PORTRAIT_ORIENTATION) {
            nextPageBtnParams.setMargins(0, bannershortside / 60, 0, 0);
        }
        nextPageBtn.setLayoutParams(nextPageBtnParams);
        nextPageBtn.setTextSize(50);
        float nextPageBtnHeight = adjustTvTextHeightSize(nextPageBtn, bannershortside / 12, sendPhoneBtnTxt);
        nextPageBtn.setTextSize(TypedValue.COMPLEX_UNIT_PX, nextPageBtnHeight);
        nextPageBtn.setText(fourthCloseBtnTxt);
        nextPageBtn.setTextColor(Color.parseColor("#FFFFFF"));
        nextPageBtn.setTypeface(Typeface.DEFAULT_BOLD);
        buttonLinearLayout.addView(nextPageBtn);
        nextPageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (mlistener != null)
//                    mlistener.onEvent(-1, "close VIPDialog");
                dismiss();
            }
        });

        return vipContentLinearLayout;
    }


    private ViewGroup vipWebviewPageContent() {
        RelativeLayout vipWebviewRelativeLayout = new RelativeLayout(this.mAct);
        android.widget.RelativeLayout.LayoutParams vipContentParams = new android.widget.RelativeLayout.LayoutParams(-1, -1);
        vipContentParams.addRule(13);
        vipWebviewRelativeLayout.setLayoutParams(vipContentParams);

        final ProgressBar webviewProgressBar = new ProgressBar(this.mAct);
        webviewProgressBar.setId(NPToolUtils.getIDFromItem(mAct , "webviewProgressBar"));
        android.widget.RelativeLayout.LayoutParams progressBarParams = new android.widget.RelativeLayout.LayoutParams(-2, -2);
        progressBarParams.addRule(13);
        webviewProgressBar.setLayoutParams(progressBarParams);
        webviewProgressBar.setIndeterminateDrawable(this.mAct.getResources().getDrawable(NPToolUtils.getIDFromDrawable(this.mAct, "toollsit_progress_round")));
        vipWebviewRelativeLayout.addView(webviewProgressBar);

        final WebView mWebView = new WebView(this.mAct);
        mWebView.setId(NPToolUtils.getIDFromItem(mAct , "mWebView"));
        android.widget.RelativeLayout.LayoutParams webViewParams = new android.widget.RelativeLayout.LayoutParams(-1, -1);
        mWebView.setLayoutParams(webViewParams);
        mWebView.getSettings().setJavaScriptEnabled(true);                   //是否支持網頁裡的JavaScript語法
        mWebView.getSettings().setSupportZoom(false);                        //是否支持點擊縮放
        mWebView.getSettings().setBuiltInZoomControls(false);                //是否支持手指縮放
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.requestFocus();
        mWebView.setWebViewClient(new WebViewClient() {                       //使網頁顯示在WebView
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }


            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Log.d("acc4", "onPageFinished");
                if (webviewProgressBar != null) {
                    webviewProgressBar.setVisibility(View.GONE);
                }

                mWebView.setVisibility(View.VISIBLE);
            }
        });
        mWebView.loadUrl(this.webURL);
        mWebView.setVisibility(View.GONE);
        vipWebviewRelativeLayout.addView(mWebView);
        return vipWebviewRelativeLayout;
    }



    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

            mAct.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(mAct, "第" + position + "個", Toast.LENGTH_LONG).show();
                }
            });
        }
    };

    public static void adjustTvTextWidthSize(TextView tv, int maxWidth, String text) {

        int avaiWidth = maxWidth - tv.getPaddingLeft() - tv.getPaddingRight() - 10;
        if (avaiWidth <= 0) {
            return;
        }
        TextPaint textPaintClone = new TextPaint(tv.getPaint());
        // note that Paint text size works in px not sp
        float trySize = textPaintClone.getTextSize();
        while (textPaintClone.measureText(text) > avaiWidth) {
            trySize = trySize - 2;
            textPaintClone.setTextSize(trySize);
        }
        tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, trySize);
    }


    public static float adjustTvTextHeightSize(TextView tv, int maxHeight, String text) {
        int avaiHeight = maxHeight - tv.getPaddingTop() - tv.getPaddingBottom() - 10;
        if (avaiHeight <= 0) {
            return 20;
        }
        TextPaint textPaintClone = new TextPaint(tv.getPaint());
        // note that Paint text size works in px not sp
        float trySize = textPaintClone.getTextSize();

        while (textPaintClone.getFontMetrics().descent - textPaintClone.getFontMetrics().ascent > avaiHeight) {
            trySize = trySize - 2;
            textPaintClone.setTextSize(trySize);
        }
        return trySize;
    }

}
