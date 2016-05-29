package com.jiayusoft.mobile.oa.sign;

import com.jiayusoft.mobile.oa.utils.Phrase;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by ASUS on 2014/9/25.
 */
public class SignInfo {
    String usercode,cpuxlh,sysdatetime,flag,url;

    private SignInfo(String usercode, String cpuxlh, String sysdatetime, String flag, String url) {
        this.usercode = usercode;
        this.cpuxlh = cpuxlh;
        this.sysdatetime = sysdatetime;
        this.flag = flag;
        this.url = url;
    }

    public static SignInfo generate(String qrcodeResult) {
        String[] strs = StringUtils.split(qrcodeResult,',');
        if (strs.length==5){
            String tempUrl = StringUtils.removeEnd(strs[4],"?wsdl");
            return new SignInfo(strs[0],strs[1],strs[2],strs[3],tempUrl);
        }else{
            return new SignInfo("","","","","");
        }
    }

    public String getUrl() {
        return url;
    }

    public CharSequence getTextToShow(){
        CharSequence formatted =
                Phrase.from("<h2>签到信息:</h2><br>" +
                        "工号 <b>{usercode}</b><br>" +
                        "CPU序列号 <b>{cpuxlh}</b><br>" +
                        "系统时间 <b>{sysdatetime}</b><br>" +
                        "上下班类型 <b>{flag}</b><br>" +
                        "接口url <b>{url}</b><br>")
                .put("usercode",usercode)
                .put("cpuxlh",cpuxlh)
                .put("sysdatetime",sysdatetime)
                .put("flag",flag)
                .put("url",url)
                .format();
        return formatted;
    }

    public String getXmlToUp(String imei){
        CharSequence formatted =
                Phrase.from("<?xml version=\"1.0\" encoding=\"UTF-8\"?><root><request>" +
                        "<usercode>{usercode}</usercode>" +
                        "<cpuxlh>{cpuxlh}</cpuxlh>" +
                        "<sysdatetime>{sysdatetime}</sysdatetime>" +
                        "<cs>{flag}</cs>" +
                        "<phone>{imei}</phone></request></root>")
                        .put("usercode",usercode)
                        .put("cpuxlh",cpuxlh)
                        .put("sysdatetime",sysdatetime)
                        .put("flag",flag)
                        .put("imei",imei)
                        .format();
        return formatted.toString();
    }
}
