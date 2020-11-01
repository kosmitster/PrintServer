package ru.altm;

import fi.iki.elonen.NanoHTTPD;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.util.JRLoader;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
// NOTE: If you're using NanoHTTPD >= 3.0.0 the namespace is different,
//       instead of the above import use the following:
// import org.nanohttpd.NanoHTTPD;

public class App extends NanoHTTPD {

    static String pathToReportDirectory;
    static int port;
    static String jdbcUrl;
    static String username;
    static String password;

    public App() throws IOException {
        super(App.port);
        start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
        System.out.println("\nPrint Service работает! порт " + App.port + "\n");
    }

    public static void main(String[] args) {
        try {
            App.pathToReportDirectory = "c:\\Common\\Reporter\\reports";
            App.port = Integer.parseInt(args[0]);
            App.jdbcUrl = args[1];
            App.username = args[2];
            App.password = args[3];

            new App();
        } catch (IOException ioe) {
            System.err.println("Couldn't start server:\n" + ioe);
        }
    }


    @Override
    public Response serve(IHTTPSession session) {

        //Получаю значение параметров
        final HashMap<String, Object> hm = new HashMap<String, Object>();
        final Map<String, String> params = session.getParms();
        for (Map.Entry<String, String> item : params.entrySet()) hm.put(item.getKey(), item.getValue());

        //Получаю подключение к базе данных
        final Connection connection;
        try {
            connection = DriverManager.getConnection(App.jdbcUrl, App.username, App.password);
        } catch (SQLException e) {
            StringWriter errors = new StringWriter();
            e.printStackTrace(new PrintWriter(errors));
            return newFixedLengthResponse(
                    Response.Status.BAD_REQUEST, MIME_PLAINTEXT, errors.toString());
        }

        final boolean isXls = params.containsKey("xls");

        if (connection != null) {

            /*Получаю данные отчёта*/
            try {

                final JasperReport jasperReport = (JasperReport) JRLoader.loadObject(new File(App.pathToReportDirectory + session.getUri()));

                final JasperPrint jasPrint;
                jasPrint = JasperFillManager.fillReport(jasperReport, hm, connection);

                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                if(isXls)
                {
                    JRXlsExporter exporter = new JRXlsExporter();
                    exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasPrint);
                    exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, outputStream);

                    exporter.exportReport();

                    return HttpTool.getXlsResponse(Response.Status.OK, outputStream);
                }else {

                    JasperExportManager.exportReportToPdfStream(jasPrint, outputStream);
                    return HttpTool.getPdfResponse(Response.Status.OK, outputStream);
                }

            } catch (JRException e) {

                StringWriter errors = new StringWriter();
                e.printStackTrace(new PrintWriter(errors));

                return newFixedLengthResponse(
                        Response.Status.BAD_REQUEST, MIME_PLAINTEXT, errors.toString());
            }


            //Response response = newFixedLengthResponse(Response.Status.OK, "application/pdf", );
            //response.addHeader("Access-Control-Allow-Origin", "*");

        } else {
            return newFixedLengthResponse(
                    Response.Status.BAD_REQUEST, MIME_PLAINTEXT, "Подключение к базе данных отсутствует!");
        }
    }
}
