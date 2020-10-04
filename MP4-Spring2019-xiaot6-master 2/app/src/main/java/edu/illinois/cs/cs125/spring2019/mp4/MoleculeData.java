package edu.illinois.cs.cs125.spring2019.mp4;

import java.util.ArrayList;

import edu.illinois.cs.cs125.spring2019.mp4.lib.BondedAtom;
import edu.illinois.cs.cs125.spring2019.mp4.lib.CyclicOrganicMoleculeBuilder;
import edu.illinois.cs.cs125.spring2019.mp4.lib.MoleculeAnalyzer;
import edu.illinois.cs.cs125.spring2019.mp4.lib.OrganicMoleculeBuilder;

/**
 * Class to create molecule data objects that can be parsed by GSON into JSON objects.
 * <p>
 * This class takes in OrganicMoleculeBuilder objects and copies the data necessary to draw them
 * with P5.js onto the screen.
 */
@SuppressWarnings({"FieldCanBeLocal", "unused"})
class MoleculeData {
    /**
     * Number of core carbons.
     */
    private int coreCarbons;

    /**
     * List of lists of substituents bonded to each atom in the molecule.
     */
    private ArrayList<ArrayList<Substituent>> substituents;

    /**
     * Tells if molecule is cyclic or not.
     */
    private boolean cyclic;

    /**
     * IUPAC name of the molecule.
     */
    private String name;

    /**
     * Chemical formula of the molecule.
     */
    private String formula;

    /**
     * Atomic mass of the molecule.
     */
    private double mass;

    /**
     * Whether this is being run under the app test suite.
     */
    private static boolean testing = false;

    /**
     * Return the IUPAC name of the molecule.
     *
     * @return the IUPAC name of the molecule.
     */
    public String getName() {
        return name;
    }

    /**
     * Return the atomic mass of the molecule.
     *
     * @return the atomic mass of the molecule.
     */
    public double getMass() {
        return mass;
    }

    /**
     * Return the chemical formula of the molecule.
     *
     * @return the chemical formula of the molecule.
     */
    public String getFormula() {
        return formula;
    }

    /**
     * Constructs the molecule data for a organic molecule object.
     *
     * @param builder the molecule builder object to copy data from
     */
    MoleculeData(final OrganicMoleculeBuilder builder) {
        initHelper(builder);
        cyclic = builder instanceof CyclicOrganicMoleculeBuilder;
    }

    /**
     * Helper function to be called by the constructors to initialize molecule data.
     *
     * @param builder the molecule builder object to copy data from
     */
    private void initHelper(final OrganicMoleculeBuilder builder) {
        if (!testing) {
            // Only use MoleculeAnalyzer if the app is actually being run
            // This makes it possible to run the app test even if MoleculeAnalyzer is crashing
            BondedAtom atom = builder.build();
            MoleculeAnalyzer analyzer = new MoleculeAnalyzer(atom);
            name = analyzer.getIupacName();
            formula = analyzer.getFormula();
            mass = analyzer.getMolecularWeight();
        }

        coreCarbons = builder.getCoreCarbons();
        substituents = new ArrayList<>();

        for (int i = 0; i < this.coreCarbons; i++) {
            this.substituents.add(new ArrayList<Substituent>());
            for (int j = 0; j < builder.getSubstituents(i).length && builder.getSubstituents(i)[j] != null; j++) {
                int bondCount;
                int chainLength = builder.getSubstituents(i)[j].getAlkylLength();
                OrganicMoleculeBuilder.SubstituentType type = builder.getSubstituents(i)[j].getType();

                String substituentFormula;
                try {
                    substituentFormula = builder.getSubstituents(i)[j].getHalogen().getSymbol();
                } catch (NullPointerException unused) {
                    substituentFormula = "";
                }

                if (type == OrganicMoleculeBuilder.SubstituentType.CARBONYL) {
                    bondCount = 2;
                } else {
                    bondCount = 1;
                }

                substituents.get(i).add(new Substituent(bondCount, chainLength, substituentFormula, type.toString()));
            }
        }
    }

    /**
     * Notifies the class that it's being run under the app test suite. For testing purposes only.
     */
    public static void setTesting() {
        testing = true;
    }

    /**
     * Class for storing substitent information.
     */
    private class Substituent {
        /**
         * Stores the substituent bond count.
         */
        private int bondCount;

        /**
         * Stores the substituent chain length.
         */
        private int chainLength;

        /**
         * Stores the substituent formula.
         */
        private String formula;

        /**
         * Stores the substituent type.
         */
        private String type;

        /**
         * Create a new Substituent object.
         *
         * @param setBondCount   the new substituent's bond count
         * @param setChainLength the new substituent's chain length
         * @param setFormula     the new substituent's formula
         * @param setType        the new substituent's type
         */
        Substituent(final int setBondCount, final int setChainLength, final String setFormula, final String setType) {
            bondCount = setBondCount;
            chainLength = setChainLength;
            formula = setFormula;
            type = setType;
        }
    }
}
