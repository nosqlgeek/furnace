package com.tinkerpop.furnace.computer.programs.clustering;

import com.google.common.base.Preconditions;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.furnace.computer.Evaluator;
import com.tinkerpop.furnace.computer.GlobalMemory;
import com.tinkerpop.furnace.computer.GraphComputer;
import com.tinkerpop.furnace.computer.GraphComputerBuilder;
import com.tinkerpop.furnace.computer.Isolation;
import com.tinkerpop.furnace.computer.LocalMemory;
import com.tinkerpop.furnace.util.VertexQueryBuilder;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public class PeerPressureGraphComputer extends GraphComputer {

    protected int totalIterations;

    public static final String CLUSTER = "cluster";
    public static final String EDGE_COUNT = "edgeCount";

    protected PeerPressureGraphComputer() {
        super();
    }

    public boolean terminate() {
        return this.getGlobalMemory().getIteration() >= this.totalIterations;
    }

    public static Builder create() {
        return new Builder();
    }

    public static class Builder extends GraphComputerBuilder {

        protected int iterations = 30;
        protected VertexQueryBuilder incomingQuery = new VertexQueryBuilder().direction(Direction.IN);
        protected VertexQueryBuilder outgoingQuery = new VertexQueryBuilder().direction(Direction.OUT);

        protected Builder() {
        }

        public Builder isolation(final Isolation isolation) {
            super.isolation(isolation);
            return this;
        }

        public Builder globalMemory(final GlobalMemory globalMemory) {
            super.globalMemory(globalMemory);
            return this;
        }

        public Builder localMemory(final LocalMemory localMemory) {
            super.localMemory(localMemory);
            return this;
        }

        public Builder evaluator(final Evaluator evaluator) {
            super.evaluator(evaluator);
            return this;
        }

        public Builder graph(final Graph graph) {
            super.graph(graph);
            return this;
        }

        public Builder iterations(final int iterations) {
            this.iterations = iterations;
            return this;
        }

        public Builder incomingQuery(final VertexQueryBuilder builder) {
            this.incomingQuery = builder;
            return this;
        }

        public Builder outgoingQuery(final VertexQueryBuilder builder) {
            this.outgoingQuery = builder;
            return this;
        }

        public PeerPressureGraphComputer build() {
            super.build();
            Preconditions.checkNotNull(this.iterations);
            Preconditions.checkNotNull(this.incomingQuery);
            Preconditions.checkNotNull(this.outgoingQuery);

            final PeerPressureGraphComputer computer = new PeerPressureGraphComputer();
            computer.globalMemory = this.globalMemory;
            computer.isolation = this.isolation;

            computer.localMemory = this.localMemory;
            computer.localMemory.setComputeKeys(CLUSTER);
            computer.localMemory.setFinalComputeKeys(EDGE_COUNT);
            computer.localMemory.setIsolation(this.isolation);

            computer.totalIterations = this.iterations;
            computer.vertexProgram = new PeerPressureVertexProgram(this.outgoingQuery, this.incomingQuery);
            computer.evaluator = this.evaluator;
            computer.graph = graph;
            return computer;
        }
    }
}