package tools;

import objects.Group;
import objects.Person;
import objects.Task;
import org.tc33.jheatchart.HeatChart;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static tools.MetricsCalculator.getPeople;

public class EnvyGrapher {
    // this class will take in a set of preferences and graph the pairwise envy of each agent

    public static void graph(Group group, String algorithmName) {
        String fileName = group.getGroupName() + algorithmName + ".jpg";
        File fileDest = new File(fileName);
        Map<Person, List<Task>> allocations = group.getPersonTasksMap();
        Person[] people = getPeople(allocations);
        double[][] zValues = MetricsCalculator.calculateEnvyGraph(group);
        removeNegatives(zValues);
        HeatChart hc = new HeatChart(zValues, 0, 20);
        hc.setXValues(people);
        hc.setYValues(people);
        hc.setChartMargin(50);
        hc.setBackgroundColour(new Color(255,255,255,255));
        hc.setHighValueColour(Color.RED);
        hc.setLowValueColour(Color.lightGray);
        hc.setTitle(algorithmName); // probably make more verbose soon
        try {
            hc.saveToFile(fileDest);
        } catch (IOException ex) {
            System.out.println(fileName + " could not be created.");
            ex.printStackTrace();
        }

    }

    public static void removeNegatives(double[][] vals) {
        for (int i = 0; i< vals.length; i++)
            for (int j = 0; j < vals.length; j++)
                vals[i][j] = Math.max(0.0, vals[i][j]);

    }
}
