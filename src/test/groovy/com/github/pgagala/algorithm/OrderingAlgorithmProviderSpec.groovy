package com.github.pgagala.algorithm

import groovy.transform.CompileStatic
import org.junit.platform.engine.TestDescriptor
import spock.lang.Specification

class OrderingAlgorithmProviderSpec extends Specification {

    def 'Default random ordering algorithm should be returned'() {
        expect:
            new OrderingAlgorithmProvider().getOrderingAlgorithm() instanceof RandomOrderingAlgorithm
    }

    def 'For more than one defined ordering algorithm exception should be thrown'() {
        given: "More than one ordering algorithm predefined"
            OrderingAlgorithmProvider orderingAlgorithmProvider =
                    new OrderingAlgorithmProvider({ [new TestAlgorithm(), new TestAlgorithm2()] })

        when: 'ordering algorithm is requested'
            orderingAlgorithmProvider.getOrderingAlgorithm()

        then: 'exception is thrown'
            thrown IllegalStateException
    }

    @CompileStatic
    class TestAlgorithm implements OrderingAlgorithm {
        @Override
        <T extends TestDescriptor> List<T> shuffle(List<T> testDescriptorsToShuffle) {
            return testDescriptorsToShuffle.asUnmodifiable()
        }
    }

    @CompileStatic
    class TestAlgorithm2 implements OrderingAlgorithm {
        @Override
        <T extends TestDescriptor> List<T> shuffle(List<T> testDescriptorsToShuffle) {
            return testDescriptorsToShuffle.asUnmodifiable()
        }
    }
}
