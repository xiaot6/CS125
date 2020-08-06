package edu.illinois.cs.cs125.spring2019.mp4.lib;

import org.apache.commons.collections4.CollectionUtils;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Test suite for the organic molecule analysis tests.
 * <p>
 * The provided test suite is correct and complete. You should not need to modify it.
 * However, you should understand it.
 *
 * @see <a href="https://cs125.cs.illinois.edu/MP/4/">MP4 Documentation</a>
 */
public class MoleculeAnalyzerTest {

    /** Timeout for analysis tests. Reference solution takes 1-40 ms from cold start. */
    private static final int ANALYZE_TEST_TIMEOUT = 400;

    /** Timeout for molecule naming tests. Reference solution takes 1-150 ms from warm start. */
    private static final int NAME_TEST_TIMEOUT = 900;

    /** Timeout for helper function tests. */
    private static final int HELPER_TEST_TIMEOUT = 400;

    /** Tolerance for differences in molecular weight. */
    private static final double MW_COMPARE_DELTA = 0.005;

    /** Test molecular weight calculation. */
    @Test(timeout = ANALYZE_TEST_TIMEOUT)
    public void testMolecularWeight() {
        for (MoleculeAnalysisTestInput input : analysisTestCases) {
            MoleculeAnalyzer analyzer = new MoleculeAnalyzer(input.molecule.build());
            Assert.assertEquals(input.molecularWeight, analyzer.getMolecularWeight(), MW_COMPARE_DELTA);
        }
    }

    /** Test determining whether there are any charged atoms. */
    @Test(timeout = ANALYZE_TEST_TIMEOUT)
    public void testHasCharged() {
        for (MoleculeAnalysisTestInput input : analysisTestCases) {
            MoleculeAnalyzer analyzer = new MoleculeAnalyzer(input.molecule.build());
            Assert.assertEquals(input.hasChargedAtom, analyzer.hasChargedAtoms());
        }
    }

    /** Test naming straight-chain alkanes with no substituents. */
    @Test(timeout = NAME_TEST_TIMEOUT)
    public void testNamingSimpleStraight() {
        runNamingTest(MoleculeNamingTestDifficulty.LINEAR_ALKANE);
    }

    /** Test naming cyclic alkanes with no substituents. */
    @Test(timeout = NAME_TEST_TIMEOUT)
    public void testNamingSimpleCyclic() {
        runNamingTest(MoleculeNamingTestDifficulty.CYCLIC_ALKANE);
    }

    /** Test naming cyclic alkanes with one non-suffix-affecting substituent. */
    @Test(timeout = NAME_TEST_TIMEOUT)
    public void testNamingOneSubstituentCyclic() {
        runNamingTest(MoleculeNamingTestDifficulty.MONOSUBSTITUTED_RING);
    }

    /** Test naming linear alkanes with one non-suffix affecting substituent. Alkyl substituents cause branching. */
    @Test(timeout = NAME_TEST_TIMEOUT)
    public void testNamingOneSubstituentLinear() {
        runNamingTest(MoleculeNamingTestDifficulty.SINGLE_SUBSTITUENT_LINEAR);
    }

    /** EXTRA CREDIT: Test naming molecules with multiple substituents. */
    @Test(timeout = NAME_TEST_TIMEOUT)
    public void testNamingMultipleSubstituents() {
        runNamingTest(MoleculeNamingTestDifficulty.MULTIPLE_SUBSTITUENTS);
    }

    /** EXTRA CREDIT: Test naming complicated molecules with multiple substituents and priority tiebreaks. */
    @Test(timeout = NAME_TEST_TIMEOUT)
    public void testNamingPriority() {
        runNamingTest(MoleculeNamingTestDifficulty.PRIORITY_TIEBREAK);
    }

    /**
     * Runs the naming test on the given group of molecules.
     * @param difficulty The difficulty/group of molecules to name.
     */
    private void runNamingTest(final MoleculeNamingTestDifficulty difficulty) {
        for (MoleculeNamingTestInput input : namingTestCases) {
            if (input.difficulty == difficulty) {
                BondedAtom originalStart = input.molecule.build();
                // Start exploring from somewhere on the backbone
                runNamingCheck(originalStart, input.validNames);
                // Start exploring from somewhere else
                for (BondedAtom neighbor : originalStart) {
                    runNamingCheck(neighbor, input.validNames);
                }
            }
        }
    }

    /**
     * Tests MoleculeAnalyzer's naming starting at a specific atom.
     * @param startPoint The atom to start exploring from.
     * @param validNames All valid names for the molecule.
     */
    private void runNamingCheck(final BondedAtom startPoint, final String[] validNames) {
        MoleculeAnalyzer analyzer = new MoleculeAnalyzer(startPoint);
        String result = analyzer.getIupacName();
        boolean validAnswer = false;
        for (String option : validNames) {
            if (option.equals(result)) {
                validAnswer = true;
                break;
            }
        }
        if (!validAnswer) {
            /*
            Only do an assertion if we know the answer is wrong.
            This way, the easiest-to-compute option will be shown in the error message,
            despite other fancier names being valid too.
             */
            Assert.assertEquals(validNames[0], result);
        }
    }

    private static class MoleculeAnalysisTestInput {
        OrganicMoleculeBuilder molecule;
        double molecularWeight;
        boolean hasChargedAtom;
        public MoleculeAnalysisTestInput(final OrganicMoleculeBuilder setMolecule,
                                         final double setMW, final boolean setCharge) {
            molecule = setMolecule;
            molecularWeight = setMW;
            hasChargedAtom = setCharge;
        }
    }

    private enum MoleculeNamingTestDifficulty {
        LINEAR_ALKANE,
        CYCLIC_ALKANE,
        MONOSUBSTITUTED_RING,
        SINGLE_SUBSTITUENT_LINEAR,
        MULTIPLE_SUBSTITUENTS,
        PRIORITY_TIEBREAK
    }

    private static class MoleculeNamingTestInput {
        OrganicMoleculeBuilder molecule;
        String[] validNames;
        MoleculeNamingTestDifficulty difficulty;

        public MoleculeNamingTestInput(final MoleculeNamingTestDifficulty setDifficulty,
                                       final OrganicMoleculeBuilder setMolecule, final String... setNames) {
            molecule = setMolecule;
            difficulty = setDifficulty;
            validNames = setNames;
        }
    }

    /** All the analysis (non-naming) test cases. */
    private static List<MoleculeAnalysisTestInput> analysisTestCases = new ArrayList<>();
    /** All the naming test cases. */
    private static List<MoleculeNamingTestInput> namingTestCases = new ArrayList<>();

    static {
        // Analysis test cases
        analysisTestCases.add(new MoleculeAnalysisTestInput(
                new LinearOrganicMoleculeBuilder(1), 16.043, false));
        analysisTestCases.add(new MoleculeAnalysisTestInput(
                new LinearOrganicMoleculeBuilder(2), 30.070, false));
        analysisTestCases.add(new MoleculeAnalysisTestInput(
                new LinearOrganicMoleculeBuilder(3)
                        .addSubstituent(1, OrganicMoleculeBuilder.Substituent.createAlkyl(1)),
                58.124, false));
        analysisTestCases.add(new MoleculeAnalysisTestInput(
                new LinearOrganicMoleculeBuilder(3)
                        .addSubstituent(1, OrganicMoleculeBuilder.Substituent.createAlkyl(1))
                        .addSubstituent(1, OrganicMoleculeBuilder.Substituent.createAlkyl(1)),
                72.151, false));
        analysisTestCases.add(new MoleculeAnalysisTestInput(
                new LinearOrganicMoleculeBuilder(2)
                        .addSubstituent(0, OrganicMoleculeBuilder.Substituent.createAlcohol(0)),
                45.061, true));
        analysisTestCases.add(new MoleculeAnalysisTestInput(
                new CyclicOrganicMoleculeBuilder(6), 84.162, false));
        analysisTestCases.add(new MoleculeAnalysisTestInput(
                new CyclicOrganicMoleculeBuilder(5)
                        .addSubstituent(3, OrganicMoleculeBuilder.Substituent.createAlcohol(2)),
                87.142, true));
        analysisTestCases.add(new MoleculeAnalysisTestInput(
                new LinearOrganicMoleculeBuilder(3)
                        .addSubstituent(2, OrganicMoleculeBuilder.Substituent.createCarbonyl()),
                58.080, false));
        analysisTestCases.add(new MoleculeAnalysisTestInput(
                new LinearOrganicMoleculeBuilder(2)
                        .addSubstituent(0, OrganicMoleculeBuilder.Substituent.createHalogen(ChemicalElement.FLUORINE))
                        .addSubstituent(0, OrganicMoleculeBuilder.Substituent.createHalogen(ChemicalElement.FLUORINE))
                        .addSubstituent(1, OrganicMoleculeBuilder.Substituent.createHalogen(ChemicalElement.CHLORINE))
                        .addSubstituent(1, OrganicMoleculeBuilder.Substituent.createHalogen(ChemicalElement.BROMINE)),
                179.388, false));
        analysisTestCases.add(new MoleculeAnalysisTestInput(
                new LinearOrganicMoleculeBuilder(12)
                        .addSubstituent(2, OrganicMoleculeBuilder.Substituent.createAlkyl(5))
                        .addSubstituent(4, OrganicMoleculeBuilder.Substituent.createAlcohol(0))
                        .addSubstituent(10, OrganicMoleculeBuilder.Substituent.createAlcohol(2)),
                272.473, true));
        analysisTestCases.add(new MoleculeAnalysisTestInput(
                new LinearOrganicMoleculeBuilder(2)
                        .addSubstituent(0, OrganicMoleculeBuilder.Substituent.createHalogen(ChemicalElement.HELIUM)),
                33.065, true));
        analysisTestCases.add(new MoleculeAnalysisTestInput(
                new CyclicOrganicMoleculeBuilder(323),
                4530.721, false));

        // Naming simple straight alkanes
        namingTestCases.add(new MoleculeNamingTestInput(MoleculeNamingTestDifficulty.LINEAR_ALKANE,
                new LinearOrganicMoleculeBuilder(1), "methane"));
        namingTestCases.add(new MoleculeNamingTestInput(MoleculeNamingTestDifficulty.LINEAR_ALKANE,
                new LinearOrganicMoleculeBuilder(2), "ethane"));
        namingTestCases.add(new MoleculeNamingTestInput(MoleculeNamingTestDifficulty.LINEAR_ALKANE,
                new LinearOrganicMoleculeBuilder(9), "nonane"));
        namingTestCases.add(new MoleculeNamingTestInput(MoleculeNamingTestDifficulty.LINEAR_ALKANE,
                new LinearOrganicMoleculeBuilder(3), "propane"));
        namingTestCases.add(new MoleculeNamingTestInput(MoleculeNamingTestDifficulty.LINEAR_ALKANE,
                new LinearOrganicMoleculeBuilder(6), "hexane"));
        namingTestCases.add(new MoleculeNamingTestInput(MoleculeNamingTestDifficulty.LINEAR_ALKANE,
                new LinearOrganicMoleculeBuilder(4), "butane"));
        namingTestCases.add(new MoleculeNamingTestInput(MoleculeNamingTestDifficulty.LINEAR_ALKANE,
                new LinearOrganicMoleculeBuilder(7), "heptane"));
        namingTestCases.add(new MoleculeNamingTestInput(MoleculeNamingTestDifficulty.LINEAR_ALKANE,
                new LinearOrganicMoleculeBuilder(5), "pentane"));
        namingTestCases.add(new MoleculeNamingTestInput(MoleculeNamingTestDifficulty.LINEAR_ALKANE,
                new LinearOrganicMoleculeBuilder(8), "octane"));
        /*
        The "substituents" on the remaining test cases in this section are at the ends of
        the molecule, so they actually just elongate the chain instead of add branching.
        This is to make sure the solution can't rely on the array indexing pattern of the builder,
        since what matters is the BondedAtom data structure, not the builder implementation.
        Several test cases in other sections use this trick as well.
         */
        namingTestCases.add(new MoleculeNamingTestInput(MoleculeNamingTestDifficulty.LINEAR_ALKANE,
                new LinearOrganicMoleculeBuilder(3)
                        .addSubstituent(0, OrganicMoleculeBuilder.Substituent.createAlkyl(1)), "butane"));
        namingTestCases.add(new MoleculeNamingTestInput(MoleculeNamingTestDifficulty.LINEAR_ALKANE,
                new LinearOrganicMoleculeBuilder(4)
                        .addSubstituent(0, OrganicMoleculeBuilder.Substituent.createAlkyl(2))
                        .addSubstituent(3, OrganicMoleculeBuilder.Substituent.createAlkyl(1)), "heptane"));
        namingTestCases.add(new MoleculeNamingTestInput(MoleculeNamingTestDifficulty.LINEAR_ALKANE,
                new LinearOrganicMoleculeBuilder(1)
                        .addSubstituent(0, OrganicMoleculeBuilder.Substituent.createAlkyl(4))
                        .addSubstituent(0, OrganicMoleculeBuilder.Substituent.createAlkyl(4)), "nonane"));

        // Naming simple cyclic alkanes
        namingTestCases.add(new MoleculeNamingTestInput(MoleculeNamingTestDifficulty.CYCLIC_ALKANE,
                new CyclicOrganicMoleculeBuilder(3), "cyclopropane"));
        namingTestCases.add(new MoleculeNamingTestInput(MoleculeNamingTestDifficulty.CYCLIC_ALKANE,
                new CyclicOrganicMoleculeBuilder(10), "cyclodecane"));
        namingTestCases.add(new MoleculeNamingTestInput(MoleculeNamingTestDifficulty.CYCLIC_ALKANE,
                new CyclicOrganicMoleculeBuilder(6), "cyclohexane"));
        namingTestCases.add(new MoleculeNamingTestInput(MoleculeNamingTestDifficulty.CYCLIC_ALKANE,
                new CyclicOrganicMoleculeBuilder(8), "cyclooctane"));
        namingTestCases.add(new MoleculeNamingTestInput(MoleculeNamingTestDifficulty.CYCLIC_ALKANE,
                new CyclicOrganicMoleculeBuilder(4), "cyclobutane"));
        namingTestCases.add(new MoleculeNamingTestInput(MoleculeNamingTestDifficulty.CYCLIC_ALKANE,
                new CyclicOrganicMoleculeBuilder(5), "cyclopentane"));

        // Naming cyclic alkanes with one low-priority substituent
        namingTestCases.add(new MoleculeNamingTestInput(MoleculeNamingTestDifficulty.MONOSUBSTITUTED_RING,
                new CyclicOrganicMoleculeBuilder(3)
                        .addSubstituent(0, OrganicMoleculeBuilder.Substituent.createHalogen(ChemicalElement.FLUORINE)), "1-fluorocyclopropane", "fluorocyclopropane"));
        namingTestCases.add(new MoleculeNamingTestInput(MoleculeNamingTestDifficulty.MONOSUBSTITUTED_RING,
                new CyclicOrganicMoleculeBuilder(6)
                        .addSubstituent(3, OrganicMoleculeBuilder.Substituent.createAlkyl(1)), "1-methylcyclohexane", "methylcyclohexane"));
        namingTestCases.add(new MoleculeNamingTestInput(MoleculeNamingTestDifficulty.MONOSUBSTITUTED_RING,
                new CyclicOrganicMoleculeBuilder(8)
                        .addSubstituent(5, OrganicMoleculeBuilder.Substituent.createHalogen(ChemicalElement.BROMINE)), "1-bromocyclooctane", "bromocyclooctane"));
        namingTestCases.add(new MoleculeNamingTestInput(MoleculeNamingTestDifficulty.MONOSUBSTITUTED_RING,
                new CyclicOrganicMoleculeBuilder(4)
                        .addSubstituent(0, OrganicMoleculeBuilder.Substituent.createAlkyl(3)), "1-propylcyclobutane", "propylcyclobutane"));
        namingTestCases.add(new MoleculeNamingTestInput(MoleculeNamingTestDifficulty.MONOSUBSTITUTED_RING,
                new CyclicOrganicMoleculeBuilder(5)
                        .addSubstituent(4, OrganicMoleculeBuilder.Substituent.createAlkyl(8)), "1-octylcyclopentane", "octylcyclopentane"));
        namingTestCases.add(new MoleculeNamingTestInput(MoleculeNamingTestDifficulty.MONOSUBSTITUTED_RING,
                new CyclicOrganicMoleculeBuilder(10)
                        .addSubstituent(9, OrganicMoleculeBuilder.Substituent.createHalogen(ChemicalElement.CHLORINE)), "1-chlorocyclodecane", "chlorocyclodecane"));
        namingTestCases.add(new MoleculeNamingTestInput(MoleculeNamingTestDifficulty.MONOSUBSTITUTED_RING,
                new CyclicOrganicMoleculeBuilder(7)
                        .addSubstituent(1, OrganicMoleculeBuilder.Substituent.createAlkyl(7)), "1-heptylcycloheptane", "heptylcycloheptane"));
        namingTestCases.add(new MoleculeNamingTestInput(MoleculeNamingTestDifficulty.MONOSUBSTITUTED_RING,
                new CyclicOrganicMoleculeBuilder(9)
                        .addSubstituent(5, OrganicMoleculeBuilder.Substituent.createAlkyl(6)), "1-hexylcyclononane", "hexylcyclononane"));
        namingTestCases.add(new MoleculeNamingTestInput(MoleculeNamingTestDifficulty.MONOSUBSTITUTED_RING,
                new CyclicOrganicMoleculeBuilder(5)
                        .addSubstituent(0, OrganicMoleculeBuilder.Substituent.createHalogen(ChemicalElement.BROMINE)), "1-bromocyclopentane", "bromocyclopentane"));
        namingTestCases.add(new MoleculeNamingTestInput(MoleculeNamingTestDifficulty.MONOSUBSTITUTED_RING,
                new CyclicOrganicMoleculeBuilder(4)
                        .addSubstituent(0, OrganicMoleculeBuilder.Substituent.createAlcohol()), "cyclobutan-1-ol", "cyclobutanol"));
        namingTestCases.add(new MoleculeNamingTestInput(MoleculeNamingTestDifficulty.MONOSUBSTITUTED_RING,
                new CyclicOrganicMoleculeBuilder(9)
                        .addSubstituent(2, OrganicMoleculeBuilder.Substituent.createAlcohol()), "cyclononan-1-ol", "cyclononanol"));
        namingTestCases.add(new MoleculeNamingTestInput(MoleculeNamingTestDifficulty.MONOSUBSTITUTED_RING,
                new CyclicOrganicMoleculeBuilder(5)
                        .addSubstituent(0, OrganicMoleculeBuilder.Substituent.createCarbonyl()), "cyclopentan-1-one", "cyclopentanone"));
        namingTestCases.add(new MoleculeNamingTestInput(MoleculeNamingTestDifficulty.MONOSUBSTITUTED_RING,
                new CyclicOrganicMoleculeBuilder(6)
                        .addSubstituent(3, OrganicMoleculeBuilder.Substituent.createCarbonyl()), "cyclohexan-1-one", "cyclohexanone"));


        // Naming linear/branching alkanes with one low-priority substituent on the main chain
        namingTestCases.add(new MoleculeNamingTestInput(MoleculeNamingTestDifficulty.SINGLE_SUBSTITUENT_LINEAR,
                new LinearOrganicMoleculeBuilder(3)
                        .addSubstituent(1, OrganicMoleculeBuilder.Substituent.createAlkyl(1)), "2-methylpropane", "isobutane"));
        namingTestCases.add(new MoleculeNamingTestInput(MoleculeNamingTestDifficulty.SINGLE_SUBSTITUENT_LINEAR,
                new LinearOrganicMoleculeBuilder(1)
                        .addSubstituent(0, OrganicMoleculeBuilder.Substituent.createHalogen(ChemicalElement.CHLORINE)), "1-chloromethane", "chloromethane"));
        namingTestCases.add(new MoleculeNamingTestInput(MoleculeNamingTestDifficulty.SINGLE_SUBSTITUENT_LINEAR,
                new LinearOrganicMoleculeBuilder(5)
                        .addSubstituent(2, OrganicMoleculeBuilder.Substituent.createAlkyl(2)), "3-ethylpentane"));
        namingTestCases.add(new MoleculeNamingTestInput(MoleculeNamingTestDifficulty.SINGLE_SUBSTITUENT_LINEAR,
                new LinearOrganicMoleculeBuilder(4)
                        .addSubstituent(0, OrganicMoleculeBuilder.Substituent.createAlkyl(2))
                        .addSubstituent(3, OrganicMoleculeBuilder.Substituent.createHalogen(ChemicalElement.BROMINE)), "1-bromohexane"));
        namingTestCases.add(new MoleculeNamingTestInput(MoleculeNamingTestDifficulty.SINGLE_SUBSTITUENT_LINEAR,
                new LinearOrganicMoleculeBuilder(4)
                        .addSubstituent(1, OrganicMoleculeBuilder.Substituent.createAlkyl(1)), "2-methylbutane"));
        namingTestCases.add(new MoleculeNamingTestInput(MoleculeNamingTestDifficulty.SINGLE_SUBSTITUENT_LINEAR,
                new LinearOrganicMoleculeBuilder(3)
                        .addSubstituent(1, OrganicMoleculeBuilder.Substituent.createAlkyl(2)), "2-methylbutane"));
        namingTestCases.add(new MoleculeNamingTestInput(MoleculeNamingTestDifficulty.SINGLE_SUBSTITUENT_LINEAR,
                new LinearOrganicMoleculeBuilder(4)
                        .addSubstituent(2, OrganicMoleculeBuilder.Substituent.createAlkyl(1)), "2-methylbutane"));
        namingTestCases.add(new MoleculeNamingTestInput(MoleculeNamingTestDifficulty.SINGLE_SUBSTITUENT_LINEAR,
                new LinearOrganicMoleculeBuilder(9)
                        .addSubstituent(7, OrganicMoleculeBuilder.Substituent.createAlkyl(2)), "3-methyldecane"));
        namingTestCases.add(new MoleculeNamingTestInput(MoleculeNamingTestDifficulty.SINGLE_SUBSTITUENT_LINEAR,
                new LinearOrganicMoleculeBuilder(7)
                        .addSubstituent(6, OrganicMoleculeBuilder.Substituent.createAlkyl(2))
                        .addSubstituent(4, OrganicMoleculeBuilder.Substituent.createAlkyl(3)), "5-propylnonane"));
        namingTestCases.add(new MoleculeNamingTestInput(MoleculeNamingTestDifficulty.SINGLE_SUBSTITUENT_LINEAR,
                new LinearOrganicMoleculeBuilder(7)
                        .addSubstituent(3, OrganicMoleculeBuilder.Substituent.createAlkyl(4)), "4-propyloctane"));
        namingTestCases.add(new MoleculeNamingTestInput(MoleculeNamingTestDifficulty.SINGLE_SUBSTITUENT_LINEAR,
                new LinearOrganicMoleculeBuilder(6)
                        .addSubstituent(2, OrganicMoleculeBuilder.Substituent.createHalogen(ChemicalElement.FLUORINE)), "3-fluorohexane"));
        // ^^^ ABOVE: low-priority substituents
        // vvv BELOW: high-priority substituents
        namingTestCases.add(new MoleculeNamingTestInput(MoleculeNamingTestDifficulty.SINGLE_SUBSTITUENT_LINEAR,
                new LinearOrganicMoleculeBuilder(5)
                        .addSubstituent(2, OrganicMoleculeBuilder.Substituent.createCarbonyl()), "pentan-3-one"));
        namingTestCases.add(new MoleculeNamingTestInput(MoleculeNamingTestDifficulty.SINGLE_SUBSTITUENT_LINEAR,
                new LinearOrganicMoleculeBuilder(1)
                        .addSubstituent(0, OrganicMoleculeBuilder.Substituent.createAlcohol()), "methan-1-ol", "methanol"));
        namingTestCases.add(new MoleculeNamingTestInput(MoleculeNamingTestDifficulty.SINGLE_SUBSTITUENT_LINEAR,
                new LinearOrganicMoleculeBuilder(2)
                        .addSubstituent(0, OrganicMoleculeBuilder.Substituent.createAlcohol()), "ethan-1-ol", "ethanol"));
        namingTestCases.add(new MoleculeNamingTestInput(MoleculeNamingTestDifficulty.SINGLE_SUBSTITUENT_LINEAR,
                new LinearOrganicMoleculeBuilder(2)
                        .addSubstituent(1, OrganicMoleculeBuilder.Substituent.createAlcohol()), "ethan-1-ol", "ethanol"));
        namingTestCases.add(new MoleculeNamingTestInput(MoleculeNamingTestDifficulty.SINGLE_SUBSTITUENT_LINEAR,
                new LinearOrganicMoleculeBuilder(3)
                        .addSubstituent(1, OrganicMoleculeBuilder.Substituent.createAlcohol()), "propan-2-ol", "isopropanol"));
        namingTestCases.add(new MoleculeNamingTestInput(MoleculeNamingTestDifficulty.SINGLE_SUBSTITUENT_LINEAR,
                new LinearOrganicMoleculeBuilder(3)
                        .addSubstituent(2, OrganicMoleculeBuilder.Substituent.createAlcohol()), "propan-1-ol"));
        namingTestCases.add(new MoleculeNamingTestInput(MoleculeNamingTestDifficulty.SINGLE_SUBSTITUENT_LINEAR,
                new LinearOrganicMoleculeBuilder(6)
                        .addSubstituent(1, OrganicMoleculeBuilder.Substituent.createCarbonyl()), "hexan-2-one"));
        namingTestCases.add(new MoleculeNamingTestInput(MoleculeNamingTestDifficulty.SINGLE_SUBSTITUENT_LINEAR,
                new LinearOrganicMoleculeBuilder(6)
                        .addSubstituent(4, OrganicMoleculeBuilder.Substituent.createCarbonyl()), "hexan-2-one"));
        // ^^^ ABOVE: high-priority substituents
        // vvv BELOW: end groups
        namingTestCases.add(new MoleculeNamingTestInput(MoleculeNamingTestDifficulty.SINGLE_SUBSTITUENT_LINEAR,
                new LinearOrganicMoleculeBuilder(4)
                        .addSubstituent(0, OrganicMoleculeBuilder.Substituent.createCarbonyl()), "butanal"));
        namingTestCases.add(new MoleculeNamingTestInput(MoleculeNamingTestDifficulty.SINGLE_SUBSTITUENT_LINEAR,
                new LinearOrganicMoleculeBuilder(3)
                        .addSubstituent(2, OrganicMoleculeBuilder.Substituent.createCarbonyl()), "propanal"));
        namingTestCases.add(new MoleculeNamingTestInput(MoleculeNamingTestDifficulty.SINGLE_SUBSTITUENT_LINEAR,
                new LinearOrganicMoleculeBuilder(1)
                        .addSubstituent(0, OrganicMoleculeBuilder.Substituent.createCarbonyl()), "methanal", "formaldehyde"));
        namingTestCases.add(new MoleculeNamingTestInput(MoleculeNamingTestDifficulty.SINGLE_SUBSTITUENT_LINEAR,
                new LinearOrganicMoleculeBuilder(10)
                        .addSubstituent(9, OrganicMoleculeBuilder.Substituent.createCarbonyl()), "decanal"));
        namingTestCases.add(new MoleculeNamingTestInput(MoleculeNamingTestDifficulty.SINGLE_SUBSTITUENT_LINEAR,
                new LinearOrganicMoleculeBuilder(6)
                        .addSubstituent(0, OrganicMoleculeBuilder.Substituent.createCarbonyl()), "hexanal"));
        namingTestCases.add(new MoleculeNamingTestInput(MoleculeNamingTestDifficulty.SINGLE_SUBSTITUENT_LINEAR,
                new LinearOrganicMoleculeBuilder(1)
                        .addSubstituent(0, OrganicMoleculeBuilder.Substituent.createCarbonyl())
                        .addSubstituent(0, OrganicMoleculeBuilder.Substituent.createAlcohol()), "methanoic acid", "formic acid"));
        namingTestCases.add(new MoleculeNamingTestInput(MoleculeNamingTestDifficulty.SINGLE_SUBSTITUENT_LINEAR,
                new LinearOrganicMoleculeBuilder(2)
                        .addSubstituent(0, OrganicMoleculeBuilder.Substituent.createCarbonyl())
                        .addSubstituent(0, OrganicMoleculeBuilder.Substituent.createAlcohol()), "ethanoic acid", "acetic acid"));
        namingTestCases.add(new MoleculeNamingTestInput(MoleculeNamingTestDifficulty.SINGLE_SUBSTITUENT_LINEAR,
                new LinearOrganicMoleculeBuilder(2)
                        .addSubstituent(1, OrganicMoleculeBuilder.Substituent.createCarbonyl())
                        .addSubstituent(1, OrganicMoleculeBuilder.Substituent.createAlcohol()), "ethanoic acid", "acetic acid"));
        namingTestCases.add(new MoleculeNamingTestInput(MoleculeNamingTestDifficulty.SINGLE_SUBSTITUENT_LINEAR,
                new LinearOrganicMoleculeBuilder(8)
                        .addSubstituent(7, OrganicMoleculeBuilder.Substituent.createCarbonyl())
                        .addSubstituent(7, OrganicMoleculeBuilder.Substituent.createAlcohol()), "octanoic acid"));
        namingTestCases.add(new MoleculeNamingTestInput(MoleculeNamingTestDifficulty.SINGLE_SUBSTITUENT_LINEAR,
                new LinearOrganicMoleculeBuilder(5)
                        .addSubstituent(0, OrganicMoleculeBuilder.Substituent.createCarbonyl())
                        .addSubstituent(0, OrganicMoleculeBuilder.Substituent.createAlcohol()), "pentanoic acid"));
        namingTestCases.add(new MoleculeNamingTestInput(MoleculeNamingTestDifficulty.SINGLE_SUBSTITUENT_LINEAR,
                new LinearOrganicMoleculeBuilder(3)
                        .addSubstituent(0, OrganicMoleculeBuilder.Substituent.createCarbonyl())
                        .addSubstituent(0, OrganicMoleculeBuilder.Substituent.createAlcohol()), "propanoic acid"));
        namingTestCases.add(new MoleculeNamingTestInput(MoleculeNamingTestDifficulty.SINGLE_SUBSTITUENT_LINEAR,
                new LinearOrganicMoleculeBuilder(4)
                        .addSubstituent(3, OrganicMoleculeBuilder.Substituent.createCarbonyl())
                        .addSubstituent(3, OrganicMoleculeBuilder.Substituent.createAlcohol()), "butanoic acid"));

        // Naming structures with multiple substituents but no difficult priority tiebreaks
        // This is extra credit!
        namingTestCases.add(new MoleculeNamingTestInput(MoleculeNamingTestDifficulty.MULTIPLE_SUBSTITUENTS,
                new LinearOrganicMoleculeBuilder(5)
                        .addSubstituent(1, OrganicMoleculeBuilder.Substituent.createAlkyl(1))
                        .addSubstituent(3, OrganicMoleculeBuilder.Substituent.createAlkyl(1)), "2,4-dimethylpentane"));
        namingTestCases.add(new MoleculeNamingTestInput(MoleculeNamingTestDifficulty.MULTIPLE_SUBSTITUENTS,
                new LinearOrganicMoleculeBuilder(3)
                        .addSubstituent(1, OrganicMoleculeBuilder.Substituent.createAlkyl(1))
                        .addSubstituent(1, OrganicMoleculeBuilder.Substituent.createAlkyl(1)), "2,2-dimethylpropane", "neopentane"));
        namingTestCases.add(new MoleculeNamingTestInput(MoleculeNamingTestDifficulty.MULTIPLE_SUBSTITUENTS,
                new LinearOrganicMoleculeBuilder(6)
                        .addSubstituent(1, OrganicMoleculeBuilder.Substituent.createAlkyl(1))
                        .addSubstituent(2, OrganicMoleculeBuilder.Substituent.createAlkyl(2)), "3-ethyl-2-methylhexane"));
        namingTestCases.add(new MoleculeNamingTestInput(MoleculeNamingTestDifficulty.MULTIPLE_SUBSTITUENTS,
                new LinearOrganicMoleculeBuilder(2)
                        .addSubstituent(0, OrganicMoleculeBuilder.Substituent.createAlcohol())
                        .addSubstituent(1, OrganicMoleculeBuilder.Substituent.createAlcohol()), "ethane-1,2-diol"));
        namingTestCases.add(new MoleculeNamingTestInput(MoleculeNamingTestDifficulty.MULTIPLE_SUBSTITUENTS,
                new LinearOrganicMoleculeBuilder(1)
                        .addSubstituent(0, OrganicMoleculeBuilder.Substituent.createAlcohol())
                        .addSubstituent(0, OrganicMoleculeBuilder.Substituent.createAlcohol())
                        .addSubstituent(0, OrganicMoleculeBuilder.Substituent.createAlcohol())
                        .addSubstituent(0, OrganicMoleculeBuilder.Substituent.createAlcohol()), "methane-1,1,1,1-tetrol", "methanetetrol"));
        namingTestCases.add(new MoleculeNamingTestInput(MoleculeNamingTestDifficulty.MULTIPLE_SUBSTITUENTS,
                new LinearOrganicMoleculeBuilder(4)
                        .addSubstituent(2, OrganicMoleculeBuilder.Substituent.createCarbonyl())
                        .addSubstituent(1, OrganicMoleculeBuilder.Substituent.createCarbonyl()), "butane-2,3-dione"));
        namingTestCases.add(new MoleculeNamingTestInput(MoleculeNamingTestDifficulty.MULTIPLE_SUBSTITUENTS,
                new LinearOrganicMoleculeBuilder(8)
                        .addSubstituent(2, OrganicMoleculeBuilder.Substituent.createCarbonyl())
                        .addSubstituent(3, OrganicMoleculeBuilder.Substituent.createCarbonyl())
                        .addSubstituent(4, OrganicMoleculeBuilder.Substituent.createCarbonyl())
                        .addSubstituent(5, OrganicMoleculeBuilder.Substituent.createCarbonyl())
                        .addSubstituent(6, OrganicMoleculeBuilder.Substituent.createCarbonyl()), "octane-2,3,4,5,6-pentone"));
        namingTestCases.add(new MoleculeNamingTestInput(MoleculeNamingTestDifficulty.MULTIPLE_SUBSTITUENTS,
                new LinearOrganicMoleculeBuilder(8)
                        .addSubstituent(5, OrganicMoleculeBuilder.Substituent.createAlkyl(1))
                        .addSubstituent(4, OrganicMoleculeBuilder.Substituent.createAlkyl(1))
                        .addSubstituent(3, OrganicMoleculeBuilder.Substituent.createAlkyl(2))
                        .addSubstituent(2, OrganicMoleculeBuilder.Substituent.createAlkyl(1))
                        .addSubstituent(1, OrganicMoleculeBuilder.Substituent.createAlkyl(1)), "4-ethyl-2,3,5,6-tetramethyloctane"));
        namingTestCases.add(new MoleculeNamingTestInput(MoleculeNamingTestDifficulty.MULTIPLE_SUBSTITUENTS,
                new LinearOrganicMoleculeBuilder(6)
                        .addSubstituent(3, OrganicMoleculeBuilder.Substituent.createAlkyl(2))
                        .addSubstituent(4, OrganicMoleculeBuilder.Substituent.createAlkyl(2)), "4-ethyl-3-methylheptane"));
        namingTestCases.add(new MoleculeNamingTestInput(MoleculeNamingTestDifficulty.MULTIPLE_SUBSTITUENTS,
                new LinearOrganicMoleculeBuilder(8)
                        .addSubstituent(2, OrganicMoleculeBuilder.Substituent.createHalogen(ChemicalElement.BROMINE))
                        .addSubstituent(6, OrganicMoleculeBuilder.Substituent.createHalogen(ChemicalElement.FLUORINE))
                        .addSubstituent(3, OrganicMoleculeBuilder.Substituent.createAlkyl(3)), "6-bromo-2-fluoro-5-propyloctane"));
        namingTestCases.add(new MoleculeNamingTestInput(MoleculeNamingTestDifficulty.MULTIPLE_SUBSTITUENTS,
                new LinearOrganicMoleculeBuilder(2)
                        .addSubstituent(0, OrganicMoleculeBuilder.Substituent.createHalogen(ChemicalElement.FLUORINE))
                        .addSubstituent(1, OrganicMoleculeBuilder.Substituent.createHalogen(ChemicalElement.FLUORINE)), "1,2-difluoroethane"));
        namingTestCases.add(new MoleculeNamingTestInput(MoleculeNamingTestDifficulty.MULTIPLE_SUBSTITUENTS,
                new LinearOrganicMoleculeBuilder(6)
                        .addSubstituent(2, OrganicMoleculeBuilder.Substituent.createAlkyl(2))
                        .addSubstituent(3, OrganicMoleculeBuilder.Substituent.createAlkyl(2))
                        .addSubstituent(5, OrganicMoleculeBuilder.Substituent.createHalogen(ChemicalElement.BROMINE)), "1-bromo-3,4-diethylhexane"));
        namingTestCases.add(new MoleculeNamingTestInput(MoleculeNamingTestDifficulty.MULTIPLE_SUBSTITUENTS,
                new LinearOrganicMoleculeBuilder(4)
                        .addSubstituent(0, OrganicMoleculeBuilder.Substituent.createHalogen(ChemicalElement.FLUORINE))
                        .addSubstituent(1, OrganicMoleculeBuilder.Substituent.createHalogen(ChemicalElement.BROMINE))
                        .addSubstituent(1, OrganicMoleculeBuilder.Substituent.createAlkyl(1))
                        .addSubstituent(2, OrganicMoleculeBuilder.Substituent.createHalogen(ChemicalElement.CHLORINE)), "2-bromo-3-chloro-1-fluoro-2-methylbutane"));
        namingTestCases.add(new MoleculeNamingTestInput(MoleculeNamingTestDifficulty.MULTIPLE_SUBSTITUENTS,
                new CyclicOrganicMoleculeBuilder(4)
                        .addSubstituent(0, OrganicMoleculeBuilder.Substituent.createAlkyl(1))
                        .addSubstituent(1, OrganicMoleculeBuilder.Substituent.createAlkyl(1))
                        .addSubstituent(2, OrganicMoleculeBuilder.Substituent.createAlkyl(1))
                        .addSubstituent(3, OrganicMoleculeBuilder.Substituent.createAlkyl(1)), "1,2,3,4-tetramethylcyclobutane"));
        namingTestCases.add(new MoleculeNamingTestInput(MoleculeNamingTestDifficulty.MULTIPLE_SUBSTITUENTS,
                new CyclicOrganicMoleculeBuilder(9)
                        .addSubstituent(2, OrganicMoleculeBuilder.Substituent.createHalogen(ChemicalElement.CHLORINE))
                        .addSubstituent(2, OrganicMoleculeBuilder.Substituent.createAlkyl(8)), "1-chloro-1-octylcyclononane"));
        namingTestCases.add(new MoleculeNamingTestInput(MoleculeNamingTestDifficulty.MULTIPLE_SUBSTITUENTS,
                new CyclicOrganicMoleculeBuilder(6)
                        .addSubstituent(1, OrganicMoleculeBuilder.Substituent.createAlkyl(4))
                        .addSubstituent(4, OrganicMoleculeBuilder.Substituent.createAlkyl(4)), "1,4-dibutylcyclohexane"));
        namingTestCases.add(new MoleculeNamingTestInput(MoleculeNamingTestDifficulty.MULTIPLE_SUBSTITUENTS,
                new CyclicOrganicMoleculeBuilder(3)
                        .addSubstituent(0, OrganicMoleculeBuilder.Substituent.createAlcohol())
                        .addSubstituent(1, OrganicMoleculeBuilder.Substituent.createAlcohol())
                        .addSubstituent(2, OrganicMoleculeBuilder.Substituent.createAlcohol())
                        .addSubstituent(0, OrganicMoleculeBuilder.Substituent.createAlcohol())
                        .addSubstituent(1, OrganicMoleculeBuilder.Substituent.createAlcohol())
                        .addSubstituent(2, OrganicMoleculeBuilder.Substituent.createAlcohol()), "cyclopropane-1,1,2,2,3,3-hexol"));
        namingTestCases.add(new MoleculeNamingTestInput(MoleculeNamingTestDifficulty.MULTIPLE_SUBSTITUENTS,
                new CyclicOrganicMoleculeBuilder(5)
                        .addSubstituent(0, OrganicMoleculeBuilder.Substituent.createCarbonyl())
                        .addSubstituent(1, OrganicMoleculeBuilder.Substituent.createCarbonyl())
                        .addSubstituent(2, OrganicMoleculeBuilder.Substituent.createCarbonyl())
                        .addSubstituent(3, OrganicMoleculeBuilder.Substituent.createCarbonyl())
                        .addSubstituent(4, OrganicMoleculeBuilder.Substituent.createCarbonyl()), "cyclopentane-1,2,3,4,5-pentone"));

        // Naming structures with multiple substituents requiring priority tiebreaks
        // This is extra credit and very difficult!
        namingTestCases.add(new MoleculeNamingTestInput(MoleculeNamingTestDifficulty.PRIORITY_TIEBREAK,
                new LinearOrganicMoleculeBuilder(2)
                        .addSubstituent(0, OrganicMoleculeBuilder.Substituent.createAlcohol())
                        .addSubstituent(1, OrganicMoleculeBuilder.Substituent.createHalogen(ChemicalElement.CHLORINE))
                        .addSubstituent(1, OrganicMoleculeBuilder.Substituent.createHalogen(ChemicalElement.BROMINE)), "2-bromo-2-chloroethan-1-ol"));
        namingTestCases.add(new MoleculeNamingTestInput(MoleculeNamingTestDifficulty.PRIORITY_TIEBREAK,
                new LinearOrganicMoleculeBuilder(4)
                        .addSubstituent(3, OrganicMoleculeBuilder.Substituent.createCarbonyl())
                        .addSubstituent(3, OrganicMoleculeBuilder.Substituent.createAlcohol())
                        .addSubstituent(0, OrganicMoleculeBuilder.Substituent.createAlkyl(1))
                        .addSubstituent(0, OrganicMoleculeBuilder.Substituent.createAlkyl(1)), "4-methylpentanoic acid"));
        namingTestCases.add(new MoleculeNamingTestInput(MoleculeNamingTestDifficulty.PRIORITY_TIEBREAK,
                new LinearOrganicMoleculeBuilder(7)
                        .addSubstituent(6, OrganicMoleculeBuilder.Substituent.createCarbonyl())
                        .addSubstituent(2, OrganicMoleculeBuilder.Substituent.createHalogen(ChemicalElement.BROMINE)), "5-bromoheptanal"));
        namingTestCases.add(new MoleculeNamingTestInput(MoleculeNamingTestDifficulty.PRIORITY_TIEBREAK,
                new LinearOrganicMoleculeBuilder(4)
                        .addSubstituent(1, OrganicMoleculeBuilder.Substituent.createAlcohol())
                        .addSubstituent(3, OrganicMoleculeBuilder.Substituent.createHalogen(ChemicalElement.FLUORINE)), "4-fluorobutan-2-ol"));
        namingTestCases.add(new MoleculeNamingTestInput(MoleculeNamingTestDifficulty.PRIORITY_TIEBREAK,
                new LinearOrganicMoleculeBuilder(3)
                        .addSubstituent(0, OrganicMoleculeBuilder.Substituent.createAlcohol())
                        .addSubstituent(1, OrganicMoleculeBuilder.Substituent.createAlkyl(5))
                        .addSubstituent(2, OrganicMoleculeBuilder.Substituent.createAlcohol()), "2-pentylpropane-1,3-diol"));
        namingTestCases.add(new MoleculeNamingTestInput(MoleculeNamingTestDifficulty.PRIORITY_TIEBREAK,
                new LinearOrganicMoleculeBuilder(3)
                        .addSubstituent(0, OrganicMoleculeBuilder.Substituent.createHalogen(ChemicalElement.FLUORINE))
                        .addSubstituent(0, OrganicMoleculeBuilder.Substituent.createHalogen(ChemicalElement.FLUORINE))
                        .addSubstituent(1, OrganicMoleculeBuilder.Substituent.createHalogen(ChemicalElement.BROMINE))
                        .addSubstituent(2, OrganicMoleculeBuilder.Substituent.createHalogen(ChemicalElement.FLUORINE)), "2-bromo-1,1,3-trifluoropropane"));
        namingTestCases.add(new MoleculeNamingTestInput(MoleculeNamingTestDifficulty.PRIORITY_TIEBREAK,
                new LinearOrganicMoleculeBuilder(4)
                        .addSubstituent(0, OrganicMoleculeBuilder.Substituent.createHalogen(ChemicalElement.FLUORINE))
                        .addSubstituent(1, OrganicMoleculeBuilder.Substituent.createHalogen(ChemicalElement.FLUORINE))
                        .addSubstituent(1, OrganicMoleculeBuilder.Substituent.createAlkyl(1))
                        .addSubstituent(3, OrganicMoleculeBuilder.Substituent.createCarbonyl())
                        .addSubstituent(3, OrganicMoleculeBuilder.Substituent.createAlcohol()), "3,4-difluoro-3-methylbutanoic acid"));
        namingTestCases.add(new MoleculeNamingTestInput(MoleculeNamingTestDifficulty.PRIORITY_TIEBREAK,
                new LinearOrganicMoleculeBuilder(3)
                        .addSubstituent(0, OrganicMoleculeBuilder.Substituent.createHalogen(ChemicalElement.BROMINE))
                        .addSubstituent(2, OrganicMoleculeBuilder.Substituent.createHalogen(ChemicalElement.CHLORINE)), "1-bromo-3-chloropropane"));
        namingTestCases.add(new MoleculeNamingTestInput(MoleculeNamingTestDifficulty.PRIORITY_TIEBREAK,
                new LinearOrganicMoleculeBuilder(3)
                        .addSubstituent(2, OrganicMoleculeBuilder.Substituent.createHalogen(ChemicalElement.BROMINE))
                        .addSubstituent(0, OrganicMoleculeBuilder.Substituent.createHalogen(ChemicalElement.CHLORINE)), "1-bromo-3-chloropropane"));
        namingTestCases.add(new MoleculeNamingTestInput(MoleculeNamingTestDifficulty.PRIORITY_TIEBREAK,
                new LinearOrganicMoleculeBuilder(3)
                        .addSubstituent(0, OrganicMoleculeBuilder.Substituent.createHalogen(ChemicalElement.BROMINE))
                        .addSubstituent(2, OrganicMoleculeBuilder.Substituent.createHalogen(ChemicalElement.CHLORINE))
                        .addSubstituent(2, OrganicMoleculeBuilder.Substituent.createAlcohol()), "3-bromo-1-chloropropan-1-ol"));
        namingTestCases.add(new MoleculeNamingTestInput(MoleculeNamingTestDifficulty.PRIORITY_TIEBREAK,
                new LinearOrganicMoleculeBuilder(2)
                        .addSubstituent(0, OrganicMoleculeBuilder.Substituent.createHalogen(ChemicalElement.BROMINE))
                        .addSubstituent(1, OrganicMoleculeBuilder.Substituent.createHalogen(ChemicalElement.BROMINE))
                        .addSubstituent(0, OrganicMoleculeBuilder.Substituent.createHalogen(ChemicalElement.CHLORINE))
                        .addSubstituent(1, OrganicMoleculeBuilder.Substituent.createHalogen(ChemicalElement.FLUORINE)), "1,2-dibromo-1-chloro-2-fluoroethane"));
        namingTestCases.add(new MoleculeNamingTestInput(MoleculeNamingTestDifficulty.PRIORITY_TIEBREAK,
                new LinearOrganicMoleculeBuilder(2)
                        .addSubstituent(0, OrganicMoleculeBuilder.Substituent.createHalogen(ChemicalElement.BROMINE))
                        .addSubstituent(1, OrganicMoleculeBuilder.Substituent.createHalogen(ChemicalElement.BROMINE))
                        .addSubstituent(1, OrganicMoleculeBuilder.Substituent.createHalogen(ChemicalElement.CHLORINE))
                        .addSubstituent(0, OrganicMoleculeBuilder.Substituent.createHalogen(ChemicalElement.FLUORINE)), "1,2-dibromo-1-chloro-2-fluoroethane"));
        namingTestCases.add(new MoleculeNamingTestInput(MoleculeNamingTestDifficulty.PRIORITY_TIEBREAK,
                new LinearOrganicMoleculeBuilder(4)
                        .addSubstituent(0, OrganicMoleculeBuilder.Substituent.createHalogen(ChemicalElement.CHLORINE))
                        .addSubstituent(0, OrganicMoleculeBuilder.Substituent.createHalogen(ChemicalElement.CHLORINE))
                        .addSubstituent(0, OrganicMoleculeBuilder.Substituent.createHalogen(ChemicalElement.CHLORINE))
                        .addSubstituent(2, OrganicMoleculeBuilder.Substituent.createCarbonyl()), "4,4,4-trichlorobutan-2-one"));
        namingTestCases.add(new MoleculeNamingTestInput(MoleculeNamingTestDifficulty.PRIORITY_TIEBREAK,
                new LinearOrganicMoleculeBuilder(5)
                        .addSubstituent(1, OrganicMoleculeBuilder.Substituent.createHalogen(ChemicalElement.BROMINE))
                        .addSubstituent(2, OrganicMoleculeBuilder.Substituent.createAlkyl(1))
                        .addSubstituent(3, OrganicMoleculeBuilder.Substituent.createAlkyl(3))
                        .addSubstituent(4, OrganicMoleculeBuilder.Substituent.createCarbonyl()), "4-bromo-3-methyl-2-propylpentanal"));
        namingTestCases.add(new MoleculeNamingTestInput(MoleculeNamingTestDifficulty.PRIORITY_TIEBREAK,
                new CyclicOrganicMoleculeBuilder(3)
                        .addSubstituent(0, OrganicMoleculeBuilder.Substituent.createAlcohol())
                        .addSubstituent(2, OrganicMoleculeBuilder.Substituent.createAlcohol()), "cyclopropane-1,2-diol"));
        namingTestCases.add(new MoleculeNamingTestInput(MoleculeNamingTestDifficulty.PRIORITY_TIEBREAK,
                new CyclicOrganicMoleculeBuilder(3)
                        .addSubstituent(1, OrganicMoleculeBuilder.Substituent.createAlcohol())
                        .addSubstituent(2, OrganicMoleculeBuilder.Substituent.createAlcohol()), "cyclopropane-1,2-diol"));
        namingTestCases.add(new MoleculeNamingTestInput(MoleculeNamingTestDifficulty.PRIORITY_TIEBREAK,
                new CyclicOrganicMoleculeBuilder(5)
                        .addSubstituent(1, OrganicMoleculeBuilder.Substituent.createHalogen(ChemicalElement.BROMINE))
                        .addSubstituent(4, OrganicMoleculeBuilder.Substituent.createHalogen(ChemicalElement.BROMINE)), "1,3-dibromocyclopentane"));
        namingTestCases.add(new MoleculeNamingTestInput(MoleculeNamingTestDifficulty.PRIORITY_TIEBREAK,
                new CyclicOrganicMoleculeBuilder(6)
                        .addSubstituent(0, OrganicMoleculeBuilder.Substituent.createAlkyl(1))
                        .addSubstituent(1, OrganicMoleculeBuilder.Substituent.createHalogen(ChemicalElement.BROMINE)), "1-bromo-2-methylcyclohexane"));
        namingTestCases.add(new MoleculeNamingTestInput(MoleculeNamingTestDifficulty.PRIORITY_TIEBREAK,
                new CyclicOrganicMoleculeBuilder(6)
                        .addSubstituent(0, OrganicMoleculeBuilder.Substituent.createAlkyl(2))
                        .addSubstituent(0, OrganicMoleculeBuilder.Substituent.createAlkyl(2))
                        .addSubstituent(1, OrganicMoleculeBuilder.Substituent.createHalogen(ChemicalElement.BROMINE)), "2-bromo-1,1-diethylcyclohexane"));
        namingTestCases.add(new MoleculeNamingTestInput(MoleculeNamingTestDifficulty.PRIORITY_TIEBREAK,
                new CyclicOrganicMoleculeBuilder(5)
                        .addSubstituent(2, OrganicMoleculeBuilder.Substituent.createAlcohol())
                        .addSubstituent(0, OrganicMoleculeBuilder.Substituent.createAlkyl(5)), "3-pentylcyclopentan-1-ol"));
        namingTestCases.add(new MoleculeNamingTestInput(MoleculeNamingTestDifficulty.PRIORITY_TIEBREAK,
                new CyclicOrganicMoleculeBuilder(5)
                        .addSubstituent(0, OrganicMoleculeBuilder.Substituent.createAlkyl(3))
                        .addSubstituent(2, OrganicMoleculeBuilder.Substituent.createAlcohol())
                        .addSubstituent(3, OrganicMoleculeBuilder.Substituent.createAlcohol()), "4-propylcyclopentane-1,2-diol"));
        namingTestCases.add(new MoleculeNamingTestInput(MoleculeNamingTestDifficulty.PRIORITY_TIEBREAK,
                new CyclicOrganicMoleculeBuilder(4)
                        .addSubstituent(1, OrganicMoleculeBuilder.Substituent.createHalogen(ChemicalElement.BROMINE))
                        .addSubstituent(1, OrganicMoleculeBuilder.Substituent.createHalogen(ChemicalElement.BROMINE))
                        .addSubstituent(2, OrganicMoleculeBuilder.Substituent.createCarbonyl())
                        .addSubstituent(3, OrganicMoleculeBuilder.Substituent.createAlkyl(1)), "2,2-dibromo-4-methylcyclobutan-1-one"));
        namingTestCases.add(new MoleculeNamingTestInput(MoleculeNamingTestDifficulty.PRIORITY_TIEBREAK,
                new CyclicOrganicMoleculeBuilder(4)
                        .addSubstituent(1, OrganicMoleculeBuilder.Substituent.createHalogen(ChemicalElement.BROMINE))
                        .addSubstituent(3, OrganicMoleculeBuilder.Substituent.createHalogen(ChemicalElement.BROMINE))
                        .addSubstituent(2, OrganicMoleculeBuilder.Substituent.createCarbonyl())
                        .addSubstituent(3, OrganicMoleculeBuilder.Substituent.createAlkyl(1))
                        .addSubstituent(0, OrganicMoleculeBuilder.Substituent.createCarbonyl()), "2,4-dibromo-2-methylcyclobutane-1,3-dione"));
        namingTestCases.add(new MoleculeNamingTestInput(MoleculeNamingTestDifficulty.PRIORITY_TIEBREAK,
                new CyclicOrganicMoleculeBuilder(7)
                        .addSubstituent(1, OrganicMoleculeBuilder.Substituent.createCarbonyl())
                        .addSubstituent(3, OrganicMoleculeBuilder.Substituent.createHalogen(ChemicalElement.CHLORINE))
                        .addSubstituent(4, OrganicMoleculeBuilder.Substituent.createCarbonyl())
                        .addSubstituent(5, OrganicMoleculeBuilder.Substituent.createAlkyl(2))
                        .addSubstituent(5, OrganicMoleculeBuilder.Substituent.createHalogen(ChemicalElement.FLUORINE)), "2-chloro-7-ethyl-7-fluorocycloheptane-1,4-dione"));
        namingTestCases.add(new MoleculeNamingTestInput(MoleculeNamingTestDifficulty.PRIORITY_TIEBREAK,
                new CyclicOrganicMoleculeBuilder(8)
                        .addSubstituent(0, OrganicMoleculeBuilder.Substituent.createAlkyl(1))
                        .addSubstituent(0, OrganicMoleculeBuilder.Substituent.createAlkyl(2))
                        .addSubstituent(1, OrganicMoleculeBuilder.Substituent.createAlcohol())
                        .addSubstituent(3, OrganicMoleculeBuilder.Substituent.createAlcohol())
                        .addSubstituent(5, OrganicMoleculeBuilder.Substituent.createAlcohol())
                        .addSubstituent(5, OrganicMoleculeBuilder.Substituent.createAlcohol())
                        .addSubstituent(6, OrganicMoleculeBuilder.Substituent.createAlkyl(3)), "6-ethyl-6-methyl-8-propylcyclooctane-1,1,3,5-tetrol"));
        namingTestCases.add(new MoleculeNamingTestInput(MoleculeNamingTestDifficulty.PRIORITY_TIEBREAK,
                new CyclicOrganicMoleculeBuilder(3)
                        .addSubstituent(0, OrganicMoleculeBuilder.Substituent.createHalogen(ChemicalElement.CHLORINE))
                        .addSubstituent(1, OrganicMoleculeBuilder.Substituent.createHalogen(ChemicalElement.FLUORINE))
                        .addSubstituent(2, OrganicMoleculeBuilder.Substituent.createHalogen(ChemicalElement.BROMINE)), "1-bromo-2-chloro-3-fluorocyclopropane"));
        namingTestCases.add(new MoleculeNamingTestInput(MoleculeNamingTestDifficulty.PRIORITY_TIEBREAK,
                new CyclicOrganicMoleculeBuilder(3)
                        .addSubstituent(0, OrganicMoleculeBuilder.Substituent.createHalogen(ChemicalElement.CHLORINE))
                        .addSubstituent(1, OrganicMoleculeBuilder.Substituent.createHalogen(ChemicalElement.BROMINE))
                        .addSubstituent(2, OrganicMoleculeBuilder.Substituent.createHalogen(ChemicalElement.FLUORINE)), "1-bromo-2-chloro-3-fluorocyclopropane"));
        namingTestCases.add(new MoleculeNamingTestInput(MoleculeNamingTestDifficulty.PRIORITY_TIEBREAK,
                new CyclicOrganicMoleculeBuilder(3)
                        .addSubstituent(0, OrganicMoleculeBuilder.Substituent.createHalogen(ChemicalElement.CHLORINE))
                        .addSubstituent(0, OrganicMoleculeBuilder.Substituent.createHalogen(ChemicalElement.BROMINE))
                        .addSubstituent(1, OrganicMoleculeBuilder.Substituent.createHalogen(ChemicalElement.FLUORINE))
                        .addSubstituent(2, OrganicMoleculeBuilder.Substituent.createHalogen(ChemicalElement.BROMINE)), "1,2-dibromo-1-chloro-3-fluorocyclopropane"));
        namingTestCases.add(new MoleculeNamingTestInput(MoleculeNamingTestDifficulty.PRIORITY_TIEBREAK,
                new CyclicOrganicMoleculeBuilder(3)
                        .addSubstituent(0, OrganicMoleculeBuilder.Substituent.createHalogen(ChemicalElement.CHLORINE))
                        .addSubstituent(0, OrganicMoleculeBuilder.Substituent.createHalogen(ChemicalElement.BROMINE))
                        .addSubstituent(1, OrganicMoleculeBuilder.Substituent.createHalogen(ChemicalElement.FLUORINE))
                        .addSubstituent(1, OrganicMoleculeBuilder.Substituent.createAlcohol())
                        .addSubstituent(2, OrganicMoleculeBuilder.Substituent.createHalogen(ChemicalElement.BROMINE)), "2,3-dibromo-2-chloro-1-fluorocyclopropan-1-ol"));
    }

    /*

    ^^^^^^^^^^^^^^^^^^^
    ABOVE: GRADED TESTS

    The remaining tests are to help you test your helper functions.
    They are not graded; you may ignore them if you know what you're doing
    and your set of helper functions is incompatible with ours.

    IMPORTANT: Passing these does not guarantee correct implementations!
    They're here to help you test first steps. Authoritative evaluation is
    done by the graded test cases.

    Some of these tests rely on the order in which OrganicMoleculeBuilder
    attaches neighbors to each atom. You should not do this in your code;
    it's finicky and not guaranteed to work. (In general, relying on undocumented
    behavior of other people's code is bad.)

    Note that these don't necessarily check functionality required for getting extra credit -
    for that, you're on your own.

    BELOW: HELPER TESTS
    vvvvvvvvvvvvvvvvvvv

     */

    /** getAllAtoms returns a collection of all the atoms in the molecule */
    @Test(timeout = HELPER_TEST_TIMEOUT)
    public void helpGetAllAtoms() {
        MoleculeAnalyzer helium = new MoleculeAnalyzer(new BondedAtom(ChemicalElement.HELIUM, new BondedAtom[0], new int[0]));
        Assert.assertEquals("getAllAtoms failed on a one-atom molecule", 1, helium.getAllAtoms().size());

        MoleculeAnalyzer methane = new MoleculeAnalyzer(new LinearOrganicMoleculeBuilder(1).build());
        Assert.assertEquals("getAllAtoms failed on a 1-carbon molecule", 5, methane.getAllAtoms().size());
        Assert.assertEquals("getAllAtoms on methane included duplicate atoms", 5, methane.getAllAtoms().stream().distinct().count());

        MoleculeAnalyzer butane = new MoleculeAnalyzer(new LinearOrganicMoleculeBuilder(4).build());
        Assert.assertEquals("getAllAtoms failed on a 4-carbon molecule", 14, butane.getAllAtoms().size());
        Assert.assertEquals("getAllAtoms on butane included duplicate atoms", 14, butane.getAllAtoms().stream().distinct().count());

        MoleculeAnalyzer isopropanol = new MoleculeAnalyzer(new LinearOrganicMoleculeBuilder(3)
                .addSubstituent(1, OrganicMoleculeBuilder.Substituent.createAlcohol()).build());
        Assert.assertEquals("getAllAtoms failed on a chain with a substituent", 12, isopropanol.getAllAtoms().size());
        Assert.assertEquals("getAllAtoms on propan-2-ol included duplicate atoms", 12, isopropanol.getAllAtoms().stream().distinct().count());

        MoleculeAnalyzer ethoxide = new MoleculeAnalyzer(new LinearOrganicMoleculeBuilder(2)
                .addSubstituent(0, OrganicMoleculeBuilder.Substituent.createAlcohol(0)).build());
        Assert.assertEquals("getAllAtoms failed on a charged molecule", 8, ethoxide.getAllAtoms().size());
        Assert.assertEquals("getAllAtoms on ionized ethanol included duplicate atoms", 8, ethoxide.getAllAtoms().stream().distinct().count());

        MoleculeAnalyzer cyclopropane = new MoleculeAnalyzer(new CyclicOrganicMoleculeBuilder(3).build());
        Assert.assertEquals("getAllAtoms failed on a cyclic molecule", 9, cyclopropane.getAllAtoms().size());
        Assert.assertEquals("getAllAtoms on cyclopropane included duplicate atoms", 9, cyclopropane.getAllAtoms().stream().distinct().count());

        MoleculeAnalyzer ethylcyclobutane = new MoleculeAnalyzer(new CyclicOrganicMoleculeBuilder(4)
                .addSubstituent(2, OrganicMoleculeBuilder.Substituent.createAlkyl(2)).build());
        Assert.assertEquals("getAllAtoms failed on a substituted cyclic molecule", 18, ethylcyclobutane.getAllAtoms().size());
        Assert.assertEquals("getAllAtoms on ethylcyclobutane included duplicate atoms", 18, ethylcyclobutane.getAllAtoms().stream().distinct().count());
    }

    /** getTips finds all the tip carbons in the molecule */
    @SuppressWarnings("ConstantConditions")
    @Test(timeout = HELPER_TEST_TIMEOUT)
    public void helpGetTips() {
        List<BondedAtom> ethaneTips = new MoleculeAnalyzer(new LinearOrganicMoleculeBuilder(2).build()).getTips();
        Assert.assertEquals("getTips didn't find the tips of a 2-carbon molecule", 2, ethaneTips.size());
        Assert.assertTrue("getTips returned non-carbon atoms", ethaneTips.get(0).isCarbon() && ethaneTips.get(1).isCarbon());

        BondedAtom propanal = new LinearOrganicMoleculeBuilder(3)
                .addSubstituent(0, OrganicMoleculeBuilder.Substituent.createCarbonyl()).build();
        List<BondedAtom> propaneTips = new MoleculeAnalyzer(propanal).getTips();
        Assert.assertEquals("getTips didn't find the tips of a substituted 3-carbon molecule", 2, propaneTips.size());
        Assert.assertTrue("getTips missed the first backbone carbon", propaneTips.contains(propanal));
        Assert.assertTrue("getTips missed the last backbone carbon", propaneTips.contains(propanal.getConnectedAtom(1).getConnectedAtom(1)));

        List<BondedAtom> methaneTips = new MoleculeAnalyzer(new LinearOrganicMoleculeBuilder(1).build()).getTips();
        Assert.assertEquals("getTips didn't recognize a lone carbon as a tip", 1, methaneTips.size());

        BondedAtom isopentane = new LinearOrganicMoleculeBuilder(4)
                .addSubstituent(2, OrganicMoleculeBuilder.Substituent.createAlkyl(1)).build();
        List<BondedAtom> isopentaneTips = new MoleculeAnalyzer(isopentane).getTips();
        Assert.assertEquals("getTips failed on a branched linear molecule", 3, isopentaneTips.size());
        Assert.assertEquals("getTips on 2-methylbutane included duplicate atoms", 3, isopentaneTips.stream().distinct().count());
        final String isopentaneFail = "getTips misidentified the tips of 2-methylbutane";
        Assert.assertTrue(isopentaneFail, isopentaneTips.contains(isopentane));
        Assert.assertTrue(isopentaneFail, isopentaneTips.contains(isopentane.getConnectedAtom(1).getConnectedAtom(1).getConnectedAtom(1)));
        Assert.assertTrue(isopentaneFail, isopentaneTips.contains(isopentane.getConnectedAtom(1).getConnectedAtom(1).getConnectedAtom(2)));

        List<BondedAtom> cyclohexaneTips = new MoleculeAnalyzer(new CyclicOrganicMoleculeBuilder(6).build()).getTips();
        Assert.assertEquals("getTips found tips on an unsubstituted ring", 0, cyclohexaneTips.size());

        List<BondedAtom> methylcyclopropaneTips = new MoleculeAnalyzer(new CyclicOrganicMoleculeBuilder(3)
                .addSubstituent(0, OrganicMoleculeBuilder.Substituent.createAlkyl(1)).build()).getTips();
        Assert.assertEquals("getTips missed the tip of a substituted ring", 1, methylcyclopropaneTips.size());
        Assert.assertFalse("getTips misidentified the tip of a substituted ring", methylcyclopropaneTips.get(0).getConnectedAtom(1).isCarbon());
    }

    /** findPath finds the path from one atom to another in a linear molecule */
    @SuppressWarnings("ConstantConditions")
    @Test(timeout = HELPER_TEST_TIMEOUT)
    public void helpFindPath() {
        BondedAtom ethane = new LinearOrganicMoleculeBuilder(2).build();
        List<BondedAtom> ethanePath = new MoleculeAnalyzer(ethane).findPath(ethane, ethane.getConnectedAtom(1));
        Assert.assertEquals("findPath found a path of the wrong length on ethane", 2, ethanePath.size());
        Assert.assertSame("findPath's path on ethane started at the wrong atom", ethane, ethanePath.get(0));
        Assert.assertSame("findPath's path on ethane ended at the wrong atom", ethane.getConnectedAtom(1), ethanePath.get(1));

        BondedAtom isobutane = new LinearOrganicMoleculeBuilder(3)
                .addSubstituent(1, OrganicMoleculeBuilder.Substituent.createAlkyl(1)).build();
        List<BondedAtom> isobutanePath = new MoleculeAnalyzer(isobutane).findPath(isobutane.getConnectedAtom(1).getConnectedAtom(2), isobutane.getConnectedAtom(1).getConnectedAtom(1));
        Assert.assertEquals("findPath found a path of the wrong length from one tip of 2-methylpropane to another", 3, isobutanePath.size());
        Assert.assertSame("findPath's path on 2-methylpropane started at the wrong atom", isobutane.getConnectedAtom(1).getConnectedAtom(2), isobutanePath.get(0));
        Assert.assertSame("findPath's path on 2-methylpropane was incorrect", isobutane.getConnectedAtom(1), isobutanePath.get(1));
        Assert.assertSame("findPath's path on 2-methylpropane ended at the wrong atom", isobutane.getConnectedAtom(1).getConnectedAtom(1), isobutanePath.get(2));
    }

    /** getRing gets a list of the carbon atoms in the cycle, or null for linear molecules */
    @SuppressWarnings("ConstantConditions")
    @Test(timeout = HELPER_TEST_TIMEOUT)
    public void helpGetRing() {
        MoleculeAnalyzer methane = new MoleculeAnalyzer(new LinearOrganicMoleculeBuilder(1).build());
        Assert.assertNull("getRing misidentified methane as a ring", methane.getRing());

        MoleculeAnalyzer isopentane = new MoleculeAnalyzer(new LinearOrganicMoleculeBuilder(4)
                .addSubstituent(1, OrganicMoleculeBuilder.Substituent.createAlkyl(1)).build());
        Assert.assertNull("getRing found a ring in a branched linear molecule", isopentane.getRing());

        MoleculeAnalyzer cyclopentane = new MoleculeAnalyzer(new CyclicOrganicMoleculeBuilder(5).build());
        List<BondedAtom> cyclopentaneRing = cyclopentane.getRing();
        Assert.assertNotNull("getRing didn't find a ring on a simple cyclic molecule", cyclopentaneRing);
        Assert.assertEquals("getRing found a ring of the wrong size on a simple cyclic molecule", 5, cyclopentaneRing.size());
        Assert.assertEquals("getRing on cyclopentane included duplicate atoms", 5, cyclopentaneRing.stream().distinct().count());

        MoleculeAnalyzer cyclobutanone = new MoleculeAnalyzer(new CyclicOrganicMoleculeBuilder(4)
                .addSubstituent(0, OrganicMoleculeBuilder.Substituent.createCarbonyl()).build());
        List<BondedAtom> cyclobutanoneRing = cyclobutanone.getRing();
        Assert.assertNotNull("getRing didn't find a ring on a cyclic molecule with a substituent", cyclobutanoneRing);
        Assert.assertEquals("getRing found a ring of the wrong size on a cyclic molecule with a substituent", 4, cyclobutanoneRing.size());
        Assert.assertEquals("getRing on cyclobutanone included duplicate atoms", 4, cyclobutanoneRing.stream().distinct().count());
        Assert.assertTrue("getRing on cyclobutanone included non-carbon atoms", cyclobutanoneRing.stream().allMatch(BondedAtom::isCarbon));

        MoleculeAnalyzer tripropylcyclopropane = new MoleculeAnalyzer(new CyclicOrganicMoleculeBuilder(3)
                .addSubstituent(0, OrganicMoleculeBuilder.Substituent.createAlkyl(3))
                .addSubstituent(1, OrganicMoleculeBuilder.Substituent.createAlkyl(3))
                .addSubstituent(2, OrganicMoleculeBuilder.Substituent.createAlkyl(3)).build());
        List<BondedAtom> tripropylcyclopropaneRing = tripropylcyclopropane.getRing();
        Assert.assertNotNull("getRing didn't find a ring on a cyclic molecule with alkyl substituents", tripropylcyclopropaneRing);
        Assert.assertEquals("getRing found a ring of the wrong size on a cyclic molecule with alkyl substituents", 3, tripropylcyclopropaneRing.size());
        Assert.assertEquals("getRing on 1,2,3-tripropylcyclopropane included duplicate atoms", 3, tripropylcyclopropaneRing.stream().distinct().count());

        BondedAtom ethylcyclopropaneStart = new CyclicOrganicMoleculeBuilder(3)
                .addSubstituent(0, OrganicMoleculeBuilder.Substituent.createAlkyl(2)).build();
        MoleculeAnalyzer ethylcyclopropane = new MoleculeAnalyzer(ethylcyclopropaneStart.getConnectedAtom(0).getConnectedAtom(1));
        List<BondedAtom> ethylcyclopropaneRing = ethylcyclopropane.getRing();
        Assert.assertNotNull("getRing didn't find a ring when starting on a substituent tip", ethylcyclopropaneRing);
        Assert.assertEquals("getRing found a ring of the wrong size when starting on a substituent tip", 3, ethylcyclopropaneRing.size());

        ethylcyclopropane = new MoleculeAnalyzer(ethylcyclopropaneStart.getConnectedAtom(0));
        ethylcyclopropaneRing = ethylcyclopropane.getRing();
        Assert.assertNotNull("getRing didn't find a ring when starting in the middle of a substituent", ethylcyclopropaneRing);
        Assert.assertEquals("getRing found a ring of the wrong size when starting in the middle of a substituent", 3, ethylcyclopropaneRing.size());
    }

    /** getBackbones finds all possible backbones (paths between two tips) for a linear molecule */
    @Test(timeout = HELPER_TEST_TIMEOUT)
    public void helpGetBackbones() {
        List<List<BondedAtom>> ethaneBackbones = new MoleculeAnalyzer(new LinearOrganicMoleculeBuilder(2).build()).getBackbones();
        Assert.assertTrue("getBackbones on ethane included non-carbon atoms in a backbone",
                ethaneBackbones.stream().allMatch(b -> b.stream().allMatch(BondedAtom::isCarbon)));
        Assert.assertEquals("getBackbones only returned one direction for traversing a 2-carbon molecule", 2, ethaneBackbones.size());
        Assert.assertNotSame("getBackbones on ethane returned duplicate traversals", ethaneBackbones.get(0).get(0), ethaneBackbones.get(1).get(0));

        List<List<BondedAtom>> isopropanolBackbones = new MoleculeAnalyzer(new LinearOrganicMoleculeBuilder(3)
                .addSubstituent(1, OrganicMoleculeBuilder.Substituent.createAlcohol()).build()).getBackbones();
        Assert.assertEquals("getBackbones failed on a substituted 3-carbon molecule", 2, isopropanolBackbones.size());
        Assert.assertTrue("getBackbones on propan-2-ol included non-carbon atoms in a backbone",
                isopropanolBackbones.stream().allMatch(b -> b.stream().allMatch(BondedAtom::isCarbon)));

        List<List<BondedAtom>> isobutaneBackbones = new MoleculeAnalyzer(new LinearOrganicMoleculeBuilder(3)
                .addSubstituent(1, OrganicMoleculeBuilder.Substituent.createAlkyl(1)).build()).getBackbones();
        Assert.assertEquals("getBackbones failed on a singly-branched molecule", 6, isobutaneBackbones.size());

        List<List<BondedAtom>> trimethylbutaneBackbones = new MoleculeAnalyzer(new LinearOrganicMoleculeBuilder(4)
                .addSubstituent(1, OrganicMoleculeBuilder.Substituent.createAlkyl(1))
                .addSubstituent(2, OrganicMoleculeBuilder.Substituent.createAlkyl(1))
                .addSubstituent(2, OrganicMoleculeBuilder.Substituent.createAlkyl(1)).build()).getBackbones();
        Assert.assertEquals("getBackbones failed on a highly branched molecule", 20, trimethylbutaneBackbones.size());
    }

    /** getLinearBackbone finds the carbons in a linear molecule's backbone (longest chain, under most circumstances) */
    @Test(timeout = HELPER_TEST_TIMEOUT)
    public void helpGetLinearBackbone() {
        // NOTE: This will probably not pass until helpFindPath and helpGetBackbones are both passing

        MoleculeAnalyzer methane = new MoleculeAnalyzer(new LinearOrganicMoleculeBuilder(1).build());
        List<BondedAtom> methaneBackbone = methane.getLinearBackbone();
        Assert.assertEquals("getLinearBackbone didn't find the backbone of a one-carbon molecule", 1, methaneBackbone.size());
        Assert.assertTrue("getLinearBackbone selected a non-carbon atom as the backbone for a one-carbon molecule", methaneBackbone.get(0).isCarbon());

        MoleculeAnalyzer ethane = new MoleculeAnalyzer(new LinearOrganicMoleculeBuilder(2).build());
        Assert.assertEquals("getLinearBackbone didn't find the backbone of a two-carbon molecule", 2, ethane.getLinearBackbone().size());

        MoleculeAnalyzer ethanol = new MoleculeAnalyzer(new LinearOrganicMoleculeBuilder(2)
                .addSubstituent(0, OrganicMoleculeBuilder.Substituent.createAlcohol()).build());
        Assert.assertEquals("getLinearBackbone misidentified the backbone of an unbranched substituted molecule", 2, ethanol.getLinearBackbone().size());

        MoleculeAnalyzer bromopropane = new MoleculeAnalyzer(new LinearOrganicMoleculeBuilder(3)
                .addSubstituent(1, OrganicMoleculeBuilder.Substituent.createHalogen(ChemicalElement.BROMINE)).build());
        List<BondedAtom> bromopropaneBackbone = bromopropane.getLinearBackbone();
        Assert.assertEquals("getLinearBackbone misidentified the backbone of an unbranched substituted three-carbon molecule", 3, bromopropaneBackbone.size());
        Assert.assertTrue("getLinearBackbone included a non-carbon atom in the backbone", bromopropaneBackbone.stream().allMatch(BondedAtom::isCarbon));

        MoleculeAnalyzer methylbutane = new MoleculeAnalyzer(new LinearOrganicMoleculeBuilder(3)
                .addSubstituent(1, OrganicMoleculeBuilder.Substituent.createAlkyl(2)).build());
        Assert.assertEquals("getLinearBackbone misidentified the backbone of a branched molecule with 3 tips", 4, methylbutane.getLinearBackbone().size());

        MoleculeAnalyzer neopentane = new MoleculeAnalyzer(new LinearOrganicMoleculeBuilder(3)
                .addSubstituent(1, OrganicMoleculeBuilder.Substituent.createAlkyl(1))
                .addSubstituent(1, OrganicMoleculeBuilder.Substituent.createAlkyl(1)).build());
        Assert.assertEquals("getLinearBackbone misidentified the backbone of a symmetrical branched molecule with 4 tips", 3, neopentane.getLinearBackbone().size());

        MoleculeAnalyzer manyTips = new MoleculeAnalyzer(new LinearOrganicMoleculeBuilder(6)
                .addSubstituent(1, OrganicMoleculeBuilder.Substituent.createAlkyl(1))
                .addSubstituent(1, OrganicMoleculeBuilder.Substituent.createAlkyl(1))
                .addSubstituent(3, OrganicMoleculeBuilder.Substituent.createAlkyl(2))
                .addSubstituent(4, OrganicMoleculeBuilder.Substituent.createAlkyl(5))
                .addSubstituent(5, OrganicMoleculeBuilder.Substituent.createAlkyl(3)).build());
        Assert.assertEquals("getLinearBackbone misidentified the backbone of a molecule with 6 tips", 10, manyTips.getLinearBackbone().size());
    }

    /** rotateRing rotates and/or flips the ring such that a substituent, if present, is at the first position */
    @SuppressWarnings("ConstantConditions")
    @Test(timeout = HELPER_TEST_TIMEOUT)
    public void helpRotateRing() {
        // NOTE: If helpGetRing isn't passing, this won't pass either

        MoleculeAnalyzer cyclopropane = new MoleculeAnalyzer(new CyclicOrganicMoleculeBuilder(3).build());
        List<BondedAtom> cyclopropaneRing = cyclopropane.getRing();
        Assert.assertTrue("rotateRing didn't preserve the atoms in the ring",
                CollectionUtils.isEqualCollection(cyclopropane.rotateRing(cyclopropaneRing), cyclopropaneRing));

        MoleculeAnalyzer bromocyclopropane = new MoleculeAnalyzer(new CyclicOrganicMoleculeBuilder(3)
                .addSubstituent(0, OrganicMoleculeBuilder.Substituent.createHalogen(ChemicalElement.BROMINE)).build());
        List<BondedAtom> bromocyclopropaneRing = bromocyclopropane.getRing();
        List<BondedAtom> bromocyclopropaneRotated = bromocyclopropane.rotateRing(bromocyclopropaneRing);
        Assert.assertTrue("rotateRing didn't preserve the atoms in a substituted ring",
                CollectionUtils.isEqualCollection(bromocyclopropaneRotated, bromocyclopropaneRing));
        Assert.assertSame("rotateRing unnecessarily rotated an already-correct substituted ring",
                ChemicalElement.BROMINE, bromocyclopropaneRotated.get(0).getConnectedAtom(0).getElement());

        MoleculeAnalyzer fluorocyclopropane = new MoleculeAnalyzer(new CyclicOrganicMoleculeBuilder(3)
                .addSubstituent(1, OrganicMoleculeBuilder.Substituent.createHalogen(ChemicalElement.FLUORINE)).build());
        List<BondedAtom> fluorocyclopropaneRing = fluorocyclopropane.getRing();
        List<BondedAtom> fluorocyclopropaneRotated = fluorocyclopropane.rotateRing(fluorocyclopropaneRing);
        Assert.assertTrue("rotateRing didn't preserve the atoms in a substituted ring with necessary rotation",
                CollectionUtils.isEqualCollection(fluorocyclopropaneRing, fluorocyclopropaneRotated));
        Assert.assertSame("rotateRing didn't rotate a substituted ring correctly",
                ChemicalElement.FLUORINE, fluorocyclopropaneRotated.get(0).getConnectedAtom(2).getElement());

        BondedAtom methylcylobutaneMol = new CyclicOrganicMoleculeBuilder(4)
                .addSubstituent(2, OrganicMoleculeBuilder.Substituent.createAlkyl(1)).build();
        MoleculeAnalyzer methylcyclobutane = new MoleculeAnalyzer(methylcylobutaneMol);
        List<BondedAtom> methylcyclobutaneRing = methylcyclobutane.getRing();
        List<BondedAtom> methylcyclobutaneRotated = methylcyclobutane.rotateRing(methylcyclobutaneRing);
        Assert.assertFalse("rotateRing included a methyl substituent in the ring",
                methylcyclobutaneRotated.contains(methylcylobutaneMol.getConnectedAtom(1).getConnectedAtom(1).getConnectedAtom(2)));
        Assert.assertTrue("rotateRing didn't preserve the atoms in a methylated ring",
                CollectionUtils.isEqualCollection(methylcyclobutaneRing, methylcyclobutaneRotated));
        Assert.assertSame("rotateRing didn't rotate a methylated ring correctly",
                methylcylobutaneMol.getConnectedAtom(1).getConnectedAtom(1), methylcyclobutaneRotated.get(0));
    }
}
