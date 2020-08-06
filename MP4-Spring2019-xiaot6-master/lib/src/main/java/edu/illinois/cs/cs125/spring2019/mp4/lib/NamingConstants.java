package edu.illinois.cs.cs125.spring2019.mp4.lib;

import java.util.HashMap;

/**
 * Class to hold various naming constants used by MoleculeAnalyzer.
 * <p>
 * This is just to move them out of that class.
 * <pre>
 *  SSSS TTTTT  OOO  PPPP  !
 * S       T   O   O P   P !
 *  SSS    T   O   O PPPP  !
 *     S   T   O   O P     !
 * SSSS    T    OOO  P     !
 * </pre>
 * Do not modify this class! Changes will be overwritten during official grading.
 */
public abstract class NamingConstants {
    /**
     * Base names of carbon chains/rings based on their length.
     */
    public static final String[] CHAIN_BASE_NAMES = {
        "meth",
        "eth",
        "prop",
        "but",
        "pent",
        "hex",
        "hept",
        "oct",
        "non",
        "dec"
    };

    /**
     * Prefixes for naming when there are multiple instances of the same substituent.
     */
    public static final String[] MULTIPLICITY_NAMES = {
        "",
        "di",
        "tri",
        "tetra",
        "penta",
        "hexa",
        "hepta",
        "octa",
        "nona",
        "deca"
    };

    /**
     * All letters that are considered vowels for purposes of molecule naming.
     */
    public static final String VOWELS = "aeiou";

    /**
     * Names of halogen substituents.
     */
    public static final HashMap<ChemicalElement, String> HALIDES = new HashMap<ChemicalElement, String>() {
        {
            put(ChemicalElement.BROMINE, "bromo");
            put(ChemicalElement.CHLORINE, "chloro");
            put(ChemicalElement.FLUORINE, "fluoro");
        }
    };
}
