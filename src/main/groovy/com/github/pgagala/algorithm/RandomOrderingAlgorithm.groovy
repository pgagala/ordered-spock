package com.github.pgagala.algorithm

import groovy.transform.CompileStatic
import org.junit.platform.engine.TestDescriptor
import org.spockframework.runtime.SpecNode

/**
 * Randomly shuffling the test descriptors. Purpose is to avoid tests running in same order during
 * subsequent executions.
 * Different order of tests execution can unrevealed bugs which otherwise will be hidden.
 */
@CompileStatic
class RandomOrderingAlgorithm implements OrderingAlgorithm {

    @Override
    <T extends TestDescriptor> List<T> shuffle(List<T> testDescriptorsToShuffle) {
        List<T> shuffledTestDescriptors = new ArrayList<>(testDescriptorsToShuffle)

        while ({
            shuffledTestDescriptors.shuffle(new Random(System.nanoTime()))
            !orderIsDifferent(shuffledTestDescriptors, testDescriptorsToShuffle)
        }())
            continue

        return shuffledTestDescriptors.asUnmodifiable()
    }

    def <T extends TestDescriptor> boolean orderIsDifferent(List<T> shuffledElements, List<T> elementsToShuffle) {
        return shuffledElements.stream()
                .anyMatch((SpecNode node) -> {
                    int indexOfShuffledNode = shuffledElements.indexOf(node)
                    node.uniqueId != elementsToShuffle[indexOfShuffledNode].uniqueId
                })
    }
}
