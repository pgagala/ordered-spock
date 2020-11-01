package com.github.pgagala.engine

import com.github.pgagala.OrderedSpockProperties
import com.github.pgagala.algorithm.OrderingAlgorithmProvider
import org.junit.platform.engine.EngineDiscoveryRequest
import org.junit.platform.engine.TestDescriptor
import org.junit.platform.engine.UniqueId
import org.junit.platform.engine.support.descriptor.EngineDescriptor
import org.junit.platform.engine.support.discovery.EngineDiscoveryRequestResolver
import org.spockframework.runtime.ClassSelectorResolver
import org.spockframework.runtime.MethodSelectorResolver
import org.spockframework.runtime.RunContext
import org.spockframework.runtime.SpecUtil
import org.spockframework.runtime.SpockEngine
import org.spockframework.runtime.SpockEngineDescriptor

import java.util.function.BiFunction

import static java.util.Optional.of

/**
 * Ordered test engine based on {@link SpockEngine}. It runs specifications in order based on {@link com.github.pgagala.algorithm.OrderingAlgorithm}.
 */
class OrderedSpockEngine extends SpockEngine {

    private final OrderedSpockProperties properties
    private final OrderingAlgorithmProvider algorithmProvider
    private final BiFunction<UniqueId, RunContext, EngineDescriptor> engineDescriptorFactory
    private final OrderedSpockEngineDiscoveryPostProcessor engineDiscoveryPostProcessor

    OrderedSpockEngine(OrderedSpockProperties properties, OrderingAlgorithmProvider algorithmProvider,
                       BiFunction<UniqueId, RunContext, EngineDescriptor> engineDescriptorFactory,
                       OrderedSpockEngineDiscoveryPostProcessor engineDiscoveryPostProcessor) {
        this.properties = properties
        this.algorithmProvider = algorithmProvider
        this.engineDescriptorFactory = engineDescriptorFactory
        this.engineDiscoveryPostProcessor = engineDiscoveryPostProcessor
    }

    OrderedSpockEngine() {
        this.algorithmProvider = new OrderingAlgorithmProvider()
        this.properties = new OrderedSpockProperties()
        this.engineDescriptorFactory = (uniqueId, runContext) -> { new SpockEngineDescriptor(uniqueId, runContext) }
        this.engineDiscoveryPostProcessor = new OrderedSpockEngineDiscoveryPostProcessor()
    }

    @Override
    TestDescriptor discover(EngineDiscoveryRequest discoveryRequest, UniqueId uniqueId) {
        RunContext runContext = RunContext.get()
        SpockEngineDescriptor engineDescriptor = engineDescriptorFactory.apply(uniqueId, runContext)
        EngineDiscoveryRequestResolver.builder()
                .addClassContainerSelectorResolver(SpecUtil.&isRunnableSpec)
                .addSelectorResolver({ context -> new ClassSelectorResolver(context.getClassNameFilter()) })
                .addSelectorResolver(new MethodSelectorResolver())
                .build()
                .resolve(discoveryRequest, engineDescriptor)

        return engineDiscoveryPostProcessor
                .postProcessEngineDescriptor(uniqueId, runContext, engineDescriptor, algorithmProvider.getOrderingAlgorithm())
    }

    @Override
    String getId() {
        return properties.id()
    }

    @Override
    Optional<String> getGroupId() {
        return of(properties.groupId())
    }

    @Override
    Optional<String> getArtifactId() {
        return of(properties.artifactId())
    }

    @Override
    Optional<String> getVersion() {
        return of(properties.version())
    }
}