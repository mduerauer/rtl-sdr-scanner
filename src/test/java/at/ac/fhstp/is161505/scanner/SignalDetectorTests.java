package at.ac.fhstp.is161505.scanner;

import at.ac.fhstp.is161505.scanner.input.Baseline;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

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
public class SignalDetectorTests {

    private static final Logger LOGGER = LoggerFactory.getLogger(SignalDetectorTests.class);

    private Baseline baseline;

    private SignalDetector signalDetector;

    @Before
    public void initialize() throws URISyntaxException {
        this.baseline = Baseline.fromInputStream(TestData.getBaselineTestData());
        this.signalDetector = SignalDetector.withBaseline(this.baseline);
    }

    @Test
    public void shouldFailOnInvalidFrequency() {
        try {
            signalDetector.detect(TestData.INVALID_F, TestData.ALERT_D1, TestData.ALERT_THRESHOLD);
            fail("InvalidFrequencyException not thrown.");
        } catch (InvalidFrequencyException e) { }
    }

    @Test
    public void shouldAlert() {
        TestCallBack callBack = new TestCallBack();

        final boolean[] alertCaught = {false};

        signalDetector.register(callBack);
        signalDetector.register(alert -> alertCaught[0] = true);

        try {
            signalDetector.detect(TestData.F1, TestData.ALERT_D1, TestData.ALERT_THRESHOLD);
        } catch (InvalidFrequencyException e) {
            fail(e.getMessage());
        }

        assertTrue("No alert caught!", alertCaught[0]);
    }

    @Test
    public void shouldAlertOnceOnList() {
        TestCallBack callBack = new TestCallBack();

        final List<SignalDetectorAlert> alertsCaught = new ArrayList<>();

        signalDetector.register(callBack);
        signalDetector.register(alert -> alertsCaught.add(alert));

        try {
            signalDetector.detect(TestData.getDensityPoints(), TestData.ALERT_THRESHOLD);
        } catch (InvalidFrequencyException e) {
            fail(e.getMessage());
        }

        assertTrue("Invalid number of alerts caught.", alertsCaught.size() == 1);
    }

    @Test
    public void shouldNotAlert() {
        TestCallBack callBack = new TestCallBack();

        final boolean[] alertCaught = {false};

        signalDetector.register(callBack);
        signalDetector.register(alert -> alertCaught[0] = true);

        try {
            signalDetector.detect(TestData.F1, TestData.NO_ALERT_D1, TestData.ALERT_THRESHOLD);
        } catch (InvalidFrequencyException e) {
            fail(e.getMessage());
        }

        assertFalse("Alert caught!", alertCaught[0]);
    }

    private class TestCallBack implements SignalDetectorAlertCallBack {

        @Override
        public void signalDetectorAlertCaught(SignalDetectorAlert alert) {
            LOGGER.info("Alert caught for frequency {} Hz and spectral density {} dB/Hz.", alert.getFrequency(), alert.getLevel());
        }
    }

}
