package com.example;

import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.collections.impl.factory.Maps;
import org.eclipse.collections.impl.factory.Sets;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The {@code UD} class represents an object that stores an uncertain database.
 *
 * <p>This class uses Eclipse Collections for efficient management of collections.</p>
 *
 * <p>It stores a mutable list of items and a map containing unique items along with their probabilities.</p>
 *
 * <p>Usage example:</p>
 * <pre>
 * UD myUD = new UD("path/to/database.txt");
 * MutableMap&lt;String, Double&gt; probabilities = myUD.getProbability();
 * MutableList&lt;Item&gt; uncertainDatabase = myUD.getUD();
 * System.out.println(myUD);
 * </pre>
 */
public class UD {
    // Mutable list of items
    private MutableList<Item> UD = Lists.mutable.empty();

    // Map containing unique items and their probabilities
    private MutableMap<String, Double> uniqueProbability = Maps.mutable.empty();

    /**
     * Constructs a {@code UD} object with the uncertain database stored in the specified file.
     *
     * @param path the path to the file containing the uncertain database
     */
    public UD(String path) {
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line;
            MutableList<Double> probArr = Lists.mutable.empty();

            // Read each line from the file
            while ((line = reader.readLine()) != null) {
                String[] tempItem = line.split(" ");
                if (isProbability(tempItem[tempItem.length - 1])) {
                    this.UD.add(new Item(Arrays.copyOf(tempItem, tempItem.length - 1)));
                    probArr.add(Double.parseDouble(tempItem[tempItem.length - 1]));
                } else {
                    this.UD.add(new Item(tempItem));
                    probArr.add(0.0);
                }
            }

            MutableList<MutableSet<String>> tempDataset = removeProbFromUD();
            MutableSet<String> tempUnique = Sets.mutable.empty();
            for (MutableSet<String> transaction : tempDataset) {
                // TODO: check again
                tempUnique.addAll(transaction);
            }

            // Create separate item
            tempUnique.each(t -> this.uniqueProbability.put(t, 0.0));

            // Read the prob in transaction
            // int i = 0;
            AtomicInteger i = new AtomicInteger(0);
            for (MutableSet<String> transaction : tempDataset) {
                transaction.each(t -> {
                    if (probArr.get(i.get()) != 0.0) {
                        double prob = this.uniqueProbability.get(t);
                        if (prob == 0) {
                            prob = 1;
                        }
                        this.uniqueProbability.put(t, prob * probArr.get(i.get()));
                    }
                });
                i.incrementAndGet();
            }

            // Generate prob with some item
            this.uniqueProbability.forEachKeyValue((key, value) -> {
                if (value == 0.0) {
                    this.uniqueProbability.put(key, probRandom());
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private double probRandom() {
        Random random = new Random();
        return Double.parseDouble(String.format("%.2f", random.nextDouble()));
    }

    private boolean isProbability(String s) {
        try {
            double prob = Double.parseDouble(s);
            return prob > 0 && prob < 1;
        } catch (Exception e) {
            return false;
        }
    }

    // Separate the prob from the UD
    public MutableList<MutableSet<String>> removeProbFromUD() {
        MutableList<MutableSet<String>> res = Lists.mutable.empty();

        this.UD.collect(Item::getItem).each(res::add);

        return res;
    }

    public MutableMap<String, Double> getProbability() {
        return this.uniqueProbability;
    }

    public MutableList<Item> getUD() {
        return this.UD;
    }

    public int getSize() {
        return this.UD.size();
    }

    @Override
    public String toString() {
        this.UD.each(t -> System.out.println(t.toString()));
        System.out.println("Probability: " + this.uniqueProbability);
        return "";
    }
}
