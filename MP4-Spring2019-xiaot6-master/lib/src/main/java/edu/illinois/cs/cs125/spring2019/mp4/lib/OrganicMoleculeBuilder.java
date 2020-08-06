package edu.illinois.cs.cs125.spring2019.mp4.lib;

import java.util.Arrays;

/**
 * Provides a convenient way to construct OrganicMolecule objects.
 * You do not need to use this class.
 * <pre>
 *
 *   SSSS TTTTT  OOO  PPPP  !
 *  S       T   O   O P   P !
 *   SSS    T   O   O PPPP  !
 *      S   T   O   O P     !
 *  SSSS    T    OOO  P     !
 * </pre>
 * Do not modify this class! Changes will be overwritten during official grading.
 */
public abstract class OrganicMoleculeBuilder {

    /**
     * The maximum possible number of substituents on one carbon.
     */
    private static final int MAX_SUBSTITUENTS = 4;

    /**
     * How many carbons make up the backbone of the molecule.
     */
    private int coreCarbons;
    /**
     * The substituents on each core/backbone carbon.
     */
    private Substituent[][] substituents;

    /**
     * Creates an organic molecule builder.
     *
     * @param setCoreCarbons The number of core carbons.
     */
    public OrganicMoleculeBuilder(final int setCoreCarbons) {
        if (setCoreCarbons <= 0) {
            throw new IllegalArgumentException("Need at least one carbon");
        }
        coreCarbons = setCoreCarbons;
        substituents = new Substituent[setCoreCarbons][MAX_SUBSTITUENTS];
    }

    /**
     * Adds a substituent to the backbone at the given position.
     *
     * @param position    The position.
     * @param substituent The new substituent.
     * @return This builder.
     */
    public OrganicMoleculeBuilder addSubstituent(final int position,
                                                 final Substituent substituent) {
        for (int i = 0; i < MAX_SUBSTITUENTS; i++) {
            if (substituents[position][i] == null) {
                substituents[position][i] = substituent;
                return this;
            }
        }
        throw new IllegalStateException("This carbon is already full");
    }

    /**
     * Gets all present substituents at the given position.
     *
     * @param position The carbon in the base.
     * @return An array containing the carbon's substituents.
     */
    public Substituent[] getSubstituents(final int position) {
        int count = 0;
        for (Substituent s : substituents[position]) {
            if (s != null) {
                count++;
            }
        }
        return Arrays.copyOfRange(substituents[position], 0, count);
    }

    /**
     * Gets the number of carbons in the base chain/cycle of this molecule.
     *
     * @return The number of core carbons.
     */
    public int getCoreCarbons() {
        return coreCarbons;
    }

    /**
     * Builds the molecule.
     *
     * @return An array of the carbons in the backbone.
     */
    private BondedAtom[] buildBackbone() {
        BondedAtom[] backboneCarbons = new BondedAtom[coreCarbons];
        BondedAtom[][] backboneBonds = new BondedAtom[coreCarbons][];
        BondedAtom placeholder = new BondedAtom(ChemicalElement.CARBON, null, null);
        for (int i = 0; i < coreCarbons; i++) {
            int valenceFilled = 0;
            Substituent[] presentSubs = getSubstituents(i);
            for (Substituent s : presentSubs) {
                valenceFilled += s.getValenceConsumed();
            }
            int neededHydrogens = freeValence(i) - valenceFilled;
            int totalLinks = ChemicalElement.CARBON.getValence() - (valenceFilled - presentSubs.length);
            int[] bondNumbers = new int[totalLinks];
            Arrays.fill(bondNumbers, 1);
            backboneBonds[i] = new BondedAtom[totalLinks];
            backboneCarbons[i] = new BondedAtom(ChemicalElement.CARBON,
                backboneBonds[i], bondNumbers);
            if (i > 0) {
                backboneBonds[i][0] = backboneCarbons[i - 1];
            }
            if (i < coreCarbons - 1) {
                backboneBonds[i][1] = placeholder; // So it doesn't get filled by a hydrogen
            }
            int hydridesAdded = 0;
            int substituentsConnected = 0;
            for (int s = 0; s < totalLinks; s++) {
                if (backboneBonds[i][s] == null) {
                    if (substituentsConnected < presentSubs.length) {
                        backboneBonds[i][s] =
                            presentSubs[substituentsConnected].attach(backboneCarbons[i]);
                        bondNumbers[s] = presentSubs[substituentsConnected].getValenceConsumed();
                        substituentsConnected++;
                    } else if (hydridesAdded < neededHydrogens) {
                        backboneBonds[i][s] = new BondedAtom(ChemicalElement.HYDROGEN,
                            new BondedAtom[]{backboneCarbons[i]}, new int[]{1});
                        hydridesAdded++;
                    }
                }
            }
        }
        for (int i = 0; i < coreCarbons - 1; i++) {
            backboneBonds[i][1] = backboneCarbons[i + 1];
        }
        finishBackbone(backboneCarbons[0], backboneBonds[0],
            backboneCarbons[coreCarbons - 1], backboneBonds[coreCarbons - 1]);
        return backboneCarbons;
    }

    /**
     * Completes the backbone as appropriate for the type of builder.
     *
     * @param first      The first backbone carbon.
     * @param firstBonds The first carbon's connected atoms.
     * @param last       The last backbone carbon.
     * @param lastBonds  The last carbon's connected atoms.
     */
    protected abstract void finishBackbone(BondedAtom first, BondedAtom[] firstBonds,
                                           BondedAtom last, BondedAtom[] lastBonds);

    /**
     * Constructs a molecule.
     *
     * @return An atom in the molecule.
     */
    public BondedAtom build() {
        return buildBackbone()[0];
    }

    /**
     * Gets how many bonds of the given carbon are not satisfied in the backbone.
     *
     * @param position The zero-based index of the carbon.
     * @return How many bonds the given carbon has yet to make.
     */
    protected abstract int freeValence(int position);

    /**
     * Helps represent, build, and attach substituents to the backbone.
     * You do not need to use this class.
     */
    public static final class Substituent {
        /**
         * How many carbons are in an alkane substituent.
         */
        private int alkylLength;
        /**
         * How many hydrogens are on the O in an alcohol substituent.
         */
        private int alcoholProtons;
        /**
         * Which element a halogen substituent is.
         */
        private ChemicalElement halogen;
        /**
         * The type of substituent.
         */
        private SubstituentType type;

        /**
         * How many bonds this substituent consumes on the backbone carbon it's attached to.
         *
         * @return The number of bonds used to connect to this substituent.
         */
        public int getValenceConsumed() {
            if (type == SubstituentType.CARBONYL) {
                return 2;
            } else {
                return 1;
            }
        }

        /**
         * Builds the substituent and attaches it to the backbone.
         *
         * @param coreCarbon The core carbon bonded to the substituent.
         * @return The first atom of the substituent.
         */
        public BondedAtom attach(final BondedAtom coreCarbon) {
            switch (type) {
                case ALKANE:
                    BondedAtom[] carbons = new BondedAtom[alkylLength];
                    BondedAtom[][] carbonAttachments =
                        new BondedAtom[alkylLength][ChemicalElement.CARBON.getValence()];
                    for (int i = 0; i < alkylLength; i++) {
                        carbons[i] = new BondedAtom(ChemicalElement.CARBON,
                            carbonAttachments[i], new int[]{1, 1, 1, 1});
                    }
                    carbonAttachments[0][0] = coreCarbon;
                    for (int i = 1; i < alkylLength; i++) {
                        carbonAttachments[i][0] = carbons[i - 1];
                    }
                    for (int i = 0; i < alkylLength - 1; i++) {
                        carbonAttachments[i][1] = carbons[i + 1];
                    }
                    // Hydrogen isn't a halogen, but it is monovalent so this will work as shorthand
                    carbonAttachments[alkylLength - 1][1] =
                        createHalogen(ChemicalElement.HYDROGEN).attach(carbons[alkylLength - 1]);
                    for (int i = 2; i < ChemicalElement.CARBON.getValence(); i++) {
                        for (int c = 0; c < alkylLength; c++) {
                            carbonAttachments[c][i] =
                                createHalogen(ChemicalElement.HYDROGEN).attach(carbons[c]);
                        }
                    }
                    return carbons[0];
                case ALCOHOL:
                    BondedAtom[] oBonds = new BondedAtom[alcoholProtons + 1];
                    oBonds[0] = coreCarbon;
                    int[] oBondCounts = new int[alcoholProtons + 1];
                    Arrays.fill(oBondCounts, 1);
                    BondedAtom oxygen = new BondedAtom(ChemicalElement.OXYGEN, oBonds, oBondCounts);
                    for (int i = 0; i < alcoholProtons; i++) {
                        BondedAtom hydrogen = new BondedAtom(ChemicalElement.HYDROGEN,
                            new BondedAtom[]{oxygen}, new int[]{1});
                        oBonds[i + 1] = hydrogen;
                    }
                    return oxygen;
                case CARBONYL:
                    return new BondedAtom(ChemicalElement.OXYGEN, new BondedAtom[]{coreCarbon}, new int[]{2});
                case HALOGEN:
                    return new BondedAtom(halogen, new BondedAtom[]{coreCarbon}, new int[]{1});
                default:
                    return null; // Shouldn't happen
            }
        }

        /**
         * Gets what kind of substituent this is.
         *
         * @return The substituent type.
         */
        public SubstituentType getType() {
            return type;
        }

        /**
         * Gets how long this alkyl substituent is.
         *
         * @return The number of carbons.
         */
        public int getAlkylLength() {
            return alkylLength;
        }

        /**
         * Gets what halogen element this substituent is.
         *
         * @return The chemical element.
         */
        public ChemicalElement getHalogen() {
            return halogen;
        }

        /**
         * Creates an alkyl substituent.
         *
         * @param length How many carbons in the alkyl chain.
         * @return The new substituent.
         */
        public static Substituent createAlkyl(final int length) {
            Substituent s = new Substituent();
            s.type = SubstituentType.ALKANE;
            s.alkylLength = length;
            return s;
        }

        /**
         * Creates a halogen (single-atom) substituent.
         *
         * @param element Which element this halogen is.
         * @return The new substituent.
         */
        public static Substituent createHalogen(final ChemicalElement element) {
            Substituent s = new Substituent();
            s.type = SubstituentType.HALOGEN;
            s.halogen = element;
            return s;
        }

        /**
         * Creates a neutral alcohol (OH) substituent.
         *
         * @return The new substituent.
         */
        public static Substituent createAlcohol() {
            return createAlcohol(1);
        }

        /**
         * Creates an (optionally charged) alcohol substituent.
         *
         * @param protonation The number of protons on the O, from zero to two.
         * @return The new substituent.
         */
        public static Substituent createAlcohol(final int protonation) {
            Substituent s = new Substituent();
            s.type = SubstituentType.ALCOHOL;
            s.alcoholProtons = protonation;
            return s;
        }

        /**
         * Creates a carbonyl (double-bonded O) substituent.
         *
         * @return The new substituent.
         */
        public static Substituent createCarbonyl() {
            Substituent s = new Substituent();
            s.type = SubstituentType.CARBONYL;
            return s;
        }
    }

    /**
     * Possible types of substituents.
     * You do not need to use this.
     */
    public enum SubstituentType {
        /**
         * A single-bonded chain of carbons.
         */
        ALKANE,
        /**
         * A halogen.
         */
        HALOGEN,
        /**
         * An alcohol (-OH group).
         */
        ALCOHOL,
        /**
         * A carbonyl (=O group).
         */
        CARBONYL
    }
}
