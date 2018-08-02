package com.nsplay.vip;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;


import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLException;


/**
 * Created by user on 2016/7/19.
 */
public class NPVIPHttpClient {

    private Activity act;

//    private boolean TestMode =false;

    //GET
    private String GetVIPMemberQualifications = "https://api-sdk.9splay.com/api/VIPMember/GetVIPMemberQualifications?";

    private String GameToolsGameBindVIP = "https://api-sdk.9splay.com/api/ToolList/GameToolsGameBindVIP?";

    //( 開 關 ) ---------------------------------------------------------------------------------------------------------------
    //正式
    //private String  VIPModelState_Get= "http://api-sdk.9splay.com/api/ToolList/VIPModelState_Get?"; //正式
    //測試
    private String  VIPModelState_Get= "http://api-sdktest.9splay.com/api/ToolList/VIPModelState_Get?";

    //( 金 牌 ) ---------------------------------------------------------------------------------------------------------------
    //正式
    //private String  GameToolsVIPModalInfo_Get= "http://api-sdk.9splay.com/api/ToolList/GameToolsVIPModalInfo_Get?";
    //測試
    private String   GameToolsVIPModalInfo_Get= "http://api-sdktest.9splay.com/api/ToolList/GameToolsVIPModalInfo_Get?";
    //--------------------------------------------------------------------------------------------------------------------

    private String CommunicationBIND = "https://api-sdk.9splay.com/api/VIPMember/CommunicationBIND?";

    //POST
    private String CheckBinded = "https://api.9splay.com/API/PhoneMemberBind/CheckBinded";

    private String CheckVerifyCode = "https://api.9splay.com/API/PhoneMemberBind/CheckVerifyCode";

    private String BindPhoneAccount = "https://api.9splay.com/API/PhoneMemberBind/BindPhoneAccount";

    private String DebugLogUrl = "https://api-sdk.9splay.com/api/Log/SetLog";

    private OnVIPHttpListener onVIPHttpListener;// 事件Listener實體

    public static boolean isNetWorking = false;

    final String TAG = "NPVIPhttpLog";

    private Activity mAct;

    private NPVIPCommandType t;

    private String showvipmodal = "";

    private Map<String, String> params = null;

    public NPVIPHttpClient(Activity act) {
        this.act = act;
        Log.i(TAG, "deviceIdTask.execute()");
    }

    public interface OnVIPHttpListener {
        public void onEvent(int Code, String Message, String jsonData, int type);
    }

    public void setVIPHttpListener(OnVIPHttpListener onVIPHttpListener) {

        this.onVIPHttpListener = onVIPHttpListener;
    }


    // 網路是否可用
    @SuppressLint("MissingPermission")
	public boolean isInternetAvailable() {

        ConnectivityManager manager = (ConnectivityManager) act.getSystemService(Context.CONNECTIVITY_SERVICE);

        // For WiFi Check
        if (manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting()) {
            return true;
        }

        NetworkInfo networkInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        // For Data network check
        if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
            return true;
        }

        return false;
    }

    // HttpPost完成事件處理 "接網路上傳回來的值"
    Handler httpPostHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int code = msg.getData().getInt("Status");
            switch (NPVIPHttpClient.VIPNetWorkState.valueOf(code)) {
                //post成功
                //處理回傳資料
                case PostSuccess:
                    int type = msg.getData().getInt("VIPType");
                    //取得網路上的值
                    String cmdstr = msg.getData().getString("VIPValue");
                    Log.i(TAG, "cmdstr = " + cmdstr);
                    try {
                        //處理回傳資料
                        processVIPBackData(type, cmdstr);
                    } catch (Exception e) {
                        Log.d(TAG, e.toString());
                    }
                    break;
                //post錯誤
                //回調-1001
                case PostError:
                    onVIPHttpListener.onEvent(-1001, msg.getData().getString("VIPStatusCode"), "", msg.getData().getInt("VIPType"));
                    break;
                //PostException
                //回調-1000以及Exception內容
                case PostException:
                    onVIPHttpListener.onEvent(-1000, msg.getData().getString("VIPException"), "", msg.getData().getInt("VIPType"));
                    break;
                default:
                    onVIPHttpListener.onEvent(-100099, "error", "", -1);
                    break;
            }
        }
    };

    // HttpGet完成事件處理 "接網路上傳回來的值"
    Handler httpGetHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int code = msg.getData().getInt("Status");
            int VIPType = msg.getData().getInt("VIPType");
            switch (VIPNetWorkState.valueOf(code)) {
                case GetSuccess:
                    // 取得網路上的值
                    String vipCmdstr = msg.getData().getString("VIPValue");
                    Log.i(TAG, "vipCmdstr = " + vipCmdstr);
                    try {
                        processVIPBackData(VIPType, vipCmdstr);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case GetError:
                    onVIPHttpListener.onEvent(2000, "連線回傳", msg.getData().getString("VIPValue"), VIPType);
                    break;
                case GetException:
                    onVIPHttpListener.onEvent(2000, "連線回傳", msg.getData().getString("VIPValue"), VIPType);
                    break;
                default:
                    break;
            }
        }
    };

    private String getPackgetName(Context context) {
        return context.getApplicationContext().getPackageName();
    }

    private int getOSVersion() {
        return Build.VERSION.SDK_INT;
    }


    private void processVIPBackData(int vipCommandType, String Data) {

        try {
            Log.i("NPNetWork", "qa");
            switch (NPVIPCommandType.fromInteger(vipCommandType)){
				case GameToolsVIPModalInfo_Get:                             //取得是否為金牌VIP及彈窗內容
                    JSONObject jObj = new JSONObject(Data);
                    int code = jObj.getInt("Status");
                    Log.d(TAG, "code = " + code);
                    onVIPHttpListener.onEvent(code, "連線回傳", Data, vipCommandType);
                    break;
                case VIPMemberQualifications:                                   //取得VIP用戶是否要彈窗
                    JSONObject jObj1 = new JSONObject(Data);
                    int code1 = jObj1.getInt("code");
                    Log.d(TAG, "code = " + code1);
                    onVIPHttpListener.onEvent(code1, "連線回傳", Data, vipCommandType);
                    break;
                case VIPModelState_Get:                                     //開關
                    JSONObject jObj2 = new JSONObject(Data);
                    //Log.d(TAG,"NPVIPModelState_Get"+Data);
                    int code2 = jObj2.getInt("Status");
                    JSONArray insideobj = jObj2.getJSONArray("Data");
                    for(int i = 0; i < insideobj.length(); ++i) {
                        JSONObject obj2 = insideobj.getJSONObject(i);
                            showvipmodal = showvipmodal + obj2.getString("ShowVIPModal");
                    }
                    Log.d(TAG, "showvipmodal = " + showvipmodal);
                    Log.d(TAG, "code = " + code2);
                    onVIPHttpListener.onEvent(code2, showvipmodal, Data, vipCommandType);
                    break;
                case GameToolsGameBindVIP:
                    JSONObject jObj3 = new JSONObject(Data);
                    int code3 = jObj3.getInt("Status");
                    Log.d(TAG, "code = " + code3);
                    onVIPHttpListener.onEvent(code3, "連線回傳", Data, vipCommandType);
                    break;
                case CheckBinded:
                    JSONObject jObj4 = new JSONObject(Data);
                    int code4 = jObj4.getInt("code");
                    String msg4 = jObj4.getString("msg");
                    Log.d(TAG, "code = " + code4);
                    onVIPHttpListener.onEvent(code4, msg4, Data, vipCommandType);
                    break;
                case CheckVerifyCode:
                    JSONObject jObj5 = new JSONObject(Data);
                    int code5 = jObj5.getInt("code");
                    String msg5 = jObj5.getString("msg");
                    Log.d(TAG, "code = " + code5);
                    onVIPHttpListener.onEvent(code5, msg5, Data, vipCommandType);
                    break;
                case BindPhoneAccount:
                    JSONObject jObj6 = new JSONObject(Data);
                    int code6 = jObj6.getInt("code");
                    String msg6 = jObj6.getString("msg");
                    Log.d(TAG, "code = " + code6);
                    onVIPHttpListener.onEvent(code6, msg6, Data, vipCommandType);
                    break;
                case FianlCheckBinded:
                    JSONObject jObj7 = new JSONObject(Data);
                    int code7 = jObj7.getInt("code");
                    String msg7 = jObj7.getString("msg");
                    Log.d(TAG, "code = " + code7);
                    onVIPHttpListener.onEvent(code7, msg7, Data, vipCommandType);
                    break;
                case CommunicationBIND:
                    JSONObject jObj8 = new JSONObject(Data);
                    int code8 = jObj8.getInt("code");
                    String msg8 = jObj8.getString("msg");
                    Log.d(TAG, "code = " + code8);
                    onVIPHttpListener.onEvent(code8, msg8, Data, vipCommandType);
                    break;
            }

        } catch (Exception e) {
            Log.i(TAG, "Exception(-1002) : " + e);
            onVIPHttpListener.onEvent(-1002, "DataParseError", "", vipCommandType);
        }

    }


    public void vipHttpConnection(final Activity mAct, final NPVIPCommandType t, String npGameUID, String appID, String phoneNumber, String SMSCode, String bindType) {
        Log.d(TAG, "start vipHttpConnection");
        this.mAct = mAct;
        this.t = t;
        if (!isInternetAvailable()) {
            onVIPHttpListener.onEvent(-500, "網路連線失敗", "", t.getIntValue());
            return;
        }
        params = new HashMap<String, String>();
        if (npGameUID.compareTo("") != 0)
            params.put("UID", npGameUID);
        if (appID.compareTo("") != 0)
            params.put("AppID", appID);
        if (phoneNumber.compareTo("") != 0)
            params.put("PhoneNumber", phoneNumber);
        if (SMSCode.compareTo("") != 0)
            params.put("Code", SMSCode);
        if (bindType.compareTo("") != 0)
            params.put("BindType", bindType);

        for (String key : params.keySet()) {
            String value = params.get(key);
            Log.i(TAG, " 參數: KEY = " + key + " , Value = " + value);
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                // 把USER 填入的帳號 密碼資料傳入至網路上
                // 這是按下登入時
                NPVIPHttpClient.isNetWorking = true;

                switch (t) {
                    case CheckBinded:
                        Log.i(TAG, "UrlType = CheckBinded");
                        httpPOST(t, CheckBinded, params);
                        break;
                    case CheckVerifyCode:
                        Log.i(TAG, "UrlType = CheckVerifyCode");
                        httpPOST(t, CheckVerifyCode, params);
                        break;
                    case BindPhoneAccount:
                        Log.i(TAG, "UrlType = BindPhoneAccount");
                        httpPOST(t, BindPhoneAccount, params);
                        break;
                    case FianlCheckBinded:
                        Log.i(TAG, "UrlType = CheckBinded");
                        httpPOST(t, CheckBinded, params);
                        break;
                    default:
                        break;
                }
            }
        }).start();

    }


    // HttpPost
    private void httpPOST(NPVIPCommandType t, String url, Map<String, String> map) {
        Log.i(TAG, t + " httpPost start");
        try {
            //POST
            String[] result = startHttpPost(url, map);
            Log.i(TAG, "httpResponse after ");
            Log.i(TAG, "status code= " + result[0]);
            // 檢查狀態碼，200表示OK
            if (result[0].compareTo("200") == 0) {
                // *********取出回應字串 取回SERVER傳回來的資料
                String strResult = result[1];
                Message msg = new Message();
                Bundle data = new Bundle();
                //成功
                data.putInt("Status", VIPNetWorkState.PostSuccess.value());
                data.putString("VIPValue", strResult);
                data.putInt("VIPType", t.getIntValue());
                msg.setData(data);
                // 送資料 接到資料送到這裡
                httpPostHandler.sendMessage(msg);
            } else {
                //失敗
                Log.i(TAG, t + " status NO OK");
                //送訊息去處理
                Message msg = new Message();
                Bundle data = new Bundle();
                data.putInt("Status", VIPNetWorkState.PostError.value());
                data.putInt("VIPType", t.getIntValue());
                data.putString("AuthStatusCode", "ServerHttpStatusError : " + result[0]);
                msg.setData(data);
                httpPostHandler.sendMessage(msg);
            }
        } catch (final Exception e) {
            //Exception若是handShark則https改http
            if (e instanceof SSLException) {
                if (url.contains("https")) {
                    url = url.replace("https", "http");
                    mAct.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(mAct, e.toString(), Toast.LENGTH_LONG).show();
                        }
                    });
                    Log.i("NicePlaySSLexception", "" + e.toString());
                    //重新執行一次該Post
                    httpPOST(t, url, map);
                    return;
                }
            }
            Log.i(TAG, "Exception = " + e.toString());
            Message msg = new Message();
            Bundle data = new Bundle();
            data.putInt("Status", VIPNetWorkState.PostException.value());
            data.putInt("VIPType", t.getIntValue());
            data.putString("VIPException", e.toString());
            msg.setData(data);
            // 送資料 接到資料送到這裡
            httpPostHandler.sendMessage(msg);

        } finally {
            //連線TAG關閉
            NPVIPHttpClient.isNetWorking = false;
            Log.i(TAG, t + " httpPost end");
        }
    }


    public String[] startHttpPost(String targeturl, Map<String, String> map) throws Exception {

        Log.i(TAG, "targeturl = " + targeturl);
        String[] result = null;
        String resultData = "";
        URL url = null;
        url = new URL(targeturl);
        //使用HttURLConnection打开链接
        HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
        //只有第一次設定timeout
        urlConn.setReadTimeout(10000);
        urlConn.setConnectTimeout(10000);
        //post設定，需要改為true，以進行參數的讀寫
        urlConn.setDoOutput(true);
        urlConn.setDoInput(true);
        //设置以POST方式
        urlConn.setRequestMethod("POST");
        //POST请求不能使用缓存
        urlConn.setUseCaches(false);
        urlConn.setInstanceFollowRedirects(true);
        //配置本次连接的Content_type,配置为application/x-www-form-urlencoded
        urlConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        urlConn.setRequestProperty("charset", "utf-8");
        //"param1=a&param2=b&param3=c";
        String urlParameters = "";
        for (String key : map.keySet()) {
            String value = map.get(key);
            String paramsstring = key + "=" + URLEncoder.encode(value, "UTF-8") + "&";
            urlParameters += paramsstring;
        }
        byte[] postData = urlParameters.getBytes(Charset.forName("UTF-8"));
        int postDataLength = postData.length;
        urlConn.setRequestProperty("Content-Length", Integer.toString(postDataLength));
        //连接，从postUrl.OpenConnection()至此的配置必须要在connect之前完成。
        //要注意的是connection.getOutputStream会隐含地进行connect.
        urlConn.connect();
        //DataOutputStream流。
//        DataOutputStream out = new DataOutputStream(urlConn.getOutputStream());
        DataOutputStream out = new DataOutputStream(urlConn.getOutputStream());
        out.write(postData);
        //要上传的参数
//        String content = "par=" + URLEncoder.encode("ABCDEF","gb2312");
        //将要上传的内容写入流中
//        out.writeBytes(content);
        //刷新、关闭
        out.flush();
        out.close();
        //获取数据
        BufferedReader reader = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
        String inputLine = null;

        //---///得到读取的内容(流)
        //---InputStreamReader in = new InputStreamReader(urlConn.getInputStream());
        //---// 为输出创建BufferedReader
        //---BufferedReader buffer = new BufferedReader(in);
        //---String inputLine = null;

        //---//使用循环来读取获得的数据
        while (((inputLine = reader.readLine()) != null)) {
            //我们在每一行后面加上一个"\n"来换行
            resultData += inputLine + "\n";
        }
        reader.close();
        //关闭http链接
        urlConn.disconnect();
        //设置显示取得的内容
        if (!resultData.equals("")) {
            //傳送result data
            String[] resultary = {"200", resultData};
            result = resultary;
        } else {
            String[] resulterrorary = {"-100000", ""};
            result = resulterrorary;
        }
        //关闭InputStreamReader
        reader.close();
        //关闭http连接
        urlConn.disconnect();
        //设置显示取得的内容
        return result;
    }


    private void httpGET(NPVIPCommandType t, String strUrl) {
        Log.i(TAG, t + " httpGET start");
        HttpURLConnection connection = null;
        try {
            // 初始化 URL
            URL url = new URL(strUrl);
            // 取得連線物件
            connection = (HttpURLConnection) url.openConnection();
            // 設定 request timeout
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(10000);
//            // 模擬 Chrome 的 user agent, 因為手機的網頁內容較不完整
//            connection.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2490.71 Safari/537.36");
//            // 設定開啟自動轉址
//            connection.setInstanceFollowRedirects(true);
            // 若要求回傳 200 OK 表示成功取得網頁內容
            if (connection.getResponseCode() == HttpsURLConnection.HTTP_OK) {		// 讀取網頁內容
                InputStream inputStream = connection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String tempStr;
                StringBuffer stringBuffer = new StringBuffer();
                while ((tempStr = bufferedReader.readLine()) != null) {
                    stringBuffer.append(tempStr);
                }
                bufferedReader.close();
                inputStream.close();
                NPVIPHttpClient.isNetWorking = false;
                // 網頁內容字串
                String responseString = stringBuffer.toString();
                // 送資料
                Message msg = new Message();
                Bundle data = new Bundle();
                data.putInt("Status", VIPNetWorkState.GetSuccess.value());
                data.putString("VIPValue", responseString);
                data.putInt("VIPType", t.getIntValue());
                msg.setData(data);
                // 送資料 接到資料送到這裡
                httpGetHandler.sendMessage(msg);
				Log.d(TAG, "msg = " + msg.toString());
            } else {
                //失敗
                //送DebugLog到server紀錄
                Log.i(TAG, "getResponseCode != HttpsURLConnection.HTTP_OK ");
                // 送資料
                Message msg = new Message();
                Bundle data = new Bundle();
                data.putInt("Status", VIPNetWorkState.GetError.value());
                data.putString("VIPValue", "Get Error : " + connection.getResponseCode());
                data.putInt("VIPType", t.getIntValue());
                msg.setData(data);
                // 送資料 接到資料送到這裡
                httpGetHandler.sendMessage(msg);
            }
        } catch (Exception e) {
            //送DebugLog到server紀錄
            e.printStackTrace();
            Log.i(TAG, "Exception : " + e.toString());
            // 送資料
            Message msg = new Message();
            Bundle data = new Bundle();
            data.putInt("Status", VIPNetWorkState.GetException.value());
            data.putString("AuthValue", "Get Exception : " + e.toString());
            data.putInt("AuthType", t.getIntValue());
            msg.setData(data);
            // 送資料 接到資料送到這裡
            httpGetHandler.sendMessage(msg);
        } finally {
            // 中斷連線
            if (connection != null) {
                connection.disconnect();
            }
            NPVIPHttpClient.isNetWorking = false;
            Log.i(TAG, t + " httpGET end");
        }
    }


    public void vipModelState(String appID) {       //開關

        final StringBuilder api = new StringBuilder();
        api.append(VIPModelState_Get);
        api.append("AppID=");
        api.append(appID);

        Log.d(TAG, "get url = " + api.toString());
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                NPVIPHttpClient.isNetWorking = true;
                httpGET(NPVIPCommandType.VIPModelState_Get, api.toString());
            }
        };
        new Thread(runnable).start();
    }

    public void queryVIPState(String appID, String GameUid) {       //判斷尊爵

        final StringBuilder api = new StringBuilder();
        api.append(GetVIPMemberQualifications);
        api.append("AppID=");
        api.append(appID);
        api.append("&GameUid=");
        api.append(GameUid);

        Log.d(TAG, "get url = " + api.toString());
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                NPVIPHttpClient.isNetWorking = true;
                httpGET(NPVIPCommandType.VIPMemberQualifications, api.toString());
            }
        };
        new Thread(runnable).start();
    }


    public void queryVIPDialogData(String appID) {          //調尊爵

        final StringBuilder api = new StringBuilder();
        api.append(GameToolsGameBindVIP);
        api.append("AppID=");
        api.append(appID);
        api.append("&Language=zh_TW");
        Log.d(TAG, "get url = " + api.toString());
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                NPVIPHttpClient.isNetWorking = true;
                httpGET(NPVIPCommandType.GameToolsGameBindVIP, api.toString());
            }
        };
        new Thread(runnable).start();
    }



    public void queryGoldenVIPDialogData(String appID,String uid,String appkey,String dbversion) {          //調金牌

        final StringBuilder api = new StringBuilder();
        api.append(GameToolsVIPModalInfo_Get);
        api.append("AppID=");
        api.append(appID);
        api.append("&Language=zh_TW");
        api.append("&GameUid=");
        api.append(uid);
		api.append("&Sign=");
        String Sign = MD5(appID+appkey+uid+dbversion);							//DB = 1
        api.append(Sign);
        Log.d(TAG, "get url = " + api.toString());
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                NPVIPHttpClient.isNetWorking = true;
                httpGET(NPVIPCommandType.GameToolsVIPModalInfo_Get, api.toString());
            }
	};
        new Thread(runnable).start();
    }



    public void bindingCommunication(String phoneNumber, String communicationType, String communicationID) {

        final StringBuilder api = new StringBuilder();
        api.append(CommunicationBIND);
        api.append("Phone=");
        api.append(phoneNumber);
        api.append("&CommunicationType=");
        api.append(communicationType);
        api.append("&CommunicationID=");
        api.append(communicationID);
        Log.d(TAG, "get url = " + api.toString());
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                NPVIPHttpClient.isNetWorking = true;
                httpGET(NPVIPCommandType.CommunicationBIND, api.toString());
            }
        };
        new Thread(runnable).start();
    }

    enum VIPNetWorkState {
        PostSuccess(1),
        PostError(2),
        PostException(3),
        GetSuccess(4),
        GetError(5),
        GetException(6);
        private int intValue;

        VIPNetWorkState(int Value) {
            this.intValue = Value;
        }

        public int value() {
            return intValue;
        }

        public static VIPNetWorkState valueOf(int value) {    //    手写的从int到enum的转换函数
            switch (value) {
                case 1:
                    return PostSuccess;
                case 2:
                    return PostError;
                case 3:
                    return PostException;
                case 4:
                    return GetSuccess;
                case 5:
                    return GetError;
                case 6:
                    return GetException;
                default:
                    return null;
            }
        }
    }

	private static String MD5(String str) {
		MessageDigest md5 = null;

		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}

		char[] charArray = str.toCharArray();
		byte[] byteArray = new byte[charArray.length];

		for(int i = 0; i < charArray.length; ++i) {
			byteArray[i] = (byte)charArray[i];
		}

		byte[] md5Bytes = md5.digest(byteArray);
		StringBuffer hexValue = new StringBuffer();

		for(int i = 0; i < md5Bytes.length; ++i) {
			int val = md5Bytes[i] & 255;
			if (val < 16) {
				hexValue.append("0");
			}

			hexValue.append(Integer.toHexString(val));
		}
		return hexValue.toString();
	}
}
