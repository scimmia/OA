package com.jiayusoft.mobile.oa;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Xml;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.jiayusoft.mobile.oa.utils.DebugLog;
import com.jiayusoft.mobile.oa.utils.GlobalData;
import com.jiayusoft.mobile.oa.utils.Phrase;
import com.jiayusoft.mobile.oa.utils.ToolUtils;
import com.jiayusoft.mobile.oa.utils.webservice.SoapRequestStruct;
import com.jiayusoft.mobile.oa.utils.webservice.WebServiceListener;
import com.jiayusoft.mobile.oa.utils.webservice.WebServiceTask;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.StringReader;

/**
 * Created by ASUS on 2014/9/29.
 */
public class BindingActivity extends Activity implements GlobalData{
    Button sign;
    private EditText mUsernameView;
    private EditText mPasswordView;
    String url;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.binding_activity);
        Bundle bundle=getIntent().getExtras();
        url=bundle.getString("URL");
        mUsernameView = (EditText) findViewById(R.id.username);
        mPasswordView = (EditText) findViewById(R.id.password);
        findViewById(R.id.binding).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                beginRegist();
            }
        });
    }

    public void beginRegist(){
        // Reset errors.
        mUsernameView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String username = mUsernameView.getText().toString();
        String password = mPasswordView.getText().toString();
        boolean cancel = false;
        View focusView = null;

        // Check for a valid password.
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(username)) {
            mUsernameView.setError(getString(R.string.error_field_required));
            focusView = mUsernameView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            CharSequence formatted =
                    Phrase.from("<?xml version=\"1.0\" encoding=\"UTF-8\"?><root><request>" +
                            "<usercode>{usercode}</usercode>" +
                            "<password>{password}</password>" +
                            "<phone>{imei}</phone></request></root>")
                            .put("usercode",username)
                            .put("password",password)
                            .put("imei",ToolUtils.getUUID(BindingActivity.this))
                            .format();

            SoapRequestStruct soapRequestStruct = new SoapRequestStruct();
            soapRequestStruct.setServiceNameSpace(WS_NameSpace);
            soapRequestStruct.setMethodName(WS_Method_Binding);
            soapRequestStruct.setServiceUrl(url);
//            soapRequestStruct.setServiceUrl("http://58.56.20.118:9090/oa/ws/ImpData");
            soapRequestStruct.addProperty(WS_Property_Binding,formatted.toString());
            DebugLog.e("WS_Property_Binding: " + formatted.toString());

            new WebServiceTask(BindingActivity.this, "校验中...",soapRequestStruct, bindingListener).execute();

        }
    }
    private WebServiceListener bindingListener = new WebServiceListener() {
        @Override
        public void onSuccess(String content) {
            try {
                String result = getResultMsgFromXml(content);
                showToast(result);
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

        }

        @Override
        public void onError(Exception exception) {

        }
    };

    private Toast toast;
    public void showToast(String msg) {
        if (toast == null) {
            toast = Toast.makeText(BindingActivity.this,"",Toast.LENGTH_SHORT);
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
}