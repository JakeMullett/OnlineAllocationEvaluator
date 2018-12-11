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

import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OnlineAllocationEvaluator {

    /*
     * This is going to be the main class. It is going to be a command line program
     * where you can enter in the current allocations and preferences and new tasks or
     * start from scratch with just the preferences CSV.
     */
    public static void main(String[] args) throws ArgumentParserException, IOException, ParseException {
        try {
            Namespace arguments = getArgs(args);
            System.out.println(arguments);
            String preferenceFilename = arguments.getString("pref_filename");
            String taskFilename = arguments.getString("task_filename");
            boolean jsonInput = arguments.getString("pref_input").equals("json");
            boolean allTasksAtOnce = arguments.getBoolean("batched");
            String groupName = arguments.getString("name");

            Map<AllocationAlgorithm, Group> groupMap;
            Gson gson = new GsonBuilder()
                    .disableHtmlEscaping()
                    .setPrettyPrinting()
                    .serializeNulls()
                    .enableComplexMapKeySerialization()
                    .create();

            if (!jsonInput) {
                // We are creating a new set of preferences.
                Group newGroup = new Group(preferenceFilename, groupName);
                int timeSteps = 200, groupSize = newGroup.getPersonTasksMap().size();
                groupMap = instantiateAlgorithms(newGroup, timeSteps, groupSize, gson);
            } else {
                Type type = new TypeToken<Map<AllocationAlgorithm, Group>>(){}.getType();
                groupMap = gson.fromJson(new FileReader(preferenceFilename), type);
            }

            if (allTasksAtOnce) {
                evalAlgorithms(groupMap, FormParser.getAllTasksFromCSV(taskFilename));
            } else {
                for (List<Task> tasks : FormParser.getBatchedTasksfromCSV(taskFilename)) {
                    evalAlgorithms(groupMap, tasks);
                }
            }

            for (Map.Entry<AllocationAlgorithm, Group> entry : groupMap.entrySet()) {
                EnvyGrapher.graph(entry.getValue(), entry.getKey().getName());
            }

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

    private static Map<AllocationAlgorithm, Group> instantiateAlgorithms(Group group, int timeSteps, int numPeople, Gson gson) {
        AllocationAlgorithm[] algs = {new RandomAllocation()};//, new GreedySingleAllocation(), new OnlineSingleAllocation(timeSteps, numPeople)};
        HashMap<AllocationAlgorithm, Group> groupHashMap = new HashMap<>();
        Type type = new TypeToken<Group>(){}.getType();
        for (AllocationAlgorithm alg : algs) {
            Group curGroup = gson.fromJson(gson.toJson(group, type), type); // using Gson to deep copy, ez
            alg.assignTasks(curGroup);
            groupHashMap.put(alg, curGroup);
        }
        return groupHashMap;
    }

    private static void evalAlgorithms(Map<AllocationAlgorithm, Group> map, List<Task> newTasks) {
        for (Map.Entry<AllocationAlgorithm, Group> entry : map.entrySet()) {
            Group group = entry.getValue();
            group.addTasks(newTasks);
            entry.getKey().assignTasks(group);
        }
    }
}
