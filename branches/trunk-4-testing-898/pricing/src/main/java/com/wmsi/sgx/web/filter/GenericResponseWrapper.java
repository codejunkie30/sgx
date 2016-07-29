package com.wmsi.sgx.web.filter;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

/**
 * Response wrapper for maniuplating data after it's written to the servlet output stream
 * Inspired by (read: copied from) Oracle docs 
 * http://docs.oracle.com/cd/B32110_01/web.1013/b28959/filters.htm#BCFIAAAH
 *  * 
 * @author JLee
 *
 */
public class GenericResponseWrapper extends HttpServletResponseWrapper {
	 
    private ByteArrayOutputStream output;
    private int contentLength;
    private String contentType;
 
    public GenericResponseWrapper(HttpServletResponse response) {
        super(response);
 
        output = new ByteArrayOutputStream();
    }
 
    public byte[] getData() {
        return output.toByteArray();
    }
 
    public ServletOutputStream getOutputStream() {
        return new FilterServletOutputStream(output);
    }
 
    public PrintWriter getWriter() {
        return new PrintWriter(getOutputStream(), true);
    }
 
    public void setContentLength(int length) {
        this.contentLength = length;
        super.setContentLength(length);
    }
 
    public int getContentLength() {
        return contentLength;
    }
 
    public void setContentType(String type) {
        this.contentType = type;
        super.setContentType(type);
    }
 
    public String getContentType() {
        return contentType;
    }
}