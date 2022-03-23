package com.mach.core.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class CommandRunner {

    private static final Logger LOG = LoggerFactory.getLogger(CommandRunner.class);

    private CommandRunner() {
    }

    public static List<String> executeCommand(String command) {
        List<String> response = new ArrayList<>();
        LOG.info("executeCommand: {}", command);
        try {
            final Process process = Runtime.getRuntime().exec(command.split(" "));
            BufferedReader readerOK = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader readerError = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String line;

            try {
                while ((line = readerOK.readLine()) != null) {
                    LOG.info("responseCommand: {}", line);
                    response.add(line);
                }
                if(response.size()>0){
                    return response;
                }
                while ((line = readerError.readLine()) != null) {
                    LOG.warn("responseErrorCommand: {}", line);
                    response.add(line);
                }

            } catch (IOException ex) {
                LOG.error("Error while readLine response command e:", ex);
            }
        } catch (IOException e) {
            LOG.error("Error while executing command e:", e);
        }
        return response;
    }
}
