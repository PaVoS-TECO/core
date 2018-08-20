package server.core.controller;

import server.core.command.StreamProcessingStrategy;

/**
 * A component that is used to build a ProcessorTopology. A topology contains an
 * acyclic graph of sources, processors, and sinks. A source is a node in the
 * graph that consumes one or more Kafka topics and forwards them to its child
 * nodes. A processor is a node in the graph that receives input records from
 * upstream nodes, processes that records, and optionally forwarding new records
 * to one or all of its children. Finally, a sink is a node in the graph that
 * receives records from upstream nodes and writes them to a Kafka topic. This
 * builder allows you to construct an acyclic graph of these nodes, and the
 * builder is then passed into a new KafkaStreams instance that will then begin
 * consuming, processing, and producing records
 */
public class TopologyBuilder {

	/**
	 * Default constructor
	 */
	public TopologyBuilder() {
	}

	/**
	 * Add a new source that consumes from topics matching the given pattern and
	 * forward the records to child processor and/or sink nodes.
	 * 
	 * @param name         name of the Input Topic Stream
	 * @param topicPattern topicPattern is a Pattern to filter the data from the
	 *                     Input Topic Stream
	 */
	public void addSource(String name, Topic topicPattern) {
		// TODO implement here
	}

	/**
	 * Add a new processor node that receives and processes records output by one or
	 * more parent source or processor node.
	 * 
	 * @param name     is the name of the Processor Stratgie
	 * @param supplier supplier is the supplier of the Process instant to generate
	 *                 more then 1 Process
	 * @param input    input Topic Stream name
	 */
	public void addProcessor(String name, StreamProcessingStrategy supplier, String input) {
		// TODO implement here
	}

	/**
	 * Add a new sink that forwards records from upstream parent processor and/or
	 * source nodes to the named Kafka topic.
	 * 
	 * @param name  name of the Sink
	 * @param topic name of the Topic Stream
	 */
	public void addSink(String name, String topic) {
		// TODO implement here
	}

}