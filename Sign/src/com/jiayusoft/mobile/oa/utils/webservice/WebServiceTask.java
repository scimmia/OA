package com.jiayusoft.mobile.oa.utils.webservice;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.util.Xml;
import com.jiayusoft.mobile.oa.sign.SignInfo;
import com.jiayusoft.mobile.oa.utils.DebugLog;
import org.apache.commons.lang3.StringUtils;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.MarshalBase64;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;

/**
 * Created by ASUS on 2014/5/19.
 */
public class WebServiceTask extends AsyncTask<Void,Void,Void> {
    public ProgressDialog mpDialog;
    public Context mContent;
    public String msgToShow;
    SoapRequestStruct soapRequestStruct;
    public WebServiceListener webServiceListener;
    Exception exception;
    String responseString;

    public WebServiceTask(Context mContent, String msgToShow, SoapRequestStruct soapRequestStruct, WebServiceListener webServiceListener) {
        super();
        this.mContent = mContent;
        this.msgToShow = msgToShow;
        this.soapRequestStruct = soapRequestStruct;
        this.webServiceListener = webServiceListener;
        this.exception = null;
        this.responseString = null;
    }

    @Override
    protected void onPreExecute(){
        super.onPreExecute();
        mpDialog = new ProgressDialog(mContent);
//        mpDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);//设置风格为圆形进度条
        mpDialog.setMessage(msgToShow);
        mpDialog.setCancelable(true);

        mpDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                dialog.dismiss();
                cancel(true);
            }
        });
        mpDialog.show();
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
//            Log.e("Tag", signInfo.getXmlToUp(deviceID).toString());
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER10);
            SoapObject request = new SoapObject(soapRequestStruct.getServiceNameSpace(), soapRequestStruct.getMethodName());
            if (soapRequestStruct.getPropertys()!=null){
                for (PropertyInfo propertyInfo : soapRequestStruct.getPropertys()){
                    request.addProperty(propertyInfo);
                }
            }
            envelope.bodyOut = request;
            (new MarshalBase64()).register(envelope);
            HttpTransportSE transport = new HttpTransportSE(soapRequestStruct.getServiceUrl());
            transport.call(null, envelope);
            if (envelope.getResponse() != null) {
                SoapObject result = (SoapObject) envelope.bodyIn;
                responseString = result.getProperty(0).toString();
                DebugLog.e("responseString:   "+responseString);
            }
        } catch (IOException e) {
            this.exception = e;
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            this.exception = e;
            e.printStackTrace();
        }
        return null;
    }
    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
        mpDialog.dismiss();
        if(webServiceListener!=null){
            if (exception!=null){
                webServiceListener.onError(exception);
            }else{
                if (StringUtils.isEmpty(responseString)){
                    webServiceListener.onFailure("服务器连接失败，请稍后重试或联系管理员");
                }else {
                    webServiceListener.onSuccess(responseString);
                }
            }
        }
    }
}

//package com.jiayusoft.mobile.oa.utils.webservice;
//
//import android.app.ProgressDialog;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.SharedPreferences;
//import android.net.wifi.WifiManager;
//import android.os.AsyncTask;
//import android.preference.PreferenceManager;
//import android.provider.Settings;
//import android.telephony.TelephonyManager;
//import android.util.Log;
//import android.util.Xml;
//import com.jiayusoft.mobile.oa.sign.SignInfo;
//import org.apache.commons.lang3.StringUtils;
//import org.ksoap2.SoapEnvelope;
//import org.ksoap2.serialization.MarshalBase64;
//import org.ksoap2.serialization.PropertyInfo;
//import org.ksoap2.serialization.SoapObject;
//import org.ksoap2.serialization.SoapSerializationEnvelope;
//import org.ksoap2.transport.HttpTransportSE;
//import org.xmlpull.v1.XmlPullParser;
//import org.xmlpull.v1.XmlPullParserException;
//
//import java.io.IOException;
//import java.io.StringReader;
//import java.io.UnsupportedEncodingException;
//
///**
// * Created by ASUS on 2014/5/19.
// */
//public class WebServiceTask extends AsyncTask<Void,Void,Void> {
//    public ProgressDialog mpDialog;
//    public Context mContent;
//    public String msgToShow;
//    SoapRequestStruct soapRequestStruct;
//    public WebServiceListener webServiceListener;
//    Exception exception;
//    String message;
//    String responseString;
//
//    public WebServiceTask(Context mContent, String msgToShow, SoapRequestStruct soapRequestStruct, WebServiceListener webServiceListener) {
//        super();
//        this.mContent = mContent;
//        this.msgToShow = msgToShow;
//        this.soapRequestStruct = soapRequestStruct;
//        this.webServiceListener = webServiceListener;
//        this.exception = null;
//        this.message = null;
//        this.responseString = null;
//    }
//
//    @Override
//    protected void onPreExecute(){
//        super.onPreExecute();
//        mpDialog = new ProgressDialog(mContent);
////        mpDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);//设置风格为圆形进度条
//        mpDialog.setMessage(msgToShow);
//        mpDialog.setCancelable(true);
//
//        mpDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
//            @Override
//            public void onCancel(DialogInterface dialog) {
//                dialog.dismiss();
//                cancel(true);
//            }
//        });
//        mpDialog.show();
//    }
//
////    @Override
////    protected Void doInBackground(Void... params) {
////        try {
////            String deviceID = getUUID(mContent);
////            Log.e("Tag", signInfo.getXmlToUp(deviceID).toString());
////            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER10);
////            SoapObject request = new SoapObject("http://com.zljy.oa.webservice", "save");
////            request.addProperty("Request", signInfo.getXmlToUp(deviceID).toString());
////            envelope.bodyOut = request;
////            (new MarshalBase64()).register(envelope);
////            HttpTransportSE transport = new HttpTransportSE(signInfo.getUrl());
////            transport.call(null, envelope);
////            if (envelope.getResponse() != null) {
////                SoapObject result = (SoapObject) envelope.bodyIn;
////                responseString = result.getProperty(0).toString();
////                Log.e("TAG", responseString);
////                if (StringUtils.isNotEmpty(responseString)) {
////                    this.message = getResultMsgFromXml(responseString);
////                }
////            }
////        } catch (IOException e) {
////            this.exception = e;
////            e.printStackTrace();
////        } catch (XmlPullParserException e) {
////            this.exception = e;
////            e.printStackTrace();
////        }
////        return null;
////    }
//
//    @Override
//    protected Void doInBackground(Void... params) {
//        try {
////            String deviceID = getUUID(mContent);
////            Log.e("Tag", signInfo.getXmlToUp(deviceID).toString());
//            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER10);
//            SoapObject request = new SoapObject(soapRequestStruct.getServiceNameSpace(), soapRequestStruct.getMethodName());
//            if (soapRequestStruct.getPropertys()!=null){
//                for (PropertyInfo propertyInfo : soapRequestStruct.getPropertys()){
//                    request.addProperty(propertyInfo);
//                }
//            }
//            envelope.bodyOut = request;
//            (new MarshalBase64()).register(envelope);
//            HttpTransportSE transport = new HttpTransportSE(soapRequestStruct.getServiceUrl());
//            transport.call(null, envelope);
//            if (envelope.getResponse() != null) {
//                SoapObject result = (SoapObject) envelope.bodyIn;
//                responseString = result.getProperty(0).toString();
//                Log.e("TAG", responseString);
//                if (StringUtils.isNotEmpty(responseString)) {
//                    this.message = getResultMsgFromXml(responseString);
//                }
//            }
//        } catch (IOException e) {
//            this.exception = e;
//            e.printStackTrace();
//        } catch (XmlPullParserException e) {
//            this.exception = e;
//            e.printStackTrace();
//        }
//        return null;
//    }
//    @Override
//    protected void onPostExecute(Void result) {
//        super.onPostExecute(result);
//        mpDialog.dismiss();
//        if(webServiceListener!=null){
//            if (exception!=null){
//                webServiceListener.onError(exception);
//            }else{
//                if (StringUtils.isEmpty(message)){
//                    webServiceListener.onFailure("服务器连接失败，请稍后重试或联系管理员");
//                }else {
//                    webServiceListener.onSuccess(message);
//                }
//            }
//        }
//    }
//
//    public String getResultMsgFromXml(String xmlStr) throws XmlPullParserException, IOException {
//        XmlPullParser parser = Xml.newPullParser();
//        parser.setInput(new StringReader(xmlStr));
////            parser.setInput(new StringReader("<?xml version='1.0' encoding='utf-8'?><Body><Response ErrMsg=''><D_GRDABH>371625060160000204</D_GRDABH></Response></Body>"));
//        int event = parser.getEventType();
//        while (event != XmlPullParser.END_DOCUMENT) {
//            switch (event) {
//                case XmlPullParser.START_DOCUMENT:
//                    break;
//                case XmlPullParser.START_TAG:
//                    if (parser.getName().equals("result")) {
//                        return parser.nextText();
//                    }
//                    break;
//                case XmlPullParser.TEXT:
//                    break;
//                case XmlPullParser.END_TAG:
//                    break;
//                default:
//                    break;
//            }
//            event = parser.next();
//        }
//        return null;
//    }
//
//}