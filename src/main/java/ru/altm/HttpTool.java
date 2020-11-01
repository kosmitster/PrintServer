package ru.altm;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.Response;
import fi.iki.elonen.NanoHTTPD.Response.Status;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

public class HttpTool {

    public static Response getXlsResponse(Status code, OutputStream stream) {

        ByteArrayInputStream byteArrayInputStream = new  ByteArrayInputStream(((ByteArrayOutputStream)stream).toByteArray());
        return NanoHTTPD.newFixedLengthResponse(code, "application/vnd.ms-excel", byteArrayInputStream, ((ByteArrayOutputStream) stream).size());
    }

    public static Response getPdfResponse(Status code, OutputStream stream) {

        ByteArrayInputStream byteArrayInputStream = new  ByteArrayInputStream(((ByteArrayOutputStream)stream).toByteArray());
        return NanoHTTPD.newFixedLengthResponse(code, "application/pdf", byteArrayInputStream, ((ByteArrayOutputStream) stream).size());
    }
}
