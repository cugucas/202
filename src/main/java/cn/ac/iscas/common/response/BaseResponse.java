package cn.ac.iscas.common.response;

/** 
 * @description: 基本响应类 
 * @param: null 
 * @return:  
 * @author LJian
 * @date: 2022/10/14 20:44
 */ 
public class BaseResponse {
    public int status;
    public String info;


    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }


}
