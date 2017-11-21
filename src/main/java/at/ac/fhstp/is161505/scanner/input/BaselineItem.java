package at.ac.fhstp.is161505.scanner.input;

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
 * Created by n17405180 on 18.11.17.
 */
public class BaselineItem {

    private double frequency;

    private double min = Double.MAX_VALUE;

    private double max = Double.MAX_VALUE * -1;

    private double mean;

    private double standardDeviation;

    private final List<Double> levels = new ArrayList<>();

    public double getFrequency() {
        return frequency;
    }

    public double getMin() {
        return min;
    }

    public double getMax() {
        return max;
    }

    public double getMean() {
        return mean;
    }

    public double getStandardDeviation() {
        return standardDeviation;
    }

    public int getCount() {
        return levels.size();
    }

    public BaselineItem(double frequency, double level) {
        this.frequency = frequency;
        this.levels.add(level);
    }

    public void appendValue(double value) {
        this.levels.add(value);
    }

    public void recalculate() {
        calcMinMaxMean(levels);
        standardDeviation = getStandardDeviation(levels, mean);
    }

    private void calcMinMaxMean(List<Double> table) {
        double total = 0;
        for (int i = 0; i < table.size(); i++) {

            double currentNum = table.get(i);
            total += currentNum;
            if(Double.compare(this.min,currentNum) > 0) {
                this.min = currentNum;
            }

            if(Double.compare(this.max,currentNum) < 0) {
                this.max = currentNum;
            }
        }
        this.mean = total / table.size();
    }

    private static double getStandardDeviation(List<Double> table, double mean) {
        double temp = 0;
        for (int i = 0; i < table.size(); i++)
        {
            temp += Math.pow(table.get(i) - mean, 2);
        }
        return Math.sqrt(temp / (double) (table.size()));

    }

    @Override
    public String toString() {
        return "BaselineItem{" +
                "frequency=" + frequency +
                ", min=" + min +
                ", max=" + max +
                ", mean=" + mean +
                ", standardDeviation=" + standardDeviation +
                '}';
    }
}

