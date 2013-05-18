package org.hogel;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.DumperOptions.FlowStyle;
import org.yaml.snakeyaml.Yaml;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.Files;

public class Configuration {
    private static final Logger LOG = LoggerFactory.getLogger(Configuration.class);

    private static final String SHOW_CONFIRM_DIALOG = "show_confirm_dialog";

    private static final String REPLACE_PATTERNS = "replace_patterns";

    private static final DumperOptions DUMPER_OPTIONS = new DumperOptions();

    static {
        DUMPER_OPTIONS.setDefaultFlowStyle(FlowStyle.BLOCK);
    }

    private final Yaml yaml = new Yaml(DUMPER_OPTIONS);

    private final File configFile;

    private Map<String, Object> configMap;

    private boolean showConfirmDialog;

    private Map<String, String> replacePatterns;

    public Configuration(String configPath) {
        this(new File(configPath));
    }

    public Configuration(File configFile) {
        this.configFile = configFile;
        if (!configFile.exists()) {
            initConfigFile();
        } else {
            loadConfigFile();
        }
    }

    @SuppressWarnings("unchecked")
    public void loadConfigFile() {
        try {
            final BufferedReader reader = Files.newReader(configFile, Charsets.UTF_8);
            configMap = (Map<String, Object>) yaml.load(reader);
            if (configMap == null) {
                configMap = new LinkedHashMap<String, Object>();
            }
            showConfirmDialog = getBoolean(SHOW_CONFIRM_DIALOG);
            replacePatterns = getMap(REPLACE_PATTERNS);
        } catch (final FileNotFoundException e) {
            LOG.error(e.getMessage(), e);
            if (configFile.delete()) {
                initConfigFile();
            }
        }
    }

    @SuppressWarnings("unchecked")
    private <T> Map<String, T> getMap(String key) {
        Map<String, T> value = (Map<String, T>) configMap.get(key);
        if (value == null)
            return new LinkedHashMap<String, T>();
        return value;
    }

    @SuppressWarnings("unchecked")
    private boolean getBoolean(String key) {
        Boolean value = (Boolean) configMap.get(key);
        if (value == null)
            return false;
        return value;
    }

    private void initConfigFile() {
        try {
            configFile.createNewFile();
            configMap = new LinkedHashMap<String, Object>();
            showConfirmDialog = false;
            replacePatterns = new LinkedHashMap<String, String>();
        } catch (final IOException e) {
            LOG.error(e.getMessage(), e);
        }
        configMap.put(REPLACE_PATTERNS, replacePatterns);
        saveConfigFile();
    }

    public void saveConfigFile() {
        try {
            final BufferedWriter writer = Files.newWriter(configFile, Charsets.UTF_8);
            yaml.dump(configMap, writer);
        } catch (final IOException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    public File getConfigFile() {
        return configFile;
    }

    public void addReplacePattern(String search, String replace) {
        replacePatterns.put(search, replace);
        saveConfigFile();
    }

    public String getReplacePattern(String search) {
        return replacePatterns.get(search);
    }

    public void setReplacePatterns(Map<String, String> patterns) {
        replacePatterns.clear();
        replacePatterns.putAll(patterns);
        saveConfigFile();
    }

    public Map<String, String> getReplacePatterns() {
        return ImmutableMap.copyOf(replacePatterns);
    }

    public boolean isShowConfirmDialog() {
        return showConfirmDialog;
    }
}
