document.addEventListener("DOMContentLoaded", () => {
  const nodes = graphConfig.nodes;
  const links = graphConfig.links;

  // Initialize D3.js force simulation
  const width = document.getElementById("graph-container").offsetWidth;
  const height = 600;

  const svg = d3
    .select("#graph-container")
    .append("svg")
    .attr("width", width)
    .attr("height", height);

  const simulation = d3
    .forceSimulation(nodes)
    .force(
      "link",
      d3
        .forceLink(links)
        .id((d) => d.id)
        .distance(150)
    )
    .force("charge", d3.forceManyBody().strength(-400))
    .force("center", d3.forceCenter(width / 2, height / 2));

  // Define the arrow markers for directed links
  svg
    .append("defs")
    .selectAll("marker")
    .data(["arrow"])
    .enter()
    .append("marker")
    .attr("id", (d) => d)
    .attr("viewBox", "0 -5 10 10")
    .attr("refX", 25) // Adjusted refX to position arrowhead closer to the end of the line
    .attr("refY", 0) // Y-offset from center of node
    .attr("markerWidth", 6)
    .attr("markerHeight", 6)
    .attr("orient", "auto")
    .append("path")
    .attr("d", "M0,-5L10,0L0,5"); // Arrowhead path

  // Add links to the graph with arrows
  const link = svg
    .append("g")
    .selectAll(".link")
    .data(links)
    .enter()
    .append("line")
    .attr("class", "link")
    .attr("marker-end", "url(#arrow)"); // Add marker-end for arrows

  // Add nodes to the graph
  const node = svg
    .append("g")
    .selectAll(".node")
    .data(nodes)
    .enter()
    .append("g")
    .attr("class", "node")
    .call(
      d3
        .drag()
        .on("start", dragstarted)
        .on("drag", dragged)
        .on("end", dragended)
    );

  // Add circles for agents with color and size
  node
    .filter((d) => d.type === "Agent")
    .append("circle")
    .attr("r", 25) // Size of circles
    .attr("fill", "lightblue"); // Color for circles

  // Add rectangles for topics with color and size
  node
    .filter((d) => d.type === "Topic")
    .append("rect")
    .attr("width", 80) // Size of rectangles
    .attr("height", 40) // Size of rectangles
    .attr("rx", 10) // Rounded corners
    .attr("ry", 10)
    .attr("fill", "lightgreen"); // Color for rectangles

  // Add labels to nodes
  node
    .append("text")
    .text((d) => d.id.slice(1))
    .attr("text-anchor", "middle")
    .attr("dominant-baseline", "middle")
    .style("font-size", "10px") // Font size for text
    .each(function (d) {
      // Position text based on node type
      if (d.type === "Agent") {
        // For circles (agents), position text at the center
        d3.select(this).attr("transform", `translate(0, 0)`);
      } else if (d.type === "Topic") {
        // For rectangles (topics), position text inside the rectangle
        const rectWidth = 80; // Adjust according to your rectangle width
        const rectHeight = 40; // Adjust according to your rectangle height
        d3.select(this).attr(
          "transform",
          `translate(${rectWidth / 2}, ${rectHeight / 2})`
        );
      }
    });

  // Simulation tick function
  simulation.on("tick", () => {
    // Update link positions
    link
      .attr("x1", (d) => d.source.x)
      .attr("y1", (d) => d.source.y)
      .attr("x2", (d) => d.target.x)
      .attr("y2", (d) => d.target.y);

    // Update node positions
    node.attr("transform", (d) => `translate(${d.x},${d.y})`);
  });

  function dragstarted(event, d) {
    if (!event.active) simulation.alphaTarget(0.3).restart();
    d.fx = d.x;
    d.fy = d.y;
  }

  function dragged(event, d) {
    const minX = 0;
    const maxX = width;
    const minY = 0;
    const maxY = height;

    d.fx = Math.max(minX, Math.min(event.x, maxX));
    d.fy = Math.max(minY, Math.min(event.y, maxY));
  }

  function dragended(event, d) {
    if (!event.active) simulation.alphaTarget(0);
    d.fx = null;
    d.fy = null;
  }
});
