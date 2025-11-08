package com.playwright.utils;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import com.microsoft.playwright.Page;

public class ScreenshotUtil {
    public static String takeScreenshot(Page page, String prefix) {
        try {
            Path dir = Paths.get("target", "screenshots");
            String fileName = prefix + "-" + UUID.randomUUID().toString() + ".png";
            Path dest = dir.resolve(fileName);
            page.screenshot(new Page.ScreenshotOptions().setPath(dest));
            return dest.toString();
        } catch (Exception e) {
            return null;
        }
    }
}
