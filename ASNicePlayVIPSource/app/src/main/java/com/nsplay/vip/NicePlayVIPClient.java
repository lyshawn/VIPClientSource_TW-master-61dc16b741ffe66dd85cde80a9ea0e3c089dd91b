package com.nsplay.vip;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Base64;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.TimeZone;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by 9splay on 2017/12/1.
 */

public class NicePlayVIPClient {

    private Activity mAct;

    public static String AppID = "";

    public static String Uid = "";

    public static String Appkey = "";

    private String TAG = "NicePlayVIPClient";

    public NicePlayVIPClient(Activity act) {
        this.mAct = act;
    }

	private int VIPState = 0;

    public void showVIPDialog(String appID, final int screenOrientation, final NPVIPDialog.OnVIPBindingListener onVIPBindingListener) {

        showVIPDialog(appID,"", "", screenOrientation, onVIPBindingListener);
    }

    public void showVIPDialog(String appID, String appkey , String uid, final int screenOrientation, final NPVIPDialog.OnVIPBindingListener onVIPBindingListener) {

        this.AppID = appID;

		this.Appkey = appkey;

        this.Uid = uid;

        //今日不在顯示選項
        if (Saveaccountandpassword.getVIPDate(mAct).compareTo("") != 0 && Saveaccountandpassword.getVIPDate(mAct).length() != 0) {

            //現在時間
            TimeZone defZone = TimeZone.getDefault();                   //台灣標準時間
            Calendar rightNow = Calendar.getInstance(defZone);          //使用指定時區建立Calendar實體
            CharSequence s = DateFormat.format("yyyy-MM-dd", rightNow.getTime());  //輸出格式 yyyy-MM-dd
            String[] vipRightNowDateSplit = s.toString().split("-");            // 以 " - " 分割字串
            //時間
            String lastVIPDate = Saveaccountandpassword.getVIPDate(mAct);
            String[] vipLastDateSplit = lastVIPDate.split("-");         // 以 " - " 分割字串
            int i = 0;
            for (String vipLastDate : vipLastDateSplit) {
                Log.d("abc", "vipLastDate = " + vipLastDate);
                Log.d("abc", "vipRightNowDateSplit = " + vipRightNowDateSplit[i]);
                if (Integer.parseInt(vipRightNowDateSplit[i]) <= Integer.parseInt(vipLastDate)) {
                    if (i == vipLastDateSplit.length - 1) {
                        onVIPBindingListener.onEvent(-98, "Don't show this again today");
                        return;
                    }
                    i++;
                } else {
                    break;
                }
            }
        }

        final NPVIPHttpClient vipHttpclient = new NPVIPHttpClient(mAct);

        vipHttpclient.setVIPHttpListener(new NPVIPHttpClient.OnVIPHttpListener() {      //監聽
            @Override
            public void onEvent(int Code, String Message, String jsonData, int type) {
				Log.d(TAG, "JsonCallback = " + jsonData);
            	//VIPlevel = 1 ; ShowVIPModal = 1  &  VIPlevel = 2 ; ShowVIPModal = 1
				if(Message !="00") {
					switch (Code) {
						case 1:
							if (type == NPVIPCommandType.VIPModelState_Get.getIntValue() && (Message.equals("11")||Message.equals("10"))) {
								vipHttpclient.queryVIPState(AppID, Uid);
							} else if (type == NPVIPCommandType.VIPModelState_Get.getIntValue() && Message.equals("01")) {
								vipHttpclient.queryGoldenVIPDialogData(AppID, Uid, Appkey,"1");
							}
							if (type == NPVIPCommandType.VIPMemberQualifications.getIntValue()) {            //確認尊爵VIP之階段
								vipHttpclient.queryVIPDialogData(AppID);
							} else if (type == NPVIPCommandType.GameToolsGameBindVIP.getIntValue()) {            //獲取VIP(尊爵)資料之階段
								processVIPJsonData(jsonData, type, screenOrientation, onVIPBindingListener);
							} else if (type == NPVIPCommandType.GameToolsVIPModalInfo_Get.getIntValue()) {      //獲取VIP(金牌)資料之階段
								processGoldVIPJsonData(jsonData, type, screenOrientation, onVIPBindingListener);
							}
							break;
						case -101:
							if (type == NPVIPCommandType.VIPMemberQualifications.getIntValue() && Message.equals("10")) {            //確認金牌VIP之階段(金牌關閉)
								return;
							}
							if (type == NPVIPCommandType.VIPMemberQualifications.getIntValue() && !Message.equals("10")) {            //確認金牌VIP之階段
								vipHttpclient.queryGoldenVIPDialogData(AppID, Uid, Appkey,"1");
							}
							if (type == NPVIPCommandType.GameToolsVIPModalInfo_Get.getIntValue()) {      //獲取VIP(金牌)資料之階段
								onVIPBindingListener.onEvent(-101, "Not qualified");
							}
						case -102:
							onVIPBindingListener.onEvent(-102, "already binding");
							break;
						default:
							Log.d(TAG, "VIPCode = " + Code);
							if (type == NPVIPCommandType.VIPMemberQualifications.getIntValue()) {
								onVIPBindingListener.onEvent(-99, "Not qualified");
							} else if (type == NPVIPCommandType.GameToolsGameBindVIP.getIntValue()) {
								onVIPBindingListener.onEvent(-100, "Data read error");
							} else if (type == NPVIPCommandType.GameToolsVIPModalInfo_Get.getIntValue()) {
								onVIPBindingListener.onEvent(-100, "Data read error");
							}
							break;
					}
				}
			}

        });

        Log.d("NicePlayVIP", "UserUid = " + Saveaccountandpassword.getUserUid(mAct));
        if (Uid.equalsIgnoreCase("")) {
            NicePlayVIPClient.Uid = Saveaccountandpassword.getUserUid(mAct);
        }

        vipHttpclient.vipModelState(AppID);						//開關

        //vipHttpclient.queryVIPState(AppID, Uid);

    }


    private void processVIPJsonData(String Data, final int vipCommandType, final int screenOrientation, final NPVIPDialog.OnVIPBindingListener onVIPBindingListener) {
        					//尊爵
    	try {
            JSONObject obj = new JSONObject(Data);
            JSONArray insideobj1 = obj.getJSONArray("Page1");
            JSONArray insideobj2 = obj.getJSONArray("Page2");
            JSONArray insideobj3 = obj.getJSONArray("Page3");
            JSONArray insideobj4 = obj.getJSONArray("Page4");
            JSONArray areaCode = obj.getJSONArray("AreaCode");

            final Bundle bundle = new Bundle();

            //AreaCode
            bundle.putString("areaCode", areaCode.toString());

            //first page
            bundle.putString("firstTitle", insideobj1.getJSONObject(0).getString("BigTitle"));
            bundle.putString("verificationBtnTxt", insideobj1.getJSONObject(0).getString("ButtonText1"));
            bundle.putString("imageURL", insideobj1.getJSONObject(0).getString("EventBNUrl"));
            bundle.putString("checkBoxTxt", insideobj1.getJSONObject(0).getString("CheckText"));

            //second page
            bundle.putString("secondTitle", insideobj2.getJSONObject(0).getString("BigTitle"));
            bundle.putString("secondContentTitle", insideobj2.getJSONObject(0).getString("MainTitle"));
            bundle.putString("secondPhoneTitle", insideobj2.getJSONObject(0).getString("SubTitle1"));
            bundle.putString("phoneBtnTxt", insideobj2.getJSONObject(0).getString("ButtonText1_1"));
            bundle.putString("secondSmsTitle", insideobj2.getJSONObject(0).getString("SubTitle2"));
            bundle.putString("verificationPhoneBtnTxt", insideobj2.getJSONObject(0).getString("ButtonText2_1"));
            bundle.putString("TextBox_Placeholder1", insideobj2.getJSONObject(0).getString("TextBox_Placeholder1"));
            bundle.putString("TextBox_Placeholder2", insideobj2.getJSONObject(0).getString("TextBox_Placeholder2"));
            bundle.putString("ButtonText3", insideobj2.getJSONObject(0).getString("ButtonText3"));
            bundle.putString("ButtonText2_2", insideobj2.getJSONObject(0).getString("ButtonText2_2"));
            bundle.putString("ButtonText2_3", insideobj2.getJSONObject(0).getString("ButtonText2_3"));
            bundle.putString("ButtonText1_3", insideobj2.getJSONObject(0).getString("ButtonText1_3"));

            //third page
            bundle.putString("thirdTitle", insideobj3.getJSONObject(0).getString("BigTitle"));
            bundle.putString("thirdContentTitle", insideobj3.getJSONObject(0).getString("MainTitle"));
            bundle.putString("thirdLineHint", insideobj3.getJSONObject(0).getString("TextBox_Placeholder1"));
            bundle.putString("thirdLineRemind", insideobj3.getJSONObject(0).getString("SubTitle1"));
            bundle.putString("thirdRemainTitle", insideobj3.getJSONObject(0).getString("SubTitle2"));
            bundle.putString("thirdRemainTxt", insideobj3.getJSONObject(0).getString("Note"));
            bundle.putString("sendDataBtnTxt", insideobj3.getJSONObject(0).getString("ButtonText1"));
            bundle.putString("CommunicationSoftWare", insideobj3.getJSONObject(0).getString("CommunicationSoftWare"));

            //fourth page
            bundle.putString("fourthTitle", insideobj4.getJSONObject(0).getString("BigTitle"));
            bundle.putString("fourthContentTitle", insideobj4.getJSONObject(0).getString("MainTitle"));
            bundle.putString("fourthGiftTxt", insideobj4.getJSONObject(0).getString("SubTitle1"));
            bundle.putString("fourthCloseBtnTxt", insideobj4.getJSONObject(0).getString("ButtonText1"));
            bundle.putString("EventBNUrl", insideobj4.getJSONObject(0).getString("EventBNUrl"));
            bundle.putString("GiftName", insideobj4.getJSONObject(0).getString("GiftName"));

            mAct.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    onVIPBindingListener.onEvent(10, "Show VIP dialog");
                    NPVIPDialog npvipDialog = new NPVIPDialog(mAct, true, bundle, vipCommandType , screenOrientation, android.R.style.Theme_Holo_Dialog_MinWidth, onVIPBindingListener);
                    npvipDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            onVIPBindingListener.onEvent(-1, "close VIPDialog");
                        }
                    });
                    npvipDialog.show();
                }
            });

        } catch (Exception e) {
            Log.i("VIPClient", e.toString());
            onVIPBindingListener.onEvent(-100, "Data read error");

        }
		}
	private void processGoldVIPJsonData(String Data, final int vipCommandType, final int screenOrientation, final NPVIPDialog.OnVIPBindingListener onVIPBindingListener) {
    						//金牌
    	try {
			JSONObject obj = new JSONObject(Data);
			JSONArray insideobj1 = obj.getJSONArray("Data");
			final Bundle bundle = new Bundle();
			//first page
			bundle.putString("firstTitle", insideobj1.getJSONObject(0).getString("BigTitle"));
			bundle.putString("verificationBtnTxt", insideobj1.getJSONObject(0).getString("ButtonText1"));
			bundle.putString("checkBoxTxt", insideobj1.getJSONObject(0).getString("CheckText"));
			bundle.putString("imageURL", insideobj1.getJSONObject(0).getString("EventBNUrl"));

			//second page
			bundle.putString("secondTitle", insideobj1.getJSONObject(0).getString("BigTitle"));
			bundle.putString("webURL", insideobj1.getJSONObject(0).getString("WebURL"));
			mAct.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					onVIPBindingListener.onEvent(10, "Show VIP dialog");
					NPVIPDialog npvipDialog = new NPVIPDialog(mAct, true, bundle, vipCommandType , screenOrientation, android.R.style.Theme_Holo_Dialog_MinWidth, onVIPBindingListener);
					npvipDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
						@Override
						public void onDismiss(DialogInterface dialog) {
							onVIPBindingListener.onEvent(-1, "close VIPDialog");
						}
					});
					npvipDialog.show();
				}
			});
		} catch (Exception e) {
			Log.i("VIPClient", e.toString());
			onVIPBindingListener.onEvent(-100, "Data read error");
		}

	}


    }


