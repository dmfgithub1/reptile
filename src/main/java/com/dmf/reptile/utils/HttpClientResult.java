package com.dmf.reptile.utils;

import java.io.Serializable;

public class HttpClientResult implements Serializable{
	@Override
	public String toString() {
		return "HttpClientResult [code=" + code + ", content=" + content + "]";
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	private static final long serialVersionUID = 1L;

	/**
     * 响应状态码
     */
    private int code;

    /**
     * 响应数据
     */
    private String content;
    
    public HttpClientResult(int code) {
    	this.code = code;
    }
    
    public HttpClientResult(int code,String content) {
    	this.code = code;
    	this.content = content;
    }
}
