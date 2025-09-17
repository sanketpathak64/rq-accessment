package com.reliaquest.api.dto;

import lombok.Data;

@Data
public class Response<T> {
    private T data;
    private String status;

    public static <T> Response<T> handledWith(T data) {
        Response<T> response = new Response<>();
        response.setData(data);
        response.setStatus("Successfully processed request.");
        return response;
    }

    public static <T> Response<T> handled() {
        Response<T> response = new Response<>();
        response.setStatus("Failed to process request.");
        return response;
    }
}

