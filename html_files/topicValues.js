/**sendValuesToGraphupdateValues
 * This function sends values to the graphFrame for updating the graph.
 * It uses the postMessage API to send a message to the iframe.
 *
 * @param {Array} values - The array of values to be sent to the graphFrame.
 */
function sendDataToGraph(values) {
  const graphiframe = window.parent.document.getElementById("graph-iframe");
  if (graphiframe) {
    graphiframe.contentWindow.postMessage(
      { type: "topicValues", values: values },
      "*"
    );
  } else {
    console.error("Error: graph-iframe element not found");
  }
}