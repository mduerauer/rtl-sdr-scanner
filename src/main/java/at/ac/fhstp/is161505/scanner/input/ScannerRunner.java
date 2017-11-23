package at.ac.fhstp.is161505.scanner.input;

import at.ac.fhstp.is161505.scanner.config.MessagingConfig;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.InputStream;

/**
 * This file is part of rtl-sdr-scanner.
 * <p>
 * rtl-sdr-scanner is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * rtl-sdr-scanner is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 * <p>
 * Diese Datei ist Teil von rtl-sdr-scanner.
 * <p>
 * rtl-sdr-scanner ist Freie Software: Sie können es unter den Bedingungen
 * der GNU General Public License, wie von der Free Software Foundation,
 * Version 3 der Lizenz oder (nach Ihrer Wahl) jeder späteren
 * veröffentlichten Version, weiterverbreiten und/oder modifizieren.
 * <p>
 * rtl-sdr-scanner wird in der Hoffnung, dass es nützlich sein wird, aber
 * OHNE JEDE GEWÄHRLEISTUNG, bereitgestellt; sogar ohne die implizite
 * Gewährleistung der MARKTFÄHIGKEIT oder EIGNUNG FÜR EINEN BESTIMMTEN ZWECK.
 * Siehe die GNU General Public License für weitere Details.
 * <p>
 * Sie sollten eine Kopie der GNU General Public License zusammen mit diesem
 * Programm erhalten haben. Wenn nicht, siehe <http://www.gnu.org/licenses/>.
 * <p>
 * Created by n17405180 on 21.11.17.
 */
@Component
public class ScannerRunner implements CommandLineRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScannerRunner.class);

    @Autowired
    private JmsTemplate jmsTemplate;

    public void run(String... args) {

        for(int i = 0; i < args.length; i++) {
            LOGGER.debug("ARG {}: {}", i, args[i]);
        }

        LOGGER.debug("ScannerRunner.run() called.");

        if(args.length < 1) {
            throw new RuntimeException("Please provide a frequency scanner executable");
        }

        try {
            ProcessBuilder processBuilder = new ProcessBuilder();
            processBuilder.directory(new File(System.getProperty("user.home")));
            processBuilder.command(args[0]);
            Process process = processBuilder.start();

            byte[] buffer = new byte[4000];
            InputStream out = process.getInputStream();
            while (isAlive(process)) {

                int no = out.available();
                if (no > 0) {
                    int n = out.read(buffer, 0, Math.min(no, buffer.length));
                    String input = new String(buffer, 0, n).trim();
                    jmsTemplate.convertAndSend(MessagingConfig.SCANNER_QUEUE, input);
                }

            }
        } catch(Exception e) {
            throw new RuntimeException(e);
        }

    }

    public static boolean isAlive(Process p) {
        try {
            p.exitValue();
            return false;
        }
        catch (IllegalThreadStateException e) {
            return true;
        }
    }
}
