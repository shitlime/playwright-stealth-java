package com.shitlime.psj;

import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;


/**
 * Playwright stealth configuration that applies stealth strategies to Playwright.
 * The stealth strategies are contained in ./js package and are basic javascript scripts that are executed
 * on every page.goto() called.
 * <p>
 * Note:
 * All init scripts are combined by playwright into one script and then executed this means
 * the scripts should not have conflicting constants/variables etc. !
 */
public class Stealth {
    private static final Map<String, String> SCRIPTS = new HashMap<>();
    
    // Evasion options
    private boolean chromeApp = true;
    private boolean chromeCsi = true;
    private boolean chromeLoadTimes = true;
    private boolean chromeRuntime = false;
    private boolean hairline = true;
    private boolean iframeContentWindow = true;
    private boolean mediaCodecs = true;
    private boolean navigatorHardwareConcurrency = true;
    private boolean navigatorLanguages = true;
    private boolean navigatorPermissions = true;
    private boolean navigatorPlatform = true;
    private boolean navigatorPlugins = true;
    private boolean navigatorUserAgent = true;
    private boolean navigatorVendor = true;
    private boolean navigatorWebdriver = true;
    private boolean errorPrototype = true;
    private boolean secChUa = true;
    private boolean webglVendor = true;
    
    // Override options
    private List<String> navigatorLanguagesOverride = Arrays.asList("en-US", "en");
    private String navigatorPlatformOverride = "Win32";
    private String navigatorUserAgentOverride = null;
    private String navigatorVendorOverride = null;
    private String secChUaOverride = null;
    private String webglRendererOverride = "Intel Iris OpenGL Engine";
    private String webglVendorOverride = "Intel Inc.";
    
    // Other options
    private boolean initScriptsOnly = false;
    private boolean scriptLogging = false;
    
    static {
        loadScripts();
    }
    
    private static void loadScripts() {
        try {
            SCRIPTS.put("generate_magic_arrays", readFile("js/generate.magic.arrays.js"));
            SCRIPTS.put("utils", readFile("js/utils.js"));
            SCRIPTS.put("chrome_app", readFile("js/evasions/chrome.app.js"));
            SCRIPTS.put("chrome_csi", readFile("js/evasions/chrome.csi.js"));
            SCRIPTS.put("chrome_hairline", readFile("js/evasions/chrome.hairline.js"));
            SCRIPTS.put("chrome_load_times", readFile("js/evasions/chrome.load.times.js"));
            SCRIPTS.put("chrome_runtime", readFile("js/evasions/chrome.runtime.js"));
            SCRIPTS.put("iframe_content_window", readFile("js/evasions/iframe.contentWindow.js"));
            SCRIPTS.put("media_codecs", readFile("js/evasions/media.codecs.js"));
            SCRIPTS.put("navigator_hardware_concurrency", readFile("js/evasions/navigator.hardwareConcurrency.js"));
            SCRIPTS.put("navigator_languages", readFile("js/evasions/navigator.languages.js"));
            SCRIPTS.put("navigator_permissions", readFile("js/evasions/navigator.permissions.js"));
            SCRIPTS.put("navigator_platform", readFile("js/evasions/navigator.platform.js"));
            SCRIPTS.put("navigator_plugins", readFile("js/evasions/navigator.plugins.js"));
            SCRIPTS.put("navigator_user_agent", readFile("js/evasions/navigator.userAgent.js"));
            SCRIPTS.put("navigator_vendor", readFile("js/evasions/navigator.vendor.js"));
            SCRIPTS.put("navigator_webdriver", readFile("js/evasions/navigator.webdriver.js"));
            SCRIPTS.put("error_prototype", readFile("js/evasions/error.prototype.js"));
            SCRIPTS.put("webgl_vendor", readFile("js/evasions/webgl.vendor.js"));
        } catch (IOException e) {
            throw new RuntimeException("Failed to load scripts", e);
        }
    }
    
    private static String readFile(String relativePath) throws IOException {
        try (InputStream inputStream = Stealth.class.getClassLoader().getResourceAsStream(relativePath)) {
            if (inputStream == null) {
                throw new IOException("Resource file not found in classpath:" + relativePath);
            }
            byte[] bytes = inputStream.readAllBytes();
            return new String(bytes, StandardCharsets.UTF_8);
        }
    }

    /**
     * Constructs a new {@code Stealth} instance with default evasion options enabled.
     */
    public Stealth() {
        // Default constructor with all options enabled
    }

    /**
     * Constructs a new {@code Stealth} instance with a custom configuration.
     * This allows enabling or disabling specific evasions and setting override values.
     *
     * @param config The {@link StealthConfig} object containing the desired settings.
     */
    public Stealth(StealthConfig config) {
        // Apply configuration
        if (config.chromeApp != null) this.chromeApp = config.chromeApp;
        if (config.chromeCsi != null) this.chromeCsi = config.chromeCsi;
        if (config.chromeLoadTimes != null) this.chromeLoadTimes = config.chromeLoadTimes;
        if (config.chromeRuntime != null) this.chromeRuntime = config.chromeRuntime;
        if (config.hairline != null) this.hairline = config.hairline;
        if (config.iframeContentWindow != null) this.iframeContentWindow = config.iframeContentWindow;
        if (config.mediaCodecs != null) this.mediaCodecs = config.mediaCodecs;
        if (config.navigatorHardwareConcurrency != null) this.navigatorHardwareConcurrency = config.navigatorHardwareConcurrency;
        if (config.navigatorLanguages != null) this.navigatorLanguages = config.navigatorLanguages;
        if (config.navigatorPermissions != null) this.navigatorPermissions = config.navigatorPermissions;
        if (config.navigatorPlatform != null) this.navigatorPlatform = config.navigatorPlatform;
        if (config.navigatorPlugins != null) this.navigatorPlugins = config.navigatorPlugins;
        if (config.navigatorUserAgent != null) this.navigatorUserAgent = config.navigatorUserAgent;
        if (config.navigatorVendor != null) this.navigatorVendor = config.navigatorVendor;
        if (config.navigatorWebdriver != null) this.navigatorWebdriver = config.navigatorWebdriver;
        if (config.errorPrototype != null) this.errorPrototype = config.errorPrototype;
        if (config.secChUa != null) this.secChUa = config.secChUa;
        if (config.webglVendor != null) this.webglVendor = config.webglVendor;

        if (config.navigatorLanguagesOverride != null) this.navigatorLanguagesOverride = config.navigatorLanguagesOverride;
        if (config.navigatorPlatformOverride != null) this.navigatorPlatformOverride = config.navigatorPlatformOverride;
        if (config.navigatorUserAgentOverride != null) this.navigatorUserAgentOverride = config.navigatorUserAgentOverride;
        if (config.navigatorVendorOverride != null) this.navigatorVendorOverride = config.navigatorVendorOverride;
        if (config.secChUaOverride != null) this.secChUaOverride = config.secChUaOverride;
        if (config.webglRendererOverride != null) this.webglRendererOverride = config.webglRendererOverride;
        if (config.webglVendorOverride != null) this.webglVendorOverride = config.webglVendorOverride;

        if (config.initScriptsOnly != null) this.initScriptsOnly = config.initScriptsOnly;
        if (config.scriptLogging != null) this.scriptLogging = config.scriptLogging;
    }

    /**
     * Generates the complete JavaScript payload by combining all enabled evasion scripts.
     * The payload is wrapped in an IIFE (Immediately Invoked Function Expression) to avoid polluting the global scope.
     *
     * @return A string containing the full JavaScript payload, or an empty string if no scripts are enabled.
     */
    public String getScriptPayload() {
        String scriptsBlock = String.join("\n", getEnabledScripts());
        if (scriptsBlock.isEmpty()) {
            return "";
        }
        return "(() => {\n" + scriptsBlock + "\n})();";
    }

    /**
     * Generates the JavaScript options block based on the current override settings.
     * This block defines a global {@code opts} constant that the evasion scripts can use.
     *
     * @return A string containing the JavaScript options definition.
     */
    public String getOptionsPayload() {
        Map<String, Object> opts = new HashMap<>();
        opts.put("navigator_hardware_concurrency", navigatorHardwareConcurrency);
        opts.put("navigator_languages_override", navigatorLanguagesOverride);
        opts.put("navigator_platform", navigatorPlatformOverride);
        opts.put("navigator_user_agent", navigatorUserAgentOverride);
        opts.put("navigator_vendor", navigatorVendorOverride);
        opts.put("webgl_renderer", webglRendererOverride);
        opts.put("webgl_vendor", webglVendorOverride);
        opts.put("script_logging", scriptLogging);
        
        return "const opts = " + Utils.toJSONString(opts) + ";";
    }
    
    private List<String> getEnabledScripts() {
        List<String> scripts = new ArrayList<>();
        String evasionScriptBlock = String.join("\n", getEvasionScripts());
        
        if (!evasionScriptBlock.isEmpty()) {
            scripts.add(getOptionsPayload());
            scripts.add(SCRIPTS.get("utils"));
            scripts.add(SCRIPTS.get("generate_magic_arrays"));
            scripts.add(evasionScriptBlock);
        }
        
        return scripts;
    }
    
    private List<String> getEvasionScripts() {
        List<String> scripts = new ArrayList<>();
        
        if (chromeApp) scripts.add(SCRIPTS.get("chrome_app"));
        if (chromeCsi) scripts.add(SCRIPTS.get("chrome_csi"));
        if (hairline) scripts.add(SCRIPTS.get("chrome_hairline"));
        if (chromeLoadTimes) scripts.add(SCRIPTS.get("chrome_load_times"));
        if (chromeRuntime) scripts.add(SCRIPTS.get("chrome_runtime"));
        if (iframeContentWindow) scripts.add(SCRIPTS.get("iframe_content_window"));
        if (mediaCodecs) scripts.add(SCRIPTS.get("media_codecs"));
        if (navigatorLanguages) scripts.add(SCRIPTS.get("navigator_languages"));
        if (navigatorPermissions) scripts.add(SCRIPTS.get("navigator_permissions"));
        if (navigatorPlatform) scripts.add(SCRIPTS.get("navigator_platform"));
        if (navigatorPlugins) scripts.add(SCRIPTS.get("navigator_plugins"));
        if (navigatorUserAgent) scripts.add(SCRIPTS.get("navigator_user_agent"));
        if (navigatorVendor) scripts.add(SCRIPTS.get("navigator_vendor"));
        if (navigatorWebdriver) scripts.add(SCRIPTS.get("navigator_webdriver"));
        if (errorPrototype) scripts.add(SCRIPTS.get("error_prototype"));
        if (webglVendor) scripts.add(SCRIPTS.get("webgl_vendor"));
        
        return scripts;
    }

    /**
     * Applies the configured stealth evasions to a specific Playwright {@link Page}.
     * This method injects a script that runs at the beginning of every document creation in the page.
     * It should be called before the first navigation.
     *
     * @param page The Playwright {@link Page} to apply stealth to.
     */
    public void applyStealth(Page page) {
        String payload = getScriptPayload();
        if (!payload.isEmpty()) {
            page.addInitScript(payload);
        }
    }

    /**
     * Applies the configured stealth evasions to a Playwright {@link BrowserContext}.
     * This ensures that all new pages created within this context will have the stealth scripts applied.
     *
     * @param context The Playwright {@link BrowserContext} to apply stealth to.
     */
    public void applyStealth(BrowserContext context) {
        String payload = getScriptPayload();
        if (!payload.isEmpty()) {
            context.addInitScript(payload);
        }
    }

    /**
     * Configuration class for {@link Stealth}.
     * Use this class to customize which evasions are enabled and to provide specific override values.
     * Any properties left as {@code null} will use the default settings in {@link Stealth}.
     */
    public static class StealthConfig {
        /** Enables/disables the 'chrome.app' evasion. Defaults to true. */
        public Boolean chromeApp;
        /** Enables/disables the 'chrome.csi' evasion. Defaults to true. */
        public Boolean chromeCsi;
        /** Enables/disables the 'chrome.loadTimes' evasion. Defaults to true. */
        public Boolean chromeLoadTimes;
        /** Enables/disables the 'chrome.runtime' evasion. Defaults to false. */
        public Boolean chromeRuntime;
        /** Enables/disables the 'hairline' evasion. Defaults to true. */
        public Boolean hairline;
        /** Enables/disables the 'iframe.contentWindow' evasion. Defaults to true. */
        public Boolean iframeContentWindow;
        /** Enables/disables the 'media.codecs' evasion. Defaults to true. */
        public Boolean mediaCodecs;
        /** Enables/disables the 'navigator.hardwareConcurrency' evasion. Defaults to true. */
        public Boolean navigatorHardwareConcurrency;
        /** Enables/disables the 'navigator.languages' evasion. Defaults to true. */
        public Boolean navigatorLanguages;
        /** Enables/disables the 'navigator.permissions' evasion. Defaults to true. */
        public Boolean navigatorPermissions;
        /** Enables/disables the 'navigator.platform' evasion. Defaults to true. */
        public Boolean navigatorPlatform;
        /** Enables/disables the 'navigator.plugins' evasion. Defaults to true. */
        public Boolean navigatorPlugins;
        /** Enables/disables the 'navigator.userAgent' evasion. Defaults to true. */
        public Boolean navigatorUserAgent;
        /** Enables/disables the 'navigator.vendor' evasion. Defaults to true. */
        public Boolean navigatorVendor;
        /** Enables/disables the 'navigator.webdriver' evasion. Defaults to true. */
        public Boolean navigatorWebdriver;
        /** Enables/disables the 'error.prototype' evasion. Defaults to true. */
        public Boolean errorPrototype;
        /** Enables/disables the 'sec-ch-ua' evasion. Defaults to true. */
        public Boolean secChUa;
        /** Enables/disables the 'webgl.vendor' evasion. Defaults to true. */
        public Boolean webglVendor;

        /** Overrides the 'navigator.languages' property. Defaults to ["en-US", "en"]. */
        public List<String> navigatorLanguagesOverride;
        /** Overrides the 'navigator.platform' property. Defaults to "Win32". */
        public String navigatorPlatformOverride;
        /** Overrides the 'navigator.userAgent' property. Defaults to null. */
        public String navigatorUserAgentOverride;
        /** Overrides the 'navigator.vendor' property. Defaults to null. */
        public String navigatorVendorOverride;
        /** Overrides the 'sec-ch-ua' header. Defaults to null. */
        public String secChUaOverride;
        /** Overrides the 'webgl.renderer' property. Defaults to "Intel Iris OpenGL Engine". */
        public String webglRendererOverride;
        /** Overrides the 'webgl.vendor' property. Defaults to "Intel Inc.". */
        public String webglVendorOverride;

        /** If true, only initialization scripts are returned. Defaults to false. */
        public Boolean initScriptsOnly;
        /** Enables/disables logging within the injected scripts. Defaults to false. */
        public Boolean scriptLogging;

        /**
         * Creates a new, empty {@code StealthConfig} instance.
         */
        public StealthConfig() {}
    }
}