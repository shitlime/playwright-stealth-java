package com.shitlime.psj;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;

import java.io.File;
import java.io.FileOutputStream;

public class StealthTest {
    public static void main(String[] args) {
        try (Playwright playwright = Playwright.create()) {
            Stealth stealth = new Stealth();
            Browser browser = playwright.chromium().launch();
            BrowserContext browserContext = browser.newContext();
            stealth.applyStealth(browserContext);
            Page page = browserContext.newPage();
            page.navigate("https://bot.sannysoft.com/");
            byte[] screenshot = page.screenshot(new Page.ScreenshotOptions().setFullPage(true));
            FileOutputStream fos = new FileOutputStream(new File("./test-output.png"));
            fos.write(screenshot);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
