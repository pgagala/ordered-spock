package com.github.pgagala.engine

import com.github.pgagala.algorithm.OrderingAlgorithm
import groovy.util.logging.Slf4j
import org.junit.platform.engine.TestDescriptor
import org.junit.platform.engine.UniqueId
import org.junit.platform.engine.support.descriptor.EngineDescriptor
import org.spockframework.runtime.RunContext
import org.spockframework.runtime.SpockEngineDescriptor
import org.spockframework.runtime.SpockEngineDiscoveryPostProcessor

import java.util.function.BiFunction

/**
 * Discovery post processor based on {@link org.spockframework.runtime.SpockEngineDiscoveryPostProcessor}.
 * Changing order of running specifications is possible via {@link com.github.pgagala.algorithm.OrderingAlgorithm}.
 */
@Slf4j
class OrderedSpockEngineDiscoveryPostProcessor {
    private final def spockPostProcessor
    private final BiFunction<UniqueId, RunContext, EngineDescriptor> engineDescriptorFactory

    OrderedSpockEngineDiscoveryPostProcessor(BiFunction<UniqueId, RunContext, EngineDescriptor> engineDescriptorFactory,
                                             SpockEngineDiscoveryPostProcessor spockPostProcessor) {
        this.engineDescriptorFactory = engineDescriptorFactory
        this.spockPostProcessor = spockPostProcessor
    }

    OrderedSpockEngineDiscoveryPostProcessor() {
        this.engineDescriptorFactory = (uniqueId, runContext) -> { new SpockEngineDescriptor(uniqueId, runContext) }
        this.spockPostProcessor = new SpockEngineDiscoveryPostProcessor()
    }

    SpockEngineDescriptor postProcessEngineDescriptor(UniqueId uniqueId, RunContext runContext,
                                                      SpockEngineDescriptor engineDescriptor,
                                                      OrderingAlgorithm orderingAlgorithm) {
        EngineDescriptor processedEngineDescriptor = engineDescriptorFactory.apply(uniqueId, runContext)
        getChildren(engineDescriptor, orderingAlgorithm)
                .stream()
                .map(child -> spockPostProcessor.processSpecNode(child, runContext))
                .forEach(processedEngineDescriptor::addChild)
        return processedEngineDescriptor
    }

    private static List<? extends TestDescriptor> getChildren(SpockEngineDescriptor engineDescriptor,
                                                              OrderingAlgorithm orderingAlgorithm) {
        def children = engineDescriptor.getChildren().toList()
        def shuffled = orderingAlgorithm.shuffle(children)
        log.info("Order of running specifications:\n${shuffled*.displayName}")
        return shuffled
    }
}
