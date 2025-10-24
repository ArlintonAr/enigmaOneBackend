package com.enigmaOne.enigmaOne.service.dto;

public class ApiResponseTest<T> {
    private  String message;
    private  boolean success;
    private  T data;
    private Integer status;

    public ApiResponseTest(String message,T data, Integer status){
        this.message=message;
        this.success = true;
        this.data = data;
        this.status = status;

    }


    public  boolean isSuccess(){
        return this.success;
    }
    public  void setSuccess(boolean success){
        this.success = success;
    }
    public String getMessage(){return  this.message;}
    public void setMessage(String message){
        this.message = message;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }



}
