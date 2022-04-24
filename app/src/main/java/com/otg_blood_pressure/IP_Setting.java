package com.otg_blood_pressure;

/**
 * 本APP所有容之智慧財權，均為蝸牛手建館所有，包括文字、照片、圖片、軟體程式碼，皆受中華民國著作權法、商標法、
 * 公平交易法與其他相關法令之保障，請尊重智慧財權並遵守法令規範，請勿自行使用、轉載、修改、重製、發行、公開發表、教學、散佈，
 * 若需此方面的使用或服務，請務必事先與蝸牛手建館人員接洽，並且取得授權。
 */

public class IP_Setting {
    private int ip1 = 192, ip2 = 168, ip3 = 1, ip4 = 104
            , ip5 = 80;
    public String blood_server_url = "http://" + String.valueOf(ip1) + "." + String.valueOf(ip2) + "." + String.valueOf(ip3) + "." + String.valueOf(ip4) + ":" + String.valueOf(ip5) + "/";
}
