package at.ac.fhstp.is161505.scanner.input;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

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
 * Created by n17405180 on 18.11.17.
 */
public class Baseline {

    private static final Logger LOGGER = LoggerFactory.getLogger(Baseline.class);

    private final Map<Double, BaselineItem> data = new HashMap<>();

    private Baseline() {
    }

    public static Baseline fromInputStream(InputStream input) {
        Baseline baseline = new Baseline();
        baseline.read(input);
        baseline.recalculate();
        return baseline;
    }

    public BaselineItem get(double frequency) {
        return data.get(frequency);
    }

    private void read(InputStream input) {

        try (BufferedReader br = new BufferedReader(new InputStreamReader(input))) {
            String line;
            while ((line = br.readLine()) != null) {

                if(!line.matches("^[0-9].+")) {
                    continue;
                }

                Double frequency;
                Double level;

                String[] parts = line.split("\\s+");

                frequency = Double.parseDouble(parts[0]);
                level = Double.parseDouble(parts[1]);

                if(!data.containsKey(frequency)) {
                    data.put(frequency, new BaselineItem(frequency, level));
                } else {
                    data.get(frequency).appendValue(level);
                }

                LOGGER.trace("Parsing line {}", line);
            }
        } catch(IOException e) {
            LOGGER.error("Can't parse file due to execption: " + e.getMessage(), e);
        }

    }

    private void recalculate() {
        for(BaselineItem item : data.values()) {
            item.recalculate();
        }
    }

    public void dump() {
        for(BaselineItem item : data.values()) {
            System.out.println(item.toString());
        }
    }

    public Collection<BaselineItem> getBaselineItems() {
        return data.values();
    }

}
