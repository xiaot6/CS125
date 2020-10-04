package edu.illinois.cs.cs125.spring2019.mp4.lib;

/**
 * Provides a convenient way to construct and render cyclic organic molecules.
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
public final class CyclicOrganicMoleculeBuilder extends OrganicMoleculeBuilder {

    /**
     * The length of the smallest possible cycle (cyclopropane).
     */
    private static final int SMALLEST_CYCLE = 3;

    /**
     * Creates a builder for a cyclic molecule containing the given number of carbons in the ring.
     *
     * @param setCycleLength The number (at least 3) of carbons in the ring.
     */
    public CyclicOrganicMoleculeBuilder(final int setCycleLength) {
        super(setCycleLength);
        if (setCycleLength < SMALLEST_CYCLE) {
            throw new IllegalArgumentException("The smallest possible cycle is cyclopropane");
        }
    }

    /**
     * Makes the molecule cyclic by linking the first and last backbone carbons.
     *
     * @param first      The first backbone carbon.
     * @param firstBonds The first carbon's connected atoms.
     * @param last       The last backbone carbon.
     * @param lastBonds  The last carbon's connected atoms.
     */
    @Override
    protected void finishBackbone(final BondedAtom first, final BondedAtom[] firstBonds,
                                  final BondedAtom last, final BondedAtom[] lastBonds) {
        for (int i = 0; i < firstBonds.length; i++) {
            if (firstBonds[i] == null) {
                firstBonds[i] = last;
                break;
            }
        }
        for (int i = 0; i < lastBonds.length; i++) {
            if (lastBonds[i] == null) {
                lastBonds[i] = first;
                break;
            }
        }
    }

    /**
     * Gets how many bonds a core carbon can make after inclusion in the backbone.
     *
     * @param position The zero-based carbon index.
     * @return Always two, since carbons in the ring are guaranteed two links.
     */
    @Override
    protected int freeValence(final int position) {
        return 2;
    }
}
