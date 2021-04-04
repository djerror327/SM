async function loadVialationData() {
    const violationURL = vioalionAPI('SonarQubeOpenViolationMonitor', '2021-03');
    await fetch(violationURL)
        .then(res => res.json())
        .then(data => document.getElementById("a").innerHTML = JSON.stringify(data));
}
