package com.jiayusoft.mobile.oa;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.util.Log;
import android.util.Xml;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import com.jiayusoft.mobile.oa.sign.Activity_Capture;
import com.jiayusoft.mobile.oa.sign.SignInfo;
import com.jiayusoft.mobile.oa.utils.DebugLog;
import com.jiayusoft.mobile.oa.utils.GlobalData;
import com.jiayusoft.mobile.oa.utils.ToolUtils;
import com.jiayusoft.mobile.oa.utils.webservice.SoapRequestStruct;
import com.jiayusoft.mobile.oa.utils.webservice.WebServiceListener;
import com.jiayusoft.mobile.oa.utils.webservice.WebServiceTask;
import org.apache.commons.lang3.StringUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.StringReader;

public class MyActivity extends Activity implements GlobalData{
    Button btnScan;
    Button btnSign;
    CheckBox autoScan;
    CheckBox autoSign;
    TextView tvSignInfo;
    SignInfo signInfo;



    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        tvSignInfo = (TextView) findViewById(R.id.sign_info);
        autoScan = (CheckBox) findViewById(R.id.auto_scan);
        autoSign = (CheckBox) findViewById(R.id.auto_sign);


        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MyActivity.this);
        autoScan.setChecked(sharedPreferences.getBoolean(AUTO_SCAN, false));
        autoSign.setChecked(sharedPreferences.getBoolean(AUTO_SIGN, false));



        findViewById(R.id.begin_scan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startScan();
            }
        });
        findViewById(R.id.begin_bind).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (signInfo==null || StringUtils.isEmpty(signInfo.getUrl())){
                    showToast("请先扫描二维码");
                }else {
                    Intent intent = new Intent(MyActivity.this, BindingActivity.class);
                    intent.putExtra("URL", signInfo.getUrl());
                    startActivity(intent);
                }
            }
        });
        findViewById(R.id.begin_sign).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSign();
            }
        });
        autoScan.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(MyActivity.this);
                SharedPreferences.Editor spEd = sp.edit();
                spEd.putBoolean(AUTO_SCAN, isChecked);
                spEd.commit();
            }
        });
        autoSign.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(MyActivity.this);
                SharedPreferences.Editor spEd = sp.edit();
                spEd.putBoolean(AUTO_SIGN, isChecked);
                spEd.commit();
            }
        });
//        startScan();
//        new WebServiceTask(MyActivity.this, "签到中...", signInfo, signListener).execute();
        if (autoScan.isChecked()){
            startScan();
        }
    }

    private WebServiceListener signListener = new WebServiceListener() {
        @Override
        public void onSuccess(String content) {
            signInfo = null;
            DebugLog.e(content);
            try {
                String result = getResultMsgFromXml(content);
                tvSignInfo.setText(Html.fromHtml("<h2>签到信息:</h2><br><b>" +result+"</b>"));
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        public String getResultMsgFromXml(String xmlStr) throws XmlPullParserException, IOException {
            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(new StringReader(xmlStr));
//            parser.setInput(new StringReader("<?xml version='1.0' encoding='utf-8'?><Body><Response ErrMsg=''><D_GRDABH>371625060160000204</D_GRDABH></Response></Body>"));
            int event = parser.getEventType();
            while (event != XmlPullParser.END_DOCUMENT) {
                switch (event) {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        if (parser.getName().equals("result")) {
                            return parser.nextText();
                        }
                        break;
                    case XmlPullParser.TEXT:
                        break;
                    case XmlPullParser.END_TAG:
                        break;
                    default:
                        break;
                }
                event = parser.next();
            }
            return null;
        }

        @Override
        public void onFailure(String content) {
            signInfo = null;
            DebugLog.e(content);
            showToast(content);
        }

        @Override
        public void onError(Exception exception) {
            signInfo = null;
            DebugLog.e(exception.toString());
            showToast("服务器异常，请联系管理员");
        }
    };
    private Toast toast;
    public void showToast(String msg) {
        if (toast == null) {
            toast = Toast.makeText(MyActivity.this,"",Toast.LENGTH_SHORT);
        }
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setText(msg);
        toast.show();
    }
    public void cancelToast() {
        if (toast != null) {
            toast.cancel();
        }
    }

    public void startScan(){
        Intent intent = new Intent(MyActivity.this, Activity_Capture.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        startActivityForResult(intent, SCAN_OK);

    }

    public void startSign(){
//        String xmlString = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
//                "<Body >\n" +
//                "<usercode>1123</usercode>"+
//                "<cpuxlh>3321</cpuxlh>"+
//                "<sysdatetime>123</sysdatetime>"+
//                "<flag>1</flag>"+
//                "<phone>1231231</phone>"+
//                "</Body>";
        if (signInfo==null){
            showToast("请先扫描签到二维码");
        }else {
            SoapRequestStruct soapRequestStruct = new SoapRequestStruct();
            soapRequestStruct.setServiceNameSpace(WS_NameSpace);
            soapRequestStruct.setMethodName(WS_Method_Save);
            soapRequestStruct.setServiceUrl(signInfo.getUrl());
            soapRequestStruct.addProperty(WS_Property_Save,signInfo.getXmlToUp(ToolUtils.getUUID(MyActivity.this)));
            DebugLog.e("WS_Property_Save: "+signInfo.getXmlToUp(ToolUtils.getUUID(MyActivity.this)));
            new WebServiceTask(MyActivity.this, "签到中...",soapRequestStruct, signListener).execute();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) { //resultCode为回传的标记，我在B中回传的是RESULT_OK
            case SCAN_OK:
                Bundle b = data.getExtras();  //data为B中回传的Intent
                String str = b.getString("result");//str即为回传的值"Hello, this is B speaking"
//                tvSignInfo.setText("签到信息:"+str);
//                CharSequence formatted = Phrase.from("签到信息:<br><h1><mxgsa>{first_name}</mxgsa></h1>, you are {age} years old.")
//                        .put("first_name", b.getString("result"))
//                        .put("age", b.getString("res2ult"))
//                        .format();
                DebugLog.e("onActivityResult  "+str);
                signInfo = SignInfo.generate(str);
                tvSignInfo.setText(Html.fromHtml(str+"<br>----------<br>"+signInfo.getTextToShow().toString()));
                if (autoSign.isChecked()){
                    startSign();
                }
                break;
            default:
                break;
        }
    }
}
