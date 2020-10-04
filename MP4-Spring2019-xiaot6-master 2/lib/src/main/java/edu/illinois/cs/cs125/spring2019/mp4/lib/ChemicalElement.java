package edu.illinois.cs.cs125.spring2019.mp4.lib;

/**
 * Represents an element, that is, a type of atom.
 * <p>
 * You do not need to modify this class, but you will need to use its methods.
 */
public final class ChemicalElement {

    /**
     * The one- or two-letter symbol on the periodic table for this element.
     */
    private String symbol;
    /**
     * The weight of this element in grams per mole.
     */
    private double weight;
    /**
     * How many bonds this element wants.
     */
    private int valence;

    /**
     * Element 6, carbon.
     */
    public static final ChemicalElement CARBON = new ChemicalElement("C", 12.011, 4);
    /**
     * Element 1, hydrogen.
     */
    public static final ChemicalElement HYDROGEN = new ChemicalElement("H", 1.008, 1);
    /**
     * Element 8, oxygen.
     */
    public static final ChemicalElement OXYGEN = new ChemicalElement("O", 15.999, 2);
    /**
     * Element 9, the first halogen, fluorine.
     */
    public static final ChemicalElement FLUORINE = new ChemicalElement("F", 18.998, 1);
    /**
     * Element 17, the second halogen, chlorine.
     */
    public static final ChemicalElement CHLORINE = new ChemicalElement("Cl", 35.45, 1);
    /**
     * Element 35, the third halogen, bromine.
     */
    public static final ChemicalElement BROMINE = new ChemicalElement("Br", 79.904, 1);
    /**
     * Element 2, the first noble gas, helium.
     */
    public static final ChemicalElement HELIUM = new ChemicalElement("He", 4.003, 0);

    /**
     * Creates a new element.
     *
     * @param setSymbol  The element's symbol.
     * @param setWeight  The element's atomic mass in grams per mole.
     * @param setValence The element's valence.
     */
    private ChemicalElement(final String setSymbol, final double setWeight, final int setValence) {
        symbol = setSymbol;
        weight = setWeight;
        valence = setValence;
    }

    /**
     * Gets the element's symbol on the periodic table.
     *
     * @return The one- or two-letter element symbol.
     */
    public String getSymbol() {
        return symbol;
    }

    /**
     * Gets the element's average atomic mass.
     *
     * @return The atomic mass in grams per mole.
     */
    public double getWeight() {
        return weight;
    }

    /**
     * Gets the element's valence, that is, how many bonds an atom of this element should have.
     *
     * @return The valence.
     */
    public int getValence() {
        return valence;
    }

    /**
     * Gets a string representation of the element object.
     *
     * @return The stringified element.
     */
    @Override
    public String toString() {
        return "{ChemicalElement " + symbol + "}";
    }
}
