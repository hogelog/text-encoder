package org.hogel;

import java.io.File;
import java.io.IOException;
import java.nio.charset.CharacterCodingException;
import java.util.Arrays;
import java.util.List;

import com.google.common.io.Files;
import com.google.inject.Inject;

import javax.swing.*;

public class SjisEncoder {
    @Inject Configuration config;
    @Inject Encoding encoding;
    @Inject Printer printer;

    public void encodeFile(File file) {
        try {
            final byte[] readData = Files.toByteArray(file);
            final byte[] writeData = encoding.encode(Encoding.SJIS, readData);
            if (Arrays.equals(readData, writeData)) {
                printer.print(String.format("%s は既にShift-JISです。", file.getPath()));
            } else {
                Files.write(writeData, file);
                printer.print(String.format("%s をShift-JISに変換しました。", file.getPath()));
            }
        } catch (final EncodingException e) {
            printer.error(String.format("%sへの変換に失敗しました:%n%s", e.getTarget().displayName(), e.getSource()), e);
        } catch (final CharacterCodingException e) {
            printer.error(String.format("未知の文字コードのファイルです:%n%s", file), e);
        } catch (final IOException e) {
            printer.error(e.getMessage(), e);
        }
    }

    public void encodeFiles(String[] args) {
        for (final String arg : args) {
            final File file = new File(arg);
            encodeFile(file);
        }
    }

    public void encodeFiles(List<File> files) {
        for (final File file : files) {
            encodeFile(file);
        }
    }

    public void encodeFilesFromCommand(String[] args, ConsolePrinter printer) {
        encodeFiles(args);
        if (config.isShowConfirmDialog()) {
            JOptionPane.showMessageDialog(null, printer.getMessages());
        }
    }

    public void loadConfig() {
        encoding.setCharacterMapping(config.getReplacePatterns());
    }
}
