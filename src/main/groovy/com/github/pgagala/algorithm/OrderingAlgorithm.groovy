package com.github.pgagala.algorithm

import groovy.transform.CompileStatic
import org.junit.platform.engine.TestDescriptor

@CompileStatic
trait OrderingAlgorithm {

    /**
     * Return new shuffled list of test descriptors.
     */
    abstract <T extends TestDescriptor> List<T> shuffle(List<T> testDescriptors)
}
