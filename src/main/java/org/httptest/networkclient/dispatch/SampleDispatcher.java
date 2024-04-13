package org.httptest.networkclient.dispatch;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.httptest.networkclient.config.Env;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;

@Slf4j
public class SampleDispatcher extends HttpServlet {

    public SampleDispatcher() {
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.doPost(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            this.doPerform(request, "userId", response);
        } catch (Exception var5) {
            Exception e = var5;
            log.error("", e);
            request.getSession().invalidate();
            response.sendRedirect("/");
        }
    }

    private void doPerform(HttpServletRequest request, String userId, HttpServletResponse response) throws ServletException, IOException {
        PrintWriter out;
        response.setContentType("text/plain");
        out = response.getWriter();

        try {
            out.print("default Test");
        } catch (Throwable var32) {
            if (out != null) {
                try {
                    out.close();
                } catch (Throwable var23) {
                    var32.addSuppressed(var23);
                }
            }

            throw var32;
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }
}
