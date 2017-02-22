package com.kaishengit.dto;

import org.omg.PortableInterceptor.SUCCESSFUL;

/**
 * Created by 刘忠伟 on 2017/2/22.
 */
public class AjaxResult {


    public static final String SUCCESS = "success";
    public static final String ERROR    =   "error";

    private String status;
    private Object data;
    private String message;

    public AjaxResult(){}
    public  AjaxResult(String message){
        this.status = ERROR;
        this.message = message;
    }
    public AjaxResult(String status,Object data){
        this.status = SUCCESS;
        this.data = data;
    }
    public  AjaxResult(Object data){
        this.status = SUCCESS;
        this.data = data;
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
