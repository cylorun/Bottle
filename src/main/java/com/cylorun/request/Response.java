package com.cylorun.request;

import com.cylorun.request.enums.ContentType;
import com.cylorun.util.ResourceUtil;

import java.io.FileNotFoundException;

public class Response {
    private int status = 200;
    private ContentType contentType = ContentType.TEXT_HTML;
    private StringBuilder body = new StringBuilder();
    private static final String TEMPLATES_DIR = "templates/";

    public Response setStatus(int status) {
        this.status = status;
        return this;
    }

    public Response setContentType(ContentType contentType) {
        this.contentType = contentType;
        return this;
    }

    public Response writeBody(String content) {
        this.body.append(content);
        return this;
    }

    /**
     * All html templates are stored in resources/templates
     *
     * @param templateName - name of the html template to render
     * @return this
     */
    public Response renderHTML(String templateName) {
        String templatePath = this.fixTemplatePath(templateName);
        String data;
        try {
            data = ResourceUtil.readFile(templatePath);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        this.setContentType(ContentType.TEXT_HTML);
        this.writeBody(data);

        return this;
    }


    public Response serveStatic(String resourcePath) {
        String data;
        try {
            data = ResourceUtil.readFile(resourcePath);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("File not found: " + resourcePath);
        }

        if (resourcePath.endsWith(".css")) {
            this.setContentType(ContentType.TEXT_CSS);
        } else if (resourcePath.endsWith(".js")) {
            this.setContentType(ContentType.APPLICATION_JAVASCRIPT);
        } else {
            this.setContentType(ContentType.TEXT_PLAIN);
        }

        this.writeBody(data);

        return this;
    }

    public String getResponse() {
        StringBuilder response = new StringBuilder();
        response.append("HTTP/1.1 ").append(this.status).append(" OK\r\n");
        response.append("Content-Type: ").append(this.contentType.toString()).append("\r\n");
        response.append("\r\n");
        response.append(this.body);
        return response.toString();
    }

    private static String fixTemplatePath(String templatePath) {
        templatePath = templatePath.trim();

        if (!templatePath.endsWith(".html")) {
            throw new UnsupportedOperationException("Unsupported file type: " + templatePath);
        }

        if (templatePath.startsWith(TEMPLATES_DIR)) {
            return templatePath;
        }

        if (templatePath.startsWith("/")) {
            return TEMPLATES_DIR + templatePath.substring(1);
        }

        return TEMPLATES_DIR + templatePath;
    }

    @Override
    public String toString() {
        return this.getResponse();
    }
}