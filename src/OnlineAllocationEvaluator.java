import algorithms.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.helper.HelpScreenException;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
import objects.Group;
import objects.Task;
import tools.EnvyGrapher;
import tools.FormParser;

import java.io.*;
import java.lang.reflect.Type;

import com.google.gson.reflect.TypeToken;
import tools.MetricsCalculator;

import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OnlineAllocationEvaluator {

    /*
     * This method instantiates all of the algorithims which will be assesed in each run.
     */
    private static AllocationAlgorithm[] getAlgorithms(int timeSteps, int numPeople) {
        return new AllocationAlgorithm[]{new LPAllocationAlgorithm(new EgalitarianEquivalentLPSolver()), new GreedySingleAllocation(), new OnlineSingleAllocation(timeSteps, numPeople),
                new LPAllocationAlgorithm(new EnvyFreeLPSolver()), new RandomAllocation()};
    }

    /*
     * This is going to be the main class. It is going to be a command line program
     * where you can enter in the current allocations and preferences and new tasks or
     * start from scratch with just the preferences CSV.
     */
    public static void main(String[] args) throws ArgumentParserException, IOException, ParseException {
        try {
            Namespace arguments = getArgs(args);
           // String cwd = System.getProperty("user.dir") + "\\";
            String preferenceFilename = arguments.getString("pref_filename");
            String taskFilename = arguments.getString("task_filename");
            boolean jsonInput = arguments.getString("pref_input").equals("json");
            boolean allTasksAtOnce = arguments.getBoolean("batched");
            String groupName = arguments.getString("name");

            Map<String, AllocationAlgorithm> algNameMap;
            Map<String, Group> groupMap;
            Gson gson = new GsonBuilder()
                    .disableHtmlEscaping()
                    .setPrettyPrinting()
                    .serializeNulls()
                    .enableComplexMapKeySerialization()
                    .create();

            if (jsonInput) {
                Type type = new TypeToken<Map<String, Group>>(){}.getType();
                groupMap = gson.fromJson(new FileReader(preferenceFilename), type);
            } else {
                // We are creating a new set of preferences.
                Group newGroup = new Group(preferenceFilename, groupName);
                groupMap = generateGroupMap(newGroup, gson);
            }


            int numPeople = groupMap.entrySet().iterator().next().getValue().getPersonTasksMap().size(); //oh god
            AllocationAlgorithm[] algorithms = getAlgorithms(200, numPeople);
            /*
             * Run each algorithm, batched if the flag is set otherwise all incoming tasks at once.
             */
            if (allTasksAtOnce) {
                evalAlgorithms(groupMap, algorithms, FormParser.getAllTasksFromCSV(taskFilename));
            } else {
                for (List<Task> tasks : FormParser.getBatchedTasksfromCSV(taskFilename)) {
                    evalAlgorithms(groupMap, algorithms, tasks);
                }
            }

            /*
             * Graph each algorithm's pairwise envy.
             */
            FileOutputStream manifest = new FileOutputStream(new File(groupName + "manifest.txt"));
            int max = 0;
            for (Group g : groupMap.values()) {
                max = Math.max(max, MetricsCalculator.getMaxEnvy(g).intValue());
            }
            for (Map.Entry<String, Group> entry : groupMap.entrySet()) {
                EnvyGrapher.graph(entry.getValue(), entry.getKey(), max);
                String disutility = "Total disutility for algorithm " + entry.getKey() + " is: " + MetricsCalculator.calculateTotalDisutility(entry.getValue())+ "\n";
                String avgEnvy = "Average pairwise envy for algorithm " + entry.getKey() + " is: " + MetricsCalculator.calculateTotalEnvy(entry.getValue())/entry.getValue().getPersonTasksMap().size()+ "\n";
                String pwEnvy = "Maximum pairwise envy for algorithm " + entry.getKey() + " is: " + MetricsCalculator.getMaxEnvy(entry.getValue())+ "\n\n";

                manifest.write(disutility.getBytes());
                manifest.write(avgEnvy.getBytes());
                manifest.write(pwEnvy.getBytes());
            }
            manifest.close();

            // save in json
            String json = gson.toJson(groupMap);
            FileOutputStream fos = new FileOutputStream(new File(groupName + ".json"));
            fos.write(json.getBytes());
            fos.close();
        } catch (HelpScreenException helpex) {
            // catch this dumb help screen exception and exit gracefully.
        }
    }

    private static Namespace getArgs(String[] args) throws ArgumentParserException {
        ArgumentParser parser = ArgumentParsers.newFor("OnlineAllocationEvaluator").build()
                .description("Multi-algorithm allocation method for comparing online allocation algorithms");
        parser.addArgument("--pref_input").choices("csv", "json").setDefault("json");
        parser.addArgument("--name").setDefault("TestGroup");
        parser.addArgument("--batched").setDefault(false).type(Boolean.class).help("Run the algorithm on each line (date) of tasks once at a time.");
        parser.addArgument("pref_filename").help("Filename for reading the user preferences.");
        parser.addArgument("task_filename").help("CSV Filename for adding new tasks. Format of the CSV is: \nDate,<task_type1>,<task_type2>,...\ndd/MM/yyyy,<count_1>,<count_2>,...");
        return parser.parseArgs(args);
    }

    private static Map<String, Group> generateGroupMap(Group group, Gson gson) {
        Type type = new TypeToken<Group>(){}.getType();
        String json = gson.toJson(group, type);
        Map<String, Group> map = new HashMap<>();
        for (AllocationAlgorithm alg : getAlgorithms(0,0)) {
            Group curGroup = gson.fromJson(json, type); // using Gson to deep copy, ez
            map.put(alg.getName(), curGroup);
        }
        return map;
    }

    private static void evalAlgorithms(Map<String, Group> map, AllocationAlgorithm[] algorithms, List<Task> newTasks) {
        for (AllocationAlgorithm alg : algorithms) {
            if (map.containsKey(alg.getName())) {
                Group group = map.get(alg.getName());
                group.addTasks(newTasks);
                alg.assignTasks(group);
            }
        }
    }
}
