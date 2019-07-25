package com.jiyouliang.fmap.server.data;

/**
 * @author YouLiang.Ji
 *
 * 服务器返回数据基类,解析服务器返回数据需继承该类
 */
public class BaseResponse {

    // 状态码:0-成功,具体请参考服务器返回数据文档
    private int code;
    // 错误或者成功信息
    private String msg;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "BaseResponse{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                '}';
    }
}
