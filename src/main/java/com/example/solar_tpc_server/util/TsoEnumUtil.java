package com.example.solar_tpc_server.util;

import com.example.solar_tpc_server.enums.TsoProjectStatusEnum;

public class TsoEnumUtil {

    public static String getProjectStatusName(Integer statusId) {
        if (statusId == null) {
            return "";
        }
        for (TsoProjectStatusEnum status : TsoProjectStatusEnum.values()) {
            if (status.getStatusId().equals(statusId)) {
                String translated = TsoMessageUtil.getMessage(status.getName());
                // Nếu getMessage trả về đúng key (nghĩa là không tìm thấy trong properties), dùng name2 dự phòng
                if (translated.equals(status.getName())) {
                    return status.getName2();
                }
                return translated;
            }
        }
        return "";
    }
}
