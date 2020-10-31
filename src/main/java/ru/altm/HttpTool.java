package ru.altm;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.Response;
import fi.iki.elonen.NanoHTTPD.Response.Status;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class HttpTool {

    public static Response getPdfResponce(Status code, OutputStream stream) {

/*
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        out.write(stream.getB);
        class2.processDataFromInputStream(
                new ByteArrayInputStream(out.toByteArray())
        );
*/
        ByteArrayInputStream byteArrayInputStream = new  ByteArrayInputStream(((ByteArrayOutputStream)stream).toByteArray());


        //return NanoHTTPD.newFixedLengthResponse(code, "application/pdf", (InputStream) byteArrayInputStream, ((ByteArrayOutputStream) stream).size());
        return NanoHTTPD.newFixedLengthResponse(code, "application/pdf", (InputStream) byteArrayInputStream, ((ByteArrayOutputStream) stream).size());
    }
}
