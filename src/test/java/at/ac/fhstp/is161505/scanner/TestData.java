package at.ac.fhstp.is161505.scanner;

import at.ac.fhstp.is161505.scanner.input.SpectralDensityPoint;

import java.io.File;
import java.net.URISyntaxException;
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
public final class TestData {

    public static final String BASELINE_RESOURCE = "/testdata.out";

    // BaselineItem{frequency=4750000.0, min=-65.9766, max=-65.7945, mean=-65.89078888888889, standardDeviation=0.06396525870336069}

    public static final double INVALID_F = 0D;

    public static final double F1 = 4750000.0D;

    public static final double ALERT_D1 = -60D;

    public static final double NO_ALERT_D1 = -65.7D;

    public static final double ALERT_THRESHOLD = 0.5;

    public static File getBaselineTestData() {
        try {
            return new File(BaselineTests.class.getResource(TestData.BASELINE_RESOURCE).toURI());
        } catch (URISyntaxException e) {
            return null;
        }
    }

    public static List<SpectralDensityPoint> getDensityPoints() {
        List<SpectralDensityPoint> list = new ArrayList<>();
        list.add(SpectralDensityPoint.forInput(F1, ALERT_D1));
        list.add(SpectralDensityPoint.forInput(F1, NO_ALERT_D1));
        return list;
    }

}
