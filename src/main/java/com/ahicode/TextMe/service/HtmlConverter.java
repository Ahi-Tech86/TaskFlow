package com.ahicode.TextMe.service;

import java.io.IOException;

public interface HtmlConverter {
    byte[] convertHtmlToPdf(String html) throws IOException;
}
