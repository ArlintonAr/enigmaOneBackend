package com.enigmaOne.enigmaOne.service.dto;

import org.springframework.stereotype.Component;

import java.util.Optional;


public class ApiResponse<T> {

    private  String message;
    private  boolean success;
    private  T data;


    public ApiResponse(String message,T data){
        this.message=message;
        this.success = true;
        this.data = data;

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



    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }





}
