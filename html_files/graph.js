// Example of a function to generate a directed graph using D3.js
function generateGraph(graphConfig) {
    const nodes = graphConfig.nodes;
    const links = graphConfig.links;

    // Initialize D3.js force simulation
    const width = document.getElementById('graph-container').offsetWidth;
    const height = 600;

    const svg = d3.select('#graph-container')
                  .append('svg')
                  .attr('width', width)
                  .attr('height', height);

    const simulation = d3.forceSimulation(nodes)
                         .force('link', d3.forceLink(links).id(d => d.id).distance(150))
                         .force('charge', d3.forceManyBody().strength(-400))
                         .force('center', d3.forceCenter(width / 2, height / 2));

    // Define the arrow markers for directed links
    svg.append('defs').selectAll('marker')
        .data(['arrow'])
        .enter().append('marker')
        .attr('id', d => d)
        .attr('viewBox', '0 -5 10 10')
        .attr('refX', 25) // Adjusted refX to position arrowhead closer to the end of the line
        .attr('refY', 0)  // Y-offset from center of node
        .attr('markerWidth', 6)
        .attr('markerHeight', 6)
        .attr('orient', 'auto')
        .append('path')
        .attr('d', 'M0,-5L10,0L0,5'); // Arrowhead path

    // Add links to the graph with arrows
    const link = svg.append('g')
                    .selectAll('.link')
                    .data(links)
                    .enter().append('line')
                    .attr('class', 'link')
                    .attr('marker-end', 'url(#arrow)'); // Add marker-end for arrows

    // Add nodes to the graph
    const node = svg.append('g')
                    .selectAll('.node')
                    .data(nodes)
                    .enter().append('g')
                    .attr('class', 'node')
                    .call(d3.drag()
                            .on('start', dragstarted)
                            .on('drag', dragged)
                            .on('end', dragended));

    // Add circles for agents
    node.filter(d => d.type === 'agent')
        .append('circle')
        .attr('r', 20);

    // Add rectangles for topics
    node.filter(d => d.type === 'topic')
        .append('rect')
        .attr('width', 60)
        .attr('height', 30)
        .attr('rx', 5) // Rounded corners
        .attr('ry', 5);

    // Add labels to nodes
    node.append('text')
        .text(d => d.id)
        .attr('text-anchor', 'middle')
        .attr('dominant-baseline', 'middle')
        .style('font-size', d => (d.type === 'agent' ? '12px' : '14px')) // Adjust font size based on node type
        .each(function(d) {
            // Calculate text width and height
            const textWidth = this.getComputedTextLength();
            const textHeight = parseFloat(d3.select(this).style('font-size'));
            
            // Position text based on node type
            if (d.type === 'agent') {
                // For circles (agents), position text at the center
                d3.select(this).attr('transform', `translate(0, ${textHeight / 2})`);
            } else if (d.type === 'topic') {
                // For rectangles (topics), position text inside the rectangle
                const rectWidth = 60; // Adjust according to your rectangle width
                const rectHeight = 30; // Adjust according to your rectangle height
                d3.select(this).attr('transform', `translate(${rectWidth / 2}, ${rectHeight / 2})`);
            }
        });

    // Simulation tick function
    simulation.on('tick', () => {
        // Update link positions
        link.attr('x1', d => d.source.x)
            .attr('y1', d => d.source.y)
            .attr('x2', d => d.target.x)
            .attr('y2', d => d.target.y);

        // Update node positions
        node.attr('transform', d => `translate(${d.x},${d.y})`);
    });

    function dragstarted(event, d) {
        if (!event.active) simulation.alphaTarget(0.3).restart();
        d.fx = d.x;
        d.fy = d.y;
    }

    function dragged(event, d) {
        d.fx = event.x;
        d.fy = event.y;
    }

    function dragended(event, d) {
        if (!event.active) simulation.alphaTarget(0);
        d.fx = null;
        d.fy = null;
    }
}

// Example usage:
const graphConfig = {
    nodes: [
        { id: 'Agent1', type: 'agent' },
        { id: 'Agent2', type: 'agent' },
        { id: 'Topic1', type: 'topic' },
        { id: 'Topic2', type: 'topic' },
        { id: 'Topic3', type: 'topic' }
    ],
    links: [
        { source: 'Agent1', target: 'Topic1' },
        { source: 'Agent1', target: 'Topic2' },
        { source: 'Agent2', target: 'Topic1' },
        { source: 'Agent2', target: 'Topic3' }
    ]
};

generateGraph(graphConfig);
