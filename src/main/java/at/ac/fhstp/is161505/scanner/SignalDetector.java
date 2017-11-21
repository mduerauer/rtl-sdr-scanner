package at.ac.fhstp.is161505.scanner;

import at.ac.fhstp.is161505.scanner.input.Baseline;
import at.ac.fhstp.is161505.scanner.input.BaselineItem;
import at.ac.fhstp.is161505.scanner.input.SpectralDensityPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

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
public class SignalDetector {

    private static final Logger LOGGER = LoggerFactory.getLogger(SignalDetector.class);

    public static final double DEFAULT_THRESHOLD = 0.5D;

    private Baseline baseline;

    public Baseline getBaseline() {
        return baseline;
    }

    public void setBaseline(Baseline baseline) {
        this.baseline = baseline;
    }

    private final List<SignalDetectorAlertCallBack> alertCallBacks = new ArrayList<>();

    public SignalDetector() {

    }

    private SignalDetector(Baseline baseline) {
        this.baseline = baseline;
    }

    public static SignalDetector withBaseline(Baseline baseline) {
        return new SignalDetector(baseline);
    }

    public void register(SignalDetectorAlertCallBack callBack) {
        this.alertCallBacks.add(callBack);
    }

    public void detect(List<SpectralDensityPoint> points) throws InvalidFrequencyException {
        detect(points, DEFAULT_THRESHOLD);
    }

    public void detect(List<SpectralDensityPoint> points, double threshold) throws InvalidFrequencyException {
        for(SpectralDensityPoint point : points) {
            detect(point.getFrequency(), point.getLevel(), threshold);
        }
    }

    public void detect(SpectralDensityPoint point) throws InvalidFrequencyException {
        detect(point.getFrequency(), point.getLevel(), DEFAULT_THRESHOLD);
    }

    public void detect(SpectralDensityPoint point, double threshold) throws InvalidFrequencyException {
        detect(point.getFrequency(), point.getLevel(), threshold);
    }

    public void detect(double frequency, double level, double threshold) throws InvalidFrequencyException {

        BaselineItem item = baseline.get(frequency);
        boolean raiseAlert = false;

        SignalDetectorAlert alert =
                SignalDetectorAlert.forInput(frequency, level);

        // Check if the baseline contains the frequency
        if(item == null) {
            LOGGER.warn("No such frequency {} in baseline.", frequency);
            throw new InvalidFrequencyException(frequency);
        }


        //
        double mean = StatisticsUtils.mean(level, item.getMin(), item.getMax());
        double standardDeviation = StatisticsUtils.standardDeviation(mean, level, item.getMin(), item.getMax());

        LOGGER.debug("Standard Deviation is " + standardDeviation);

        /*
            If the density level standard deviation between level, min and max
            is higher than the treshold raise an alert
         */
        if(standardDeviation > threshold) {
            raiseAlert = true;
        }

        if(raiseAlert) {
            raiseAlert(alert);
        }
    }

    private void raiseAlert(SignalDetectorAlert alert) {
        LOGGER.debug("Raising alert for frequency {}", alert.getFrequency());
        for(SignalDetectorAlertCallBack callBack : alertCallBacks) {
            LOGGER.debug("Calling callback function.");
            callBack.signalDetectorAlertCaught(alert);
        }
    }

}
