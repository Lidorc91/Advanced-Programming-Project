// Core script with shared functionality
export function getIframe(iframeId) {
  return document.getElementById(iframeId);
}

export function injectHtmlIntoIframe(iframe, html) {
  iframe.contentDocument.body.innerHTML = html;
}
