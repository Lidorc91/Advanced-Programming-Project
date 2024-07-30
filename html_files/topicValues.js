/**sendValuesToGraphupdateValues
 * This function sends values to the graphFrame for updating the graph.
 * It uses the postMessage API to send a message to the iframe.
 *
 * @param {Array} values - The array of values to be sent to the graphFrame.
 */
function sendDataToGraph(values) {
  // Get the reference to the graphFrame iframe
  console.log("inside sendValuesToGraph");
  const graphiframe = window.parent.document.getElementById("graph-iframe");
  // Send a message to the graphFrame
  graphiframe.contentWindow.postMessage(
    { type: "topicValues", values: values },
    "*"
  );
}
