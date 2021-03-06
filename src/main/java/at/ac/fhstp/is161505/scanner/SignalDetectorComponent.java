package at.ac.fhstp.is161505.scanner;

import at.ac.fhstp.is161505.scanner.config.WebSocketConfig;
import at.ac.fhstp.is161505.scanner.input.Baseline;
import at.ac.fhstp.is161505.scanner.input.BaselineItem;
import at.ac.fhstp.is161505.scanner.input.SpectralDensityPoint;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Collection;

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
public class SignalDetectorComponent {

    private static final Logger LOGGER = LoggerFactory.getLogger(SignalDetectorComponent.class);

    private static final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private SimpMessageSendingOperations msgTemplate;

    @Value("${scanner.baseline.resourceName}")
    private String baselineResource;

    private SignalDetector signalDetector;

    public SignalDetectorComponent() {

    }

    @PostConstruct
    public void init() throws URISyntaxException {

        LOGGER.debug("Resource Name: {}", baselineResource);

        InputStream baselineStream = SignalDetectorComponent.class.getResourceAsStream(baselineResource);
        Baseline baseline = Baseline.fromInputStream(baselineStream);
        signalDetector = SignalDetector.withBaseline(baseline);

        signalDetector.register(alert -> {
            LOGGER.info("Alert caught: f={} Hz, l={} dB/Hz", alert.getFrequency(), alert.getLevel());
        });

        signalDetector.register(alert -> {
            try {
                msgTemplate.convertAndSend(WebSocketConfig.ALERT_DESTINATION, mapper.writeValueAsString(alert));
            } catch (JsonProcessingException e) {
                LOGGER.warn("Can't convert alert to json.");
            }
        });
    }

    public void detect(SpectralDensityPoint point) throws InvalidFrequencyException {
        signalDetector.detect(point);
    }

    public Collection<BaselineItem> getBaseline() {
        return signalDetector.getBaseline().getBaselineItems();
    }
}
