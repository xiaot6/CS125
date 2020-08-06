package edu.illinois.cs.cs125.spring2019.mp4.lib;

/**
 * Provides a convenient way to build and render linear/branched organic molecules.
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
public final class LinearOrganicMoleculeBuilder extends OrganicMoleculeBuilder {

    /**
     * Creates a builder for a linear/branched molecule.
     * @param setCoreCarbons How many carbons there are in the starting chain.
     */
    public LinearOrganicMoleculeBuilder(final int setCoreCarbons) {
        super(setCoreCarbons);
    }

    /**
     * Does nothing - freeValence makes sure the right number of hydrogens is added.
     * @param first The first backbone carbon.
     * @param firstBonds The first carbon's connected atoms.
     * @param last The last backbone carbon.
     * @param lastBonds The last carbon's connected atoms.
     */
    @Override
    protected void finishBackbone(final BondedAtom first, final BondedAtom[] firstBonds,
                                  final BondedAtom last, final BondedAtom[] lastBonds) {
        // This doesn't have to do anything - it's already filled with hydrogens
    }

    /**
     * Gets how many bonds a core carbon can make after inclusion in the backbone.
     * @param position The zero-based carbon index.
     * @return How many bonds of the given carbon are not satisfied in the backbone.
     */
    @Override
    protected int freeValence(final int position) {
        final int methaneValence = 4;
        final int chainEndValence = 3;
        final int midChainValence = 2;
        if (position == 0 && getCoreCarbons() == 1) {
            return methaneValence;
        } else if (position == 0 || position == getCoreCarbons() - 1) {
            return chainEndValence;
        } else {
            return midChainValence;
        }
    }
}
