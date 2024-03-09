package com.example;

import java.util.Random;

import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.collections.impl.factory.Sets;

/**
 * The {@code Item} class represents an object that stores a set of items
 * and their associated random probabilities.
 *
 * <p>
 * This class uses Eclipse Collections for efficient management of the set and
 * list of probabilities.
 * </p>
 *
 * <p>
 * Items are added to the set during object initialization, and each item is
 * associated with a random probability stored in a separate list.
 * </p>
 *
 * <p>
 * The probabilities are generated using a random number generator.
 * </p>
 *
 * <p>
 * Usage example:
 * </p>
 * 
 * <pre>
 * String[] items = { "item1", "item2", "item3" };
 * Item myItem = new Item(items);
 * MutableSet<String> itemSet = myItem.getItem();
 * MutableList<Double> probabilities = myItem.getProbTemp();
 * System.out.println("Items: " + itemSet);
 * System.out.println("Probabilities: " + probabilities);
 * </pre>
 */
public class Item {
    // Set of items
    private MutableSet<String> item;

    // List of associated random probabilities
    private MutableList<Double> probTemp;

    /**
     * Constructs an {@code Item} object with the specified array of items.
     *
     * @param items an array of items to be added to the set
     */
    public Item(String[] items) {
        // Initialize the item set and probability list
        this.item = Sets.mutable.empty();
        this.probTemp = Lists.mutable.empty();

        // Add each item to the set and generate a random probability for each
        for (String i : items) {
            this.item.add(i);
            this.probTemp.add(probRandom());
        }
    }

    /**
     * Returns the set of items stored in this {@code Item} object.
     *
     * @return the set of items
     */
    public MutableSet<String> getItem() {
        return this.item;
    }

    /**
     * Returns a random probability generated using a random number generator.
     *
     * @return a random probability
     */
    private double probRandom() {
        Random random = new Random();
        return random.nextDouble();
    }

    /**
     * Returns a string representation of the set of items.
     *
     * @return a string representation of the set of items
     */
    @Override
    public String toString() {
        return String.valueOf(this.item);
    }
}
